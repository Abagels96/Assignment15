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
        
        // Check if Railway provides DATABASE_URL or MYSQL_URL (mysql:// format) instead of JDBC_DATABASE_URL
        String railwayDatabaseUrl = environment.getProperty("DATABASE_URL", "");
        String mysqlUrl = environment.getProperty("MYSQL_URL", "");
        String jdbcDatabaseUrl = environment.getProperty("JDBC_DATABASE_URL", "");
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");
        
        // Convert Railway's mysql:// URL to JDBC format if needed
        if (!railwayDatabaseUrl.isEmpty() && jdbcDatabaseUrl.isEmpty() && 
            railwayDatabaseUrl.startsWith("mysql://")) {
            DatabaseConnectionInfo dbInfo = parseMysqlUrl(railwayDatabaseUrl);
            Map<String, Object> urlProps = new HashMap<>();
            urlProps.put("spring.datasource.url", dbInfo.jdbcUrl);
            if (dbInfo.username != null && !dbInfo.username.isEmpty()) {
                urlProps.put("spring.datasource.username", dbInfo.username);
            }
            if (dbInfo.password != null && !dbInfo.password.isEmpty()) {
                urlProps.put("spring.datasource.password", dbInfo.password);
            }
            environment.getPropertySources().addFirst(
                new org.springframework.core.env.MapPropertySource("railway-database-url-conversion", urlProps));
            datasourceUrl = dbInfo.jdbcUrl;
        } else if (!mysqlUrl.isEmpty() && jdbcDatabaseUrl.isEmpty() && 
                   mysqlUrl.startsWith("mysql://")) {
            DatabaseConnectionInfo dbInfo = parseMysqlUrl(mysqlUrl);
            Map<String, Object> urlProps = new HashMap<>();
            urlProps.put("spring.datasource.url", dbInfo.jdbcUrl);
            if (dbInfo.username != null && !dbInfo.username.isEmpty()) {
                urlProps.put("spring.datasource.username", dbInfo.username);
            }
            if (dbInfo.password != null && !dbInfo.password.isEmpty()) {
                urlProps.put("spring.datasource.password", dbInfo.password);
            }
            environment.getPropertySources().addFirst(
                new org.springframework.core.env.MapPropertySource("railway-database-url-conversion", urlProps));
            datasourceUrl = dbInfo.jdbcUrl;
        }
        
        // Always ensure MySQL dialect and driver are set for Spring Session JDBC
        Map<String, Object> props = new HashMap<>();
        props.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");
        props.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        environment.getPropertySources().addFirst(
            new org.springframework.core.env.MapPropertySource("database-platform-override", props));
    }
    
    /**
     * Parses Railway's mysql:// URL format and extracts connection info
     * Example: mysql://user:pass@host:port/dbname
     */
    private DatabaseConnectionInfo parseMysqlUrl(String mysqlUrl) {
        try {
            // Remove mysql:// prefix
            String url = mysqlUrl.replace("mysql://", "");
            
            // Parse: user:password@host:port/database
            int atIndex = url.indexOf('@');
            String username = "";
            String password = "";
            String hostAndDb = url;
            
            if (atIndex != -1) {
                String credentials = url.substring(0, atIndex);
                hostAndDb = url.substring(atIndex + 1);
                
                // Split credentials (handle URL-encoded passwords)
                int colonIndex = credentials.indexOf(':');
                if (colonIndex > 0) {
                    username = credentials.substring(0, colonIndex);
                    password = credentials.substring(colonIndex + 1);
                } else {
                    username = credentials;
                }
            }
            
            // Build JDBC URL (credentials go in separate properties, not in URL)
            String jdbcUrl = "jdbc:mysql://" + hostAndDb;
            
            return new DatabaseConnectionInfo(jdbcUrl, username, password);
        } catch (Exception e) {
            // If parsing fails, return basic JDBC URL
            String jdbcUrl = mysqlUrl.startsWith("jdbc:") ? mysqlUrl : "jdbc:mysql://" + mysqlUrl.replace("mysql://", "");
            return new DatabaseConnectionInfo(jdbcUrl, "", "");
        }
    }
    
    /**
     * Helper class to hold database connection information
     */
    private static class DatabaseConnectionInfo {
        final String jdbcUrl;
        final String username;
        final String password;
        
        DatabaseConnectionInfo(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }
    }
}

