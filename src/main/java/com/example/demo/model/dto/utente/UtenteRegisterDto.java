package com.example.demo.model.dto.utente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la registrazione di un nuovo utente.
 * <p>Contiene le informazioni necessarie per creare un nuovo account utente.</p>
 * <ul>
 *   <li>{@code email} – email dell'utente</li>
 *   <li>{@code password} – password dell'utente</li>
 *   <li>{@code name} – nome dell'utente</li>
 *   <li>{@code cognome} – cognome dell'utente</li>
 *   <li>{@code codiceAmministratore} – codice amministratore (opzionale)</li>
 * </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteRegisterDto {


    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String cognome;

    private String codiceAmministratore;
}
