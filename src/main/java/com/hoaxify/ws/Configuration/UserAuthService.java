package com.hoaxify.ws.Configuration;

import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return foundUser;
    }
}
