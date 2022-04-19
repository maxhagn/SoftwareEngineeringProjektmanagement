package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.helper.TestData;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

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
    @DisplayName("Finding artist by existing ID should return artist id 1")
    public void findingArtistIdById_existing_shouldReturnArtistWithId() {
        artistRepository.findById(1L).ifPresent(artist -> Assertions.assertEquals(1L, artist.getId()));
    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist firstname Maximilian")
    public void findingArtistLastNameById_existing_shouldReturnArtistWithId() {
        artistRepository.findById(1L).ifPresent(artist -> Assertions.assertEquals("Herbert", artist.getFirstname()));

    }

    @Test
    @DisplayName("Finding artist by existing ID should return artist lastname Mustermann")
    public void findingArtistSurNameById_existing_shouldReturnArtistWithId() {
        artistRepository.findById(1L).ifPresent(artist -> Assertions.assertEquals("Grönemeyer", artist.getSurname()));
    }


    @Test
    @DisplayName("Finding artist by letter a should return total elements size equals 2")
    public void filterArtistIdByFirstName_existing_shouldReturnTotalElementsLength2() {
        Page<PreviewArtistDto> returnPage = artistRepository.filter("a", null, PageRequest.of(0,12));
        Assertions.assertEquals(42, returnPage.getTotalElements());
    }

    @Test
    @DisplayName("Finding artist by letter a should return a page with size 2")
    public void filterArtistIdByFirstName_existing_shouldReturnPageLength2() {
        Page<PreviewArtistDto> returnPage = artistRepository.filter("a", null, PageRequest.of(0,12));
        Assertions.assertEquals(12, returnPage.getContent().size());
    }
}
