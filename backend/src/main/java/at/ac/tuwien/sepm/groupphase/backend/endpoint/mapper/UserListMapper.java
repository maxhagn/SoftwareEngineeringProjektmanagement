package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserListDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserListMapper {
    @Mapping(
        target = "locked",
        expression = "java(isUserLocked(user))"
    )
    UserListDto entityToUserListDto(User user);

    List<UserListDto> entityToUserListDtoCollection(List<User> user);

    default boolean isUserLocked(User user) {
        return user.getSignInAttempts() >= 5;
    }
}
