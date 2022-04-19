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

@Profile("generateMR3Data")
@Component
public class MR3DataService {
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

    public MR3DataService(UserRepository applicationUserRepository, EventRepository eventRepository, ArtistRepository artistRepository, LocationRepository locationRepository, PriceCategoryRepository priceCategoryRepository, HallRepository hallRepository, AreaRepository areaRepository, SeatRepository seatRepository, TicketRepository ticketRepository, PerformanceRepository performanceRepository, NewsRepository newsRepository, NewsImageRepository imageRepository) {
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
        generateCoherentData();
        generateNews();
        generateTicketsForTopTenEvent();
    }

    private void generateTicketsForTopTenEvent() {

        List<Event> all = eventRepository.findAll();
        for (Event e : all) {
            for (Performance p : e.getPerformances()) {
                generateRandomTicketsForTopTen(p);
                generateRandomTicketsForTopTen(p);
                generateRandomTicketsForTopTen(p);
            }


        }
        LOGGER.info("TopTen Events in DB");
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
        List<Artist> artists = new ArrayList<>();
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(1L).firstname("Herbert").surname("Grönemeyer").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(2L).firstname("Xavier").surname("Naidoo").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(3L).firstname("Ludwig").surname("van Beethoven").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(4L).firstname("Richard").surname("Strauss").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(5L).firstname("Johann Sebastian").surname("Bach").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(6L).firstname("Johannes").surname("Brahms").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(7L).firstname("Tim").surname("Benzko").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(8L).firstname("Lena").surname("Meyer-Landrut").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(9L).firstname("Max").surname("Giesinger").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(10L).firstname("Paul Hartmut").surname("Würdig").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(11L).firstname("Johann").surname("Hölzel").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(12L).firstname("Udo").surname("Jürgens").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(13L).firstname("Christina").surname("Stürmer").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(14L).firstname("Reinhard").surname("Fendrich").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(15L).firstname("RAF").surname("Camora").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(16L).firstname("Andy").surname("Borg").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(17L).firstname("Wolfgang").surname("Ambros").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(18L).firstname("Helene").surname("Fischer").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(19L).firstname("Peter").surname("Cornelius").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(20L).firstname("Dieter").surname("Bohlen").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(21L).firstname("Andreas").surname("Gabalier").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(22L).firstname("Peter").surname("Alexander").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(23L).firstname("Freddy").surname("Quinn").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(24L).firstname("John").surname("Lennon").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(25L).firstname("Paul").surname("McCartney").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(26L).firstname("Ringo").surname("Star").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(27L).firstname("George").surname("Harrison").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(28L).firstname("Anthony").surname("Kiedies").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(29L).firstname("Kurt").surname("Cobain").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(30L).firstname("Amy").surname("Winehouse").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(31L).firstname("Brian").surname("Jones").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(32L).firstname("Jimi").surname("Hendrix").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(33L).firstname("Janis").surname("Joplin").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(34L).firstname("Jim").surname("Morrison").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(35L).firstname("Robert").surname("Johnson").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(36L).firstname("Andreas").surname("Bourani").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(37L).firstname("Sarah").surname("Connor").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(38L).firstname("Peter").surname("Maffay").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(39L).firstname("Udo").surname("Lindenberg").build()));
        artists.add(artistRepository.saveAndFlush(Artist.builder().id(40L).firstname("Peter").surname("Fox").build()));

        List<Location> locations = new ArrayList<>();
        locations.add(locationRepository.saveAndFlush(Location.builder().id(1L).name("Pratersauna").street("Waldsteingartenstraße 135").city("Wien").area_code("1020").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(2L).name("VIEiPEE").street("Csardastraße 135").city("Wien").area_code("1020").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(3L).name("Grelle Forelle").street("Spittelauer Lände 12").city("Wien").area_code("1090").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(4L).name("The Loft").street(" Lerchenfelder Gürtel 37").city("Wien").area_code("1160").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(5L).name("Fluc & Fluc Wanne").street("Praterstern 5").city("Wien").area_code("1020").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(6L).name("Flex").street("Augartenbrücke 1").city("Wien").area_code("1010").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(7L).name("U1 Underground Club").street("Favoritenstraße 164").city("Wien").area_code("1100").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(8L).name("DonauTechno").street(" Karl-Schweighofer-Gasse 10").city("Wien").area_code("1070").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(9L).name("Camera").street("Neubaugasse 2").city("Wien").area_code("1070").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(10L).name("Sass Music Club").street("Karlsplatz 1").city("Wien").area_code("1010").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(11L).name("Dual Club").street("Burggasse 70").city("Wien").area_code("1070").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(12L).name("Opera Club").street("Mahlerstraße 11").city("Wien").area_code("1010").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(13L).name("Club U").street("Karlsplatz").city("Wien").area_code("1010").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(14L).name("Das Werk").street("Spittelauer Lände 12").city("Wien").area_code("1090").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(15L).name("P1").street("Prinzregentenstraße 1").city("München").area_code("80538").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(16L).name("Pa2").street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build()));
        locations.add(locationRepository.saveAndFlush(Location.builder().id(17L).name("Pa3").street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build()));

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


    private void generateNews() {
        faker = new Faker();
        News newsData, news;
        for (int i = 0; i < 12; i++) {
            newsData = News.builder()
                .author(faker.name().fullName())
                .date(LocalDate.now())
                .event(eventRepository.findAll().get(i)).build();

            newsData.setTitle(createTitle(newsData.getEvent().getCategory(), newsData.getEvent()));
            newsData.setText(generateText(newsData));
            newsData.setSummary(generateSummary(newsData));

            news = newsRepository.saveAndFlush(newsData);
            generatePicture(news, getPictureOfArtist(news));
            generatePicture(news, getPictureOfLocation(news));


        }


    }


    private String createTitle(Category category, Event event) {
        String title = "";
        switch (category) {
            case ACTION:
                title = event.getTitle().toUpperCase() + " !";
                break;
            case COMEDY:
                title = "Haha! " + event.getTitle();
                break;
            case DOCUMENTATION:
                title = "Watch " + event.getTitle();
                break;
            default:
                title = event.getTitle() + "?";
                break;
        }
        return title;

    }

    private String selectCategory(Category category) {
        String cat = "";
        switch (category) {
            case ACTION:
                cat = "an action movie";
                break;
            case COMEDY:
                cat = " a comedy movie";
                break;
            case DOCUMENTATION:
                cat = "a documentary";
                break;
            default:
                cat = " an event";
                break;
        }
        return cat;
    }

    private String generateSummary(News newsData) {
        return newsData.getText().split("\\.")[0];

    }

    private String generateText(News newsData) {
        Location location = null;
        Artist artist = null;
        Optional<Location> byId = locationRepository.findById(newsData.getEvent().getLocation_id());
        if (byId.isPresent()) {
            location = byId.get();
        }
        Optional<Artist> byId1 = artistRepository.findById(newsData.getEvent().getArtist_id());
        if (byId1.isPresent()) {
            artist = byId1.get();
        }
        String halls_str = "";
        String x = "", y = "";
        Set<Hall> halls = location.getHalls();
        for (Hall h : halls) {
            x = "hall " + h.getName() + "with cols " + h.getCols() + " and with rows " + h.getRows() +
                " with areas with name ";
            for (Area a : h.getAreas()) {
                y = a.getName() + " ";
            }
            x += y;
            halls_str += x;
        }
        String text = "This is a text about some very special event! The event is so secret that is the name and the location " +
            "is not known.\n";
        String category = selectCategory(newsData.getEvent().getCategory());
        if (artist != null && location != null) {
            text = newsData.getTitle() + " is " + category + " that will be played in "
                + location.getName() + " in the street " + location.getStreet() + " in " + location.getCity() + ".\n" +
                " The artist is " + artist.getFirstname() + " " + artist.getSurname() + " ." +
                "The movie can be view in" + halls_str + ". The event will be held for " + newsData.getEvent().getDuration()
                + " mins.\n" +
                "Seats are available from column 7 to 12.\n";
        }
        return text;


    }

    private String getPictureOfArtist(News newsData) {
        Artist artist = newsData.getEvent().getArtist();
        String url = "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/";
        switch (artist.getFirstname()) {
            case "Herbert":
                url += "artists/herbert_g.txt";
                break;
            case "Xavier":
                url += "artists/xavier.txt";
                break;
            case "Ludwig":
                url += "artists/beethoven.txt";
                break;
            case "Richard":
                url += "artists/strauss.txt";
                break;
            case "Johann Sebastian":
                url += "artists/bach.txt";
                break;
            case "Tim":
                url += "artists/tim.txt";
                break;
            case "Lena":
                url += "artists/lena.txt";
                break;
            case "Max":
                url += "artists/max.txt";
                break;
            case "Paul Hartmut":
                url += "artists/paul.txt";
                break;
            case "Johann":
                url += "artists/falco.txt";
                break;
            case "Udo":
                if (artist.getSurname().equals("Lindenberg")) {
                    url += "artists/lindenberg.txt";
                    break;
                } else if (artist.getSurname().equals("Jürgens")) {
                    url += "artists/jürgens.txt";
                    break;
                }

            case "Christina":
                url += "artists/stürmer.txt";
                break;
            case "Johannes":
                url += "artists/johannes.txt";
                break;
            case "Reinhard":
                url += "artists/reinhard.txt";
                break;
            case "RAF":
                url += "artists/raf.txt";
                break;
            case "Andy":
                url += "artists/andy.txt";
                break;
            case "Wolfgang":
                url += "artists/ambros.txt";
                break;
            case "Helene":
                url += "artists/fischer.txt";
                break;
            case "Peter":
                if (artist.getSurname().equals("Cornelius")) {
                    url += "artists/cornelius.txt";
                    break;
                } else if (artist.getSurname().equals("Alexander")) {
                    url += "artists/alexander.txt";
                    break;
                } else if (artist.getSurname().equals("Maffay")) {
                    url += "artists/maffay.txt";
                    break;
                } else if (artist.getSurname().equals("Fox")) {
                    url += "artists/fox.txt";
                    break;
                }
            case "Dieter":
                url += "artists/bohlen.txt";
                break;
            case "Andreas":
                if (artist.getSurname().equals("Bourani")) {
                    url += "artists/bourani.txt";
                    break;
                } else {
                    url += "artists/gab.txt";
                    break;
                }

            case "Freddy":
                url += "artists/quinn.txt";
                break;
            case "John":
                url += "artists/lennon.txt";
                break;
            case "Paul":
                url += "artists/paul_mc.txt";
                break;
            case "Ringo":
                url += "artists/ringo.txt";
                break;
            case "George":
                url += "artists/harrison.txt";
                break;
            case "Anthony":
                url += "artists/anthony.txt";
                break;
            case "Kurt":
                url += "artists/kurt.txt";
                break;
            case "Amy":
                url += "artists/amy.txt";
                break;
            case "Brian":
                url += "artists/brian.txt";
                break;
            case "Jimi":
                url += "artists/jimi.txt";
                break;
            case "Janis":
                url += "artists/janis.txt";
                break;
            case "Jim":
                url += "artists/jim.txt";
                break;
            case "Robert":
                url += "artists/rob.txt";
                break;
            case "Sarah":
                url += "artists/sarah.txt";
                break;


        }
        if (url.equals("src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/")) {
            url += "picture.txt";
        }
        return url;
    }

    private String getPictureOfLocation(News news) {
        String url = "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/";
        switch (news.getEvent().getLocation().getName()) {
            case "Pratersauna":
                url += "locations/pratersauna.txt";
                break;
            case "Camera":
                url += "locations/camera.txt";
                break;
            case "Club U":
                url += "locations/u.txt";
                break;
            case "DonauTechno":
                url += "locations/donau.txt";
                break;
            case "Dual Club":
                url += "locations/dual.txt";
                break;
            case "Flex":
                url += "locations/flex.txt";
                break;
            case "Grelle Forelle":
                url += "locations/grelle.txt";
                break;
            case "Opera Club":
                url += "locations/opera.txt";
                break;
            case "P1":
                url += "locations/pratersauna.txt";
                break;
            case "Sass Music Club":
                url += "locations/sass.txt";
                break;
            case "The Loft":
                url += "locations/loft.txt";
                break;
            case "U1 Underground Club":
                url += "locations/u1.txt";
                break;
            case "VIEiPEE":
                url += "locations/vie.txt";
                break;

            case "Das Werk":
                url += "locations/werk.txt";
                break;
            case "Pa2":
                url += "locations/pa2.txt";
                break;
            case "Pa3":
                url += "locations/pa3.txt";
                break;
            default:
                url += "locations/club.txt";
                break;
        }
        return url;
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
        applicationUserRepository.saveAndFlush(user_allg);
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












