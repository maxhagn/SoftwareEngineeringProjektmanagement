package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.opentest4j.MultipleFailuresError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private EventRepository eventRepository;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService realDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        realDataService.initData();
    }

    /*=============================POSITIVE-TESTS=============================*/

    /*-----------------------------POST-----------------------------*/
    @Test
    public void givenValidNews_whenCreate_thenCreatedNewsMatchInput() {
        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        News news = newsRepository.save(data);
        assertAll(
            () -> assertNotNull(news),
            () -> assertEquals(data.getAuthor(), news.getAuthor()),
            () -> assertEquals(data.getDate(), news.getDate()),
            () -> assertEquals(data.getNewsCol(), news.getNewsCol()),
            () -> assertEquals(data.getSummary(), news.getSummary()),
            () -> assertEquals(data.getText(), news.getText()),
            () -> assertEquals(data.getTitle(), news.getTitle()),
            () -> assertEquals(data.getEvent(), news.getEvent())
        );
    }


    /*-----------------------------PUT-----------------------------*/

    @Test
    public void givenNewsAndUserExists_whenRead_thenOK() {
        List<User> all = userRepository.findAll();
        User user = null;
        if (all != null) {
            user = all.get(0);
        }

        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        News news = newsRepository.saveAndFlush(data);
        assertAll(
            () -> assertNotNull(news),
            () -> assertEquals(news.getAuthor(), data.getAuthor()),
            () -> assertEquals(news.getTitle(), data.getTitle()),
            () -> assertEquals(news.getText(), data.getText()),
            () -> assertEquals(news.getSummary(), data.getSummary()),
            () -> assertEquals(news.getReaders(), null)
        );

        news.addReader(user);
        News updatedNews = newsRepository.saveAndFlush(news);


        assertAll(
            () -> assertNotNull(updatedNews),
            () -> assertEquals(updatedNews.getId(), news.getId()),
            () -> assertEquals(updatedNews.getAuthor(), news.getAuthor()),
            () -> assertEquals(updatedNews.getTitle(), news.getTitle()),
            () -> assertEquals(updatedNews.getText(), news.getText()),
            () -> assertEquals(updatedNews.getSummary(), news.getSummary()),
            () -> assertNotEquals(updatedNews.getReaders(), null)
        );
    }

    /*-----------------------------GET-----------------------------*/
    @Test
    public void givenNewsExists_whenFindAll_then() {
        List<News> all = newsRepository.findAll();

        assertAll(
            () -> assertNotNull(all)
        );
    }


    @Test
    public void givenNewsExists_whenFindById_thenOK() {
        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        News news = newsRepository.saveAndFlush(data);
        News byId = newsRepository.findById(news.getId()).get();


        assertAll(
            () -> assertNotNull(byId),
            () -> assertEquals(byId.getAuthor(), data.getAuthor()),
            () -> assertEquals(byId.getTitle(), data.getTitle()),
            () -> assertEquals(byId.getText(), data.getText()),
            () -> assertEquals(byId.getSummary(), data.getSummary())
        );
    }

    @Test
    public void givenUnReadNews_whenFindUnReadNews_thenOK() {
        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        newsRepository.saveAndFlush(data);
        List<News> unreadNews = newsRepository.findUnreadNews(newsRepository.findReadNews(userProvider.getAdmin().getId()));

        assertAll(
            () -> assertNotNull(unreadNews),
            () -> assertEquals(unreadNews.size(), 11)
        );
    }

    @Test
    public void givenOnlyReadNews_whenFindUnReadNews_thenOK() {
        List<User> readers = new LinkedList<>();
        readers.add(userRepository.findAll().get(0));
        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        News news = newsRepository.saveAndFlush(data);
        List<News> readNews = new LinkedList<>();
        List<News> unreadNews = newsRepository.findUnreadNews(readNews);

        assertAll(
            () -> assertNotNull(unreadNews)
        );
    }

    @Test
    public void givenNoReadNews_whenFindReadNews_thenOK() {
        List<News> readNews = newsRepository.findReadNews(userProvider.getAdmin().getId());


        assertAll(
            () -> assertNotNull(readNews),
            () -> assertEquals(readNews.size(), 0)
        );
    }

    @Test
    public void givenReadNews_whenFindReadNews_thenOK() {
        List<User> readers = new LinkedList<>();
        readers.add(userRepository.findAll().get(0));
        News data = News.builder().author("hh").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
        News news = newsRepository.saveAndFlush(data);
        news.setReaders(readers);
        newsRepository.saveAndFlush(news);
        List<News> readNews = newsRepository.findReadNews(userRepository.findAll().get(0).getId());


        assertAll(
            () -> assertNotNull(readNews),
            () -> assertEquals(readNews.size(), 1)
        );
    }



    /*=============================NEGATIVE-TESTS=============================*/

    /*-----------------------------POST-----------------------------*/
    @Test
    public void givenInvalidNews_whenCreate_thenError() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            News data = News.builder().author(null).date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol(null)
                .summary(null).text(null).title(null).build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    @Test
    public void givenInvalidAuthor_whenCreate_thenError() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            News data = News.builder().author(null).date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol("the col")
                .summary("the summary").text("the text").title("the title").build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    @Test
    public void givenInvalidSummary_whenCreate_thenError() {
        Error error = assertThrows(MultipleFailuresError.class, () -> {
            News data = News.builder().author("the author").date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol("the col")
                .summary(null).text("the text").title("the title").build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    @Test
    public void givenInvalidTitle_whenCreate_thenError() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            News data = News.builder().author("the author").date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol("the col")
                .summary("the summary").text("the text").title(null).build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    @Test
    public void givenInvalidText_whenCreate_thenError() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            News data = News.builder().author("the author").date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol("the col")
                .summary("the summary").text(null).title("the title").build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    @Test
    public void givenInvalidDate_whenCreate_thenError() {
        Error error = assertThrows(MultipleFailuresError.class, () -> {
            News data = News.builder().author("the author").date(null)
                .event(eventRepository.findAll().get(0)).newsCol("the col")
                .summary("the summary").text("the text").title("the title").build();
            News news = newsRepository.save(data);
            assertAll(
                () -> assertNull(null),
                () -> assertNotEquals(data.getAuthor(), news.getAuthor()),
                () -> assertNotEquals(data.getDate(), news.getDate()),
                () -> assertNotEquals(data.getNewsCol(), news.getNewsCol()),
                () -> assertNotEquals(data.getSummary(), news.getSummary()),
                () -> assertNotEquals(data.getText(), news.getText()),
                () -> assertNotEquals(data.getTitle(), news.getTitle()),
                () -> assertNotEquals(data.getEvent(), news.getEvent())
            );
        });


    }

    /*-----------------------------PUT-----------------------------*/
    @Test
    public void givenNewsAndNoUser_whenRead_thenThrowValidationException() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().author("hh").date(LocalDate.now())
                .event(eventRepository.findAll().get(0)).newsCol("h")
                .summary("HHHH").text("HHHHHHHHHHHH").title("H").build();
            News news = newsRepository.saveAndFlush(data);
            assertAll(
                () -> assertNotNull(news),
                () -> assertEquals(news.getAuthor(), data.getAuthor()),
                () -> assertEquals(news.getTitle(), data.getTitle()),
                () -> assertEquals(news.getText(), data.getText()),
                () -> assertEquals(news.getSummary(), data.getSummary()),
                () -> assertEquals(news.getReaders(), null)
            );

            news.addReader(null);
            News updatedNews = newsRepository.saveAndFlush(news);
            assertAll(
                () -> assertNotNull(updatedNews),
                () -> assertEquals(updatedNews.getId(), news.getId()),
                () -> assertEquals(updatedNews.getAuthor(), news.getAuthor()),
                () -> assertEquals(updatedNews.getTitle(), news.getTitle()),
                () -> assertEquals(updatedNews.getText(), news.getText()),
                () -> assertEquals(updatedNews.getSummary(), news.getSummary()),
                () -> assertEquals(updatedNews.getReaders(), null)
            );
        });
    }





}
