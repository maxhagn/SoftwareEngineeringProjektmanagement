package at.ac.tuwien.sepm.groupphase.backend.datageneration;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.utils.PictureHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Profile("generateRealData")
@Component
public class RealDataService {
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

    public RealDataService(UserRepository applicationUserRepository, EventRepository eventRepository, ArtistRepository artistRepository, LocationRepository locationRepository, PriceCategoryRepository priceCategoryRepository, HallRepository hallRepository, AreaRepository areaRepository, SeatRepository seatRepository, TicketRepository ticketRepository, PerformanceRepository performanceRepository, NewsRepository newsRepository, NewsImageRepository imageRepository) {
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
        generateFullEvent();
        generateHalls();
        generateArtists();
        generateLocations();
        generatePerformances();
        generateNews();

    }

    private void generateArtists() {

        artistRepository.saveAndFlush(Artist.builder().id(1L).firstname("Herbert").surname("Grönemeyer").build());
        artistRepository.saveAndFlush(Artist.builder().id(2L).firstname("Xavier").surname("Naidoo").build());
        artistRepository.saveAndFlush(Artist.builder().id(3L).firstname("Ludwig").surname("van Beethoven").build());
        artistRepository.saveAndFlush(Artist.builder().id(4L).firstname("Richard").surname("Strauss").build());
        artistRepository.saveAndFlush(Artist.builder().id(5L).firstname("Johann Sebastian").surname("Bach").build());
        artistRepository.saveAndFlush(Artist.builder().id(6L).firstname("Johannes").surname("Brahms").build());
        artistRepository.saveAndFlush(Artist.builder().id(7L).firstname("Tim").surname("Benzko").build());
        artistRepository.saveAndFlush(Artist.builder().id(8L).firstname("Lena").surname("Meyer-Landrut").build());
        artistRepository.saveAndFlush(Artist.builder().id(9L).firstname("Max").surname("Giesinger").build());
        artistRepository.saveAndFlush(Artist.builder().id(10L).firstname("Paul Hartmut").surname("Würdig").build());
        artistRepository.saveAndFlush(Artist.builder().id(11L).firstname("Johann").surname("Hölzel").build());
        artistRepository.saveAndFlush(Artist.builder().id(12L).firstname("Udo").surname("Jürgens").build());
        artistRepository.saveAndFlush(Artist.builder().id(13L).firstname("Christina").surname("Stürmer").build());
        artistRepository.saveAndFlush(Artist.builder().id(14L).firstname("Reinhard").surname("Fendrich").build());
        artistRepository.saveAndFlush(Artist.builder().id(15L).firstname("RAF").surname("Camora").build());
        artistRepository.saveAndFlush(Artist.builder().id(16L).firstname("Andy").surname("Borg").build());
        artistRepository.saveAndFlush(Artist.builder().id(17L).firstname("Wolfgang").surname("Ambros").build());
        artistRepository.saveAndFlush(Artist.builder().id(18L).firstname("Helene").surname("Fischer").build());
        artistRepository.saveAndFlush(Artist.builder().id(19L).firstname("Peter").surname("Cornelius").build());
        artistRepository.saveAndFlush(Artist.builder().id(20L).firstname("Dieter").surname("Bohlen").build());
        artistRepository.saveAndFlush(Artist.builder().id(21L).firstname("Andreas").surname("Gabalier").build());
        artistRepository.saveAndFlush(Artist.builder().id(22L).firstname("Peter").surname("Alexander").build());
        artistRepository.saveAndFlush(Artist.builder().id(23L).firstname("Freddy").surname("Quinn").build());
        artistRepository.saveAndFlush(Artist.builder().id(24L).firstname("John").surname("Lennon").build());
        artistRepository.saveAndFlush(Artist.builder().id(25L).firstname("Paul").surname("McCartney").build());
        artistRepository.saveAndFlush(Artist.builder().id(26L).firstname("Ringo").surname("Star").build());
        artistRepository.saveAndFlush(Artist.builder().id(27L).firstname("George").surname("Harrison").build());
        artistRepository.saveAndFlush(Artist.builder().id(28L).firstname("Anthony").surname("Kiedies").build());
        artistRepository.saveAndFlush(Artist.builder().id(29L).firstname("Kurt").surname("Cobain").build());
        artistRepository.saveAndFlush(Artist.builder().id(30L).firstname("Amy").surname("Winehouse").build());
        artistRepository.saveAndFlush(Artist.builder().id(31L).firstname("Brian").surname("Jones").build());
        artistRepository.saveAndFlush(Artist.builder().id(32L).firstname("Jimi").surname("Hendrix").build());
        artistRepository.saveAndFlush(Artist.builder().id(33L).firstname("Janis").surname("Joplin").build());
        artistRepository.saveAndFlush(Artist.builder().id(34L).firstname("Jim").surname("Morrison").build());
        artistRepository.saveAndFlush(Artist.builder().id(35L).firstname("Robert").surname("Johnson").build());
        artistRepository.saveAndFlush(Artist.builder().id(36L).firstname("Andreas").surname("Bourani").build());
        artistRepository.saveAndFlush(Artist.builder().id(37L).firstname("Sarah").surname("Connor").build());
        artistRepository.saveAndFlush(Artist.builder().id(38L).firstname("Peter").surname("Maffay").build());
        artistRepository.saveAndFlush(Artist.builder().id(39L).firstname("Udo").surname("Lindenberg").build());
        artistRepository.saveAndFlush(Artist.builder().id(40L).firstname("Peter").surname("Fox").build());

    }

    private void generatePerformances() {

        Location location = Location.builder().id(1000L).name("Pratersauna").street("Waldsteingartenstraße 135").city("Wien").area_code("1020").build();
        location = locationRepository.saveAndFlush(location);

        Event event = Event.builder().id(2001L).category(Category.ACTION).title("Das Evnt").description("Action Movie 1").duration(120).location(location)
            .artist(artistRepository.findAll().get(0)).build();
        event = eventRepository.saveAndFlush(event);

        Hall hall = Hall.builder().id(2004L).name("Hall with Empty Space").cols(10).rows(10).location(location).build();
        hall = hallRepository.saveAndFlush(hall);


        performanceRepository.saveAndFlush(Performance.builder().id(1L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(2L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(3L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(4L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(5L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(6L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(7L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(8L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(9L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(10L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(11L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());
        performanceRepository.saveAndFlush(Performance.builder().id(12L).event(event).hall(hall).datetime(LocalDateTime.now().plusDays(1)).build());

    }

    private void generateLocations() {

        locationRepository.saveAndFlush(Location.builder().id(1L).name("Pratersauna").street("Waldsteingartenstraße 135").city("Wien").area_code("1020").build());
        locationRepository.saveAndFlush(Location.builder().id(2L).name("VIEiPEE").street("Csardastraße 135").city("Wien").area_code("1020").build());
        locationRepository.saveAndFlush(Location.builder().id(3L).name("Grelle Forelle").street("Spittelauer Lände 12").city("Wien").area_code("1090").build());
        locationRepository.saveAndFlush(Location.builder().id(4L).name("The Loft").street(" Lerchenfelder Gürtel 37").city("Wien").area_code("1160").build());
        locationRepository.saveAndFlush(Location.builder().id(5L).name("Fluc & Fluc Wanne").street("Praterstern 5").city("Wien").area_code("1020").build());
        locationRepository.saveAndFlush(Location.builder().id(6L).name("Flex").street("Augartenbrücke 1").city("Wien").area_code("1010").build());
        locationRepository.saveAndFlush(Location.builder().id(7L).name("U1 Underground Club").street("Favoritenstraße 164").city("Wien").area_code("1100").build());
        locationRepository.saveAndFlush(Location.builder().id(8L).name("DonauTechno").street(" Karl-Schweighofer-Gasse 10").city("Wien").area_code("1070").build());
        locationRepository.saveAndFlush(Location.builder().id(9L).name("Camera").street("Neubaugasse 2").city("Wien").area_code("1070").build());
        locationRepository.saveAndFlush(Location.builder().id(10L).name("Sass Music Club").street("Karlsplatz 1").city("Wien").area_code("1010").build());
        locationRepository.saveAndFlush(Location.builder().id(11L).name("Dual Club").street("Burggasse 70").city("Wien").area_code("1070").build());
        locationRepository.saveAndFlush(Location.builder().id(12L).name("Opera Club").street("Mahlerstraße 11").city("Wien").area_code("1010").build());
        locationRepository.saveAndFlush(Location.builder().id(13L).name("Club U").street("Karlsplatz").city("Wien").area_code("1010").build());
        locationRepository.saveAndFlush(Location.builder().id(14L).name("Das Werk").street("Spittelauer Lände 12").city("Wien").area_code("1090").build());
        locationRepository.saveAndFlush(Location.builder().id(15L).name("P1").street("Prinzregentenstraße 1").city("München").area_code("80538").build());
        locationRepository.saveAndFlush(Location.builder().id(16L).name("Pa2").street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build());
        locationRepository.saveAndFlush(Location.builder().id(17L).name("Pa3").street("Prinzregentenstraße 1").city("Vienna").area_code("80538").build());

    }


    private void generateHalls() {
        Location location = Location.builder().id(202L).name("Babenberger Passage").city("Wien").street("Burgring 3").area_code("1010").build();
        location = locationRepository.saveAndFlush(location);

        Hall h1 = Hall.builder().id(203L).name("Hall with Sections").cols(10).rows(10).location(location).build();
        Hall h2 = Hall.builder().id(204L).name("Hall with Empty Space").cols(10).rows(10).location(location).build();
        h1 = hallRepository.saveAndFlush(h1);
        h2 = hallRepository.saveAndFlush(h2);

        PriceCategory p1 = PriceCategory.builder().id(203L).name("Category A").price(10.0f).location(location).build();
        PriceCategory p2 = PriceCategory.builder().id(204L).name("Category B").price(20.0f).location(location).build();
        p1 = priceCategoryRepository.saveAndFlush(p1);
        p2 = priceCategoryRepository.saveAndFlush(p2);

        List<Area> areas = new LinkedList<>();
        areas.add(Area.builder().id(204L).startCol(1).endCol(10).startRow(1).endRow(1).type(Type.SCREEN).name("Leinwand").hall(h1).build());
        areas.add(Area.builder().id(205L).startCol(1).endCol(10).startRow(2).endRow(5).type(Type.SECTION).name("Section A").priceCategory(p1).hall(h1).build());
        areas.add(Area.builder().id(206L).startCol(1).endCol(10).startRow(6).endRow(10).type(Type.SECTION).name("Section B").priceCategory(p2).hall(h1).build());

        areas.add(Area.builder().id(207L).startCol(1).endCol(10).startRow(1).endRow(1).type(Type.SCREEN).name("Bühne").hall(h2).build());
        areas.add(Area.builder().id(208L).startCol(1).endCol(10).startRow(2).endRow(5).type(Type.SEATS).name("Section A").priceCategory(p1).hall(h2).build());
        areas.add(Area.builder().id(209L).startCol(1).endCol(10).startRow(7).endRow(10).type(Type.SEATS).name("Section B").priceCategory(p2).hall(h2).build());
        areaRepository.saveAll(areas);

        Artist artist = Artist.builder().id(202L).firstname("Eric").surname("Clapton").build();
        artistRepository.saveAndFlush(artist);
        Event event = Event.builder().id(202L).category(Category.ACTION).title("Boom!").description("Action Movie 1").duration(120).location(location)
            .artist(artistRepository.findById(202L).get()).build();
        event = eventRepository.saveAndFlush(event);

        Performance per1 = Performance.builder().id(203L).event(event).hall(h1).datetime(LocalDateTime.now().plusDays(1))
            .build();
        Performance per2 = Performance.builder().id(204L).event(event).hall(h2).datetime(LocalDateTime.now().plusDays(1))
            .build();
        performanceRepository.save(per1);
        performanceRepository.save(per2);
    }

    private void generateFullEvent() {
        Location location = Location.builder().id(3000L).name("Babenberger Passage").city("Wien").street("Burgring 3").area_code("1010").build();
        location = locationRepository.saveAndFlush(location);

        Artist artist = Artist.builder().id(201L).firstname("Michael").surname("Jackson").build();
        artistRepository.saveAndFlush(artist);

        Event event = Event.builder().id(201L).category(Category.ACTION).title("Boom!").description("Action Movie 1").duration(120).location(location)
            .artist(artistRepository.findById(201L).get()).build();
        event = eventRepository.saveAndFlush(event);

        PriceCategory c1 = PriceCategory.builder().id(201L).name("Cheap").location(location).price(10.0f).build();
        PriceCategory c2 = PriceCategory.builder().id(202L).name("Not so cheap").location(location).price(15.0f).build();
        c1 = priceCategoryRepository.saveAndFlush(c1);
        c2 = priceCategoryRepository.saveAndFlush(c2);

        Hall h1 = Hall.builder().id(201L).cols(10).rows(20).name("Sall 1").location(location).build();
        h1 = hallRepository.saveAndFlush(h1);

        Area screen = Area.builder().id(201L).startCol(1).endCol(10).startRow(1).endRow(1).type(Type.SCREEN).hall(h1).build();
        Area a1 = Area.builder().id(202L).startCol(1).endCol(10).startRow(2).endRow(10).hall(h1)
            .name("Cheap").priceCategory(c1).type(Type.SEATS).build();
        Area a2 = Area.builder().id(203L).startCol(1).endCol(10).startRow(11).endRow(20).hall(h1)
            .name("expensive").priceCategory(c2).type(Type.SEATS).build();
        screen = areaRepository.saveAndFlush(screen);
        a1 = areaRepository.saveAndFlush(a1);
        a2 = areaRepository.saveAndFlush(a2);

        Performance p1 = Performance.builder().id(201L).event(event).hall(h1).datetime(LocalDateTime.now().plusDays(1)).build();
        Performance p2 = Performance.builder().id(202L).event(event).hall(h1).datetime(LocalDateTime.now().plusDays(1)).build();
        p1 = performanceRepository.saveAndFlush(p1);
        p2 = performanceRepository.saveAndFlush(p2);

        String password = "12345678";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        User user = User.builder()
            .id(301L)
            .email("user1@user.com")
            .firstname("user")
            .surname("user")
            .admin(false)
            .birthday(LocalDate.now())
            .deleted(0)
            .signInAttempts(0)
            .password(hashedPassword).build();
        user = applicationUserRepository.saveAndFlush(user);

        Ticket ticket =
            Ticket.builder().id(200L).performance(p1).status(Status.BOUGHT).user(user).createDate(LocalDateTime.now()).build();
        ticket = ticketRepository.saveAndFlush(ticket);

        Ticket ticket1 =
            Ticket.builder().id(201L).performance(p1).status(Status.IN_QUE).user(user).createDate(LocalDateTime.now()).build();
        ticket1 = ticketRepository.saveAndFlush(ticket1);

        Ticket ticket2 =
            Ticket.builder().id(202L).performance(p1).status(Status.IN_QUE).user(user).createDate(LocalDateTime.now()).build();
        ticketRepository.saveAndFlush(ticket2);

        Ticket ticket3 =
            Ticket.builder().id(203L).performance(p1).status(Status.RESERVED).user(user).createDate(LocalDateTime.now()).build();
        ticketRepository.saveAndFlush(ticket3);

        Seat s15 = Seat.builder().id(215L).seatCol(1).seatRow(12).ticket(ticket3).area(a2).build();
        seatRepository.saveAndFlush(s15);

        Seat s10 = Seat.builder().id(210L).seatCol(5).seatRow(12).ticket(ticket1).area(a2).build();
        Seat s11 = Seat.builder().id(211L).seatCol(6).seatRow(12).ticket(ticket1).area(a2).build();
        seatRepository.saveAndFlush(s10);
        seatRepository.saveAndFlush(s11);

        Seat s1 = Seat.builder().id(201L).seatCol(9).seatRow(12).ticket(ticket).area(a2).build();
        Seat s2 = Seat.builder().id(202L).seatCol(8).seatRow(12).ticket(ticket).area(a2).build();
        Seat s3 = Seat.builder().id(202L).seatCol(7).seatRow(12).ticket(ticket).area(a2).build();
        seatRepository.saveAndFlush(s1);
        seatRepository.saveAndFlush(s2);
        seatRepository.saveAndFlush(s3);
    }

    private void generateNews() {
        News newsData = News.builder()
            .id(201L)
            .author("J.K Rowling")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .text("Boom! is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n" +
                " The artist is Joe Doe. The ticket price categories are cheap and expensive.\n" +
                "The movie can be view in hall 1 and hall 2. The picture will be played at the 2020/01/12.\n" +
                "Seats are available from column 7 to 12.\n")
            .summary("Boom! is an action movie that will...")
            .title("Movie Boom!")
            .build();

        News news = newsRepository.saveAndFlush(newsData);

        generatePicture(news, "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom1.txt");

        newsData = News.builder()
            .id(202L)
            .author("Harry Potter")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .text("The movie Boom! is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n" +
                " The artist is Joe Doe. The ticket price categories are cheap and expensive.\n" +
                "The movie can be view in hall 1 and hall 2. The picture will be played at the 2020/01/12.\n" +
                "Seats are available from column 7 to 12.\n")
            .summary("The movie Boom! is an action movie that will...")
            .title("Watch the movie Boom!")
            .build();

        News news1 = newsRepository.saveAndFlush(newsData);
        generatePicture(news1, "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom2.txt");
        newsData = News.builder()
            .id(203L)
            .author("Ron Weasly")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .text("BOOOOMMM! THE MOVIE is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n" +
                " The artist is Joe Doe. The ticket price categories are cheap and expensive.\n" +
                "The movie can be view in hall 1 and hall 2. The picture will be played at the 2020/01/12.\n" +
                "Seats are available from column 7 to 12.\n")
            .summary("BOOOOMMM! THE MOVIE is an action movie that will...")
            .title("BOOOOMMM! THE MOVIE")
            .build();
        News news2 = newsRepository.saveAndFlush(newsData);
        generatePicture(news2, "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom3.txt");

        newsData = News.builder()
            .id(204L)
            .author("Ron Weasly")
            .date(LocalDate.now())
            .event(eventRepository.findAll().get(0))
            .text("BOOOOMMM! What a MOVIE! Boom! is an action movie that will be played in a cinema at MyStreet 1210 in Vienna.\n" +
                " The artist is Joe Doe. The ticket price categories are cheap and expensive.\n" +
                "The movie can be view in hall 1 and hall 2. The picture will be played at the 2020/01/12.\n" +
                "Seats are available from column 7 to 12.\n")
            .summary("BOOOOMMM! THE MOVIE is an action movie that will...")
            .title("BOOOOMMM! What a MOVIE")
            .build();
        News news3 = newsRepository.saveAndFlush(newsData);
        generatePicture(news3, "src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/picture.txt");

        Long id;
        for (int i = 0; i <6; i++){
            id = 204  + (long)(i);
            newsData = News.builder()
                .id(id)
                .author(pickRandomAuthor())
                .date(LocalDate.now())
                .event(pickRandomEvent()).build();

            newsData.setText(generateText(newsData));
            newsData.setSummary(generateSummary(newsData));
            newsData.setTitle(generateTitle(newsData));


            news = newsRepository.saveAndFlush(newsData);

            generatePicture(news, pickRandomPicture());
        }


    }

    private String generateTitle(News newsData) {
        return createTitle(newsData.getEvent().getCategory(), newsData.getEvent());



    }

    private String createTitle(Category category, Event event) {
        switch (category){
            case ACTION: return event.getTitle().toUpperCase() + " !";
            case COMEDY:  return "Haha "+ event.getTitle() ;
            case DOCUMENTATION: return "Watch " + event.getTitle();
        }
        return "";
    }

    private String selectCategory(Category category) {
        switch (category){
            case ACTION: return "an action movie";
            case COMEDY:  return " a  comedy movie";
            case DOCUMENTATION: return "a documentary";
        }
        return "";
    }

    private String generateSummary(News newsData) {
        return null;
    }

    private String generateText(News newsData) {
        Location location = locationRepository.findById(newsData.getEvent().getLocation_id()).get();
        Artist artist = artistRepository.findById(newsData.getEvent().getArtist_id()).get();
        String category = selectCategory(newsData.getEvent().getCategory());
        String text = newsData.getTitle()  +" is " + category + " that will be played in "
            + location.getName()  +  "in the street " + location.getStreet()+" in "+ location.getCity() +".\n" +
            " The artist is" +artist.getFirstname() + " " + artist.getSurname() + " ." +
            "The movie can be view in hall 1 and hall 2. The picture will be played at the "+ newsData.getEvent().getDuration()
            +".\n" +
            "Seats are available from column 7 to 12.\n";
        return text;


    }

    private Event pickRandomEvent() {
        List<Event> all = eventRepository.findAll();
        Random random = new Random();
        int i = random.nextInt(all.size());
        LOGGER.info("get Random event: " + all.get(i));
        all.get(i).setLocation(locationRepository.findAll().get(0));
        return all.get(i);
    }

    private String pickRandomAuthor() {
        List<String> author = new LinkedList<>();
        author.add("J.K. Rowling");
        author.add("Stephen King");
        author.add("Thomas Brezina");
        author.add("Jane Austen");
        author.add("Kerstin Gier");
        author.add("Dan Brown");
        author.add("Dan Brown");
        Random random = new Random();
        int i = random.nextInt(author.size());
        return author.get(i);
    }

    private String pickRandomPicture() {
        List<String> urls = new LinkedList<>();
        urls.add("src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom1.txt");
        urls.add("src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom2.txt");
        urls.add("src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/boom3.txt");
        urls.add("src/main/java/at/ac/tuwien/sepm/groupphase/backend/datageneration/pics/picture.txt");
        Random random = new Random();
        int i = random.nextInt(urls.size());
        return urls.get(i);

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
            LOGGER.info("IMAGE:  "  + image.getPic().length);
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
            .seenNews(null)
            .tickets(null).build();
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
            .seenNews(null)
            .tickets(null).build();
        user_allg.setEmail(user_allg.getFirstname() + "@" + user_allg.getSurname());
        applicationUserRepository.saveAndFlush(user_allg);
    }

}












