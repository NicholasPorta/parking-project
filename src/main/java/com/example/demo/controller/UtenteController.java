package com.example.demo.controller;


import com.example.demo.model.dto.utente.UtenteLoginDto;
import com.example.demo.model.dto.utente.UtenteRegisterDto;
import com.example.demo.model.entities.Utente;
import com.example.demo.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param dto oggetto {@link UtenteRegisterDto} contenente le informazioni per la registrazione
     * @return {@link ResponseEntity} contenente l'utente creato o un messaggio di errore se l'email è già registrata
     */
    @PostMapping("/registrazione")
    public ResponseEntity<?> register(@Valid @RequestBody UtenteRegisterDto dto) {
        try {
            Utente create = utenteService.register(dto);
            return ResponseEntity.ok(create);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * Effettua il login di un utente.
     * <p>Verifica le credenziali, imposta il contesto di sicurezza Spring Security
     * e crea la sessione autenticata.</p>
     *
     * @param utenteLoginDto credenziali dell'utente ({@link UtenteLoginDto})
     * @param request oggetto {@link HttpServletRequest} per la gestione della sessione
     * @return {@link ResponseEntity} contenente i dati dell'utente autenticato
     *         oppure un messaggio di errore se le credenziali non sono valide
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UtenteLoginDto utenteLoginDto,
            HttpServletRequest request) {

        Optional<Utente> utenteOpt = utenteService.login(utenteLoginDto);

        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name())
            );

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    utente.getEmail(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok(utente);
        } else {
            return ResponseEntity.status(401).body("Email o password non validi");
        }
    }

    /**
     * Restituisce le informazioni dell'utente autenticato.
     *
     * @param authentication oggetto {@link Authentication} fornito da Spring Security
     * @return {@link ResponseEntity} contenente nome, email e ruolo dell'utente
     *         oppure un messaggio di errore se l'utente non è autenticato
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato");
        }

        String email = authentication.getName();

        Utente utente = utenteService.getByEmail(email).orElse(null);

        if (utente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non più valido");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("nome", utente.getName());
        body.put("email", utente.getEmail());
        body.put("ruolo", utente.getRuolo().toString());

        return ResponseEntity.<Map<String, Object>>ok(body);
    }
}