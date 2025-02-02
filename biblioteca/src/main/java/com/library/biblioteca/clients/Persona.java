package com.library.biblioteca.clients;


public class Persona {
    private Long id;
    private String nombre;
    private String domicilio;

    public Persona(Long id, String nombre, String domicilio) {
        this.id = id;
        this.nombre = nombre;
        this.domicilio = domicilio;
    }

    public Persona() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }


}
