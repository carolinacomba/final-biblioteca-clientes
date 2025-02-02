package com.library.biblioteca.clients;

import org.springframework.web.client.RestTemplate;

import com.library.biblioteca.dto.ClienteDTO;

public class PersonaRestClient {
    RestTemplate restTemplate = new RestTemplate();

    String url = "http://localhost:8080/api/personas/aleatorio";
    
    public ClienteDTO getPersonaAleatoria() {
        return restTemplate.getForObject(url, ClienteDTO.class);
    }
}
