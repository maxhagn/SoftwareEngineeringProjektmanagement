package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class NewsServiceTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private NewsService newsService;

    @Autowired
    private EventRepository eventRepository;


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
    public void givenValidNews_whenCreate_thenOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        News news = newsService.save(data);
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
    public void givenExistingNewsAndUser_whenRead_thenOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        News news = newsRepository.saveAndFlush(data);

        assertAll(() -> assertNotNull(news),
            () -> assertEquals(data.getAuthor(), news.getAuthor()),
            () -> assertEquals(data.getDate(), news.getDate()),
            () -> assertEquals(data.getNewsCol(), news.getNewsCol()),
            () -> assertEquals(data.getSummary(), news.getSummary()),
            () -> assertEquals(data.getText(), news.getText()),
            () -> assertEquals(data.getTitle(), news.getTitle()),
            () -> assertEquals(data.getEvent(), news.getEvent()),
            () -> assertNull(news.getReaders())
        );
        User user = userRepository.saveAndFlush(userProvider.getAdmin());
        List<User> users = new LinkedList<>();
        users.add(user);
        News update = newsService.update(news.getId(), users);


        assertAll(() -> assertNotNull(update),
            () -> assertEquals(update.getAuthor(), news.getAuthor()),
            () -> assertEquals(update.getDate(), news.getDate()),
            () -> assertEquals(update.getNewsCol(), news.getNewsCol()),
            () -> assertEquals(update.getSummary(), news.getSummary()),
            () -> assertEquals(update.getText(), news.getText()),
            () -> assertEquals(update.getTitle(), news.getTitle()),
            () -> assertEquals(update.getEvent(), news.getEvent())
        );


    }

    /*-----------------------------GET-----------------------------*/

    @Test
    public void givenExistingNews_whenGet_thenOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        News news = newsRepository.saveAndFlush(data);
        News byId = newsService.findById(news.getId());
        assertAll(
            () -> assertNotNull(byId),
            () -> assertEquals(byId.getAuthor(), news.getAuthor()),
            () -> assertEquals(byId.getDate(), news.getDate()),
            () -> assertEquals(byId.getNewsCol(), news.getNewsCol()),
            () -> assertEquals(byId.getSummary(), news.getSummary()),
            () -> assertEquals(byId.getText(), news.getText()),
            () -> assertEquals(byId.getTitle(), news.getTitle()),
            () -> assertEquals(byId.getEvent(), news.getEvent())
        );
    }

    @Test
    public void givenExistingNews_whenGetReadNews_thenOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        User user1 = userRepository.saveAndFlush(userProvider.getAdmin());
        News news = newsRepository.saveAndFlush(data);
        List<User> users = new LinkedList<>();
        users.add(user1);
        News update = newsService.update(news.getId(), users);
        Pageable pageRequest = PageRequest.of(0, 4, Sort.by("date").descending());
        Page<News> readNewsWithPagination = newsService.getReadNewsWithPagination(user1.getEmail(), pageRequest);
        List<Long> newsList = new LinkedList<>();
        newsList.add(update.getId());

        List<Long> readIds = new LinkedList<>();
        for (News n : readNewsWithPagination.getContent()) {
            readIds.add(n.getId());
        }

        assertAll(
            () -> assertNotNull(readNewsWithPagination),
            () -> assertEquals(readIds, newsList)
        );
    }

    @Test
    public void givenExistingNews_whenGetUnreadNews_thenOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        News news = newsRepository.saveAndFlush(data);
        User user = userRepository.saveAndFlush(userProvider.getAdmin());
        List<News> unreadNews = newsService.getUnreadNews(user.getEmail());
        List<News> all = newsRepository.findAll();
        List<Long> newsList = new LinkedList<>();
        for (News n : all) {
            newsList.add(n.getId());
        }


        List<Long> unreadIds = new LinkedList<>();
        for (News n : unreadNews) {
            unreadIds.add(n.getId());
        }
        assertAll(
            () -> assertNotNull(unreadNews),
            () -> assertEquals(newsList, unreadIds)
        );
    }


    /*=============================NEGATIVE-TESTS=============================*/

    /*-----------------------------POST-----------------------------*/
    @Test
    public void givenInvalidNews_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author(null)
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol(null)
                .summary(null)
                .text(null)
                .title(null)
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }
    @Test
    public void givenInvalidEvent_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author(null)
                .date(LocalDate.now())
                .event(null)
                .newsCol(null)
                .summary(null)
                .text(null)
                .title(null)
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }

    @Test
    public void givenInvalidAuthor_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("news coll")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }

    @Test
    public void givenInvalidSummary_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("news coll")
                .summary("")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }


    @Test
    public void givenInvalidText_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("news coll")
                .summary("the summary")
                .text("")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }


    @Test
    public void givenInvalidTitle_whenCreate_thenNotOk() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("news coll")
                .summary("the summary")
                .text("the text")
                .title("")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsService.save(data);
        });
    }

    /*-----------------------------PUT-----------------------------*/
    @Test
    public void givenExistingNewsAndNoUser_whenRead_thenNotOk() {
        News data = News.builder().
            author("the author")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .newsCol("h")
            .summary("the summary")
            .text("the text")
            .title("the title")
            .readers(null)
            .newsImages(null)
            .build();
        News news = newsRepository.saveAndFlush(data);

        assertAll(() -> assertNotNull(news),
            () -> assertEquals(data.getAuthor(), news.getAuthor()),
            () -> assertEquals(data.getDate(), news.getDate()),
            () -> assertEquals(data.getNewsCol(), news.getNewsCol()),
            () -> assertEquals(data.getSummary(), news.getSummary()),
            () -> assertEquals(data.getText(), news.getText()),
            () -> assertEquals(data.getTitle(), news.getTitle()),
            () -> assertEquals(data.getEvent(), news.getEvent()),
            () -> assertNull(news.getReaders())
        );
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        List<User> users = new LinkedList<>();
        users.add(user);
        Exception exception = assertThrows(ValidationException.class, () -> {
            News update = newsService.update(news.getId(), users);
        });


    }

    @Test
    public void givenNoNewsAndNoUser_whenRead_thenNotOk() {


        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        List<User> users = new LinkedList<>();
        users.add(user);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News update = newsService.update(-1L, users);
        });


    }

    @Test
    public void givenNewsAndUserwithEmailNull_whenRead_thenNotOk() {

        List<User> users = new LinkedList<>();
        users.add(User.builder().firstname("a").surname("b").admin(false).birthday(LocalDate.now()).password("12345678").build());
        Exception exception = assertThrows(ValidationException.class, () -> {
            News update = newsService.update(newsRepository.findAll().get(0).getId(), users);
        });


    }
    @Test
    public void givenExistingNewsAndNoUserAndIdNull_whenRead_thenValidatorException() {

        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        List<User> users = new LinkedList<>();
        users.add(user);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News update = newsService.update(null, users);
        });


    }


    /*-----------------------------GET-----------------------------*/
    @Test
    public void givenNoNews_whenFindById_thenNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News news = newsService.findById(100L);
        });
    }

    @Test
    public void givenNewsWithNegativeId_whenFindById_thenNotFound() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News news = newsService.findById(-100L);
        });
    }


    @Test
    public void givenExistingNewsAndNoReader_whenGetReadNews_thenNot_Found() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);

            Pageable pageRequest = PageRequest.of(0, 4, Sort.by("date").descending());
            Page<News> readNewsWithPagination = newsService.getReadNewsWithPagination("a@B", pageRequest);
            List<Long> newsList = new LinkedList<>();
            newsList.add(news.getId());

            List<Long> readIds = new LinkedList<>();
            for (News n : readNewsWithPagination.getContent()) {
                readIds.add(n.getId());
            }

            assertAll(
                () -> assertNotNull(readNewsWithPagination),
                () -> assertEquals(readIds, newsList)
            );
        });
    }


    @Test
    public void givenEmailNull_whenGetReadNews_thenValidationException() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);

            Pageable pageRequest = PageRequest.of(0, 4, Sort.by("date").descending());
            Page<News> readNewsWithPagination = newsService.getReadNewsWithPagination(null, pageRequest);



        });
    }

    @Test
    public void givenEmailNull_whenGetUnReadNewsPage_thenValidationException() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);

            Pageable pageRequest = PageRequest.of(0, 4, Sort.by("date").descending());
            Page<News> readNewsWithPagination = newsService.getUnreadNewsWithPagination(null, pageRequest);



        });
    }
    @Test
    public void givenEmailNull_whenGetUnReadNews_thenValidationException() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);

            List<News> unreadNews = newsService.getUnreadNews(null);


        });
    }
    @Test
    public void givenInvalidEmail_whenGetUnReadNews_thenNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);

            List<News> unreadNews = newsService.getUnreadNews("lala@a");


        });
    }


    @Test
    public void givenExistingNewsAndNoReader_whenGetUnreadNewsWithPageination_thenNOT_FOUND() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            News data = News.builder().
                author("the author")
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(0))
                .newsCol("h")
                .summary("the summary")
                .text("the text")
                .title("the title")
                .readers(null)
                .newsImages(null)
                .build();
            News news = newsRepository.saveAndFlush(data);
            Pageable pageRequest = PageRequest.of(0, 4, Sort.by("date").descending());

            Page<News> unreadNewsWithPagination = newsService.getUnreadNewsWithPagination("a@n", pageRequest);
            List<Long> newsList = new LinkedList<>();
            newsList.add(news.getId());

            List<Long> unreadIds = new LinkedList<>();
            for (News n : unreadNewsWithPagination.getContent()) {
                unreadIds.add(n.getId());
            }
            assertAll(
                () -> assertNotNull(unreadNewsWithPagination),
                () -> assertEquals(newsList, unreadIds)
            );
        });
    }




}
