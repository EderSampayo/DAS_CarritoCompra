package com.example.das_carritocompra;

public class Producto {
    private String nombre;
    private String tipo;

    public Producto(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public void setTipo(String nuevoTipo) {
        this.tipo = nuevoTipo;
    }
}
