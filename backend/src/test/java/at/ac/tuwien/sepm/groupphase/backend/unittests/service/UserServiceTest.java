package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.SelfLockException;
import at.ac.tuwien.sepm.groupphase.backend.helper.MockUser;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProvider userProvider;

    private final static int PAGE_SIZE = 20;

    private final PageRequest testPageable0 = PageRequest.of(0, PAGE_SIZE);
    private final PageRequest testPageable1 = PageRequest.of(1, PAGE_SIZE);

    @BeforeEach
    public void beforeEach() {
        User admin = userProvider.getAdmin();
        when(userRepository.findUserByEmailIgnoreCase(admin.getEmail())).thenReturn(admin);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        User normal = userProvider.getNormal();
        when(userRepository.findUserByEmailIgnoreCase(normal.getEmail())).thenReturn(normal);
        when(userRepository.findById(normal.getId())).thenReturn(Optional.of(normal));

        when(userRepository.save(userProvider.getRandom(1000))).thenReturn(userProvider.getRandom(1000));

        List<User> testUsers = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            testUsers.add(userProvider.getRandom(i));
        }

        when(userRepository.findAll(testPageable0)).thenReturn(new PageImpl<>(testUsers.subList(0, 20), testPageable0, 25));
        when(userRepository.findAll(testPageable1)).thenReturn(new PageImpl<>(testUsers.subList(20, 25), testPageable1, 25));

        when(userRepository.findAllByDeleted(0, testPageable0)).thenReturn(new PageImpl<>(testUsers.subList(0, 20), testPageable0, 25));
        when(userRepository.findAllByDeleted(0, testPageable1)).thenReturn(new PageImpl<>(testUsers.subList(20, 25), testPageable1, 25));
    }

    @Test
    public void givenExistingUser_onSignUp_throwCreateFailedException() {
        User admin = userProvider.getAdmin();
        assertAll(
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(admin))
        );
    }

    @Test
    public void givenNotExistingUser_onSignUpWithInvalidPassword_throwCreateFailedException() {
        User user1 = User.builder()
            .id(-10L)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password("1234567")
            .signInAttempts(3)
            .email("wrongmail")
            .build();
        User user2 = User.builder()
            .id(-11L)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
            .signInAttempts(3)
            .email("wrongmail")
            .build();

        assertAll(
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(user1)),
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(user2))
        );
    }

    @Test
    public void givenAUser_onSignUpWithInvalidEmail_throwCreateFailedException() {
        User user1 = User.builder()
            .id(-10L)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password(passwordEncoder.encode("12345678"))
            .signInAttempts(3)
            .email("@mail.at")
            .build();
        User user2 = User.builder()
            .id(-10L)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password(passwordEncoder.encode("12345678"))
            .signInAttempts(3)
            .email("new.at")
            .build();
        User user3 = User.builder()
            .id(-10L)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password(passwordEncoder.encode("12345678"))
            .signInAttempts(3)
            .email("new@")
            .build();
        assertAll(
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(user1)),
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(user2)),
            () -> assertThrows(CreateFailedException.class, () -> userService.signupUser(user3))
        );
    }

    @Test
    public void givenAdminUser_whenLoadUserByUsername_thenUserWithRoleAdmin() {
        UserDetails admin = userService.loadUserByUsername(userProvider.getAdmin().getEmail());
        assertAll(
            () -> assertNotNull(admin),
            () -> assertEquals(1, (int) admin.getAuthorities().stream().filter(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")).count())
        );
    }

    @Test
    public void givenNotExistingUser_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
        assertThrows(
            UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(TestData.NOT_EXISTING_EMAIL)
        );
    }

    @Test
    public void givenUserWith3SignInAttempts_whenIncreaseSignInAttemptsByEmail_thenUserHas4SignInAttempts() {
        userService.increaseUserSignInAttemptsByEmail(userProvider.getNormal().getEmail());
        assertAll(
            () -> assertEquals(4, userService.findUserByEmail(userProvider.getNormal().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenUserWith5SignInAttempts_whenIncreaseSignInAttemptsByEmail_thenUserHas5SignInAttempts() {
        userService.increaseUserSignInAttemptsByEmail(userProvider.getAdmin().getEmail());
        assertAll(
            () -> assertEquals(5, userService.findUserByEmail(userProvider.getAdmin().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenNotExistingUser_whenIncreaseSignInAttemptsByEmail_thenThrowNotFoundException() {
        assertThrows(
            NotFoundException.class,
            () -> userService.increaseUserSignInAttemptsByEmail(TestData.NOT_EXISTING_EMAIL)
        );
    }

    @Test
    public void givenNotExistingUser_whenResetSignInAttemptsByEmail_thenThrowNotFoundException() {
        assertThrows(
            NotFoundException.class,
            () -> userService.resetUserSignInAttemptsByEmail(TestData.NOT_EXISTING_EMAIL)
        );
    }

    @Test
    public void givenUserWith5SignInAttempts_whenResetSignInAttemptsByEmail_thenUserHas0SignInAttempts() {
        userService.resetUserSignInAttemptsByEmail(userProvider.getAdmin().getEmail());
        assertAll(
            () -> assertEquals(0, userService.findUserByEmail(userProvider.getAdmin().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void given25Users_whenFindFirstPage_thenReturn20UsersTotal2PagesHasNextTrueCurrPage0() {
        CustomPage<User> users = userService.findAllPaged(testPageable0);
        assertAll(
            () -> assertEquals(20, users.getContent().size()),
            () -> assertTrue(users.hasNext()),
            () -> assertEquals(0, users.getNumber()),
            () -> assertEquals(2, users.getTotalPages())
        );
    }

    @Test
    public void given25Users_whenFindSecondPage_thenReturn5UsersTotal2PagesHasNextFalseCurrPage1() {
        CustomPage<User> users = userService.findAllPaged(testPageable1);
        assertAll(
            () -> assertEquals(5, users.getContent().size()),
            () -> assertFalse(users.hasNext()),
            () -> assertEquals(1, users.getNumber()),
            () -> assertEquals(2, users.getTotalPages())
        );
    }

    @Test
    @MockUser(username = "admin@email.com", authorities = {"ROLE_ADMIN", "ROLE_USER"})
    public void givenUnlockedUser_whenLockUser_thenUserSignInAttempts5() {
        User user = userService.findUserByEmail(userProvider.getNormal().getEmail());
        userService.lockUser(user.getId());
        assertAll(
            () -> assertEquals(5, userService.findUserByEmail(userProvider.getNormal().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenLockedUser_whenUnlockUser_thenUserSignInAttempts0() {
        User user = userService.findUserByEmail(userProvider.getAdmin().getEmail());
        userService.unlockUser(user.getId());
        assertAll(
            () -> assertEquals(0, userService.findUserByEmail(userProvider.getAdmin().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenNotExistingUser_whenUnlockUser_thenThrowNotFoundException() {
        assertThrows(
            NotFoundException.class,
            () -> userService.unlockUser(TestData.NOT_EXISTING_ID)
        );
    }

    @Test
    @MockUser(username = "admin@email.com", authorities = {"ROLE_ADMIN", "ROLE_USER"})
    public void givenCurrentUser_whenLockUser_thenThrowSelfLockException() {
        User user = userService.findUserByEmail(userProvider.getAdmin().getEmail());
        assertThrows(
            SelfLockException.class,
            () -> userService.lockUser(user.getId())
        );
    }

}
