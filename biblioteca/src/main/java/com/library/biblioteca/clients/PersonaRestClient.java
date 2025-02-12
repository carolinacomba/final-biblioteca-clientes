package com.library.biblioteca.clients;

import com.library.biblioteca.dto.ClienteDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PersonaRestClient {
    String url = "http://localhost:8081/api/personas/aleatorio";
    RestTemplate restTemplate = new RestTemplate();

    public ClienteDTO getClienteAleatorio() {
        return restTemplate.getForObject(url, ClienteDTO.class);
    }
}
