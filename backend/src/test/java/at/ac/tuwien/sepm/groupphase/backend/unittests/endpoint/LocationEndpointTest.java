package at.ac.tuwien.sepm.groupphase.backend.unittests.endpoint;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LocationEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateTestData,test")
@AutoConfigureMockMvc(addFilters = false)
public class LocationEndpointTest  {
    public static String BASE_URI = "/api/v1";
    public static String LOCATION_URI = BASE_URI + "/location";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationEndpoint locationEndpoint;


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
    @DisplayName("Finding location by existing ID should return location id 1")
    public void findingLocationIdById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals(1L, locationEndpoint.find(1L).getId());
    }

    @Test
    @DisplayName("Finding location by existing ID should return location name Super Location")
    public void findingLocationLastNameById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals("Pratersauna", locationEndpoint.find(1L).getName());
    }

    @Test
    @DisplayName("Finding location by existing ID should return location city Vienna")
    public void findingLocationSurNameById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals("Wien", locationEndpoint.find(1L).getCity());
    }

    @Test
    @DisplayName("Finding location by existing ID should return status ok")
    public void findingLocationIdById_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_URI + "/1")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    @Test
    @DisplayName("Finding location by letter a should return size of the page 2")
    public void filterLocationIdByFirstName_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(2, locationEndpoint.filter( new LocationSearchDto(null, "a", null, null, null, null, null, 0)).getTotalPages());
    }

    @Test
    @DisplayName("Finding location by first name should return status ok")
    public void filterLocationIdByFirstName_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_URI + "/?query=p&page=0")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Finding location by last name should return status ok")
    public void filterLocationIdByLastName_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_URI + "/?query=p&page=0")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /*---------------NEGATIVE TESTS----------*/
    @Test
    @DisplayName("Finding location by non-existing ID should throw NotFoundException")
    public void findingLocationById_nonExisting_shouldThrowResponseStatusException() {
        assertThrows(ResponseStatusException.class,
            () -> locationEndpoint.find(2222222L));
    }

    @Test
    @DisplayName("Finding location by negative ID should throw ValidationException")
    public void findingLocationById_nonValid_shouldThrowResponseStatusException() {
        assertThrows(ResponseStatusException.class,
            () -> locationEndpoint.find(-2L));
    }

    @Test
    @DisplayName("Finding location by non existing ID should throw status bad found")
    public void findingLocationById_existing_shouldReturnStatusNOTFOUND() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_URI + "/100000")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Finding location by negative ID should throw status bad request")
    public void findingLocationById_nonValid_shouldReturnStatusBadRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_URI + "/-1")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.valueOf(422).value(), response.getStatus());

    }
}
