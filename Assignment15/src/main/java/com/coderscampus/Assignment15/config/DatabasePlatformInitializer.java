package com.coderscampus.Assignment15.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import java.util.HashMap;
import java.util.Map;

/**
 * Auto-detects MySQL database platform from JDBC URL and sets it early in the application lifecycle.
 * This is required for Spring Session JDBC to correctly determine the database driver
 * when initializing session tables.
 */
public class DatabasePlatformInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Get the datasource URL from environment
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");
        
        // Get current platform and driver settings
        String databasePlatform = environment.getProperty("spring.jpa.database-platform", "");
        String driverClassName = environment.getProperty("spring.datasource.driver-class-name", "");
        
        // If database platform and driver are not explicitly set, auto-detect from URL
        if ((databasePlatform == null || databasePlatform.isEmpty()) && 
            (driverClassName == null || driverClassName.isEmpty())) {
            if (datasourceUrl != null && !datasourceUrl.isEmpty()) {
                if (datasourceUrl.contains("mysql")) {
                    // Set MySQL dialect and driver for Spring Session JDBC
                    Map<String, Object> props = new HashMap<>();
                    props.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");
                    props.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
                    environment.getPropertySources().addFirst(
                        new org.springframework.core.env.MapPropertySource("database-platform-override", props));
                }
            }
        }
    }
}

