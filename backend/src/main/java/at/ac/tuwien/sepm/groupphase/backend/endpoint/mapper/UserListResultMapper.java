package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserListResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.helper.CustomPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = UserListMapper.class, componentModel = "spring")
public interface UserListResultMapper {
    @Mapping(
        target = "totalPages",
        expression = "java(page.getTotalPages())"
    )
    @Mapping(
        target = "currentPage",
        expression = "java(page.getNumber())"
    )
    @Mapping(
        target = "hasNext",
        expression = "java(page.hasNext())"
    )
    UserListResultDto entityToUserListResultDto(CustomPage<User> page);

}
