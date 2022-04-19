package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    PerformanceSearchDto entityToDto(Performance performance);

    Performance dtoToEntity(PerformanceSearchDto performanceSearchDto);

}