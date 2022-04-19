package at.ac.tuwien.sepm.groupphase.backend.unittests.endpoint;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ArtistEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
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
public class ArtistEndpointTest {
    public static String BASE_URI = "/api/v1";
    public static String ARTIST_URI = BASE_URI + "/artists";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistEndpoint artistEndpoint;

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
    @DisplayName("Finding artist by existing ID should return artist id 1")
    public void findingArtistIdById_existing_shouldReturnArtistWithId() {
        Assertions.assertEquals(1L, artistEndpoint.find(1L).getId());
    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist firstname Maximilian")
    public void findingArtistLastNameById_existing_shouldReturnArtistWithId() {
        Assertions.assertEquals("Herbert", artistEndpoint.find(1L).getFirstname());
    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist lastname Mustermann")
    public void findingArtistSurNameById_existing_shouldReturnArtistWithId() {
        Assertions.assertEquals("Grönemeyer", artistEndpoint.find(1L).getSurname());
    }

    @Test
    @DisplayName("Finding artist by existing ID should return status ok")
    public void findingArtistIdById_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_URI + "/1")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Finding artist by letter a should return size of the page 29")
    public void filterArtistIdByFirstName_existing_shouldReturnPageLength29() {
        Assertions.assertEquals(29, artistEndpoint.filter("a", 0).getTotalElements());
    }

    @Test
    @DisplayName("Finding artist by first name should return status ok")
    public void filterArtistIdByFirstName_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_URI + "/?query=Max&page=0")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Finding artist by last name should return status ok")
    public void filterArtistIdByLastName_existing_shouldReturnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_URI + "/?query=Huber&page=0")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /*---------------NEGATIVE TESTS----------*/
    @Test
    @DisplayName("Finding artist by non-existing ID should throw NotFoundException")
    public void findingArtistById_nonExisting_shouldThrowResponseStatusException() {
        assertThrows(NotFoundException.class,
            () -> artistEndpoint.find(10000L));
    }

    @Test
    @DisplayName("Finding artist by negative ID should throw ValidationException")
    public void findingArtistById_nonValid_shouldThrowResponseStatusException() {
        assertThrows(ValidationException.class,
            () -> artistEndpoint.find(-2L));
    }

    @Test
    @DisplayName("Finding artist by non existing ID should throw status bad request")
    public void findingArtistById_existing_shouldReturnStatusNOTFOUND() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_URI + "/1000")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Finding artist by negative ID should throw status bad request")
    public void findingArtistById_nonValid_shouldReturnStatusBadRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_URI + "/-1")).andDo(print()).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

    }
}
