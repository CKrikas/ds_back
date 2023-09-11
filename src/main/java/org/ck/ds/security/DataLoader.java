package org.ck.ds.security;


import org.ck.ds.entities.Role;
import org.ck.ds.entities.User;
import org.ck.ds.repositories.RoleRepository;
import org.ck.ds.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class DataLoader {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // Hardcoded dummy values for testing
    private Integer adminUsername = 12345;
    private String adminPassword = "testPassword";

    public DataLoader(RoleRepository roleRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void loadData() {
        // Define roles
        List<String> roles = Arrays.asList("ADMIN", "NOTARY", "SPOUSE", "LAWYER");

        // Save roles in the database
        for (String role : roles) {
            if (roleRepository.findByName(role) == null) { // Check if role already exists
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }

        // Create an admin entity if not exists
        if (userRepository.findByAmka(adminUsername) == null) {
            User admin = new User();
            admin.setAmka(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ADMIN"))));
            userRepository.save(admin);
        }
    }
}