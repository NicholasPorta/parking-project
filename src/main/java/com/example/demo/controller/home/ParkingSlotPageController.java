package com.example.demo.controller.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;



@Controller
public class ParkingSlotPageController {

    @GetMapping("/homepage")
    public String homepage() {
        return "home";
    }
    @GetMapping("/registrazione")
    public String registrazionePage() {
        return "registrazione";
    }
    @GetMapping("/registrazione-successo")
    public String registrazioneSuccesso() {
        return "registrazione-successo";
    }

}


