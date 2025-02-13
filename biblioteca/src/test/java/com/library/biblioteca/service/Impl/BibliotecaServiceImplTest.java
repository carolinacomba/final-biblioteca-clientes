package com.library.biblioteca.service.Impl;

import com.library.biblioteca.clients.PersonaRestClient;
import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import org.aspectj.weaver.patterns.PerSingleton;
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
import java.util.Optional;

import static javax.management.Query.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BibliotecaServiceImplTest {
    @Mock
    RegistroRepository registroRepository;

    @Mock
    LibroRepository libroRepository;

    @Mock
    RestTemplate restTemplate;

    @Mock
    PersonaRestClient personaRestClient;

    @InjectMocks
    BibliotecaServiceImpl bibliotecaServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void alquilarLibros() {
        List<String> isbns = new ArrayList<>();
        isbns.add("1234");
        isbns.add("5678");

        Libro libro1 = new Libro();
        libro1.setIsbn("1234");
        libro1.setEstado(EstadoLibro.DISPONIBLE);

        Libro libro2 = new Libro();
        libro2.setIsbn("5678");
        libro2.setEstado(EstadoLibro.DISPONIBLE);

        List<Libro> libros = new ArrayList<>();
        libros.add(libro1);
        libros.add(libro2);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(1L);
        cliente.setNombre("Juan");

        Registro expected = new Registro(1L, 1L, "Juan", LocalDate.now(), LocalDate.now(), libros, null);

        when(libroRepository.findByIsbn("1234")).thenReturn(libro1);
        when(libroRepository.findByIsbn("5678")).thenReturn(libro2);
        when(personaRestClient.getClienteAleatorio()).thenReturn(cliente);
        when(registroRepository.save(any(Registro.class))).thenReturn(expected);
        Registro actual = bibliotecaServiceImpl.alquilarLibros(isbns);

        assertEquals(expected, actual);
    }

    @Test
    void testAlquilarLibrosThrowIllegalStateException(){
        List<String> isbns = new ArrayList<>();
        isbns.add("1234");
        isbns.add("5678");

        Libro libro1 = new Libro();
        libro1.setIsbn("1234");
        libro1.setEstado(EstadoLibro.RESERVADO);

        Libro libro2 = new Libro();
        libro2.setIsbn("5678");
        libro2.setEstado(EstadoLibro.DISPONIBLE);

        List<Libro> libros = new ArrayList<>();
        libros.add(libro1);
        libros.add(libro2);

        when(libroRepository.findByIsbn("1234")).thenReturn(libro1);
        when(libroRepository.findByIsbn("5678")).thenReturn(libro2);

        assertThrows(IllegalStateException.class, () -> bibliotecaServiceImpl.alquilarLibros(isbns));
    }


    @Test
    void devolverLibrosTestHappyPath() {
        Libro libro = new Libro();
        libro.setEstado(EstadoLibro.RESERVADO);

        Registro registro = new Registro();
        registro.setId(1L);
        registro.setFechaReserva(LocalDate.now().minusDays(3));
        registro.setLibrosReservados(List.of(libro));

        when(registroRepository.findById(1L)).thenReturn(Optional.of(registro));
        when(registroRepository.save(any(Registro.class))).thenReturn(registro);

        Registro actual = bibliotecaServiceImpl.devolverLibros(1L);

        assertEquals(registro, actual);
    }

    @Test
    void verTodosLosAlquileresHappyPath() {
        List<Registro> registros = new ArrayList<>();
        registros.add(new Registro(1L, 1L, "Juan", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));
        registros.add(new Registro(2L, 2L, "Pedro", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));

        when(registroRepository.findAll()).thenReturn(registros);

        List<Registro> actual = bibliotecaServiceImpl.verTodosLosAlquileres();

        assertEquals(registros, actual);
    }

    @Test
    void informeSemanalTestHappyPath() {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(7);

        List<Registro> registros = new ArrayList<>();
        registros.add(new Registro(1L, 1L, "Juan", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));
        registros.add(new Registro(2L, 2L, "Pedro", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));

        when(registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin)).thenReturn(registros);

        List<Registro> actual = bibliotecaServiceImpl.informeSemanal(fechaInicio);

        assertEquals(registros, actual);
    }

    @Test
    void informeLibrosMasAlquiladosTestHappyPath() {
        List<Registro> registros = new ArrayList<>();
        registros.add(new Registro(1L, 1L, "Juan", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));
        registros.add(new Registro(2L, 2L, "Pedro", LocalDate.now(), LocalDate.now(), new ArrayList<>(), null));

        when(registroRepository.findAll()).thenReturn(registros);

        List<Object[]> actual = bibliotecaServiceImpl.informeLibrosMasAlquilados();

        assertEquals(new ArrayList<>(), actual);
    }
}