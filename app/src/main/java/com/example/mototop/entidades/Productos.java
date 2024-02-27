package com.example.mototop.entidades;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Productos implements Parcelable {
    private int ID;
    private long barcode;
    private int rubro_ID;
    private int proveedor_ID;
    private String nombre;
    private byte[] img_data;
    private double precio;
    private int stock;
    private int oferta_descuento;
    private int oferta_paga;

    private int oferta_lleva;

    private Date oferta_inicio;

    private Date oferta_fin;
    private int cantidadActual;


    public Productos(int ID, long barcode, int rubro_ID, int proveedor_ID, String nombre, byte[] img_data, double precio, int stock, int oferta_descuento, int oferta_paga, int oferta_lleva, Date oferta_inicio, Date oferta_fin) {
        this.ID = ID;
        this.barcode = barcode;
        this.rubro_ID = rubro_ID;
        this.proveedor_ID = proveedor_ID;
        this.nombre = nombre;
        this.img_data = img_data;
        this.precio = precio;
        this.stock = stock;
        this.oferta_descuento = oferta_descuento;
        this.oferta_paga = oferta_paga;
        this.oferta_lleva = oferta_lleva;
        this.oferta_inicio = oferta_inicio;
        this.oferta_fin = oferta_fin;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getRubro_ID() {
        return rubro_ID;
    }

    public void setRubro_ID(int rubro_ID) {
        this.rubro_ID = rubro_ID;
    }

    public int getProveedor_ID() {
        return proveedor_ID;
    }

    public void setProveedor_ID(int proveedor_ID) {
        this.proveedor_ID = proveedor_ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getUrl_imagen() {
        return img_data;
    }

    public void setUrl_imagen(byte[] img_data) {
        this.img_data = img_data;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public Date getOferta_inicio() {
        return oferta_inicio;
    }

    public void setOferta_inicio(Date oferta_inicio) {
        this.oferta_inicio = oferta_inicio;
    }

    public Date getOferta_fin() {
        return oferta_fin;
    }

    public void setOferta_fin(Date oferta_fin) {
        this.oferta_fin = oferta_fin;
    }

    public int getCantidadActual() { return cantidadActual; }

    public void setCantidadActual(int cantidadActual) { this.cantidadActual = cantidadActual; }

    // Implementación de Parcelable
    protected Productos(Parcel in) {
        barcode = Long.parseLong(in.readString());
        cantidadActual = Integer.parseInt(in.readString());
        nombre = in.readString();
        precio = Double.parseDouble(in.readString());
    }

    public static final Creator<Productos> CREATOR = new Creator<Productos>() {
        @Override
        public Productos createFromParcel(Parcel in) {
            return new Productos(in);
        }

        @Override
        public Productos[] newArray(int size) {
            return new Productos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // Escribir los datos en el parcel
        dest.writeString(String.valueOf(barcode));
        dest.writeString(String.valueOf(cantidadActual));
        dest.writeString(nombre);
        dest.writeString(String.valueOf(precio));
    }
}
