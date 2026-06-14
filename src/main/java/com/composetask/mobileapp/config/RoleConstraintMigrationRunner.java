package com.composetask.mobileapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

    @Autowired
    public RoleConstraintMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("RoleConstraintMigrationRunner: applying role constraint migration (if needed)");

        try {
            // Drop existing constraint if present and recreate allowing VENDOR and CUSTOMER
            String drop = "ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check";
            String add = "ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('USER','ADMIN','VENDOR','CUSTOMER'))";

            jdbcTemplate.execute(drop);
            jdbcTemplate.execute(add);

            log.info("RoleConstraintMigrationRunner: applied users_role_check constraint to include VENDOR and CUSTOMER");
        } catch (Exception ex) {
            log.error("RoleConstraintMigrationRunner: failed to apply constraint migration", ex);
            // Re-throwing would stop the app; log and continue so the application can start.
        }
    }
}

