package com.example.demo.model.entities;


import com.example.demo.enumerators.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "utenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utente {

    @Id
    private String id;
    @Field("email")
    @Indexed(unique = true)
    private String email;
    @Field("password")
    private String password;
    @Field("name")
    private String name;
    @Field("cognome")
    private String cognome;
    @Field("ruolo")
    private Ruolo ruolo;


}