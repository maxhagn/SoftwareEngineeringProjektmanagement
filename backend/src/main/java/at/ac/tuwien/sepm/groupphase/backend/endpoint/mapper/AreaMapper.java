package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.AreaDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PriceCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Area;
import at.ac.tuwien.sepm.groupphase.backend.entity.PriceCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AreaMapper {

    AreaMapper INSTANCE = Mappers.getMapper(AreaMapper.class);

    @Named("areaToDto")
    AreaDto areaToDto(Area area);
}
