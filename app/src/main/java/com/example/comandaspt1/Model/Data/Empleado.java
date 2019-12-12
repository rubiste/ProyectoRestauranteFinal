package com.example.comandaspt1.Model.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Empleado implements Parcelable {
    private long id;
    private String nombre, apellido, imagen, username, password;
    private int telefono;
    private int gerente;

    public Empleado() {
    }

    public Empleado(long id, String nombre, String apellido, String imagen, String username, String password, int telefono, int gerente) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.imagen = imagen;
        this.username = username;
        this.password = password;
        this.telefono = telefono;
        this.gerente = gerente;
    }

    protected Empleado(Parcel in) {
        id = in.readLong();
        nombre = in.readString();
        apellido = in.readString();
        imagen = in.readString();
        username = in.readString();
        password = in.readString();
        telefono = in.readInt();
        gerente = in.readInt();
    }

    public static final Creator<Empleado> CREATOR = new Creator<Empleado>() {
        @Override
        public Empleado createFromParcel(Parcel in) {
            return new Empleado(in);
        }

        @Override
        public Empleado[] newArray(int size) {
            return new Empleado[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getGerente() {
        return gerente;
    }

    public void setGerente(int gerente) {
        this.gerente = gerente;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono=" + telefono +
                ", imagen='" + imagen + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gerente=" + gerente +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nombre);
        dest.writeString(apellido);
        dest.writeString(imagen);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(telefono);
        dest.writeInt(gerente);
    }
}
