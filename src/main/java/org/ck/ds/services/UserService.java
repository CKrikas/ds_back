package org.ck.ds.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ck.ds.entities.Role;
import org.ck.ds.entities.User;
import org.ck.ds.repositories.FormRepository;
import org.ck.ds.repositories.RoleRepository;
import org.ck.ds.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final RoleRepository roleRepository;

    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public UserService(UserRepository userRepository, FormRepository formRepository,
                       PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider,
                       RoleRepository roleRepository, SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.formRepository = formRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.roleRepository = roleRepository;
        this.securityContextRepository = securityContextRepository;
    }


    public User login(int amka, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(String.valueOf(amka), password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
        return userRepository.findByAmka(amka);
    }

    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return null;
    }


    // Admin function to create a new user
    public User createUser(User user, String userType) {
        if (userRepository.findByAmka(user.getAmka()) != null) {
            throw new IllegalArgumentException("A user with this AMKA already exists.");
        }

        if ("ADMIN".equalsIgnoreCase(userType)) {
            throw new IllegalArgumentException("Cannot create an ADMIN user.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set role based on userType
        Role role = roleRepository.findByName(userType.toUpperCase());
        if (role != null) {
            user.setRoles(new HashSet<>(Collections.singletonList(role)));
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        return userRepository.save(user);
    }

    // Admin function to update an existing user
    public User updateUser(int userId, User userDetails, String newUserType) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if ("ADMIN".equalsIgnoreCase(newUserType)) {
                throw new IllegalArgumentException("Cannot update to an ADMIN role.");
            }
            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setAmka(userDetails.getAmka());
            user.setAfm(userDetails.getAfm());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));  // Encrypt the password
            // Update role
            Role role = roleRepository.findByName(newUserType.toUpperCase());
            if (role != null) {
                user.setRoles(new HashSet<>(Collections.singletonList(role)));
            } else {
                throw new IllegalArgumentException("Invalid new user type");
            }

            return userRepository.save(user);
        }
        return null; // Or throw an exception if the user is not found
    }

    // Admin function to delete a user
    public boolean deleteUser(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        } else {
            return false;
        }
    }

    public List<User> viewUsers() {
        List<User> allUsers = userRepository.findAll();
        // Filter out users with the "ADMIN" role
        return allUsers.stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN")))
                .collect(Collectors.toList());
    }

    public Optional<User> findUserById(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent() && userOptional.get().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            return Optional.empty();  // Or throw an exception if preferred
        }

        return userOptional;
    }

    public User findUserByAmka(int amka) {
        User user = userRepository.findByAmka(amka);

        if (user != null && user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            return null;  // Or throw an exception if preferred
        }

        return user;
    }
}
