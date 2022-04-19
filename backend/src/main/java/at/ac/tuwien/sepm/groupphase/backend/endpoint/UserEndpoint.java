package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserListResultMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.security.MyAuthUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "User Endpoint")
@RequestMapping(value = "/api/v1")
public class UserEndpoint {

    private final static int PAGE_SIZE = 20;

    private final UserService userService;
    private final UserListResultMapper userListResultMapper;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserListResultMapper userListResultMapper, UserMapper userMapper) {
        this.userService = userService;
        this.userListResultMapper = userListResultMapper;
        this.userMapper = userMapper;
    }

    @PostMapping(value = "/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Create new a new user")
    public UserDto signup(@RequestBody @Valid RegisterUserDto user) throws CreateFailedException {
        user.setAdmin(false);
        User userEntity = UserMapper.INSTANCE.registerUserDtoToEntity(user);
        User createdUser = userService.signupUser(userEntity);
        return UserMapper.INSTANCE.entityToUserDto(createdUser);
    }

    @PutMapping(value = "/user")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Updates User")
    public UserDto update(@RequestBody @Valid RegisterUserDto user, Authentication authentication) throws ChangeFailedException {
        MyAuthUser authUser = new MyAuthUser(userService, authentication.getPrincipal());
        User userEntity = userMapper.registerUserDtoToEntity(user);
        User createdUser = userService.changeUser(userEntity, authUser);
        return UserMapper.INSTANCE.entityToUserDto(createdUser);
    }

    @PostMapping(value = "/user")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "Create new a new user")
    public UserDto create(@RequestBody @Valid RegisterUserDto user) throws CreateFailedException {
        User userEntity = UserMapper.INSTANCE.registerUserDtoToEntity(user);
        User createdUser = userService.signupUser(userEntity);
        return UserMapper.INSTANCE.entityToUserDto(createdUser);
    }

    @PostMapping(value = "/user/{id}/unlock")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource"),
        @ApiResponse(code = 404, message = "User not found")
    })
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "Unlocks a User")
    public ResponseEntity<String> unlock(@PathVariable long id) throws NotFoundException {
        userService.unlockUser(id);
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/user/{id}/lock")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource"),
        @ApiResponse(code = 404, message = "User not found")
    })
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "Locks a User")
    public ResponseEntity<String> lock(@PathVariable long id) throws NotFoundException, SelfLockException {
        userService.lockUser(id);
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/user/{id}/password")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource"),
        @ApiResponse(code = 404, message = "User not found")
    })
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "Resets a users password")
    public ResponseEntity<String> resetPassword(@PathVariable long id) {
        userService.resetUserPassword(id);
        return ResponseEntity.ok("Password has been reset");
    }

    @PostMapping(value = "/user/resetPw")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource"),
    })
    @ApiOperation(value = "Resets a users password")
    public Map<String, String> actuallyResetPassword(@RequestBody @Valid PwResetDto dto) {
        userService.resetUserPassword(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset");
        return response;
    }

    @GetMapping(value = "/user")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource")
    })
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "Lists all users")
    public UserListResultDto list(@RequestParam(required = false) Integer page) {
        int pageToLoad = 0;
        if (page != null) {
            pageToLoad = page;
        }
        return userListResultMapper.entityToUserListResultDto(userService.findAllPaged(PageRequest.of(pageToLoad, PAGE_SIZE)));
    }

    @GetMapping(value = "/user/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "You are not allowed to access this resource")
    })
    @ApiOperation(value = "Returns a single user")
    public UserInfoDto oneUser(@PathVariable long id, Authentication authentication) {
        MyAuthUser authUser = new MyAuthUser(userService, authentication.getPrincipal());
        return userMapper.entityToUserInfoDto(authUser.getCurrentUser());
    }


    @DeleteMapping(value = "/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
    })
    @ApiOperation(value = "Deletes the current user")
    public ResponseEntity<String> delete(Authentication authentication) throws NotFoundException {
        MyAuthUser authUser = new MyAuthUser(userService, authentication.getPrincipal());
        userService.deleteUser(authUser);
        return ResponseEntity.ok("User deleted successfully");
    }
}
