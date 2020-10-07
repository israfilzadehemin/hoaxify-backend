package com.hoaxify.ws.auth;

import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserRepository;
import com.hoaxify.ws.user.vm.UserVM;
import com.hoaxify.ws.shared.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/api/1.0/auth")
    public UserVM handleAuth(@CurrentUser User user) {
        return new UserVM(user);
    }
}
