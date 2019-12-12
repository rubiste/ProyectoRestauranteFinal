package com.example.comandaspt1.Model.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Factura implements Parcelable {

    private long id, numeroMesa, idEmpleadoInicio, idEmpleadoCierre;
    private String horaInicio, horaCierre;
    private double total;

    public Factura() {
    }

    public Factura(long id, long numeroMesa, long idEmpleadoInicio, long idEmpleadoCierre, String horaInicio, String horaCierre, double total) {
        this.id = id;
        this.numeroMesa = numeroMesa;
        this.idEmpleadoInicio = idEmpleadoInicio;
        this.idEmpleadoCierre = idEmpleadoCierre;
        this.horaInicio = horaInicio;
        this.horaCierre = horaCierre;
        this.total = total;
    }

    protected Factura(Parcel in) {
        id = in.readLong();
        numeroMesa = in.readLong();
        idEmpleadoInicio = in.readLong();
        idEmpleadoCierre = in.readLong();
        horaInicio = in.readString();
        horaCierre = in.readString();
        total = in.readDouble();
    }

    public static final Creator<Factura> CREATOR = new Creator<Factura>() {
        @Override
        public Factura createFromParcel(Parcel in) {
            return new Factura(in);
        }

        @Override
        public Factura[] newArray(int size) {
            return new Factura[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(numeroMesa);
        dest.writeLong(idEmpleadoInicio);
        dest.writeLong(idEmpleadoCierre);
        dest.writeString(horaInicio);
        dest.writeString(horaCierre);
        dest.writeDouble(total);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(long numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public long getIdEmpleadoInicio() {
        return idEmpleadoInicio;
    }

    public void setIdEmpleadoInicio(long idEmpleadoInicio) {
        this.idEmpleadoInicio = idEmpleadoInicio;
    }

    public long getIdEmpleadoCierre() {
        return idEmpleadoCierre;
    }

    public void setIdEmpleadoCierre(long getIdEmpleadoCierre) {
        this.idEmpleadoCierre = getIdEmpleadoCierre;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraCierra() {
        return horaCierre;
    }

    public void setHoraCierra(String horaCierra) {
        this.horaCierre = horaCierra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", numeroMesa=" + numeroMesa +
                ", horaInicio='" + horaInicio + '\'' +
                ", idEmpleadoInicio=" + idEmpleadoInicio +
                ", horaCierre='" + horaCierre + '\'' +
                ", idEmpleadoCierre=" + idEmpleadoCierre +
                ", total=" + total +
                '}';
    }
}
