package com.example.comandaspt1.Model.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {

    private  long  id, idCategoria;
    private String nombre, imagen, descripcion, destino;
    private float precio;


    public Producto() {

    }

    protected Producto(Parcel in) {
        id = in.readLong();
        idCategoria = in.readLong();
        nombre = in.readString();
        imagen = in.readString();
        descripcion = in.readString();
        destino = in.readString();
        precio = in.readFloat();
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                ", imagen='" + imagen + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", destino='" + destino + '\'' +
                ", precio=" + precio +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(idCategoria);
        dest.writeString(nombre);
        dest.writeString(imagen);
        dest.writeString(descripcion);
        dest.writeString(destino);
        dest.writeFloat(precio);
    }
}