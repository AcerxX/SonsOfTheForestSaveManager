package ro.appbranch.sons_of_the_forest_save_manager.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import jakarta.annotation.Nullable;
import lombok.extern.java.Log;

@Log
public class UserService {
    private static final String FILE_NAME = "UserInfo.cfg";

    private UserService() {}

    public static void saveUserNameToLocalDisk(String username) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), FILE_NAME);
            
            Files.write(filePath, username.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error writing to file: " + e.getMessage(), e);
        }
    }


    @Nullable
    public static String getUserNameFromLocalDisk() {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), FILE_NAME);

            if (Files.exists(filePath)) {
                return new String(Files.readAllBytes(filePath));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading from file: " + e.getMessage(), e);
        }

        return null;
    }
}
