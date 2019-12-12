package com.example.comandaspt1.Model.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Comanda implements Parcelable {

    private long id, idFactura, idProducto, idEmpleado;
    private int unidades, entregado;
    private double precio;

    public Comanda() {
    }

    public Comanda(long id, long idFactura, long idProducto, long idEmpleado, int unidades, double precio, int entregado) {
        this.id = id;
        this.idFactura = idFactura;
        this.idProducto = idProducto;
        this.idEmpleado = idEmpleado;
        this.unidades = unidades;
        this.entregado = entregado;
        this.precio = precio;
    }

    protected Comanda(Parcel in) {
        id = in.readLong();
        idFactura = in.readLong();
        idProducto = in.readLong();
        idEmpleado = in.readLong();
        unidades = in.readInt();
        precio = in.readDouble();
        entregado = in.readInt();
    }

    public static final Creator<Comanda> CREATOR = new Creator<Comanda>() {
        @Override
        public Comanda createFromParcel(Parcel in) {
            return new Comanda(in);
        }

        @Override
        public Comanda[] newArray(int size) {
            return new Comanda[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(idFactura);
        dest.writeLong(idProducto);
        dest.writeLong(idEmpleado);
        dest.writeInt(unidades);
        dest.writeInt(entregado);
        dest.writeDouble(precio);
    }

    @Override
    public String toString() {
        return "Comanda{" +
                "id=" + id +
                ", idFactura=" + idFactura +
                ", idProducto=" + idProducto +
                ", idEmpleado=" + idEmpleado +
                ", unidades=" + unidades +
                ", precio=" + precio +
                ", entregado=" + entregado +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(long idFactura) {
        this.idFactura = idFactura;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public long getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public int getEntregado() {
        return entregado;
    }

    public void setEntregado(int entregado) {
        this.entregado = entregado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
