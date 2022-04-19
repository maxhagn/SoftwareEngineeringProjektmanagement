package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.PwResetDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.MyAuthUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator, JavaMailSender mailSender, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.mailSender = mailSender;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public void resetUserPassword(Long id) {
        LOGGER.info("User " + id + " tried reseting password.");

        Optional<User> optU = userRepository.findById(id);
        if(optU.isEmpty()){
            throw new NotFoundException("User Not Found");
        }
        User u = optU.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validUntil = now.plusDays(2);

        PasswordResetToken myToken = PasswordResetToken.builder().token(token).user(u).expireDate(validUntil).build();
        passwordResetTokenRepository.save(myToken);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@ticketline.com");
        message.setTo(u.getEmail());
        message.setSubject("Ticketline password reset request");
        message.setText("Please click the following link to reset your password:\n http://localhost:4200/resetPw/"+token+"\n This link is valid for 48 hours");
        mailSender.send(message);
    }

    @Override
    public void resetUserPassword(PwResetDto dto) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(dto.getToken());
        LOGGER.info("pw reset" + dto);

        if(passToken == null || !passToken.isValid()){
            throw new ValidationException("This token is invalid. Please request a new one from your Administrator.");
        }

        passToken.getUser().setPassword(passwordEncoder.encode(dto.pw));
        userRepository.save(passToken.getUser());
        passwordResetTokenRepository.delete(passToken);
    }

    @Override
    public void deleteUser(MyAuthUser authUser) throws NotFoundException {
        User user = authUser.getCurrentUser();
        User dbUser;
        if( (dbUser = userRepository.findUserByEmailIgnoreCase(user.getEmail()))!=null ){
            LOGGER.info("Deleting user "+dbUser.toString());
            dbUser.setPassword("");
            dbUser.setBirthday(null);
            dbUser.setSurname(null);
            dbUser.setFirstname(null);
            dbUser.setEmail(null);
            dbUser.setDeleted(1);
            userRepository.save(dbUser);
        }else{
            throw new NotFoundException("User can not be deleted, your authentication token is invalid");
        }
    }

    @Override
    public User changeUser(User user, MyAuthUser authUser) throws ChangeFailedException {
        LOGGER.info("Change user "+ user);
        try {
            validator.validateChangeUser(user);
        } catch (ValidationException e) {
            throw new ChangeFailedException(e.getMessage());
        }
        User oldUser = authUser.getCurrentUser();
        User dbUser;
        if ((dbUser = userRepository.findUserByEmailIgnoreCase(oldUser.getEmail())) != null) {
            if(user.getEmail()!=null) {
                if (!(user.getEmail().equals(dbUser.getEmail()))) {
                    if (userRepository.findUserByEmailIgnoreCase(user.getEmail()) != null)
                        throw new ChangeFailedException("Email already in use");
                }
            }
            LOGGER.debug("Change user");
            if (user.getEmail() != null) dbUser.setEmail(user.getEmail());
            if (user.getFirstname() != null) dbUser.setFirstname(user.getFirstname());
            if (user.getSurname() != null) dbUser.setSurname(user.getSurname());
            if (user.getPassword() != null) dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(dbUser);
        }
        LOGGER.error("User with id:" + oldUser.getId() + " from AuthUser doesnt have email");
        throw new ChangeFailedException("Change failed. Database incoherent, please contact an admin!"); //Should never be reached
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            User user = findUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (user.isAdmin())
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            else
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public User signupUser(User user) throws CreateFailedException {
        LOGGER.debug("Register user by email and password");
        try {
            validator.validateCreateUser(user);
        } catch (ValidationException e) {
            throw new CreateFailedException(e.getMessage());
        }
        if (userRepository.findUserByEmailIgnoreCase(user.getEmail()) == null) {
            User hashedPassUser = new User();
            hashedPassUser.setEmail(user.getEmail());
            hashedPassUser.setPassword(passwordEncoder.encode(user.getPassword()));
            hashedPassUser.setFirstname(user.getFirstname());
            hashedPassUser.setSurname(user.getSurname());
            hashedPassUser.setAdmin(user.isAdmin());
            hashedPassUser.setDeleted(0);
            User registereduser = userRepository.save(hashedPassUser);
            return registereduser;
        }
        throw new CreateFailedException("Create failed. Email is already used by another user.");
    }

    public User findUserByEmail(String email) throws NotFoundException {
        LOGGER.debug("Find single user by mail");
        Optional<User> user = Optional.ofNullable(userRepository.findUserByEmailIgnoreCase(email));
        if (user.isPresent()) {
            return user.get();
        }
        throw new NotFoundException(String.format("Could not find the user with email address %s", email));
    }

    @Override
    public CustomPage<User> findAllPaged(Pageable pageInfo) {
        LOGGER.debug("Find all users, page {}", pageInfo.getPageNumber());
        return new CustomPage<>(userRepository.findAllByDeleted(0, pageInfo));
    }

    @Override
    public void lockUser(long userId) throws NotFoundException, SelfLockException {
        LOGGER.debug("Lock user by id {}", userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = Optional.ofNullable(userRepository.findUserByEmailIgnoreCase(authentication.getPrincipal().toString()));
        if (currentUser.isEmpty()) {
            throw new ServerErrorException("An error occurred, current user does not exist any longer.");
        }
        if (currentUser.get().getId() == userId) {
            throw new SelfLockException("Cannot lock current user.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setSignInAttempts(5);
            userRepository.save(foundUser);
            return;
        }
        throw new NotFoundException("User with id " + userId + " could not be found");
    }

    @Override
    public void unlockUser(long userId) throws NotFoundException {
        LOGGER.debug("Unlock user by id {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setSignInAttempts(0);
            userRepository.save(foundUser);
            return;
        }
        throw new NotFoundException("User with id " + userId + " could not be found");
    }

    @Override
    public Integer increaseUserSignInAttemptsByEmail(String email) throws NotFoundException {
        User foundUser = findUserByEmail(email);
        Integer newAttemptsCount = Math.min(foundUser.getSignInAttempts() + 1, 5);
        foundUser.setSignInAttempts(newAttemptsCount);
        userRepository.save(foundUser);
        return newAttemptsCount;
    }

    @Override
    public void resetUserSignInAttemptsByEmail(String email) throws NotFoundException {
        User foundUser = findUserByEmail(email);
        foundUser.setSignInAttempts(0);
        userRepository.save(foundUser);
    }
}