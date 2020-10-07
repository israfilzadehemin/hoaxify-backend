package com.hoaxify.ws.user;

import com.hoaxify.ws.user.vm.UserUpdateVM;
import com.hoaxify.ws.user.vm.UserVM;
import com.hoaxify.ws.shared.CurrentUser;
import com.hoaxify.ws.shared.GenericResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/1.0")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse createUser(@Valid @RequestBody User user) {
        userService.save(user);
        return new GenericResponse("User created successfully");

    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleValidationError(MethodArgumentNotValidException exception) {
//        ApiError error = new ApiError(400, "Validation error", "/api/1.0/users");
//        Map<String, String> validationErrors = new HashMap<>();
//        exception.getBindingResult().getFieldErrors().forEach(fe -> validationErrors.put(fe.getField(), fe.getDefaultMessage()));
//        error.setValidationErrors(validationErrors);
//        log.info(validationErrors);
//        return error;
//    }


    @GetMapping("/users")
    Page<UserVM> getUsers(Pageable page, @CurrentUser User user) {
        return userService.getUsers(page, user).map(UserVM::new);
    }

    @GetMapping("/users/{username}")
    UserVM getUser(@PathVariable String username) {
        User user = userService.getByUsername(username);
        return new UserVM(user);
    }

    @PutMapping("/users/{username}")
    @PreAuthorize(("#username == principal.username"))
    UserVM updateUser(@Valid @RequestBody UserUpdateVM updatedUser, @PathVariable String username) {
        User user = userService.updateUser(username, updatedUser);
        return new UserVM(user);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize(("#username == principal.username"))
    GenericResponse deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new GenericResponse("User removed");
    }
}
