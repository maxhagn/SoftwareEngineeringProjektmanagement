package at.ac.tuwien.sepm.groupphase.backend.datageneration;

import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Profile("deleteDb")
@Component
public class DeleteDb {
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
    private final NewsImageRepository newsImageRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public DeleteDb(UserRepository applicationUserRepository, EventRepository eventRepository, ArtistRepository artistRepository, LocationRepository locationRepository, PriceCategoryRepository priceCategoryRepository, HallRepository hallRepository, AreaRepository areaRepository, SeatRepository seatRepository, TicketRepository ticketRepository, PerformanceRepository performanceRepository, NewsRepository newsRepository, NewsImageRepository newsImageRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
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
        this.newsImageRepository = newsImageRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @PostConstruct
    public void deleteData() {
        seatRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();

        newsImageRepository.deleteAllInBatch();
        newsRepository.deleteAllInBatch();

        passwordResetTokenRepository.deleteAllInBatch();
        applicationUserRepository.deleteAllInBatch();

        performanceRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        artistRepository.deleteAllInBatch();

        areaRepository.deleteAllInBatch();
        priceCategoryRepository.deleteAllInBatch();
        hallRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
    }

}












