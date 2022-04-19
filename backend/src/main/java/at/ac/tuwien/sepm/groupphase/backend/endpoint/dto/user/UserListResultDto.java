package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserListResultDto {

    private long totalPages;
    private long currentPage;
    private boolean hasNext;
    private List<UserListDto> content;

}
