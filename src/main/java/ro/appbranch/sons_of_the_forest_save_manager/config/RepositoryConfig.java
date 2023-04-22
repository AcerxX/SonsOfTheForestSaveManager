package ro.appbranch.sons_of_the_forest_save_manager.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.persistence.EntityManager;

@Component
public class RepositoryConfig implements RepositoryRestConfigurer {

    private final EntityManager entityManager;

    public RepositoryConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        entityManager.getMetamodel().getEntities().forEach(entity -> config.exposeIdsFor(entity.getJavaType()));

        // This will return all relationships in all entities
        config.setRepositoryDetectionStrategy(RepositoryDetectionStrategy.RepositoryDetectionStrategies.ALL);
        config.setExposeRepositoryMethodsByDefault(true);
    }
}
