
package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class ApplicationSecurity {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>Configura la catena di filtri di sicurezza di Spring Security.</p>
     * <ul>
     *   <li>Disabilita CSRF, form login e logout standard</li>
     *   <li>Definisce le regole di autorizzazione per endpoint pubblici e protetti</li>
     *   <li>Imposta i ruoli richiesti per l'accesso a specifiche rotte</li>
     * </ul>
     *
     * @param http l'oggetto {@link HttpSecurity} da configurare
     * @return il {@link SecurityFilterChain} configurato
     * @throws Exception in caso di errore nella configurazione
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("START configure - setting up http rules");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/utente/registrazione", "/api/utente/login").permitAll()
                        .requestMatchers("/api/parkingLot/**").permitAll()
                        .requestMatchers("/api/appointment/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("AMMINISTRATORE")
                        .requestMatchers("/user/**").hasAnyRole("AMMINISTRATORE", "UTENTE")
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        logger.info("EXIT configure");
        return http.build();
    }

    /**
     * <p>Configura CORS (Cross-Origin Resource Sharing) per permettere al frontend React
     * su <code>http://localhost:3000</code> di comunicare con il backend.</p>
     * <p>Abilita metodi, intestazioni e credenziali.</p>
     *
     * @return un {@link WebMvcConfigurer} con le regole CORS configurate
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // <--- QUESTA È FONDAMENTALE
            }
        };
    }

    /**
     * <p>Bean per la codifica delle password.</p>
     * <p>Usa {@link BCryptPasswordEncoder} per garantire un hashing sicuro.</p>
     *
     * @return istanza di {@link BCryptPasswordEncoder}
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public UserDetailsService users() {
//        UserDetails admin = User
//                .withUsername("admin")
//                .password(passwordEncoder().encode("adminpass"))
//                .roles("AMMINISTRATORE")
//                .build();
//
//        UserDetails user = User
//                .withUsername("user")
//                .password(passwordEncoder().encode("userpass"))
//                .roles("UTENTE")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }


}
