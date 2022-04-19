package at.ac.tuwien.sepm.groupphase.backend.helper;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @return A user object representing a admin user
     * */
    public User getAdmin() {
        return User.builder()
            .id(TestData.ADMIN_ID)
            .admin(true)
            .birthday(LocalDate.now())
            .firstname("Admin")
            .surname("Admin")
            .password(passwordEncoder.encode(TestData.PLAIN_PASSWORD))
            .signInAttempts(5)
            .email("admin@email.com")
            .deleted(0)
            .build();
    }

    /**
     * @return A user object representing a normal user
     * */
    public User getNormal() {
        return User.builder()
            .id(TestData.NORMAL_ID)
            .admin(false)
            .birthday(LocalDate.now())
            .firstname("Normal")
            .surname("Normal")
            .password(passwordEncoder.encode(TestData.PLAIN_PASSWORD))
            .signInAttempts(3)
            .email("normal@email.com")
            .deleted(0)
            .build();
    }

    /**
     * @param i a unique identifier for the user
     * @return A user with random fields and a unique identifier i
     * */
    public User getRandom(int i) {
        return User.builder()
            .id((long) i)
            .admin(Math.random() < 0.5)
            .birthday(LocalDate.now())
            .firstname("User" + i)
            .surname("User" + i)
            .password(passwordEncoder.encode(TestData.PLAIN_PASSWORD))
            .signInAttempts(i % 6)
            .email("user" + i + "@email.com")
            .deleted(0)
            .build();
    }

}
