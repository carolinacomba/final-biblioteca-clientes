package com.library.biblioteca.service.Impl;

import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibroServiceImplTest {

    @Mock
    LibroRepository libroRepository;

    @InjectMocks
    LibroServiceImpl libroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarLibroHappyPath() {
        Libro libro = new Libro(1L, "123456789", "titulo", "autor", EstadoLibro.DISPONIBLE);
        when(libroRepository.save(libro)).thenReturn(libro);
        Libro expected = libroService.registrarLibro(libro);
        assertEquals(expected, libro);
    }

    @Test
    void obtenerTodosLosLibros() {
        List<Libro> libros = List.of(
                new Libro(1L, "123456789", "titulo", "autor", EstadoLibro.DISPONIBLE),
                new Libro(2L, "123456789", "titulo", "autor", EstadoLibro.DISPONIBLE)
        );
        when(libroRepository.findAll()).thenReturn(libros);
        List<Libro> expected = libroService.obtenerTodosLosLibros();
        assertEquals(expected, libros);
    }

    @Test
    void eliminarLibro() {
        Long id = 1L;
        when(libroRepository.existsById(id)).thenReturn(true);
        libroService.eliminarLibro(id);
        verify(libroRepository).deleteById(id);
    }

    @Test
    void actualizarLibroHappyPath() {
        Libro libro = new Libro(1L, "123456789", "titulo", "autor", EstadoLibro.DISPONIBLE);
        when(libroRepository.findById(libro.getId())).thenReturn(Optional.of(libro));
        when(libroRepository.save(libro)).thenReturn(libro);
        Libro expected = libroService.actualizarLibro(libro);
        assertEquals(expected, libro);
    }

    @Test
    void actualizarLibroNotFound() {
        Libro libro = new Libro(1L, "123456789", "titulo", "autor", EstadoLibro.DISPONIBLE);
        when(libroRepository.findById(libro.getId())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> libroService.actualizarLibro(libro));
    }
}