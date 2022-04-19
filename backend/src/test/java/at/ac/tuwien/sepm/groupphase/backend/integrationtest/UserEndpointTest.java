package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.RegisterUserDto;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserListResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test")
@AutoConfigureMockMvc
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

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

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();

        User admin = userProvider.getAdmin();
        // Needed so that save is seen as insert and not as update
        admin.setId(null);
        userRepository.save(admin);
        User normal = userProvider.getNormal();
        // Needed so that save is seen as insert and not as update
        normal.setId(null);
        userRepository.save(normal);

        for (int i = 0; i < 25; i++) {
            User newUser = userProvider.getRandom(i);
            // Needed so that save is seen as insert and not as update
            newUser.setId(null);
            userRepository.save(newUser);
        }

    }


    RegisterUserDto newUser1 = new RegisterUserDto("new@mail.at", "12345678", "User1Fn", "User1Sn", false);
    RegisterUserDto newUser2EmailAlreadyExists = new RegisterUserDto("new@mail.at", "12345678910", "User2Fn", "User2Sn", false);
    RegisterUserDto newUser3IllegalEmail1 = new RegisterUserDto("@mail.at", "12345678", "User3Fn", "User3Sn", false);
    RegisterUserDto newUser4IllegalEmail2 = new RegisterUserDto("new.at", "12345678", "User4Fn", "User4Sn", false);
    RegisterUserDto newUser5IllegalEmail3 = new RegisterUserDto("new@", "12345678", "User5Fn", "User5Sn", false);
    RegisterUserDto newUser6IllegalPassword1 = new RegisterUserDto("new2@mail.at", "1234567", "User6Fn", "User6Sn", false);
    RegisterUserDto newUser7IllegalPassword2 = new RegisterUserDto("new3@mail.at", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "User6Fn", "User6Sn", false);
    RegisterUserDto newUser8BarelyLegalPassword = new RegisterUserDto("new4@mail.at", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "User6Fn", "User6Sn", false);


    @Test
    public void givenUserInDb_getSingleUser_success() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(UPDATE_URI+"/0").contentType(MediaType.APPLICATION_JSON).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    public void givenUserInDb_resetPwAsAdmin_success() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(UPDATE_URI+"/2000/password").contentType(MediaType.APPLICATION_JSON).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    public void givenUserInDb_resetPwAsNonAdmin_success() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(UPDATE_URI+"/2000/password").contentType(MediaType.APPLICATION_JSON).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("normal@email.com", Arrays.asList("ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }
    @Test
    public void givenUserInDb_onDelete_successfullDelete() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(DELETE_URI).contentType(MediaType.APPLICATION_JSON).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    public void givenUserInDb_onDeleteWrongAuthHeader_error() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(DELETE_URI).contentType(MediaType.APPLICATION_JSON).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("notan@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenUserInDb_onUpdateCorrect_successfullUpdate() throws Exception {
        RegisterUserDto updateDto = RegisterUserDto.builder().email("updated@email").password("12345678910").firstname("NewAdminFirstName").surname("NewAdminSurName").build();
        MvcResult mvcResult = this.mockMvc.perform(put(UPDATE_URI).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }

    @Test
    public void givenUserNotInDb_onUpdateCorrectWithNotExistingUser_error() throws Exception {
        RegisterUserDto updateDto = RegisterUserDto.builder().email("updated@email").build();
        MvcResult mvcResult = this.mockMvc.perform(put(UPDATE_URI).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("notan@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenUserInDb_onUpdateUsedEmail_error() throws Exception {
        RegisterUserDto updateDto = RegisterUserDto.builder().email("normal@email.com").password("updatedpassword").build();
        MvcResult mvcResult = this.mockMvc.perform(put(UPDATE_URI).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        RegisterUserDto updateDto2 = RegisterUserDto.builder().firstname("    ").build();
        MvcResult mvcResult2 = this.mockMvc.perform(put(UPDATE_URI).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto2)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();

        RegisterUserDto updateDto3 = RegisterUserDto.builder().surname("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        MvcResult mvcResult3 = this.mockMvc.perform(put(UPDATE_URI).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto3)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN", "ROLE_USER"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response3 = mvcResult3.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.valueOf(422).value(), response.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response2.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response3.getStatus())
        );
    }

    @Test
    public void givenNoUsersInDB_onCreate_succesfulCreate() throws Exception {
        String body = objectMapper.writeValueAsString(newUser1);
        MvcResult mvcResult = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser8BarelyLegalPassword);
        MvcResult mvcResult2 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body2)).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.CREATED.value(), response2.getStatus())

        );
    }

    @Test
    public void getGivenNoUsersInDB_onCreateSameEmailTwice_succesfulCreateOnFirstInvalidRequestOnSecond() throws Exception {
        String body = objectMapper.writeValueAsString(newUser1);
        MvcResult mvcResult = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser2EmailAlreadyExists);
        MvcResult mvcResult2 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body2)).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response2.getStatus())
        );
    }

    @Test
    public void givenNoUsersInD_onCreatewithInvalidFields_InvalidRequest() throws Exception {
        String body = objectMapper.writeValueAsString(newUser3IllegalEmail1);
        MvcResult mvcResult = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser4IllegalEmail2);
        MvcResult mvcResult2 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body2)).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        String body3 = objectMapper.writeValueAsString(newUser5IllegalEmail3);
        MvcResult mvcResult3 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body3)).andDo(print()).andReturn();
        MockHttpServletResponse response3 = mvcResult3.getResponse();
        String body4 = objectMapper.writeValueAsString(newUser6IllegalPassword1);
        MvcResult mvcResult4 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body4)).andDo(print()).andReturn();
        MockHttpServletResponse response4 = mvcResult4.getResponse();
        String body5 = objectMapper.writeValueAsString(newUser7IllegalPassword2);
        MvcResult mvcResult5 = this.mockMvc.perform(post(SIGN_UP_URI).contentType(MediaType.APPLICATION_JSON).content(body5)).andDo(print()).andReturn();
        MockHttpServletResponse response5 = mvcResult5.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(422).value(), response.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response2.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response3.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response4.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response5.getStatus())

        );
    }


    @Test
    public void givenCreateWithoutAdminRole_unAuthorized() throws Exception {
        String body = objectMapper.writeValueAsString(newUser1);
        MvcResult mvcResult = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser8BarelyLegalPassword);
        MvcResult mvcResult2 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body2)).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response2.getStatus())

        );
    }

    @Test
    public void givenNoUsersInDB_onUserPageCreate_succesfulCreate() throws Exception {
        String body = objectMapper.writeValueAsString(newUser1);
        MvcResult mvcResult = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser8BarelyLegalPassword);
        MvcResult mvcResult2 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body2).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.CREATED.value(), response2.getStatus())

        );
    }

    @Test
    public void getGivenNoUsersInDB_onUserPageCreateSameEmailTwice_succesfulCreateOnFirstInvalidRequestOnSecond() throws Exception {
        String body = objectMapper.writeValueAsString(newUser1);
        MvcResult mvcResult = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser2EmailAlreadyExists);
        MvcResult mvcResult2 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body2).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response2.getStatus())
        );
    }

    @Test
    public void givenNoUsersInD_onUserPageCreatewithInvalidFields_InvalidRequest() throws Exception {
        String body = objectMapper.writeValueAsString(newUser3IllegalEmail1);
        MvcResult mvcResult = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String body2 = objectMapper.writeValueAsString(newUser4IllegalEmail2);
        MvcResult mvcResult2 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body2).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        String body3 = objectMapper.writeValueAsString(newUser5IllegalEmail3);
        MvcResult mvcResult3 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body3).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response3 = mvcResult3.getResponse();
        String body4 = objectMapper.writeValueAsString(newUser6IllegalPassword1);
        MvcResult mvcResult4 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body4).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response4 = mvcResult4.getResponse();
        String body5 = objectMapper.writeValueAsString(newUser7IllegalPassword2);
        MvcResult mvcResult5 = this.mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content(body5).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response5 = mvcResult5.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(422).value(), response.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response2.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response3.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response4.getStatus()),
            () -> assertEquals(HttpStatus.valueOf(422).value(), response5.getStatus())
        );
    }

    @Test
    public void givenUserWith5LoginAttempts_whenUnlock_thenSignInAttempts0() throws Exception {
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(UNLOCK_URI, user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals((Integer) 0, userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenNotExistingUser_whenUnlock_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(UNLOCK_URI, TestData.NOT_EXISTING_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenNotAdmin_whenUnlock_then401() throws Exception {
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getNormal().getEmail());
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(UNLOCK_URI, user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getNormal().getEmail(), Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void givenUser_whenLock_thenSignInAttempts5() throws Exception {
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getNormal().getEmail());
        MvcResult mvcResult = this.mockMvc
            .perform(post(String.format(LOCK_URI, user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals((Integer) 5, userRepository.findUserByEmailIgnoreCase(userProvider.getNormal().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenNotExistingUser_whenLock_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(LOCK_URI, TestData.NOT_EXISTING_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenUser_whenLockSelf_then422() throws Exception {
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getAdmin().getEmail());
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(LOCK_URI, user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void givenNotAdmin_whenLock_then403() throws Exception {
        User user = userRepository.findUserByEmailIgnoreCase(userProvider.getNormal().getEmail());
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(String.format(LOCK_URI, user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getNormal().getEmail(), Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void givenNotAdmin_whenListAll_then401() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(LIST_USER_URI + "?page=0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getNormal().getEmail(), Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void given27Users_whenListAll_thenPage20EntriesHasNextTrueTotalPages2CurrPage0() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(LIST_USER_URI + "?page=0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        UserListResultDto resDto = objectMapper.readValue(response.getContentAsByteArray(), UserListResultDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(20, resDto.getContent().size()),
            () -> assertEquals(0, resDto.getCurrentPage()),
            () -> assertEquals(2, resDto.getTotalPages()),
            () -> assertTrue(resDto.isHasNext())
        );
    }

    @Test
    public void given27Users_whenListAll_thenLastEntryUser17Surname() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(LIST_USER_URI + "?page=0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        UserListResultDto resDto = objectMapper.readValue(response.getContentAsByteArray(), UserListResultDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals("User17", resDto.getContent().get(resDto.getContent().size() - 1).getSurname())
        );
    }

    @Test
    public void given27Users_whenListAll_thenPage7EntriesHasNextFalseTotalPages2CurrPage1() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(LIST_USER_URI + "?page=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        UserListResultDto resDto = objectMapper.readValue(response.getContentAsByteArray(), UserListResultDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(7, resDto.getContent().size()),
            () -> assertEquals(1, resDto.getCurrentPage()),
            () -> assertEquals(2, resDto.getTotalPages()),
            () -> assertFalse(resDto.isHasNext())
        );
    }

    @Test
    public void given27Users_whenListAll_thenPage0EntriesHasNextFalseTotalPages2CurrPage2() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(LIST_USER_URI + "?page=2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(userProvider.getAdmin().getEmail(), Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        UserListResultDto resDto = objectMapper.readValue(response.getContentAsByteArray(), UserListResultDto.class);


        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(0, resDto.getContent().size()),
            () -> assertEquals(2, resDto.getCurrentPage()),
            () -> assertEquals(2, resDto.getTotalPages()),
            () -> assertFalse(resDto.isHasNext())
        );
    }

}
