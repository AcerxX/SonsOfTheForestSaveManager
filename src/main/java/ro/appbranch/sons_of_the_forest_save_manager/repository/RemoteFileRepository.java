package ro.appbranch.sons_of_the_forest_save_manager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ro.appbranch.sons_of_the_forest_save_manager.entity.RemoteFile;

public interface RemoteFileRepository extends CrudRepository<RemoteFile, Integer> {
    RemoteFile findTopByFolderIdAndFileName(String folderId, String fileName);
    List<RemoteFile> findAllByFolderId(String folderId);
}
