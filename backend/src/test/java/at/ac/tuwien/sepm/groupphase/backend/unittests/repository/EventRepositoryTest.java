package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
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
public class EventRepositoryTest {

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
    @DisplayName("Finding event by existing ID should return event id 1")
    public void findingEventIdById_existing_shouldReturnEventWithId() {
        eventRepository.findById(1L).ifPresent(event -> Assertions.assertEquals(1L, event.getId()));
    }

    @Test
    @DisplayName("Finding event by existing ID should return event title Boom")
    public void findingEventById_existing_shouldReturnEventWithId() {
        eventRepository.findById(4L).ifPresent(event -> Assertions.assertEquals("Boom!", event.getTitle()));

    }

    @Test
    @DisplayName("Finding artist by existing ID should return event description Action Movie 1 ")
    public void findingEventDescById_existing_shouldReturnEventWithId() {
        eventRepository.findById(4L).ifPresent(event -> Assertions.assertEquals("Action Movie 1", event.getDescription()));
    }


    @Test
    @DisplayName("Finding event by letter a should return total elements size equals 2")
    public void filterEventIdByTitleName_existing_shouldReturnTotalElementsLength2() {
        Page<Event> returnPage = eventRepository.filter("B", PageRequest.of(0,12));
        Assertions.assertEquals(2, returnPage.getTotalElements());
    }



}
