package at.ac.tuwien.sepm.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.NewsEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsInputReadersDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news.NewsOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;
import java.util.*;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.NEWS_URI;
import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.UNLOCK_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
@AutoConfigureMockMvc

public class NewsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProvider userProvider;

    private MockMvc mvc;

    private UsernamePasswordAuthenticationToken auth;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService realDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        realDataService.initData();
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity()) // sets up Spring Security with MockMvc
            .build();
        auth = new UsernamePasswordAuthenticationToken("admin@user.com", "12345678");
    }


    /*=============================POSITIVE-TESTS=============================*/

    /*-----------------------------POST-----------------------------*/
    @Test
    public void givenValidNews_whenCreateAttempt_thenOK() throws Exception {
        Event event = eventRepository.findAll().get(0);
        NewsEventDto newsEventDto = NewsEventDto.builder()
            .category(event.getCategory())
            .description(event.getDescription())
            .duration(event.getDuration())
            .id(event.getId())
            .title(event.getTitle())
            .build();
        NewsInputDto newsData = NewsInputDto.builder().author("the author").date(LocalDate.now())
            .event(newsEventDto).newsCol("h")
            .summary("the title").text("the text").title("the title").build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void givenNoReadNews_whenGetRead_ShouldOK() throws Exception {
        MvcResult mvcResult = this.mvc.perform(get(NEWS_URI + "/read")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(auth))
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    void givenUnReadNews_whenGetUnRead_ShouldOK() throws Exception {
        MvcResult mvcResult = this.mvc.perform(get(NEWS_URI + "/unread")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(auth))
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    /*-----------------------------PUT-----------------------------*/
    @Test
    public void givenExistingNews_whenPutRead_thenOK() throws Exception {
        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();

        News news = newsRepository.saveAndFlush(newsData);
        User user = userRepository.findAll().get(0);
        List<User> users = new LinkedList<>();
        users.add(user);

        NewsInputReadersDto build = NewsInputReadersDto.builder()
            .readers(users)
            .id(news.getId()).build();

        String body = objectMapper.writeValueAsString(build);
        MvcResult mvcResult = this.mockMvc.perform
            (
                put(NEWS_URI + "/" + news.getId())
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    /*-----------------------------GET-----------------------------*/

    @Test
    public void givenExistingNews_whenGetAttempt_thenOK() throws Exception {
        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();
        News news = newsRepository.saveAndFlush(newsData);

        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + news.getId())
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                        , Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenExistingNewsAndReaders_whenGetReadNews_thenOK() throws Exception {
        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();

        News news = newsRepository.saveAndFlush(newsData);
        User user = userRepository.findAll().get(0);
        List<User> users = new LinkedList<>();
        users.add(user);

        NewsInputReadersDto build = NewsInputReadersDto.builder()
            .readers(users)
            .id(news.getId()).build();

        newsService.update(build.getId(), build.getReaders());

        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/read")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
    @Test
    public void givenExistingNewsAndReaders_whenGetUnReadNews_thenOK() throws Exception {
        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();

        News news = newsRepository.saveAndFlush(newsData);
        User user = userRepository.findAll().get(0);
        List<User> users = new LinkedList<>();
        users.add(user);

        NewsInputReadersDto build = NewsInputReadersDto.builder()
            .readers(users)
            .id(news.getId()).build();

        newsService.update(build.getId(), build.getReaders());

        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/list/unread")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenExistingNewsAndReaders_whenGetUnreadNews_thenOK() throws Exception {
        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();

        News news = newsRepository.saveAndFlush(newsData);
        User user = userRepository.findAll().get(0);
        List<User> users = new LinkedList<>();
        users.add(user);

        NewsInputReadersDto build = NewsInputReadersDto.builder()
            .readers(users)
            .id(news.getId()).build();
        newsService.update(build.getId(), build.getReaders());

        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/unread")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    /*=============================NEGATIVE-TESTS=============================*/
    /*-----------------------------POST-----------------------------*/
    @Test
    public void givenInvalidNews_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        NewsInputDto newsData = NewsInputDto.builder().author(null).date(null)
            .event(null).newsCol(null)
            .summary(null).text(null).title(null).build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

    }

    @Test
    public void givenInvalidAuthors_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        Event event = eventRepository.findAll().get(0);
         NewsEventDto newsEventDto = NewsEventDto.builder()
            .category(event.getCategory())
            .description(event.getDescription())
            .duration(event.getDuration())
            .id(event.getId())
            .title(event.getTitle())
            .build();
        NewsInputDto newsData = NewsInputDto.builder().author("").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary(null).text(null).title(null).build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        NewsInputDto newsData_1 = NewsInputDto.builder().author("hh").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary(null).text(null).title(null).build();
        String body_1 = objectMapper.writeValueAsString(newsData_1);
        MvcResult mvcResult_1 = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body_1)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response_1 = mvcResult_1.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response_1.getStatus())
        );

    }

    @Test
    public void givenInvalidTitles_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        Optional<Event> byId = eventRepository.findById(2001L);
        if (byId.isPresent()) {
            Event e = byId.get();
            NewsEventDto newsEventDto = NewsEventDto.builder()
                .category(e.getCategory())
                .description(e.getDescription())
                .duration(e.getDuration())
                .id(e.getId())
                .title(e.getTitle())
                .build();
            NewsInputDto newsData = NewsInputDto.builder().author("the author").date(LocalDate.now())
                .event(newsEventDto).newsCol(null)
                .summary(null).text(null).title("").build();
            String body = objectMapper.writeValueAsString(newsData);
            MvcResult mvcResult = this.mockMvc.perform
                (
                    post(NEWS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


                )
                .andDo(print()).andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();

            NewsInputDto newsData_1 = NewsInputDto.builder().author("the author").date(LocalDate.now())
                .event(newsEventDto).newsCol(null)
                .summary(null).text(null).title("hh").build();
            String body_1 = objectMapper.writeValueAsString(newsData_1);
            MvcResult mvcResult_1 = this.mockMvc.perform
                (
                    post(NEWS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body_1)
                        .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


                )
                .andDo(print()).andReturn();
            MockHttpServletResponse response_1 = mvcResult_1.getResponse();
            assertAll(
                () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
                () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response_1.getStatus())
            );

        }

    }

    @Test
    public void givenInvalidText_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        Event event = eventRepository.findAll().get(0);
         NewsEventDto newsEventDto = NewsEventDto.builder()
            .category(event.getCategory())
            .description(event.getDescription())
            .duration(event.getDuration())
            .id(event.getId())
            .title(event.getTitle())
            .build();
        NewsInputDto newsData = NewsInputDto.builder().author("the author").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary(null).text("").title("the title").build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        NewsInputDto newsData_1 = NewsInputDto.builder().author("the author").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary(null).text("hh").title("the title").build();
        String body_1 = objectMapper.writeValueAsString(newsData_1);
        MvcResult mvcResult_1 =  this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body_1)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response_1 = mvcResult_1.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response_1.getStatus())
        );

    }

    @Test
    public void givenInvalidSummary_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        Event event = eventRepository.findAll().get(0);
         NewsEventDto newsEventDto = NewsEventDto.builder()
            .category(event.getCategory())
            .description(event.getDescription())
            .duration(event.getDuration())
            .id(event.getId())
            .title(event.getTitle())
            .build();
        NewsInputDto newsData = NewsInputDto.builder().author("the author").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary("").text("the text").title("the title").build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        NewsInputDto newsData_1 = NewsInputDto.builder().author("the author").date(LocalDate.now())
            .event(newsEventDto).newsCol(null)
            .summary("h").text("the text").title("the title").build();
        String body_1 = objectMapper.writeValueAsString(newsData_1);
        MvcResult mvcResult_1 = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body_1)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response_1 = mvcResult_1.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response_1.getStatus())
        );

    }

    @Test
    public void givenInvalidDate_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        Event event = eventRepository.findAll().get(0);
         NewsEventDto newsEventDto = NewsEventDto.builder()
            .category(event.getCategory())
            .description(event.getDescription())
            .duration(event.getDuration())
            .id(event.getId())
            .title(event.getTitle())
            .build();
        NewsInputDto newsData = NewsInputDto.builder().author("the author").date(null)
            .event(newsEventDto).newsCol(null)
            .summary("the summary").text("the text").title("the title").build();
        String body = objectMapper.writeValueAsString(newsData);
        MvcResult mvcResult = this.mockMvc.perform
            (
                post(NEWS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))


            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

    }

    @Test
    public void givenNewsAndInvalidUser_whenGetAttempt_thenNotFound() throws Exception {
        newsRepository.deleteAll();
        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + 1)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin1@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*-----------------------------PUT-----------------------------*/

    /*-----------------------------GET-----------------------------*/


    @Test
    public void givenWrongCurrentUser_whenGetUnreadNews_thenOK() throws Exception {

        News newsData = News.builder().author("the author").date(LocalDate.now())
            .event(eventRepository.findAll().get(0)).newsCol("h")
            .summary("the title").text("the text").title("the title").build();

        News news = newsRepository.saveAndFlush(newsData);
        User user = userRepository.findAll().get(0);
        List<User> users = new LinkedList<>();
        users.add(user);

        NewsInputReadersDto build = NewsInputReadersDto.builder()
            .readers(users)
            .id(news.getId()).build();
        newsService.update(build.getId(), build.getReaders());

        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/unread")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    @Test
    void givenWrongUserUnReadNews_whenGetRead_thenNOT_FOUND() throws Exception {
        MvcResult mvcResult = this.mvc.perform(get(NEWS_URI + "/unread")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin1@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(auth))
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }


}
