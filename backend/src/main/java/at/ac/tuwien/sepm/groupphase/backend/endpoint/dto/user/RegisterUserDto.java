package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
public class RegisterUserDto {
    private String email;
    private String password;
    private String firstname;
    private String surname;
    private boolean admin;
}
