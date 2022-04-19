package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.Data;

@Data
public class UserListDto {

    private long id;
    private String firstname;
    private String surname;
    private boolean locked = false;

}
