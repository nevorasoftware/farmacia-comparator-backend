package com.luppo.farmacia.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/farmacia_db}")
    private String defaultUrl;

    @Value("${spring.datasource.username:postgres}")
    private String defaultUsername;

    @Value("${spring.datasource.password:postgrespassword}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        String databaseUrl = System.getenv("DATABASE_URL");
        String springDatasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        String pgHost = System.getenv("PGHOST");

        if (springDatasourceUrl != null && !springDatasourceUrl.isBlank()) {
            log.info("Using SPRING_DATASOURCE_URL configuration");
            config.setJdbcUrl(springDatasourceUrl);
            config.setUsername(System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", defaultUsername));
            config.setPassword(System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", defaultPassword));
        } else if (databaseUrl != null && !databaseUrl.isBlank() && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            log.info("Detected Railway/Heroku DATABASE_URL. Parsing connection details...");
            try {
                // Replace protocol for standard URI parsing
                String cleanUrl = databaseUrl.replace("postgresql://", "http://").replace("postgres://", "http://");
                URI uri = new URI(cleanUrl);
                
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // /dbname
                String userInfo = uri.getUserInfo();

                String username = (userInfo != null && userInfo.contains(":")) ? userInfo.split(":")[0] : "postgres";
                String password = (userInfo != null && userInfo.contains(":")) ? userInfo.split(":", 2)[1] : "";

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                log.info("Configured JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL, falling back to defaultUrl: {}", e.getMessage());
                config.setJdbcUrl(defaultUrl);
                config.setUsername(defaultUsername);
                config.setPassword(defaultPassword);
            }
        } else if (pgHost != null && !pgHost.isBlank()) {
            String pgPort = System.getenv().getOrDefault("PGPORT", "5432");
            String pgDb = System.getenv().getOrDefault("PGDATABASE", "railway");
            String pgUser = System.getenv().getOrDefault("PGUSER", "postgres");
            String pgPass = System.getenv().getOrDefault("PGPASSWORD", "");

            String jdbcUrl = "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDb;
            log.info("Detected Railway PGHOST. Configured JDBC URL: {}", jdbcUrl);
            
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(pgUser);
            config.setPassword(pgPass);
        } else {
            log.info("Using default datasource configuration: {}", defaultUrl);
            config.setJdbcUrl(defaultUrl);
            config.setUsername(defaultUsername);
            config.setPassword(defaultPassword);
        }

        if (config.getJdbcUrl() != null && config.getJdbcUrl().startsWith("jdbc:h2:")) {
            config.setDriverClassName("org.h2.Driver");
        } else {
            config.setDriverClassName("org.postgresql.Driver");
        }
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        return new HikariDataSource(config);
    }
}
