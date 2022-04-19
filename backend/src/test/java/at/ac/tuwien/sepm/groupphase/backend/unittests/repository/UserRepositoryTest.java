package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProvider userProvider;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        userRepository.save(userProvider.getAdmin());
    }

    @Test
    public void givenUser_whenFindUserByEmail_thenFindSingleUserWithEncodedPassword() {
        User admin = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        assertAll(
            () -> assertNotNull(admin),
            () -> assertTrue(passwordEncoder.matches(TestData.PLAIN_PASSWORD, admin.getPassword()))
        );
    }

    @Test
    public void givenNotExistingUser_whenFindUserByEmail_thenFindNull() {
        User notExisting = userRepository.findUserByEmailIgnoreCase(TestData.NOT_EXISTING_EMAIL);
        assertAll(
            () -> assertNull(notExisting)
        );
    }

}
