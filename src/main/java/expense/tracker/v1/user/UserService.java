package expense.tracker.v1.user;

import expense.tracker.v1.user.dto.UserRequest;
import expense.tracker.v1.user.dto.UserResponse;
import expense.tracker.v1.utils.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements BasicService<User, UserRequest, UserResponse> {

	private final UserRepository userRepository;



}
