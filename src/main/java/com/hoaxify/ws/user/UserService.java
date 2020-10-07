package com.hoaxify.ws.user;

import com.hoaxify.ws.hoax.Hoax;
import com.hoaxify.ws.hoax.HoaxService;
import com.hoaxify.ws.user.vm.UserUpdateVM;
import com.hoaxify.ws.file.FileService;
import error.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    HoaxService hoaxService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    @Autowired
    public void setHoaxService(HoaxService hoaxService) {
        this.hoaxService = hoaxService;
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Page<User> getUsers(Pageable page, User user) {
        if (user != null) return userRepository.findByUsernameNot(user.getUsername(), page);
        return userRepository.findAll(page);
    }

    public User getByUsername(String username) {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            throw new NotFoundException();
        }
        return foundUser;
    }

    public User updateUser(String username, UserUpdateVM updatedUser) {
        User foundUser = getByUsername(username);
        foundUser.setDisplayName((updatedUser.getDisplayName()));

        if (updatedUser.getImage() != null) {
            String oldImage = foundUser.getImage();

            String storedFileName = fileService.writeBase64EncodedStringToFile(updatedUser.getImage());
            foundUser.setImage(storedFileName);
            fileService.deleteProfileImage(oldImage);
        }
        return userRepository.save(foundUser);
    }


    public void deleteUser(String username) {
//      hoaxService.deleteHoaxesByUser(username);

        fileService.deleteRelatedFiles(userRepository.findByUsername(username));
        userRepository.delete(userRepository.findByUsername(username));
    }
}
