package com.example.mototop.entidades;

import java.util.Date;

public class Pedidos {
    private int ID;
    private int cliente_DNI;
    private int vendedor_DNI;
    private String cliente_nombre;
    private String vendedor_nombre;
    private Date fecha_generado;
    private Date fecha_envio;
    private Date fecha_entrega;
    private int estado;
    private String direccion_entrega;
    private double precio_total;

    public Pedidos(int ID, int cliente_DNI, int vendedor_DNI, String cliente_nombre, String vendedor_nombre, Date fecha_generado, Date fecha_envio, Date fecha_entrega, int estado, String direccion_entrega, double precio_total) {
        this.ID = ID;
        this.cliente_DNI = cliente_DNI;
        this.vendedor_DNI = vendedor_DNI;
        this.cliente_nombre = cliente_nombre;
        this.vendedor_nombre = vendedor_nombre;
        this.fecha_generado = fecha_generado;
        this.fecha_envio = fecha_envio;
        this.fecha_entrega = fecha_entrega;
        this.estado = estado;
        this.direccion_entrega = direccion_entrega;
        this.precio_total = precio_total;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCliente_DNI() {
        return cliente_DNI;
    }

    public void setCliente_DNI(int cliente_DNI) {
        this.cliente_DNI = cliente_DNI;
    }

    public int getVendedor_DNI() {
        return vendedor_DNI;
    }

    public void setVendedor_DNI(int vendedor_DNI) {
        this.vendedor_DNI = vendedor_DNI;
    }

    public String getCliente_nombre() {
        return cliente_nombre;
    }

    public void setCliente_nombre(String cliente_nombre) {
        this.cliente_nombre = cliente_nombre;
    }

    public String getVendedor_nombre() {
        return vendedor_nombre;
    }

    public void setVendedor_nombre(String vendedor_nombre) {
        this.vendedor_nombre = vendedor_nombre;
    }

    public Date getFecha_generado() {
        return fecha_generado;
    }

    public void setFecha_generado(Date fecha_generado) {
        this.fecha_generado = fecha_generado;
    }

    public Date getFecha_envio() {
        return fecha_envio;
    }

    public void setFecha_envio(Date fecha_envio) {
        this.fecha_envio = fecha_envio;
    }

    public Date getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(Date fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getDireccion_entrega() {
        return direccion_entrega;
    }

    public void setDireccion_entrega(String direccion_entrega) {
        this.direccion_entrega = direccion_entrega;
    }

    public double getPrecio_total() {
        return precio_total;
    }

    public void setPrecio_total(double precio_total) {
        this.precio_total = precio_total;
    }
}

