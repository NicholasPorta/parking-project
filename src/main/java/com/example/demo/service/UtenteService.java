package com.example.demo.service;

import com.example.demo.config.AppProperties;
import com.example.demo.enumerators.Ruolo;
import com.example.demo.model.dto.utente.UtenteLoginDto;
import com.example.demo.model.dto.utente.UtenteRegisterDto;
import com.example.demo.model.entities.Utente;
import com.example.demo.repository.UtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtenteService {

    private static final Logger log = LoggerFactory.getLogger(UtenteService.class);

    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AppProperties appProperties;


    /**
     * Registra un nuovo utente.
     * Se l'email è già registrata, lancia un'eccezione.
     * Se il codice amministratore è corretto, assegna il ruolo di amministratore,
     * altrimenti assegna il ruolo di utente.
     * @param dto dati dell'utente da registrare
     * @return l'utente registrato
     * @throws IllegalArgumentException se l'email è già registrata
     */
    public Utente register(UtenteRegisterDto dto) {
        if (utenteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già registrata.");
        }

        Ruolo ruolo = dto.getCodiceAmministratore() != null &&
                dto.getCodiceAmministratore().trim().equals(appProperties.getCodiceAdmin())
                ? Ruolo.AMMINISTRATORE
                : Ruolo.UTENTE;

        log.info(">> Ruolo assegnato: " + ruolo);

        Utente utente = Utente.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .cognome(dto.getCognome())
                .ruolo(ruolo)
                .build();

        Utente salvato = utenteRepository.save(utente);
        log.info("Utente registrato: email={}, ruolo={}", salvato.getEmail(), salvato.getRuolo());
        return salvato;
    }


    /**
     * Esegue il login di un utente.
     * Controlla se l'email esiste e se la password corrisponde.
     *
     * @param dto dati di login dell'utente
     * @return un Optional contenente l'utente se il login ha successo, altrimenti vuoto
     */
    public Optional<Utente> login(UtenteLoginDto dto) {
        return utenteRepository.findByEmail(dto.getEmail())
                .filter(utente -> passwordEncoder.matches(dto.getPassword(), utente.getPassword()));
    }

    /**
     * Recupera un utente per email.
     * Se l'email è null o vuota, lancia un'eccezione.
     * Controlla se l'email è valida prima di cercare l'utente.
     * Se l'email è inesistente, lancia un'eccezione.
     * @param email l'email dell'utente da cercare
     * @return un Optional contenente l'utente se trovato, altrimenti vuoto
     * @throws IllegalArgumentException se l'email è null o vuota
     */
    public Optional<Utente> getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inesistente o vuota.");
        }

        return utenteRepository.findByEmail(email);
    }
}
