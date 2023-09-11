package org.ck.ds.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Arrays;

@Configuration
public class SecurityContextConfig {

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                Arrays.asList(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository()
                )
        );
    }
}
