package com.example.comandaspt1.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.comandaspt1.Conexion;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.MainActivity;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Rest.ComandasClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private final long VALOR_SUCCEED = 1;
    private static String TAG = "comandas.repository";
    private ComandasClient apiClient;

    private ArrayList<Empleado> empleadoList = new ArrayList<>();
    private MutableLiveData<List<Empleado>> mutableEmpleadoList = new MutableLiveData<>();
    private MutableLiveData<Empleado> empleadoMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<Mesa> mesaMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Mesa>> mutableMesaList = new MutableLiveData<>();

    private ArrayList<Categoria> listaCategorias = new ArrayList<>();
    private MutableLiveData<List<Categoria>> mutableCategoriasList = new MutableLiveData<>();
    private MutableLiveData<Categoria> mutableCategoria = new MutableLiveData<>();

    private ArrayList<Producto> productoList = new ArrayList<>();
    private MutableLiveData<List<Producto>> mutableProductosList = new MutableLiveData<>();
    private MutableLiveData<Producto> productoMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Comanda>> mutableComandaList = new MutableLiveData<>();
    private MutableLiveData<Comanda> comandaMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Factura>> mutableFacturaList = new MutableLiveData<>();
    private MutableLiveData<Factura> facturaMutableLiveData = new MutableLiveData<>();

    private String url = "0.0.0.0";

    public Repository() {
        setUrl();
        retrieveApiClient(url);
        //fetchEmpleadoList();
        //fetchMesaList();
        //fetchCategoriaList();
        //fetchProductoList();
    }

    public void setUrl(){
        SharedPreferences sharedPreferences = MainActivity.getContext().getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        String enlace = sharedPreferences.getString(Conexion.URL, "0.0.0.0");
        url ="http:/"+enlace+"/web/Comandas/public";
    }

    public void fetchEmpleadoList() {
        Call<ArrayList<Empleado>> call = apiClient.getEmpleado();
        call.enqueue(new Callback<ArrayList<Empleado>>() {
            @Override
            public void onResponse(Call<ArrayList<Empleado>> call, Response<ArrayList<Empleado>> response) {
                empleadoList = response.body();
                mutableEmpleadoList.setValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Empleado>> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                empleadoList = new ArrayList<>();
                mutableEmpleadoList = new MutableLiveData<>();
            }
        });
    }

    public void fetchMesaList() {
        Call<ArrayList<Mesa>> call = apiClient.getMesa();
        call.enqueue(new Callback<ArrayList<Mesa>>() {
            @Override
            public void onResponse(Call<ArrayList<Mesa>> call, Response<ArrayList<Mesa>> response) {
                mutableMesaList.setValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Mesa>> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                mutableMesaList=new MutableLiveData<>();
            }
        });
    }

    private void retrieveApiClient(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url+"/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiClient=retrofit.create(ComandasClient.class);

    }

    public String getUrl(){
        return this.url;
    }


    public void upload(File file, final OnRestListenerFoto listenerFoto) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part request = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<String> call = apiClient.fileUpload(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                listenerFoto.onSuccess("Response Foto");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                listenerFoto.onSuccess("Failure Foto");
            }
        });
    }

    public void getSpecificEmpleado(String username){
        Call<Empleado> call = apiClient.getEmpleadoLogin(username);
        call.enqueue(new Callback<Empleado>() {
            @Override
            public void onResponse(Call<Empleado> call, Response<Empleado> response) {
                empleadoMutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Empleado> call, Throwable t) {
                empleadoMutableLiveData = new MutableLiveData<>();
            }
        });
    }

    public void deleteMesa(long id) {
        Call<Integer> call = apiClient.deleteMesa(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                fetchMesaList();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void deleteEmpleado(long id) {
        Call<Integer> call = apiClient.deleteEmpleado(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                fetchEmpleadoList();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void add(Mesa mesa, final OnRestListener listener) {
        Call<Long> call = apiClient.postMesa(mesa);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if(response.body() != null){
                    long resultado = response.body();
                    if(resultado>0){
                        fetchMesaList();
                        listener.onSuccess(resultado);
                    }
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void add(Empleado empleado, final OnRestListener listener) {
        Call<Long> call = apiClient.postEmpleado(empleado);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Log.v(TAG, response.body().toString());
                long resultado = response.body();
                if(resultado>0){
                    listener.onSuccess(resultado);
                    fetchEmpleadoList();
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void getSpecificMesa(long id){
        Call<Mesa> call = apiClient.getMesa(id);
        call.enqueue(new Callback<Mesa>() {
            @Override
            public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                mesaMutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Mesa> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void updateMesa(long id, Mesa mesa, final OnRestListener listener) {
        Call<Mesa> call = apiClient.putMesa(id, mesa);
        call.enqueue(new Callback<Mesa>() {
            @Override
            public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                fetchMesaList();
                listener.onSuccess(VALOR_SUCCEED);
            }
            @Override
            public void onFailure(Call<Mesa> call, Throwable t) {
                fetchMesaList();
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void updateEmpleado(long id, Empleado empleado, final OnRestListener listener) {
        Call<Empleado> call = apiClient.putEmpleado(id, empleado);
        call.enqueue(new Callback<Empleado>() {
            @Override
            public void onResponse(Call<Empleado> call, Response<Empleado> response) {
                fetchEmpleadoList();
                listener.onSuccess(VALOR_SUCCEED);
            }
            @Override
            public void onFailure(Call<Empleado> call, Throwable t) {
                fetchEmpleadoList();
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void fetchCategoriaList() {
        Call<ArrayList<Categoria>> call = apiClient.getCategorias();
        call.enqueue(new Callback<ArrayList<Categoria>>() {
            @Override
            public void onResponse(Call<ArrayList<Categoria>> call, Response<ArrayList<Categoria>> response) {
                listaCategorias = response.body();
                mutableCategoriasList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Categoria>> call, Throwable t) {
                Log.e(ERROR_TAG, t.getMessage());
                listaCategorias  = new ArrayList<>();
                mutableCategoriasList = new MutableLiveData<>();
            }
        });
    }

    public void fetchProductoList() {
        Call<ArrayList<Producto>> call = apiClient.getProductos();
        call.enqueue(new Callback<ArrayList<Producto>>() {
            @Override
            public void onResponse(Call<ArrayList<Producto>> call, Response<ArrayList<Producto>> response) {
                productoList=response.body();
                mutableProductosList.setValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Producto>> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
                productoList=new ArrayList<>();
                mutableProductosList=new MutableLiveData<>();
            }
        });
    }

    //MUESTRA RESTLISTENER
    public void addCategoria(Categoria categoria, final OnRestListener listener){
        Call<Long> call = apiClient.postCategoria(categoria);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long result = response.body();
                Log.d(SUCCEED_TAG, ""+result);
                if(response.body() > 0){
                    fetchCategoriaList();
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e(ERROR_TAG, t.getMessage());
                listener.onFailure(ERROR_TAG);
            }
        });
    }

    public void addProducto(Producto producto, final OnRestListener listener) {
        Call<Long> call = apiClient.postProducto(producto);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long resultado = response.body();
                if (resultado > 0) {
                    fetchProductoList();
                    listener.onSuccess(resultado);
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(ERROR_TAG, "error producto:"+t.getLocalizedMessage());
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void deleteCategoria (long id){
        Call<Integer> call = apiClient.deleteCategoria(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                fetchCategoriaList();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(ERROR_TAG, t.getMessage());
            }
        });
    }

    public void deleteProducto(long id) {
        Call<Integer> call = apiClient.deleteProducto(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                fetchProductoList();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
            }
        });
    }

    public void updateCategoria(long id, Categoria categoria, final OnRestListener listener){
        Call<Categoria> call = apiClient.putCategoria(id, categoria);
        call.enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                Log.d (SUCCEED_TAG, response.body().toString());
                fetchCategoriaList();
                listener.onSuccess(VALOR_SUCCEED);
            }

            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                fetchCategoriaList();
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void updateProducto(long id, Producto producto, final OnRestListener listener) {
        Call<Producto> call = apiClient.putProducto(id, producto);
        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                fetchProductoList();
                listener.onSuccess(VALOR_SUCCEED);
            }
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                fetchProductoList();
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void getOneCategoria(long id){
        Call<Categoria> call = apiClient.getCategoria(id);
        call.enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                mutableCategoria.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                Log.e(ERROR_TAG, t.getMessage());
            }
        });
    }

    public void getSpecificProducto(long id){
        Call<Producto> call = apiClient.getProducto(id);
        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                productoMutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
            }
        });
    }

    public LiveData<List<Empleado>> getLiveEmpleadoList(){
        fetchEmpleadoList();
        return mutableEmpleadoList;
    }

    public LiveData<List<Categoria>> getLiveDataCategoriaList(){
        fetchCategoriaList();
        return mutableCategoriasList;
    }

    public LiveData<List<Producto>> getLiveProductosList(){
        fetchProductoList();
        return mutableProductosList;
    }

    public LiveData<List<Mesa>> getLiveMesaList(){
        fetchMesaList();
        return mutableMesaList;
    }

    public LiveData<Producto> productoLiveData(long id) {
        getSpecificProducto(id);
        return productoMutableLiveData;
    }

    public LiveData<Mesa> mesaLiveData(long id) {
        getSpecificMesa(id);
        return mesaMutableLiveData;
    }

    public LiveData<Empleado> empleadoLiveDataLogin(String username) {
        getSpecificEmpleado(username);
        return empleadoMutableLiveData;
    }

    public LiveData<Factura> facturaLiveData(long id) {
        getSpecificFactura(id);
        return facturaMutableLiveData;
    }

    public void getSpecificFactura(long id){
        Call<Factura> call = apiClient.getFactura(id);
        call.enqueue(new Callback<Factura>() {
            @Override
            public void onResponse(Call<Factura> call, Response<Factura> response) {
               facturaMutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Factura> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
            }
        });
    }

    public void fetchFacturaList() {
        Call<ArrayList<Factura>> call = apiClient.getFactura();
        call.enqueue(new Callback<ArrayList<Factura>>() {
            @Override
            public void onResponse(Call<ArrayList<Factura>> call, Response<ArrayList<Factura>> response) {
                mutableFacturaList.setValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Factura>> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
                mutableFacturaList = new MutableLiveData<>();
            }
        });
    }

    public LiveData<List<Factura>> getLiveFacturaList(){
        fetchFacturaList();
        return mutableFacturaList;
    }

    public void fetchComandaList() {
        Call<ArrayList<Comanda>> call = apiClient.getComanda();
        call.enqueue(new Callback<ArrayList<Comanda>>() {
            @Override
            public void onResponse(Call<ArrayList<Comanda>> call, Response<ArrayList<Comanda>> response) {
                mutableComandaList.setValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Comanda>> call, Throwable t) {
                Log.v(ERROR_TAG, t.getLocalizedMessage());
                mutableComandaList = new MutableLiveData<>();
            }
        });
    }

    public LiveData<List<Comanda>> getLiveComandaList(){
        fetchComandaList();
        return mutableComandaList;
    }

    public void addFactura(Factura factura, final OnRestListener listener) {
        Log.v("---f---", factura.toString());
        Call<Long> call = apiClient.postFactura(factura);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                fetchFacturaList();
                if(response.body() != null){
                    long resultado = response.body();
                    if (resultado > 0) {
                        listener.onSuccess(resultado);
                    }
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(ERROR_TAG, "error factura:"+t.getLocalizedMessage());
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void addComanda(Comanda comanda, final OnRestListener listener) {
        Call<Long> call = apiClient.postComanda(comanda);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                fetchComandaList();
                if(response.body() != null){
                    long resultado = response.body();
                    if (resultado > 0) {
                        listener.onSuccess(resultado);
                    }
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(ERROR_TAG, "error comanda:"+t.getLocalizedMessage());
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void deleteComanda(long id) {
        Call<Integer> call = apiClient.deleteComanda(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                fetchComandaList();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void updateComanda(long id, Comanda comanda, final OnRestListener listener) {
        Call<Comanda> call = apiClient.putComanda(id, comanda);
        call.enqueue(new Callback<Comanda>() {
            @Override
            public void onResponse(Call<Comanda> call, Response<Comanda> response) {
                fetchComandaList();
                listener.onSuccess(VALOR_SUCCEED);
            }
            @Override
            public void onFailure(Call<Comanda> call, Throwable t) {
                fetchComandaList();
                listener.onFailure(t.getMessage());
            }
        });
    }

    public void updateFactura(long id, Factura factura, final OnRestListener listener) {
        Call<Factura> call = apiClient.putFactura(id, factura);
        call.enqueue(new Callback<Factura>() {
            @Override
            public void onResponse(Call<Factura> call, Response<Factura> response) {
                fetchFacturaList();
                listener.onSuccess(VALOR_SUCCEED);
            }
            @Override
            public void onFailure(Call<Factura> call, Throwable t) {
                fetchFacturaList();
                listener.onFailure(t.getMessage());
            }
        });
    }
}
