package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

public class PerformanceWithEventDto extends PerformanceDto {
    private EventDto event;

    public EventDto getEvent() {
        return event;
    }
}
