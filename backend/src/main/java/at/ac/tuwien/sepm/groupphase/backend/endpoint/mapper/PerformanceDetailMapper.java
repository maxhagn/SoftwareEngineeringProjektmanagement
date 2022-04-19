package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.PerformanceDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserListDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PerformanceDetailMapper {

    @Mapping(
        target = "hallName",
        source = "hall.name"
    )
    @Mapping(
        target = "locationName",
        source = "hall.location.name"
    )
    @Mapping(
        target = "title",
        source = "event.title"
    )
    @Mapping(
        target = "duration",
        expression = "java(performance.getEvent().getDuration())"
    )
    @Mapping(
        target = "hallId",
        source = "performance.hall.id"
    )
    @Mapping(
        target = "date",
        source = "datetime"
    )
    @Mapping(
        target = "minPrice",
        source = "performance.min_price"
    )
    PerformanceDetailDto entityToPerformanceDetailDto(Performance performance);

}
