package com.library.biblioteca.service.Impl;


import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.BibliotecaService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BibliotecaServiceImpl implements BibliotecaService {
    private final RegistroRepository registroRepository;
    private final LibroRepository libroRepository;
    private final RestTemplate restTemplate;

    public BibliotecaServiceImpl(LibroRepository libroRepository, RegistroRepository registroRepository, RestTemplate restTemplate) {
        this.registroRepository = registroRepository;
        this.libroRepository = libroRepository;
        this.restTemplate = restTemplate;
    }


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
        ClienteDTO cliente = restTemplate.getForObject("http://localhost:8081/api/personas/aleatorio", ClienteDTO.class);
        Registro registro = new Registro();
        registro.setClienteId(cliente.getId());
        registro.setNombreCliente(cliente.getNombre());
        registro.setFechaReserva(LocalDate.now());
        registro.setLibrosReservados(libros);
        return registroRepository.save(registro);
    }

    @Override
    public Registro devolverLibros(Long registroId) {
        //TODO
        /**
         * Completar el metodo de devolucion
         * Se debe buscar la reserva por su id,
         * actualizar la fecha de devolucion y calcular el importe a facturar,
         * actualizar el estado de los libros a DISPONIBLE
         * y guardar el registro con los datos actualizados 
         */
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
        //TODO
        /**
         * Completar el metodo de calculo
         * se calcula el importe a pagar por libro en funcion de la cantidad de dias,
         * es la diferencia entre el alquiler y la devolucion, respetando la siguiente tabla:
         * hasta 2 dias se debe pagar $100 por libro
         * desde 3 dias y hasta 5 dias se debe pagar $150 por libro
         * más de 5 dias se debe pagar $150 por libro + $30 por cada día extra
         */
        //Period periodo = Period.between(inicio, fin);
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
        //TODO
        /**
         * Completar el metodo de reporte semanal
         * se debe retornar la lista de registros de la semana tomando como referencia
         * la fecha de inicio para la busqueda
         */
        LocalDate fechaFin = fechaInicio.plusDays(7);
        return registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquilados() {
        //TODO
        /**
         * Completar el metodo de reporte de libros mas alquilados
         * se debe retornar la lista de libros mas alquilados
         */
        return registroRepository.obtenerLibrosMasAlquilados();
    }

}
