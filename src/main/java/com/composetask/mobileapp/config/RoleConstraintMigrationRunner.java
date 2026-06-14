package com.composetask.mobileapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * Small application runner that applies a safe ALTER TABLE migration to extend the
 * users_role_check constraint to include VENDOR and CUSTOMER. This is an opt-in
 * replacement for Flyway in environments where Flyway fails to initialize.
 */
@Component
@ConditionalOnProperty(name = "app.migration.apply-role-constraint", havingValue = "true")
public class RoleConstraintMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleConstraintMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final boolean flywayEnabled;

    @Autowired
    public RoleConstraintMigrationRunner(JdbcTemplate jdbcTemplate, @Value("${spring.flyway.enabled:false}") boolean flywayEnabled) {
        this.jdbcTemplate = jdbcTemplate;
        this.flywayEnabled = flywayEnabled;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("RoleConstraintMigrationRunner: starting (app.migration.apply-role-constraint=true)");

        if (flywayEnabled) {
            log.info("RoleConstraintMigrationRunner: skipping because Flyway is enabled (spring.flyway.enabled=true)");
            return;
        }

        try {
            // Drop existing constraint if present and recreate allowing VENDOR and CUSTOMER
            String drop = "ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check";
            String add = "ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('USER','ADMIN','VENDOR','CUSTOMER'))";

            jdbcTemplate.execute(drop);
            jdbcTemplate.execute(add);

            log.info("RoleConstraintMigrationRunner: applied users_role_check constraint to include VENDOR and CUSTOMER");
        } catch (Exception ex) {
            log.error("RoleConstraintMigrationRunner: failed to apply constraint migration", ex);
            // Do not re-throw: allow application to start even if this runner fails
        }
    }
}

