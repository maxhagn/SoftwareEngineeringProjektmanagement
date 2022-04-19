package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.SING_IN_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SignInEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

     @Autowired
     private UserProvider userProvider;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();

        userRepository.save(userProvider.getAdmin());
        userRepository.save(userProvider.getNormal());
    }

    @Test
    public void givenUserWith5LoginAttempts_whenLoginAttempt_thenBlockAndError() throws Exception {
        UserLoginDto userData = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail(userProvider.getAdmin().getEmail()).withPassword(TestData.PLAIN_PASSWORD).build();
        String body = objectMapper.writeValueAsString(userData);
        MvcResult mvcResult = this.mockMvc.perform(post(SING_IN_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus())
        );
    }

    @Test
    public void givenUserWith3LoginAttempts_whenCorrectLoginAttempt_thenAttemptCounterReset() throws Exception {
        UserLoginDto userData = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail(userProvider.getNormal().getEmail()).withPassword(TestData.PLAIN_PASSWORD).build();
        String body = objectMapper.writeValueAsString(userData);
        MvcResult mvcResult = this.mockMvc.perform(post(SING_IN_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals((Integer) 0, userRepository.findUserByEmailIgnoreCase(userProvider.getNormal().getEmail()).getSignInAttempts())
        );
    }

    @Test
    public void givenUserNotExisting_whenLoginAttempt_thenBadCredentials() throws Exception {
        UserLoginDto userData = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto().withEmail(TestData.NOT_EXISTING_EMAIL).withPassword(TestData.PLAIN_PASSWORD).build();
        String body = objectMapper.writeValueAsString(userData);
        MvcResult mvcResult = this.mockMvc.perform(post(SING_IN_URI).contentType(MediaType.APPLICATION_JSON).content(body)).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus())
        );
    }

}
