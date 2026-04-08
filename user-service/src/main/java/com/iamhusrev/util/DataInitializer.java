package com.iamhusrev.util;

import com.iamhusrev.entity.Role;
import com.iamhusrev.entity.User;
import com.iamhusrev.enums.Gender;
import com.iamhusrev.repository.RoleRepository;
import com.iamhusrev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(role("Admin"));
            roleRepository.save(role("Manager"));
            roleRepository.save(role("Employee"));
            log.info("Roles seeded.");
        }
    }

    private void seedAdminUser() {
        if (userRepository.findByUserName("admin") == null) {
            Role adminRole = roleRepository.findByDescription("Admin");
            if (adminRole == null) return;

            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setUserName("admin");
            admin.setPassWord(passwordEncoder.encode("Admin1234"));
            admin.setEnabled(true);
            admin.setPhone("0000000000");
            admin.setGender(Gender.MALE);
            admin.setRole(adminRole);
            userRepository.save(admin);
            log.info("Admin user created. userName=admin passWord=Admin1234");
        }
    }

    private Role role(String description) {
        Role r = new Role();
        r.setDescription(description);
        return r;
    }
}
