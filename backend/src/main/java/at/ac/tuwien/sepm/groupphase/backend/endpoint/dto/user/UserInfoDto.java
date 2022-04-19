package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
public class UserInfoDto {
    private String firstname, surname, email;
}
