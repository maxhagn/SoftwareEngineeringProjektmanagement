package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.SelfLockException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.helper.MockUser;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class ArtistServiceTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistService artistService;

    @BeforeClass
    public void before() {
        artistRepository.deleteAll();
        eventRepository.deleteAll();
    }

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
        Assertions.assertEquals(1L, artistService.getArtistWithEvent(1L).getId());
    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist firstname Maximilian")
    public void findingArtistLastNameById_existing_shouldReturnArtistWithId() {
        Assertions.assertEquals(artistRepository.findById(1L).get()
            .getFirstname(), artistService.getArtistWithEvent(1L).getFirstname());
    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist lastname Mustermann")
    public void findingArtistSurNameById_existing_shouldReturnArtistWithId() {
        Assertions.assertEquals(artistRepository.findById(1L).get().getSurname(), artistService.getArtistWithEvent(1L).getSurname());
    }

    @Test
    @DisplayName("Finding artist by letter a should return size of total elements equals 2")
    public void filterArtistIdByFirstName_existing_shouldReturnPageLength2() {
        Assertions.assertEquals(42, artistService
            .filter(
                Artist.builder()
                      .firstname("a")
                      .build()
            , 0)
            .getTotalElements());
    }

    /*---------------NEGATIVE TESTS----------*/
    @Test
    @DisplayName("Finding artist by non-existing ID should throw NotFoundException")
    public void findingArtistById_nonExisting_shouldThrowResponseStatusException() {
        assertThrows(NotFoundException.class,
            () -> artistService.getArtistWithEvent(10000L));
    }

    @Test
    @DisplayName("Finding artist by negative ID should throw ValidationException")
    public void findingArtistById_nonValid_shouldThrowValidationException() {
        assertThrows(ValidationException.class,
            () -> artistService.getArtistWithEvent(-2L));
    }

}
