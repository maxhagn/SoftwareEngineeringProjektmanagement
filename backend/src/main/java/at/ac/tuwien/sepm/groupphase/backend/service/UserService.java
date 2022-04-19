package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.PwResetDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import at.ac.tuwien.sepm.groupphase.backend.exception.ChangeFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.SelfLockException;
import at.ac.tuwien.sepm.groupphase.backend.security.MyAuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Resets a users password. Is not implemented since no emails can be sent.
     *
     *
     */
    void resetUserPassword(Long id);

    /**
     *
     * @param dto token and new pw
     */
    void resetUserPassword(PwResetDto dto);

    /**
     * Find a user based on the email address
     *
     * @param email the email address
     * @return User if found
     * @throws NotFoundException if user is not found
     */
    User findUserByEmail(String email) throws NotFoundException;

    /**
     * Find a user based on the email address and increase its signInAttempts
     *
     * @param email the email address
     * @return the new signIn attempts cound
     * @throws NotFoundException if user is not found
     */
    Integer increaseUserSignInAttemptsByEmail(String email) throws NotFoundException;

    /**
     * Finds all available users
     * @param pageInfo info about which page and how much elements should be loaded
     * @return All users
     * */
    CustomPage<User> findAllPaged(Pageable pageInfo);

    /**
     * Locks a user by userId
     * @param userId The userId of the user to lock
     * @throws NotFoundException if no user exists for the given userId
     * @throws SelfLockException if user to be locked is oneself
     * */
    void lockUser(long userId) throws NotFoundException, SelfLockException;

    /**
     * Unlocks a user by userId
     * @param userId The userId of the user to unlock
     * @throws NotFoundException if no user exists for the given userId
     * */
    void unlockUser(long userId) throws NotFoundException;

    /**
     * Find a user based on the email address and reset its singInAttempts
     *
     * @param email the email address
     * @throws NotFoundException if user is not found
     */
    void resetUserSignInAttemptsByEmail(String email) throws NotFoundException;

    /**
     * Creates a User based on the email adress and password and sets name and surname
     * @param user the user containing the parameters
     * @return the created user
     * @throws CreateFailedException is thrown if the create process has failed
     */
    User signupUser(User user) throws CreateFailedException;

    /**
     *
     * @param user The new fields of the user
     * @param authUser The authentication providing the user to be changed
     * @return the changed user
     * @throws ChangeFailedException if the update fails
     */
    User changeUser(User user, MyAuthUser authUser) throws ChangeFailedException;

    /**
     *
     * @param authUser The authentication providing the user to be deleted
     * @throws NotFoundException if the authentication is invalid
     */
    void deleteUser(MyAuthUser authUser) throws NotFoundException;
}
