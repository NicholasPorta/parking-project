package com.example.demo.repository;

import com.example.demo.model.entities.Utente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends MongoRepository<Utente,String> {

    Optional<Utente> findByEmail(String email);
}
