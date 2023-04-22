package ro.appbranch.sons_of_the_forest_save_manager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.java.Log;

@Log
public final class FileUtils {
    private FileUtils() {
    }

    public static Map<String, Map<String, String>> getLocalFilesMap(String folderPath) {
        Map<String, Map<String, String>> fileContentMap = new HashMap<>();
        Path path = Paths.get(folderPath);

        if (Files.exists(path) && Files.isDirectory(path)) {
            var folder = new File(folderPath);
            var listOfFiles = List.of(folder.listFiles());

            listOfFiles
                    .stream()
                    .filter(File::isFile)
                    .filter(file -> !file.getName().toLowerCase().endsWith(".png"))
                    .forEach(file -> {
                        try {
                            Map<String, String> temp = new HashMap<>();

                            // Get Content
                            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                            temp.put("content", content);

                            // Get timestamp
                            temp.put("timestamp", String.valueOf(file.lastModified()));

                            // Get name
                            temp.put("name", file.getName());

                            fileContentMap.put(file.getName(), temp);
                        } catch (IOException e) {
                            log.log(Level.SEVERE, "Error reading file: " + file.getName() + ", " + e.getMessage(), e);
                        }
                    });
        }

        return fileContentMap;
    }

    public static void writeToFile(String path, String content, Long timestamp) {
        try {
            Path filePath = Paths.get(path);
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE);
    
            BasicFileAttributeView attributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
            FileTime newFileTime = FileTime.fromMillis(timestamp);
            attributes.setTimes(newFileTime, null, null);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error writing to file: " + e.getMessage(), e);
        }
    }
    

    public static void createZipArchive(String sourceDirectory, String destinationFile) throws IOException {
        Path sourcePath = Paths.get(sourceDirectory);

        try (FileOutputStream fos = new FileOutputStream(destinationFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(file).toString());
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!sourcePath.equals(dir)) {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(dir).toString() + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
