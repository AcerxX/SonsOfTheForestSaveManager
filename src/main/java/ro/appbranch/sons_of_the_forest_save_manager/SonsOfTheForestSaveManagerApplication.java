package ro.appbranch.sons_of_the_forest_save_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SonsOfTheForestSaveManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SonsOfTheForestSaveManagerApplication.class, args);
	}
}
