package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDto {
    private int id;
    private String email;

}
