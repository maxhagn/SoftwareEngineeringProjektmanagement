package at.ac.tuwien.sepm.groupphase.backend.integrationtest.news;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.NEWS_URI;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
@AutoConfigureMockMvc

public class News_NewsImageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private NewsRepository newsRepository;

    private final String path = "src/test/java/at/ac/tuwien/sepm/groupphase/backend/integrationtest/news/robot.jpg";

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService realDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        realDataService.initData();
    }


    /*---------------POSITIVE TESTS----------*/
    @Test
    public void givenValidFileNameOfNewsImage_whenCreateAttempt_thenOK() throws Exception {
        //given:

        File file = new File(path);
        MockMultipartFile upload = new MockMultipartFile("file", "robot.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            Files.readAllBytes(file.toPath()));
        News news = newsRepository.findAll().get(0);
        //when
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.fileUpload(NEWS_URI+ "/" + news.getId() + "/upload")
                .file(upload)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
            , Arrays.asList("ROLE_ADMIN", "ROLE_USER")));

        //then:ok

        MvcResult mvcResult = mockMvc.perform(builder).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());



    }

    @Test
    public void givenNewsImageForNews_whenGetAttempt_thenOK() throws Exception {
        //given: newsImage exists
        //when
        News news = newsRepository.findAll().get(0);
        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + news.getId() +"/pic")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                        , Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //then: ok
        assertEquals(HttpStatus.OK.value(), response.getStatus());



    }
    @Test
    public void givenNewsImageForNews_whenGetAllAttempt_thenOK() throws Exception {
        //given: newsImage exists
        //when
        News news = newsRepository.findAll().get(0);
        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + news.getId() +"/pics")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                        , Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //then: ok
        assertEquals(HttpStatus.OK.value(), response.getStatus());



    }

    /*---------------NEGATIVE TESTS----------*/
    @Test
   public void givenInvalidFileNameOfNewsImage_whenCreateAttempt_thenBAD_REQUEST() throws Exception {
        //given:

        File file = new File(path);
        MockMultipartFile upload = new MockMultipartFile("robot", "robot.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            Files.readAllBytes(file.toPath()));
        News news = newsRepository.findAll().get(0);
        //when
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.fileUpload(NEWS_URI+ "/" + news.getId() + "/upload")
                .file(upload)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                    , Arrays.asList("ROLE_ADMIN", "ROLE_USER")));

        //then: bad request

        MvcResult mvcResult = mockMvc.perform(builder).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());



    }

    @Test
    public void givenIdOfNull_whenCreatePictureAttempt_thenBAD_REQUEST() throws Exception {
        //given:

        File file = new File(path);
        MockMultipartFile upload = new MockMultipartFile("robot", "robot.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            Files.readAllBytes(file.toPath()));
        //when
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.fileUpload(NEWS_URI+ "/" + null + "/upload")
                .file(upload)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                    , Arrays.asList("ROLE_ADMIN", "ROLE_USER")));

        //then: bad request

        MvcResult mvcResult = mockMvc.perform(builder).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());



    }

    @Test
    public void givenNegativeIdOfNewsEntry_whenCreatePictureAttempt_thenBAD_REQUEST() throws Exception {
        //given:

        File file = new File(path);
        MockMultipartFile upload = new MockMultipartFile("robot", "robot.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            Files.readAllBytes(file.toPath()));
        //when
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.fileUpload(NEWS_URI+ "/" + -1 + "/upload")
                .file(upload)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                    , Arrays.asList("ROLE_ADMIN", "ROLE_USER")));

        //then: bad request

        MvcResult mvcResult = mockMvc.perform(builder).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());



    }

    @Test
    public void givenNoNewsNews_whenGetAttempt_thenBadRequest() throws Exception {
        //given: newsImage exists
        //when
        News news = newsRepository.findAll().get(0);
        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + -1 +"/pic")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                        , Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //then: ok
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());



    }

    @Test
    public void givenNoNews_whenGetAllAttempt_thenBAD_REQUEST() throws Exception {
        //given: newsImage exists
        //when
        News news = newsRepository.findAll().get(0);
        MvcResult mvcResult = this.mockMvc.perform
            (
                get(NEWS_URI + "/" + -1 +"/pics")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail()
                        , Arrays.asList("ROLE_ADMIN", "ROLE_USER")))

            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        //then: ok
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());



    }




}
