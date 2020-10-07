package com.hoaxify.ws.hoax;

import com.hoaxify.ws.user.User;
import error.AuthorizationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HoaxSecurityService {

    private final HoaxRepository hoaxRepository;

    public boolean isAllowedToDelete(long id, User loggedInUser) {
        Optional<Hoax> foundHoax = hoaxRepository.findById(id);
        if (!foundHoax.isPresent()) {
            return false;
        }

        Hoax hoax = foundHoax.get();
        if (hoax.getUser().getId() != loggedInUser.getId()) {
            return false;
        }

        return true;
    }
}
