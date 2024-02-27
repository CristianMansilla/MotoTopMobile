package com.example.mototop.entidades;

public class ListaPedidos {
    private int ID;
    private int pedido_ID;
    private long barcode;
    private int cantidad;
    private String nombre;
    private double precio;
    private int oferta_descuento;
    private int oferta_paga;
    private int oferta_lleva;
    private double total;

    public ListaPedidos(int ID, int pedido_ID, long barcode, int cantidad, String nombre, double precio, int oferta_descuento, int oferta_paga, int oferta_lleva, double total) {
        this.ID = ID;
        this.pedido_ID = pedido_ID;
        this.barcode = barcode;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.precio = precio;
        this.oferta_descuento = oferta_descuento;
        this.oferta_paga = oferta_paga;
        this.oferta_lleva = oferta_lleva;
        this.total = total;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPedido_ID() {
        return pedido_ID;
    }

    public void setPedido_ID(int pedido_ID) {
        this.pedido_ID = pedido_ID;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getOferta_descuento() {
        return oferta_descuento;
    }

    public void setOferta_descuento(int oferta_descuento) {
        this.oferta_descuento = oferta_descuento;
    }

    public int getOferta_paga() {
        return oferta_paga;
    }

    public void setOferta_paga(int oferta_paga) {
        this.oferta_paga = oferta_paga;
    }

    public int getOferta_lleva() {
        return oferta_lleva;
    }

    public void setOferta_lleva(int oferta_lleva) {
        this.oferta_lleva = oferta_lleva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
