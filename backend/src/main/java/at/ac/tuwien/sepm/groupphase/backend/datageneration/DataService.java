package at.ac.tuwien.sepm.groupphase.backend.datageneration;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.LocalDate;

@Profile("generateData")
@Component
public class DataService {
    private final DataSource dataSource;
    private final UserRepository applicationUserRepository;

    public DataService(DataSource dataSource, UserRepository applicationUserRepository) {
        this.dataSource = dataSource;
        this.applicationUserRepository = applicationUserRepository;
    }

    @PostConstruct
    public void initData() {
        generateFirstUser();
        generateSecondUser();
        // schema init
        ClassPathResource initData = new ClassPathResource("script/small/ARTIST.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/LOCATION.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/EVENT.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/NEWS.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/PRICE_CATEGORY.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);


        initData = new ClassPathResource("script/small/HALL.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/AREA.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        initData = new ClassPathResource("script/small/PERFORMANCE.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);


        initData = new ClassPathResource("script/small/USER.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);


        initData = new ClassPathResource("script/small/TICKET.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        /**
        initData = new ClassPathResource("script/small/SEAT.sql");
        databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);**/

    }

    private void generateFirstUser() {
        String password = "12345678";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
           User user_allg = User.builder()
                .firstname("admin")
                .surname("user")
                .admin(true)
                .birthday(LocalDate.now())
                .deleted(0)
                .signInAttempts(0)
                .password(hashedPassword)
                .seenNews(null)
                .tickets(null).build();
            user_allg.setEmail(user_allg.getFirstname()+"@"+user_allg.getSurname());
            applicationUserRepository.saveAndFlush(user_allg);
    }

    private void generateSecondUser() {
        String password = "12345678";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        User user_allg = User.builder()
            .firstname("user")
            .surname("user")
            .admin(false)
            .birthday(LocalDate.now())
            .deleted(0)
            .signInAttempts(0)
            .password(hashedPassword)
            .seenNews(null)
            .tickets(null).build();
        user_allg.setEmail(user_allg.getFirstname()+"@"+user_allg.getSurname());
        applicationUserRepository.saveAndFlush(user_allg);
    }
}












