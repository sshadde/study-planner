package ru.studyplanner.mediator;

import java.util.List;
import ru.studyplanner.mediator.dto.AdminUserResponse;

public interface AdminService {

    List<AdminUserResponse> getUsers();
}
