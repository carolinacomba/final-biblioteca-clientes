package com.library.biblioteca.service.Impl;

import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BibliotecaServiceImplTest {
    @Mock
    RegistroRepository registroRepository;

    @Mock
    LibroRepository libroRepository;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    BibliotecaServiceImpl bibliotecaServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void alquilarLibros() {
        List<Libro> libros = List.of(new Libro(1L, "1234", "titulo1", "autor1", EstadoLibro.DISPONIBLE),
                new Libro(2L, "5678", "titulo2", "autor2", EstadoLibro.DISPONIBLE));
        Registro registro = new Registro(1L, 1L, "nombre1",
                LocalDate.now(), LocalDate.now(), libros, BigDecimal.valueOf(23));
        when(libroRepository.findByIsbn("1234")).thenReturn(libros.get(0));
        when(libroRepository.findByIsbn("5678")).thenReturn(libros.get(1));
        when(restTemplate.getForObject("http://localhost:8081/api/personas/aleatorio", ClienteDTO.class))
                .thenReturn(new ClienteDTO(1L, "nombre1", "domicilio"));
        when(registroRepository.save(registro)).thenReturn(registro);


        Registro expected = bibliotecaServiceImpl.alquilarLibros(List.of("1234", "5678"));

        assertEquals(expected, registro);
    }

    @Test
    void devolverLibros() {
    }

    @Test
    void verTodosLosAlquileres() {
    }

    @Test
    void informeSemanal() {
    }

    @Test
    void informeLibrosMasAlquilados() {
    }
}