package com.example.demo.model.dto.utente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la richiesta di login di un utente.
 * <p>Contiene le informazioni necessarie per autenticare un utente.</p>
 * <ul>
 *   <li>{@code email} – email dell'utente</li>
 *   <li>{@code password} – password dell'utente</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteLoginDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
