package com.hoaxify.ws.hoax;

import com.hoaxify.ws.hoax.vm.HoaxSubmitVM;
import com.hoaxify.ws.hoax.vm.HoaxVM;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.shared.CurrentUser;
import com.hoaxify.ws.shared.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class HoaxController {

    private final HoaxService hoaxService;

    @PostMapping("/api/1.0/hoaxes")
    GenericResponse saveHoax(@Valid @RequestBody HoaxSubmitVM hoax, @CurrentUser User user) {
        hoaxService.save(hoax, user);
        return new GenericResponse("Hoax is saved");
    }

    @GetMapping("/api/1.0/hoaxes")
    Page<HoaxVM> getHoaxes(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable page) {
        return hoaxService.getHoaxes(page).map(HoaxVM::new);
    }

    @GetMapping({"/api/1.0/hoaxes/{id:[0-9]+}", "/api/1.0/users/{username}/hoaxes/{id:[0-9]+}"})
    ResponseEntity<?> getRelativeHoaxes(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable page,
                                        @PathVariable long id,
                                        @PathVariable(required = false) String username,
                                        @RequestParam(name = "count", required = false, defaultValue = "false") boolean count,
                                        @RequestParam(name = "direction", defaultValue = "before") String direction) {
        if (count) {
            long newHoaxCount = hoaxService.getNewHoaxesCount(id, username);
            Map<String, Long> resp = new HashMap<>();
            resp.put("count", newHoaxCount);
            return ResponseEntity.ok(resp);
        }

        if ((direction.equals("after"))) {
            List<HoaxVM> newHoaxes = hoaxService.getNewHoaxes(id, username, page.getSort())
                    .stream().map(HoaxVM::new).collect(Collectors.toList());
            return ResponseEntity.ok(newHoaxes);
        }
        return ResponseEntity.ok(hoaxService.getOldHoaxes(id, username, page).map(HoaxVM::new));
    }

    @GetMapping("/api/1.0/users/{username}/hoaxes")
    Page<HoaxVM> getUserHoaxes(@PathVariable String username, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable page) {
        return hoaxService.getHoaxesByUser(username, page).map(HoaxVM::new);
    }

    @DeleteMapping("/api/1.0/hoaxes/{id:[0-9]+}")
    @PreAuthorize("@hoaxSecurityService.isAllowedToDelete(#id, principal)")
    GenericResponse deleteHoax(@PathVariable long id) {
        hoaxService.delete(id);
        return new GenericResponse("Hoax removed!");
    }

/*    @GetMapping()
    ResponseEntity<?> getRelativeUserHoaxes(@PathVariable long id, @PathVariable String username, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable page,
                                            @RequestParam(name = "count", required = false, defaultValue = "false") boolean count,
                                            @RequestParam(name = "direction", defaultValue = "before") String direction) {
        if (count) {
            long newHoaxCount = hoaxService.getNewHoaxesCountByUser(id, username);
            Map<String, Long> response = new HashMap<>();
            response.put("count", newHoaxCount);
            return ResponseEntity.ok(response);
        }

        if ((direction.equals("after"))) {
            List<HoaxVM> newHoaxes = hoaxService.getNewHoaxesByUser(id, username, page.getSort())
                    .stream().map(HoaxVM::new).collect(Collectors.toList());
            return ResponseEntity.ok(newHoaxes);
        }

        return ResponseEntity.ok(hoaxService.getOldHoaxesByUser(id, username, page).map(HoaxVM::new));
    }*/
}
