package ro.appbranch.sons_of_the_forest_save_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import ro.appbranch.sons_of_the_forest_save_manager.service.UserService;

@RestController
@Log
@AllArgsConstructor
public class UserController {
    @GetMapping("/user/login/{username}")
    public String saveChosenUserName(@PathVariable String username) {
        
        UserService.saveUserNameToLocalDisk(username);

        log.info("New login: " + username);

        return "OK";
    }

    @GetMapping("/user/info")
    public String getLoggedUser() {
        return UserService.getUserNameFromLocalDisk();
    }
}
