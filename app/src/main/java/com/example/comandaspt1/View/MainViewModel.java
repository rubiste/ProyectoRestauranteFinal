package com.example.comandaspt1.View;

import android.app.Application;
import android.util.Log;

import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {
    private Repository repository;

    public MainViewModel(@NonNull Application application){
        super(application);
        repository= new Repository();
    }

    public String getUrl(){ return repository.getUrl();}

    public LiveData<List<Empleado>> getLiveEmpleadoList(){
        return repository.getLiveEmpleadoList();
    }

    public LiveData<List<Mesa>> getLiveMesaList(){
        return repository.getLiveMesaList();
    }

    public LiveData<Empleado> getEmpleadoLogin(String username){
        return repository.empleadoLiveDataLogin(username);
    }

    public LiveData<Producto> productoLiveData (long id){return repository.productoLiveData(id);}

    public LiveData<List<Categoria>> getLiveDataCategoriasList(){
        return repository.getLiveDataCategoriaList();
    }

    public LiveData<List<Producto>> getLiveDataProductosList(){return repository.getLiveProductosList();}

    public LiveData<Mesa> mesaLiveData (long id){return repository.mesaLiveData(id);}

    public LiveData<List<Factura>> getLiveFacturaList(){
        return repository.getLiveFacturaList();
    }

    public LiveData<Factura> facturaLiveData (long id){return repository.facturaLiveData(id);}

    public LiveData<List<Comanda>> getLiveComandaList(){
        return repository.getLiveComandaList();
    }

    //DELETES
    public void deleteMesa(long id) {
        repository.deleteMesa(id);
    }
    public void deleteEmpleado(long id) {
        repository.deleteEmpleado(id);
    }
    public void deleteCategoria(long id){repository.deleteCategoria(id);}
    public void deleteProducto(long id) {repository.deleteProducto(id);}
    public void deleteComanda(long id) {repository.deleteComanda(id);}

    //ADDS
    public void addMesa(Mesa mesa, OnRestListener listener) {
        repository.add(mesa, listener);
    }

    public void addEmpleado(Empleado empleado, OnRestListener listener) {
        repository.add(empleado, listener);
    }

    public void addCategoria(Categoria categoria, OnRestListener listener){
        repository.addCategoria(categoria, listener);
    }

    public void addProducto(Producto producto, OnRestListener listener) {
        repository.addProducto(producto, listener);
    }

    public void addFactura(Factura factura, OnRestListener listener) {
        repository.addFactura(factura, listener);
    }

    public void addComanda(Comanda comanda, OnRestListener listener) {
        repository.addComanda(comanda, listener);
    }

    //UPDATES
    public void updateMesa(long id, Mesa mesa, OnRestListener listener){
        repository.updateMesa(id, mesa, listener);
    }

    public void updateEmpleado(long id, Empleado empleado, OnRestListener listener){
        repository.updateEmpleado(id, empleado, listener);
    }

    public void updateCategoria(long id, Categoria categoria, OnRestListener listener){
        repository.updateCategoria(id, categoria, listener);
    }

    public void updateProducto(long id, Producto producto, OnRestListener listener){
        repository.updateProducto(id, producto, listener);}

    public void updateComanda(long id, Comanda comanda, OnRestListener listener){
        repository.updateComanda(id, comanda, listener);}

    public void updateFactura(long id, Factura factura, OnRestListener listener){
        repository.updateFactura(id, factura, listener);}

    //FOTO
    public void upload(File file, OnRestListenerFoto listenerFoto) {
        repository.upload(file, listenerFoto);
    }
}
