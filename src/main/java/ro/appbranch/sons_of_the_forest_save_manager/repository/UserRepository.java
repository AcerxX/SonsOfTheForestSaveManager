package ro.appbranch.sons_of_the_forest_save_manager.repository;

import org.springframework.data.repository.CrudRepository;

import ro.appbranch.sons_of_the_forest_save_manager.entity.User;


public interface UserRepository extends CrudRepository<User, Long> {
    User findTopByUsername(String username);
}


