package org.ck.ds.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ck.ds.entities.Role;
import org.ck.ds.entities.User;
import org.ck.ds.entities.UserLoginRequest;
import org.ck.ds.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    private final HttpServletRequest request;


    private final HttpServletResponse response;

    public UserController(UserService userService, HttpServletRequest request, HttpServletResponse response) {
        this.userService = userService;
        this.request = request;
        this.response = response;
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest.getAmka(), userLoginRequest.getPassword(), request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser() {
        return userService.logout(request, response);
    }


    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.viewUsers();
    }


    @GetMapping("/viewUser/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/viewUserByAmka/{amka}")
    public ResponseEntity<User> getUserByAmka(@PathVariable int amka) {
        User user = userService.findUserByAmka(amka);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/createUser")
    public User createUser(@RequestBody User user, @RequestParam String userType) {
        return userService.createUser(user, userType);
    }


    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user, @RequestParam String newUserType) {
        User updatedUser = userService.updateUser(id, user, newUserType);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}