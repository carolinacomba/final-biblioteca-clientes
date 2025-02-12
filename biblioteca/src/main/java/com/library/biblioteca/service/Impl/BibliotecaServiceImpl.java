package com.library.biblioteca.service.Impl;


import com.library.biblioteca.clients.PersonaRestClient;
import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.BibliotecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BibliotecaServiceImpl implements BibliotecaService {
    private final RegistroRepository registroRepository;
    private final LibroRepository libroRepository;
    private final RestTemplate restTemplate;
    private final PersonaRestClient personaRestClient;


    @Override
    public Registro alquilarLibros(List<String> isbns) {
        List<Libro> libros = new ArrayList<>();
        for (String isbn : isbns) {
            Libro libro = libroRepository.findByIsbn(isbn);
            if (libro.getEstado() != EstadoLibro.DISPONIBLE) {
                throw new IllegalStateException("Uno o más libros ya están reservados.");
            }
            libros.add(libro);
        }
        ClienteDTO cliente = personaRestClient.getClienteAleatorio();
        Registro registro = new Registro();
        registro.setClienteId(cliente.getId());
        registro.setNombreCliente(cliente.getNombre());
        registro.setFechaReserva(LocalDate.now());
        registro.setLibrosReservados(libros);
        return registroRepository.save(registro);
    }

    @Override
    public Registro devolverLibros(Long registroId) {
        Registro registro = registroRepository.findById(registroId).orElse(null);
        if (registro != null) {
            registro.setFechaDevolucion(LocalDate.now());
            for (Libro libro : registro.getLibrosReservados()) {
                libro.setEstado(EstadoLibro.DISPONIBLE);
                libroRepository.save(libro);
            }
            registro.setTotal(calcularCostoAlquiler(registro.getFechaReserva(), registro.getFechaDevolucion(), registro.getLibrosReservados().size()));
            return registroRepository.save(registro);
        } else {
            throw new RuntimeException("Registro no encontrado");
        }
    }

    @Override
    public List<Registro> verTodosLosAlquileres() {
        return registroRepository.findAll();
    }

    // Cálculo de costo de alquiler
    private BigDecimal calcularCostoAlquiler(LocalDate inicio, LocalDate fin, int cantidadLibros) {
        int dias = (int) ChronoUnit.DAYS.between(inicio, fin);
        if (dias <= 2) {
            return new BigDecimal(100 * cantidadLibros);
        } else if (dias <= 5) {
            return new BigDecimal(150 * cantidadLibros);
        } else {
            return new BigDecimal(150 * cantidadLibros + 30 * (dias - 5));
        }
    }

    @Override
    public List<Registro> informeSemanal(LocalDate fechaInicio) {
        LocalDate fechaFin = fechaInicio.plusDays(7);
        return registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquilados() {
        return registroRepository.obtenerLibrosMasAlquilados();
    }

}
