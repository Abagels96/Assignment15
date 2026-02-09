package com.coderscampus.Assignment15.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Auto-detects database platform from JDBC URL and sets it early in the application lifecycle.
 * This is required for Spring Session JDBC to correctly determine the database driver
 * when initializing session tables.
 */
public class DatabasePlatformInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Get the datasource URL from environment
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");
        String databasePlatform = environment.getProperty("spring.jpa.database-platform", "");
        
        // If database platform is not explicitly set, auto-detect from URL
        if (databasePlatform == null || databasePlatform.isEmpty()) {
            if (datasourceUrl != null && !datasourceUrl.isEmpty()) {
                if (datasourceUrl.contains("postgresql") || datasourceUrl.contains("postgres")) {
                    // Set in environment properties so it overrides application.properties
                    environment.getPropertySources().addFirst(
                        new org.springframework.core.env.MapPropertySource("database-platform-override",
                            java.util.Map.of("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect")));
                } else if (datasourceUrl.contains("mysql")) {
                    // Set MySQL dialect if not already set
                    environment.getPropertySources().addFirst(
                        new org.springframework.core.env.MapPropertySource("database-platform-override",
                            java.util.Map.of("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect")));
                }
            }
        }
    }
}

