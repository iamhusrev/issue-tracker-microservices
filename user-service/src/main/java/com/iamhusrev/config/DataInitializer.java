package com.iamhusrev.config;

import com.iamhusrev.entity.Organization;
import com.iamhusrev.entity.Role;
import com.iamhusrev.entity.User;
import com.iamhusrev.enums.Gender;
import com.iamhusrev.repository.OrganizationRepository;
import com.iamhusrev.repository.RoleRepository;
import com.iamhusrev.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Seed default roles
        Role adminRole = seedRole("Admin");
        seedRole("Manager");
        seedRole("Employee");

        // Seed default organization
        Organization defaultOrg = organizationRepository.findByName("Default")
                .orElseGet(() -> {
                    Organization org = new Organization();
                    org.setName("Default");
                    org.setDescription("Default organization");
                    Organization saved = organizationRepository.save(org);
                    saved.setOrganizationId(saved.getId());
                    return organizationRepository.save(saved);
                });

        // Backfill organization_id for existing rows with NULL
        backfillOrganizationId("users", defaultOrg.getId());
        backfillOrganizationId("projects", defaultOrg.getId());
        backfillOrganizationId("tasks", defaultOrg.getId());
        backfillOrganizationId("roles", defaultOrg.getId());

        // Seed default admin user
        seedAdminUser(adminRole, defaultOrg.getId());

        log.info("Data initialization completed. Default organization ID: {}", defaultOrg.getId());
    }

    private Role seedRole(String description) {
        Role existing = roleRepository.findByDescription(description);
        if (existing != null) {
            return existing;
        }
        Role role = new Role(description);
        Role saved = roleRepository.save(role);
        log.info("Seeded role: {}", description);
        return saved;
    }

    private void seedAdminUser(Role adminRole, Long orgId) {
        if (userRepository.findByUserName("admin") != null) {
            return;
        }

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setUserName("admin");
        admin.setPassWord(passwordEncoder.encode("admin123"));
        admin.setEnabled(true);
        admin.setPhone(null);
        admin.setGender(Gender.MALE);
        admin.setRole(adminRole);
        admin.setOrganizationId(orgId);
        admin.setInsertDateTime(LocalDateTime.now());

        userRepository.save(admin);
        log.info("Seeded default admin user — username: admin, password: admin123");
    }

    private void backfillOrganizationId(String tableName, Long orgId) {
        Query query = entityManager.createNativeQuery(
                "UPDATE " + tableName + " SET organization_id = ?1 WHERE organization_id IS NULL");
        query.setParameter(1, orgId);
        int updated = query.executeUpdate();
        if (updated > 0) {
            log.info("Backfilled {} rows in {} with organization_id={}", updated, tableName, orgId);
        }
    }
}
