package org.ck.ds.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;
    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService, SecurityContextRepository securityContextRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/*/error").permitAll()
                        .requestMatchers("/*/login").permitAll()
                        .requestMatchers("/*/logout").permitAll()
                        .requestMatchers("/*/user").hasRole("ADMIN")
                        .requestMatchers("/*/user/all").hasAnyRole("ADMIN", "LAWYER", "NOTARY", "SPOUSE")
                        .requestMatchers("/*/user/viewUser/**").hasAnyRole("ADMIN")
                        .requestMatchers("/*/user/viewUserByAmka/**").hasAnyRole("ADMIN", "LAWYER")
                        .requestMatchers("/*/forms/all").hasAnyRole("LAWYER", "NOTARY", "SPOUSE")
                        .requestMatchers("/*/forms/all/**").hasAnyRole("LAWYER", "NOTARY", "SPOUSE")
                        .requestMatchers("/*/forms/lawyer").hasRole("LAWYER")
                        .requestMatchers("/*/accept/**").hasAnyRole("LAWYER", "SPOUSE")
                        .requestMatchers("/*/notary").hasRole("NOTARY")
                        .anyRequest().authenticated())
                        .securityContext((securityContext) -> securityContext
                                .securityContextRepository(securityContextRepository)
                        );
        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}