package org.ck.ds.security;

import org.ck.ds.entities.User;
import org.ck.ds.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            int amka = Integer.parseInt(username);
            User user = userRepository.findByAmka(amka);
            if (user != null) {
                return user;
            }
        } catch (NumberFormatException e) {
            // Handle the exception if the username is not an integer (AMKA)
        }

        throw new UsernameNotFoundException("User not found");
    }
}