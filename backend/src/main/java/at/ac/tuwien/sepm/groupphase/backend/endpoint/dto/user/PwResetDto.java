package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
public class PwResetDto {
    @NotNull
    @NotEmpty
    @Size(min=36, max=36)
    public String token;

    @NotNull
    @Size(min=8, max=255)
    public String pw;
}
