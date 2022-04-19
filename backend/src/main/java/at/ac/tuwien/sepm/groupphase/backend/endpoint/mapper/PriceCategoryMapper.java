package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PriceCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.RegisterUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PriceCategoryMapper {

    PriceCategoryMapper INSTANCE = Mappers.getMapper(PriceCategoryMapper.class);

    @Named("priceToDto")
    PriceCategoryDto priceToDto(PriceCategory price);
}
