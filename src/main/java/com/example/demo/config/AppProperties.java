package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Classe per la gestione delle proprietà dell'applicazione.
 * Le proprietà sono caricate dal file di configurazione (application.yml)
 * con il prefisso "app".
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private String codiceAdmin;

}
