package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;

import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * @param email to use for search for user
     * @return User when found, null otherwise
     */
    User findUserByEmailIgnoreCase(String email);

    /**
     *
     * @param deleted value of deleted (filters out everything that doesnt match deleted)
     * @param pageable the pageable object
     * @return a page of users (not deleted)
     */
    Page<User> findAllByDeleted(Integer deleted, Pageable pageable);
}
