package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateTestData,test")
public class LocationServiceTest {


    @Autowired
    private LocationService locationService;

    /*---------------POSITIVE TESTS----------*/
    @Test
    @DisplayName("Finding location by existing ID should return location id 1")
    public void findingLocationIdById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals(1L, locationService.getLocationWithEvent(1L).getId());
    }

    @Test
    @DisplayName("Finding location by existing ID should return location name Super Location")
    public void findingLocationLastNameById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals("Pratersauna", locationService.getLocationWithEvent(1L).getName());
    }

    @Test
    @DisplayName("Finding location by existing ID should return location city Vienna")
    public void findingLocationSurNameById_existing_shouldReturnLocationWithId() {
        Assertions.assertEquals("Wien", locationService.getLocationWithEvent(1L).getCity());
    }


    @Test
    @DisplayName("Finding location by letter a should return 2 pages")
    public void filterLocationIdByFirstName_existing_shouldReturn2Pages() {
        Assertions.assertEquals(2, locationService.filter
            (
                new LocationSearchDto(null, "a", null, null, null, null, null, 0)
            )
            .getTotalPages()
        );
    }

    @Test
    @DisplayName("Finding location by city vienna should return total elements of 2")
    public void filterLocationIdByCity_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(2, locationService.filter
            (
                new LocationSearchDto(null, null, null, "Vienna", null, null, null, 0)
            )
            .getTotalElements());
    }


    /*---------------NEGATIVE TESTS----------*/
    @Test
    @DisplayName("Finding location by non-existing ID should throw NotFoundException")
    public void findingLocationById_nonExisting_shouldThrowResponseStatusException() {
        assertThrows(NotFoundException.class,
            () -> locationService.getLocationWithEvent(10000L));
    }

    @Test
    @DisplayName("Finding location by negative ID should throw ValidationException")
    public void findingLocationById_nonValid_shouldThrowResponseStatusException() {
        assertThrows(ValidationException.class,
            () -> locationService.getLocationWithEvent(-2L));
    }
}
