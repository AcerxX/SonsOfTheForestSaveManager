package ro.appbranch.sons_of_the_forest_save_manager.scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import ro.appbranch.sons_of_the_forest_save_manager.entity.RemoteFile;
import ro.appbranch.sons_of_the_forest_save_manager.entity.User;
import ro.appbranch.sons_of_the_forest_save_manager.repository.RemoteFileRepository;
import ro.appbranch.sons_of_the_forest_save_manager.repository.UserRepository;
import ro.appbranch.sons_of_the_forest_save_manager.service.UserService;
import ro.appbranch.sons_of_the_forest_save_manager.util.FileUtils;

@Component
@Log
@AllArgsConstructor
public class SaveSynchronizer {
    private static final String FILE_TIMESTAMP_KEY = "timestamp";
    private static final String FILE_CONTENT_KEY = "content";
    private static final String FILE_PATH_KEY = "path";

    private static final String BASE_GAME_PATH = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "LocalLow"
            + File.separator + "Endnight"
            + File.separator + "SonsOfTheForest"
            + File.separator + "Saves";

    private UserRepository usersRepository;
    private RemoteFileRepository remoteFileRepository;

    private String getFullGamePath() {
        File savesDir = new File(BASE_GAME_PATH);

        if (!savesDir.exists() || !savesDir.isDirectory()) {
            log.severe("Saves directory not found or not a directory");
            throw new RuntimeException("Saves directory not found or not a directory");
        }

        File[] subdirectories = savesDir.listFiles(File::isDirectory);

        if (subdirectories == null || subdirectories.length == 0) {
            log.severe("No subdirectories found in the Saves directory");
            throw new RuntimeException("No subdirectories found in the Saves directory");
        }

        return subdirectories[0].getAbsolutePath();
    }

    private String getHostPath() {
        return getFullGamePath()
                + File.separator + "Multiplayer"
                + File.separator;
    }

    private String getClientPath() {
        return getFullGamePath()
                + File.separator + "MultiplayerClient"
                + File.separator;
    }

    @Scheduled(fixedRate = 10000)
    public void uploadNewLocalFiles() {
        var username = UserService.getUserNameFromLocalDisk();

        if (username == null) {
            log.warning(
                    "Sync will not work until you login! Please open the browser at http://localhost:12345 to login!");

            return;
        }

        User user = usersRepository.findTopByUsername(username);

        if (user == null) {
            log.log(Level.SEVERE, "Your user does not exist!");
            return;
        }

        parseHostFolder(user);
        parseClientFolder(user);
    }

    private void parseHostFolder(User user) {
        if (user.getHostId() == null) {
            log.log(Level.SEVERE, "Setup incomplete (HOST)!");
            return;
        }

        Map<String, Map<String, String>> localFiles = FileUtils.getLocalFilesMap(getHostPath() + user.getHostId());

        checkHostFilesForUpload(user, localFiles);

        checkClientFilesForUpload(user, localFiles);
    }

    private static boolean compareStringsCharacterByCharacter(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        for (int i = 0; i < minLength; i++) {
            char c1 = str1.charAt(i);
            char c2 = str2.charAt(i);
            if (c1 != c2) {
                return false;
            }
        }

        if (str1.length() != str2.length()) {
            return false;
        }

        return true;
    }

    private void checkHostFilesForUpload(User user, Map<String, Map<String, String>> localFiles) {
        localFiles
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().startsWith("Player") && !entry.getKey().startsWith("Hotkeys"))
                .forEach(entry -> {
                    String fileName = entry.getKey();
                    Map<String, String> fileInfo = entry.getValue();

                    String fileContent = fileInfo.get(FILE_CONTENT_KEY);
                    Long fileTimestamp = Long.parseLong(fileInfo.get(FILE_TIMESTAMP_KEY));

                    RemoteFile remoteFile = remoteFileRepository.findTopByFolderIdAndFileName(user.getHostId(),
                            fileName);

                    if (remoteFile == null) {
                        log.info("Uploading first HOST FILE " + fileName);

                        var newFile = RemoteFile
                                .builder()
                                .folderId(user.getHostId())
                                .fileName(fileName)
                                .fileContent(fileContent)
                                .fileTimestamp(fileTimestamp)
                                .build();

                        remoteFileRepository.save(newFile);
                    } else if (!compareStringsCharacterByCharacter(remoteFile.getFileContent(), fileContent)
                            && remoteFile.getFileTimestamp() < fileTimestamp) {
                        log.info("Uploading new HOST FILE " + fileName);

                        remoteFile.setFileContent(fileContent);

                        remoteFileRepository.save(remoteFile);
                    }
                });
    }

    private void parseClientFolder(User user) {
        if (user.getClientId() == null) {
            log.log(Level.SEVERE, "Setup incomplete (CLIENT)!");
            return;
        }

        Map<String, Map<String, String>> localFiles = FileUtils.getLocalFilesMap(getClientPath() + user.getClientId());

        checkClientFilesForUpload(user, localFiles);
    }

    private void checkClientFilesForUpload(User user, Map<String, Map<String, String>> localFiles) {
        localFiles
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("Player") || entry.getKey().startsWith("Hotkeys"))
                .forEach(entry -> {
                    String fileName = entry.getKey();
                    Map<String, String> fileInfo = entry.getValue();

                    String fileContent = fileInfo.get(FILE_CONTENT_KEY);
                    Long fileTimestamp = Long.parseLong(fileInfo.get(FILE_TIMESTAMP_KEY));

                    RemoteFile remoteFile = remoteFileRepository.findTopByFolderIdAndFileName(user.getClientId(),
                            fileName);

                    if (remoteFile == null) {
                        log.info("Uploading first CLIENT FILE " + fileName);

                        var newFile = RemoteFile
                                .builder()
                                .folderId(user.getClientId())
                                .fileName(fileName)
                                .fileContent(fileContent)
                                .fileTimestamp(fileTimestamp).build();

                        remoteFileRepository.save(newFile);
                    } else if (!compareStringsCharacterByCharacter(remoteFile.getFileContent(), fileContent)
                            && remoteFile.getFileTimestamp() < fileTimestamp) {
                        log.info("Uploading new CLIENT FILE " + fileName);

                        remoteFile.setFileContent(fileContent);

                        remoteFileRepository.save(remoteFile);
                    }
                });
    }

    @Scheduled(fixedRate = 10000)
    public void downloadNewHostFiles() {
        var username = UserService.getUserNameFromLocalDisk();

        if (username == null) {
            log.warning(
                    "Sync will not work until you login! Please open the browser at http://localhost:12345 to login!");
            return;
        }

        User user = usersRepository.findTopByUsername(username);

        if (user == null) {
            log.log(Level.SEVERE, "Your user does not exist! Contact AcerX!");
            return;
        }

        String userHostFolderPath = getHostPath() + user.getHostId();
        File hostFolder = new File(userHostFolderPath);

        if (!hostFolder.exists() && !hostFolder.mkdirs()) {
            log.log(Level.SEVERE, "Failed to create folder " + userHostFolderPath);
            return;
        }

        String userClientFolderPath = getClientPath() + user.getClientId();
        File clientFolder = new File(userClientFolderPath);

        if (!clientFolder.exists() && !clientFolder.mkdirs()) {
            log.log(Level.SEVERE, "Failed to create folder " + userClientFolderPath);
            return;
        }

        List<Map<String, String>> filesToUpdate = new ArrayList<>();
        filesToUpdate.addAll(getUpdatedFiles(userHostFolderPath, user.getHostId()));
        filesToUpdate.addAll(getUpdatedFiles(userHostFolderPath, user.getClientId()));
        filesToUpdate.addAll(getUpdatedFiles(userClientFolderPath, user.getClientId()));

        if (!filesToUpdate.isEmpty()) {
            try {
                String archivePath = Paths
                        .get(System.getProperty("user.dir"), "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".zip")
                        .toString();
                FileUtils.createZipArchive(getFullGamePath(), archivePath);

                filesToUpdate.forEach(filesMap -> FileUtils.writeToFile(
                        filesMap.get(FILE_PATH_KEY),
                        filesMap.get(FILE_CONTENT_KEY),
                        Long.parseLong(filesMap.get(FILE_TIMESTAMP_KEY))));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Update stopped. Failed to create save backup: " + e.getMessage(), e);
            }
        }
    }

    private List<Map<String, String>> getUpdatedFiles(String folderPath, String folderId) {
        List<Map<String, String>> filesToUpdate = new ArrayList<>();

        List<RemoteFile> remoteFiles = remoteFileRepository.findAllByFolderId(folderId);
        Map<String, Map<String, String>> localFiles = FileUtils.getLocalFilesMap(folderPath);

        remoteFiles.forEach(remoteHostFile -> {
            String fileName = remoteHostFile.getFileName();
            String remoteFileContent = remoteHostFile.getFileContent();
            Long remoteFileTimestamp = remoteHostFile.getFileTimestamp();

            Map<String, String> localFileInfo = localFiles.get(fileName);
            boolean shouldDownloadFile = false;

            if (localFileInfo == null) {
                log.info("Downloading new file " + fileName);
                shouldDownloadFile = true;
            } else {
                String localFileContent = localFileInfo.get(FILE_CONTENT_KEY);
                Long localFileTimestamp = Long.parseLong(localFileInfo.get(FILE_TIMESTAMP_KEY));

                if (!localFileContent.equals(remoteFileContent) && localFileTimestamp < remoteFileTimestamp) {
                    log.info("Downloading updated file " + fileName);
                    shouldDownloadFile = true;
                }
            }

            if (shouldDownloadFile) {
                String filePath = folderPath + File.separator + fileName;

                Map<String, String> tempMap = Map.of(FILE_PATH_KEY, filePath, FILE_CONTENT_KEY, remoteFileContent,
                        FILE_TIMESTAMP_KEY, remoteFileTimestamp.toString());

                filesToUpdate.add(tempMap);
            }
        });

        return filesToUpdate;
    }

}
