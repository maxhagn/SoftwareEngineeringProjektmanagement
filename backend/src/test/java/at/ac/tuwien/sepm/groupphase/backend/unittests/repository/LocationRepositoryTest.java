package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

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
    /*---------------POSITIVE TESTS----------*/
    @Test
    @DisplayName("Finding location by existing ID should return location id 1")
    public void findingLocationIdById_existing_shouldReturnLocationWithId() {
        locationRepository.findById(1L).ifPresent(location -> Assertions.assertEquals(1L, location.getId()));
    }

    @Test
    @DisplayName("Finding location by existing ID should return location name Super Location")
    public void findingLocationNameById_existing_shouldReturnLocationWithId() {
        locationRepository.findById(1L).ifPresent(location -> Assertions.assertEquals("Pratersauna", location.getName()));

    }
    @Test
    @DisplayName("Finding location by existing ID should return location street Superstreet 123/4")
    public void findingLocationStreetById_existing_shouldReturnLocationWithId() {
        locationRepository.findById(1L).ifPresent(location -> Assertions.assertEquals("Waldsteingartenstraße 135", location.getStreet()));
    }

    @Test
    @DisplayName("Finding location by existing ID should return location city Vienna")
    public void findingLocationCityById_existing_shouldReturnLocationWithId() {
        locationRepository.findById(1L).ifPresent(location -> Assertions.assertEquals("Wien", location.getCity()));
    }

    @Test
    @DisplayName("Finding location by existing ID should return location area_code 1100")
    public void findingLocationAreaCodeById_existing_shouldReturnLocationWithId() {
        locationRepository.findById(1L).ifPresent(location -> Assertions.assertEquals("1020", location.getArea_code()));
    }

    @Test
    @DisplayName("Finding location by letter v should return total elements size equals 2")
    public void filterLocationIdByFirstName_existing_shouldReturnTotalElementsLength2() {
        Page<LocationDto> returnPage = locationRepository.mixedFilter("v", PageRequest.of(0,12));
        Assertions.assertEquals(4, returnPage.getTotalElements());
    }

    @Test
    @DisplayName("Finding location by letter v should return a page content with size 2")
    public void filterLocationIdByFirstName_existing_shouldReturnPageLength1() {
        Page<LocationDto> returnPage = locationRepository.mixedFilter("v", PageRequest.of(0,12));
        Assertions.assertEquals(4, returnPage.getContent().size());
    }
}
