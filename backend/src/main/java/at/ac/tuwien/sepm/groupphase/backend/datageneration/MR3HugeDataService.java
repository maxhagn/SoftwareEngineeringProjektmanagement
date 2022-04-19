package at.ac.tuwien.sepm.groupphase.backend.datageneration;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.utils.PictureHandler;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Profile("generateHugeMR3Data")
@Component
public class MR3HugeDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository applicationUserRepository;
    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;
    private final PriceCategoryRepository priceCategoryRepository;
    private final HallRepository hallRepository;
    private final AreaRepository areaRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final NewsRepository newsRepository;
    private final NewsImageRepository imageRepository;
    private Faker faker;
    private User adminUser;

    public MR3HugeDataService(UserRepository applicationUserRepository, EventRepository eventRepository, ArtistRepository artistRepository, LocationRepository locationRepository, PriceCategoryRepository priceCategoryRepository, HallRepository hallRepository, AreaRepository areaRepository, SeatRepository seatRepository, TicketRepository ticketRepository, PerformanceRepository performanceRepository, NewsRepository newsRepository, NewsImageRepository imageRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
        this.priceCategoryRepository = priceCategoryRepository;
        this.hallRepository = hallRepository;
        this.areaRepository = areaRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.newsRepository = newsRepository;
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void initData() {
        generateAdminUser();
        generateNormalUser();
        generateRandomUsers();
        generateCoherentData();
        generateTicketsForTopTenEvent();
        generateNews();
    }

    private void generateRandomUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String password = "12345678";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            User user_allg = User.builder()
                .firstname("random")
                .surname("user" + i)
                .admin(false)
                .birthday(LocalDate.now())
                .deleted(0)
                .signInAttempts(0)
                .password(hashedPassword)
                .build();
            user_allg.setEmail(user_allg.getFirstname() + "@" + user_allg.getSurname() + ".at");
            users.add(user_allg);
        }
        LOGGER.info("Users generated");
        applicationUserRepository.saveAll(users);
        applicationUserRepository.flush();
        LOGGER.info("Users now in DB");
    }


    private void generateTicketsForTopTenEvent() {

        List<Event> all = eventRepository.findAll();
        int size = 0;
        if (all.size()> 25){
            size = 40;
        }else {
            size = 25;
        }
        for (int i = 0; i < size; i++) {
            Random rand = new Random();
            Event event = all.get(rand.nextInt(all.size()));
            if (event != null){
                if (i % 10 == 0){
                    LOGGER.info("Top ten run " + i/10);
                }
                for (Performance p : event.getPerformances()) {
                    generateRandomTicketsForTopTen(p);


                }
            }

        }
        LOGGER.info("Top ten events in DB");
    }



    private LocalDateTime generateRandomDateTime(int startYear, int endYear) {
        int day = createRandomIntBetween(1, 28);
        int month = createRandomIntBetween(1, 12);
        int year = createRandomIntBetween(startYear, endYear);
        if (year == 2019){
            month = createRandomIntBetween(6, 12);
        }
        LocalDate date = LocalDate.of(year, month, day);
        LocalDateTime localDateTime = date.atStartOfDay();
        return localDateTime;
    }

    public static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }



    private void generateRandomTicketsForTopTen(Performance p) {

        Random random = new Random();
        int randomSize = random.nextInt(10)+5;
        LocalDateTime randomDate = generateRandomDateTime(2019, 2020);

        for (int i = 0; i < randomSize; i++) {
            Ticket ticket =
                Ticket.builder().performance(p).status(Status.BOUGHT).user(getRandomUser()).createDate(randomDate).build();
            ticketRepository.saveAndFlush(ticket);

        }



    }

    private User getRandomUser() {
        Random rand = new Random();
        return applicationUserRepository.findAll().get(rand.nextInt(applicationUserRepository.findAll().size()));

    }

    private void generateCoherentData() {
        for (int i = 0; i < 10; i++) {
            LOGGER.info("Coherent Data run " + i);
            List<Artist> artists = new ArrayList<>();
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Herbert").surname("Grönemeyer" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Xavier").surname("Naidoo" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Ludwig").surname("van Beethoven" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Richard").surname("Strauss" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Johann Sebastian").surname("Bach" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Johannes").surname("Brahms" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Tim").surname("Benzko" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Lena").surname("Meyer-Landrut" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Max").surname("Giesinger" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Paul Hartmut").surname("Würdig" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Johann").surname("Hölzel" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Udo").surname("Jürgens" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Christina").surname("Stürmer" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Reinhard").surname("Fendrich" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("RAF").surname("Camora" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Andy").surname("Borg" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Wolfgang").surname("Ambros" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Helene").surname("Fischer" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Peter").surname("Cornelius" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Dieter").surname("Bohlen" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Andreas").surname("Gabalier" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Peter").surname("Alexander" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Freddy").surname("Quinn" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("John").surname("Lennon" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Paul").surname("McCartney" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Ringo").surname("Star" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("George").surname("Harrison" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Anthony").surname("Kiedies" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Kurt").surname("Cobain" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Amy").surname("Winehouse" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Brian").surname("Jones" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Jimi").surname("Hendrix" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Janis").surname("Joplin" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Jim").surname("Morrison" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Robert").surname("Johnson" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Andreas").surname("Bourani" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Sarah").surname("Connor" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Peter").surname("Maffay" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Udo").surname("Lindenberg" + i).build()));
            artists.add(artistRepository.saveAndFlush(Artist.builder().firstname("Peter").surname("Fox" + i).build()));

            List<Location> locations = new ArrayList<>();
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Pratersauna" + i).street("Waldsteingartenstraße 135").city("Wien").area_code("1020").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("VIEiPEE" + i).street("Csardastraße 135").city("Wien").area_code("1020").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Grelle Forelle" + i).street("Spittelauer Lände 12").city("Wien").area_code("1090").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("The Loft" + i).street(" Lerchenfelder Gürtel 37").city("Wien").area_code("1160").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Fluc & Fluc Wanne" + i).street("Praterstern 5").city("Wien").area_code("1020").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Flex" + i).street("Augartenbrücke 1").city("Wien").area_code("1010").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("U1 Underground Club" + i).street("Favoritenstraße 164").city("Wien").area_code("1100").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("DonauTechno" + i).street(" Karl-Schweighofer-Gasse 10").city("Wien").area_code("1070").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Camera" + i).street("Neubaugasse 2").city("Wien").area_code("1070").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Sass Music Club" + i).street("Karlsplatz 1").city("Wien").area_code("1010").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Dual Club" + i).street("Burggasse 70").city("Wien").area_code("1070").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Opera Club" + i).street("Mahlerstraße 11").city("Wien").area_code("1010").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Club U" + i).street("Karlsplatz").city("Wien").area_code("1010").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Das Werk" + i).street("Spittelauer Lände 12").city("Wien").area_code("1090").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("P1" + i).street("Prinzregentenstraße 1").city("München").area_code("80538").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Pa2" + i).street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build()));
            locations.add(locationRepository.saveAndFlush(Location.builder().name("Pa3" + i).street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build()));

            locations.forEach(location -> {
                //generate two halls at each location
                Hall h1 = Hall.builder().name("Hall with Sections at " + location.getName()).cols(10).rows(10).location(location).build();
                Hall h2 = Hall.builder().name("Hall with Empty Space at " + location.getName()).cols(10).rows(10).location(location).build();
                h1 = hallRepository.saveAndFlush(h1);
                h2 = hallRepository.saveAndFlush(h2);

                //generate two price categories at each location
                PriceCategory p1 = PriceCategory.builder().name("Category A").price(10.0f).location(location).build();
                PriceCategory p2 = PriceCategory.builder().name("Category B").price(20.0f).location(location).build();
                p1 = priceCategoryRepository.saveAndFlush(p1);
                p2 = priceCategoryRepository.saveAndFlush(p2);

                //generate the areas at the two halls
                List<Area> areas = new LinkedList<>();
                areas.add(Area.builder().startCol(1).endCol(10).startRow(1).endRow(1).type(Type.SCREEN).name("Leinwand").hall(h1).build());
                areas.add(Area.builder().startCol(1).endCol(10).startRow(2).endRow(5).type(Type.SECTION).name("Section A").priceCategory(p1).hall(h1).build());
                areas.add(Area.builder().startCol(1).endCol(10).startRow(6).endRow(10).type(Type.SECTION).name("Section B").priceCategory(p2).hall(h1).build());

                areas.add(Area.builder().startCol(1).endCol(10).startRow(1).endRow(1).type(Type.SCREEN).name("Bühne").hall(h2).build());
                areas.add(Area.builder().startCol(1).endCol(10).startRow(2).endRow(5).type(Type.SEATS).name("Section A").priceCategory(p1).hall(h2).build());
                areas.add(Area.builder().startCol(1).endCol(10).startRow(7).endRow(10).type(Type.SEATS).name("Section B").priceCategory(p2).hall(h2).build());
                areaRepository.saveAll(areas);

                //Generate an event with two performances for each location (1 location has 1 event, each event has 2 performances - 1 for each hall)
                Random rand = new Random();
                Artist randomArtist = artists.get(rand.nextInt(artists.size()));
                Category category;
                int catDecider = rand.nextInt(3);
                if (catDecider == 0) {
                    category = Category.ACTION;
                } else if (catDecider == 1) {
                    category = Category.COMEDY;
                } else {
                    category = Category.DOCUMENTATION;
                }
                rand = new Random();
                Event event = Event.builder().title("Meet and greet with " + randomArtist.getFirstname() + " " + randomArtist.getSurname()).category(category).description("A meet and greet with " + randomArtist.getFirstname() + " " + randomArtist.getSurname()).duration(50 + rand.nextInt(120)).location(location).artist(randomArtist).build();

                rand = new Random();
                int min_price = rand.nextInt(15);
                rand = new Random();
                Performance perf1 = Performance.builder().event(event).hall(h1).datetime(LocalDateTime.now().plusDays(rand.nextInt(100))).min_price(min_price).build();
                rand = new Random();
                Performance perf2 = Performance.builder().event(event).hall(h2).datetime(LocalDateTime.now().plusDays(rand.nextInt(100))).min_price(min_price).build();

                Set<Performance> performances = new HashSet<>();
                event = eventRepository.saveAndFlush(event);
                performances.add(performanceRepository.saveAndFlush(perf1));
                performances.add(performanceRepository.saveAndFlush(perf2));

                event.setPerformances(performances);
                eventRepository.saveAndFlush(event);

            });
        }
    }


    private void generateNews() {
        faker = new Faker();
        News newsData, news;
        Event e = null;
        int j = 0;
        for (int i = 0; i < 100; i++) {

            if (i < (eventRepository.findAll().size() - 1)) {
                e = eventRepository.findAll().get(i);
            } else {
                if (j >= (eventRepository.findAll().size() - 1)) {
                    j = 0;

                }

                e = eventRepository.findAll().get(j);
                j++;
            }
            newsData = News.builder()
                .author("Harry Potter")
                .date(LocalDate.now())
                .title("The movie Boom!")
                .summary("The movie Boom! is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n")
                .text("The movie Boom! is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n" +
                    " The artist is Joe Doe. The ticket price categories are cheap and expensive.\n" +
                    "The movie can be view in hall 1 and hall 2. The picture will be played at the 2020/01/12.\n" +
                    "Seats are available from column 7 to 12.\n")
                .event(e).build();

            news = newsRepository.saveAndFlush(newsData);
            if (i % 10 == 0) {
                LOGGER.info("News run " + (i / 10));
            }
            generatePicture(news, "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom1.txt");


        }

        LOGGER.info("News generated");


    }

    private void generatePicture(News news, String path) {
        File file = new File(path);
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            String line;
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            byte[] decode = Base64.getDecoder().decode(line);

            NewsImage image = NewsImage.builder().pic(PictureHandler.compressBytes(decode)).news(news).original_name("picture.txt").build();
            NewsImage image1 = imageRepository.saveAndFlush(image);
            news.addImage(image1);
            newsRepository.saveAndFlush(news);
        } catch (Exception e) {
            LOGGER.error("Error generating picture", e);
        }
    }


    private void generateAdminUser() {
        String password = "12345678";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        User user_allg = User.builder()
            .id(302L)
            .firstname("admin")
            .surname("user")
            .admin(true)
            .birthday(LocalDate.now())
            .deleted(0)
            .signInAttempts(0)
            .password(hashedPassword)
            .build();
        user_allg.setEmail(user_allg.getFirstname() + "@" + user_allg.getSurname());
        this.adminUser = applicationUserRepository.saveAndFlush(user_allg);
    }


    private void generateNormalUser() {
        String password = "12345678";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        User user_allg = User.builder()
            .firstname("user")
            .surname("user")
            .admin(false)
            .birthday(LocalDate.now())
            .deleted(0)
            .signInAttempts(0)
            .password(hashedPassword)
            .build();
        user_allg.setEmail(user_allg.getFirstname() + "@" + user_allg.getSurname());
        applicationUserRepository.saveAndFlush(user_allg);
    }

}












