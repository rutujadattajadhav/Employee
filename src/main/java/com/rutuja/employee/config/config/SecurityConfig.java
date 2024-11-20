package com.rutuja.employee.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
//
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService(){
//         UserDetails user=User.withDefaultPasswordEncoder()
//                .username("EmployeeRoot")
//                .password("EmployeeRoot")
//                .roles("EmployeeRoot")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http .csrf(csrfSpec -> csrfSpec.disable());
        return http.build();
    }
}
