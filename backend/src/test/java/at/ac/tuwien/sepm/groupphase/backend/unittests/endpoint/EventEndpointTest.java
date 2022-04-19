package at.ac.tuwien.sepm.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.EventEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.HallEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.SimpleCreatePerformanceFromEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.UPDATE_URI;
import static org.junit.Assert.assertEquals;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateTestData,test")
@AutoConfigureMockMvc
public class EventEndpointTest {
    public static String BASE_URI = "/api/v1";
    public static String EVENTS_URI = BASE_URI + "/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventEndpoint eventEndpoint;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

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
    public void givenCorrectEventCreation_onCreate_success() throws Exception {
        hallRepository.findAll().forEach(System.out::println);
        SimpleCreatePerformanceFromEventDto performance = SimpleCreatePerformanceFromEventDto.builder()
            .hall(HallDto.builder().id(203L).name("Hall with Sections").build())
            .date(LocalDateTime.now())
            .min_price(10)
            .build();
        List<SimpleCreatePerformanceFromEventDto> perfList = new ArrayList<>();
        perfList.add(performance);
        SimpleHallDto hallDto = SimpleHallDto.builder().id(203L).name("Hall with Sections").build();
        Set<SimpleHallDto> hallDtoSet = new HashSet<>();
        hallDtoSet.add(hallDto);
        EventInputDto eventInputDto = EventInputDto.builder().title("Test").description("Test").category(Category.DOCUMENTATION).duration(1).artist(1)
            .location(LocationWithAreaCodeDto.builder().id(202L).name("Babenberger Passage").city("Wien").street("Burgring 3").area_code("1010").halls(hallDtoSet).build())
            .performances(perfList).build();

        MvcResult mvcResult = this.mockMvc.perform(post(EVENTS_URI + "/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(eventInputDto)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
    }

    @Test
    public void givenInCorrectEventCreation_onCreate_createFailed() throws Exception {
        SimpleCreatePerformanceFromEventDto performance = SimpleCreatePerformanceFromEventDto.builder()
            .hall(HallDto.builder().id(6L).name("Hall with Sections").build())
            .date(LocalDateTime.now())
            .min_price(10)
            .build();
        List<SimpleCreatePerformanceFromEventDto> perfList = new ArrayList<>();
        perfList.add(performance);
        SimpleHallDto hallDto = SimpleHallDto.builder().id(6L).name("Hall with Sections").build();
        Set<SimpleHallDto> hallDtoSet = new HashSet<>();
        hallDtoSet.add(hallDto);
        EventInputDto eventInputDto = EventInputDto.builder().title("           ").description("Test").category(Category.DOCUMENTATION).duration(1).artist(1)
            .location(LocationWithAreaCodeDto.builder().id(202L).name("Babenberger Passage").city("Wien").street("Burgring 3").area_code("1010").halls(hallDtoSet).build())
            .performances(perfList).build();

        MvcResult mvcResult = this.mockMvc.perform(post(EVENTS_URI + "/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(eventInputDto)).header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", Arrays.asList("ROLE_ADMIN"))
        )).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(422).value(), response.getStatus())
        );
    }

    @Test
    public void givenExistingEvent_findEventById_success() throws Exception {
        eventRepository.findAll().forEach(System.out::println);
        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_URI + "/201").header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Arrays.asList("ROLE_USER")))).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    public void givenExistingEvents_findAll_success() throws Exception {
        eventRepository.findAll().forEach(System.out::println);
        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_URI + "/list").header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Arrays.asList("ROLE_USER")))).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    public void givenNonExistingEvent_findEventById_notfound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_URI + "/404").header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Arrays.asList("ROLE_USER")))).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(404).value(), response.getStatus())
        );
    }

    @Test
    @DisplayName("creating invalid Event as admin")
    public void givenInvalidEvent_whencreatingEvent_shouldReturnStatusCreated() throws Exception {
        Integer i = Math.toIntExact(artistRepository.findAll().get(0).getId());
        Location location = locationRepository.findAll().get(0);
        Set<SimpleHallDto> simpleHallDtos = convertToSimpleHallDTo(location.getHalls());
        HallDto hall = null;
        for (SimpleHallDto h : simpleHallDtos) {
            hall = HallDto.builder()
                .name(h.getName())
                .id(h.getId())
                .build();
        }
        LocationWithAreaCodeDto locationWithAreaCodeDto = LocationWithAreaCodeDto.builder()
            .area_code(location.getArea_code())
            .city(location.getCity())
            .halls(simpleHallDtos)
            .street(location.getStreet())
            .name(location.getName())
            .id(location.getId())
            .build();
        SimpleCreatePerformanceFromEventDto build = SimpleCreatePerformanceFromEventDto.builder()
            .hall(hall)
            .date(LocalDateTime.now())
            .id(1L)
            .min_price(5L)
            .build();
        List<SimpleCreatePerformanceFromEventDto> dtos = new LinkedList<>();
        dtos.add(build);

        EventInputDto dto =
            EventInputDto.builder()
                .title("Test")
                .description("Valid Description")
                .category(Category.ACTION)
                .duration(120)
                .artist(i)
                .location(locationWithAreaCodeDto)
                .performances(dtos)
                .build();


        String body = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform
            (
                post(EVENTS_URI + '/')
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    @DisplayName("Get top ten events")
    public void givenTopTenEvents_whentop_thenOk() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
            get(EVENTS_URI + "/top/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Arrays.asList("ROLE_USER")))
                .param("category", "ACTION")
                .param("month", "2020-01-01")
        )
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    @DisplayName("Finding event by category a should return size of the page 2")
    public void filterEventIdByCategory_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(1, eventEndpoint.filter(null,null, null,null
            , "ACTION", 0, true,null, null).getTotalPages());
    }

    @Test
    @DisplayName("Finding event by title a should return size of the page 2")
    public void filterEventIdByTitle_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(1, eventEndpoint.filter("Boom!",null, null,null
            , null, 0, true,null, null).getTotalPages());
    }

    @Test
    @DisplayName("Finding event by description a should return size of the page 2")
    public void filterEventIdByDescription_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(1, eventEndpoint.filter(null, null, null,"Action Movie 1"
            , null, 0, true,null, null).getTotalPages());
    }

    @DisplayName("Finding event by duration a should return size of the page 2")
    @Test
    public void filterEventIdByDuration_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(1, eventEndpoint.filter(null, null, "120",null
            , null, 0, true,null, null).getTotalPages());
    }

    @DisplayName("Finding event by duration (empty string) a should return size of the page 2")
    @Test
    public void filterEventIdByDescriptionEmptyString_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(1, eventEndpoint.filter(null, null, null,""
            , null, 0, true,null, null).getTotalPages());
    }

    @DisplayName("Finding event with no-detail-search a should return size of the page 2")
    @Test
    public void filterLocationNonDetailed_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(0, eventEndpoint.filter(null, null, null,null
            , null, 0, false,null, null).getTotalPages());
    }



    /*---------------NEGATIVE TESTS----------*/
    @Test
    public void givenNoEvent_whencreatingEvent_shouldReturnBadRequest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
            post(EVENTS_URI + '/')
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    private Set<SimpleHallDto> convertToSimpleHallDTo(Set<Hall> halls) {
        Set<SimpleHallDto> dtos = new HashSet<>();
        for (Hall h : halls) {
            dtos.add(SimpleHallDto.builder()
                .id(h.getId())
                .name(h.getName())
                .build());
        }
        return dtos;
    }


}
