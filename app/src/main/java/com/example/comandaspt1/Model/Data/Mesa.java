package com.example.comandaspt1.Model.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Mesa implements Parcelable {
    private long id;
    private int numero;
    private String imagen;
    private int disponible;

    public Mesa(long id, int numero, String imagen, int disponible) {
        this.id = id;
        this.numero = numero;
        this.imagen = imagen;
        this.disponible = disponible;
    }

    public Mesa() {
    }

    protected Mesa(Parcel in) {
        id = in.readLong();
        numero = in.readInt();
        imagen = in.readString();
        disponible = in.readInt();
    }

    public static final Creator<Mesa> CREATOR = new Creator<Mesa>() {
        @Override
        public Mesa createFromParcel(Parcel in) {
            return new Mesa(in);
        }

        @Override
        public Mesa[] newArray(int size) {
            return new Mesa[size];
        }
    };

    @Override
    public String toString() {
        return "Mesa{" +
                "id=" + id +
                ", numero=" + numero +
                ", imagen='" + imagen + '\'' +
                ", disponible=" + disponible +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(numero);
        dest.writeString(imagen);
        dest.writeInt(disponible);
    }

}
