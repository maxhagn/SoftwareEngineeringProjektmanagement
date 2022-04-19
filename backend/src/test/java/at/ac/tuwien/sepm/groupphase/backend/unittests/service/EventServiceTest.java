package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,test,generateTestData")
public class EventServiceTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private HallRepository hallRepository;


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
    @DisplayName("Creating of valid event works")
    public void givenValidEvent_whenCreate_thenOK() {
        Performance performance = Performance.builder()
            .datetime(LocalDateTime.now())
            .hall(hallRepository.findById(203L).get())
            .min_price(10)
            .build();
        Set<Performance> performanceSet = new HashSet<>();
        performanceSet.add(performance);
        Event event = Event.builder()
            .title("A")
            .description("Test test.")
            .duration(120)
            .artist(artistRepository.findById(1L).get())
            .category(Category.ACTION)
            .location(locationRepository.findById(202L).get())
            .performances(performanceSet)
            .build();
        Event event1 = eventService.create(event);
        assertAll(
            () -> assertNotNull(event1),
            () -> assertEquals(event.getArtist(), event1.getArtist()),
            () -> assertEquals(event.getCategory(), event1.getCategory()),
            () -> assertEquals(event.getDescription(),event1.getDescription()),
            () -> assertEquals(event.getDuration(),event1.getDuration())
        );
    }
    @Test
    @DisplayName("Finding event by existing ID should return event id 4")
    public void findingEventIdById_existing_shouldReturnEventWithId() {
        eventRepository.findAll().forEach(System.out::println);
        Assertions.assertEquals(201, eventService.find(201).getId());
    }

    @Test
    @DisplayName("Finding event by existing ID should return event title Boom!")
    public void findingEventTitleById_existing_shouldReturnEventWithId() {

        Event event = Event.builder().id(2000L).category(Category.ACTION).title("Boom!").description("Action Movie 1").duration(120).location(null)
            .artist(null).build();
        event = eventRepository.saveAndFlush(event);

        Assertions.assertEquals(eventRepository.findById(event.getId()).get()
            .getTitle(), eventService.find(event.getId()).getTitle());
    }


}
