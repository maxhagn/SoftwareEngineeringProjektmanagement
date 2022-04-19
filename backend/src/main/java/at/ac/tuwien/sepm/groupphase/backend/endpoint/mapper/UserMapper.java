package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.RegisterUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Implicitly maps a Register User Dto to a User Entity
     * @param dto The dto to map to a User entity
     * @return the User entity mapped
     */
    @Named("registerUserDtoToEntity")
    User registerUserDtoToEntity(RegisterUserDto dto);

    /**
     * Implicitly maps a User Entity to a User Dto
     * @param user The Entity to map to a User Dto
     * @return the User Dto mapped
     */
    @Named("EntityToUserDto")
    UserDto entityToUserDto(User user);

    /**
     * Implicitly maps a User Entity to a UserInfoDto
     * @param user the Entity to map to a User Dto
     * @return the mapped UserInfoDto
     */
    @Named("EntityToUserInfoDto")
    UserInfoDto entityToUserInfoDto(User user);
}
