package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Collections;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.TICKET_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
@ActiveProfiles("deleteDb,generateTestData,test")
class TicketInvoiceEndpointTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private MockMvc mvc;

    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity()) // sets up Spring Security with MockMvc
            .build();
        auth = new UsernamePasswordAuthenticationToken("user1@user.com", "12345678");
    }

    @Test
    void whenGivenInvalidId_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mvc.perform(get(TICKET_URI + "-1/invoice")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@user.com", Collections.singletonList("ROLE_USER")))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(auth))
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    void whenGivenValidId_ShouldReturnPdf() throws Exception {
        MvcResult mvcResult = this.mvc.perform(get(TICKET_URI + "200/invoice")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@user.com", Collections.singletonList("ROLE_USER")))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(auth))
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getContentType())
        );
    }


}