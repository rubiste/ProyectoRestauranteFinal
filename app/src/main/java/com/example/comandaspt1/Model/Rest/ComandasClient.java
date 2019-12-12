package com.example.comandaspt1.Model.Rest;

import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Data.Producto;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ComandasClient {

    //EMPLEADO

    @DELETE("empleado/{id}")
    Call<Integer> deleteEmpleado(@Path("id") long id);

    @GET("empleado/{id}")
    Call<Empleado> getEmpleado(@Path("id") long id);

    @GET("empleado/login/{username}")
    Call<Empleado> getEmpleadoLogin(@Path("username") String username);

    @GET("empleado")
    Call<ArrayList<Empleado>> getEmpleado();

    @POST("empleado")
    Call<Long> postEmpleado(@Body Empleado empleado);

    @PUT("empleado/{id}")
    Call<Empleado> putEmpleado(@Path("id") long id, @Body Empleado empleado);

    //MESA

    @DELETE("mesa/{id}")
    Call<Integer> deleteMesa(@Path("id") long id);

    @GET("mesa/{id}")
    Call<Mesa> getMesa(@Path("id") long id);

    @GET("mesa")
    Call<ArrayList<Mesa>> getMesa();

    @POST("mesa")
    Call<Long> postMesa(@Body Mesa mesa);

    @PUT("mesa/{id}")
    Call<Mesa> putMesa(@Path("id") long id, @Body Mesa mesa);

    //CATEGORIAS
    @DELETE("categoria/{id}")
    Call<Integer> deleteCategoria(@Path("id") long id);

    @GET("categoria/{id}")
    Call<Categoria> getCategoria(@Path("id") long id);

    @GET("categoria")
    Call<ArrayList<Categoria>> getCategorias();

    @POST("categoria")
    Call<Long> postCategoria(@Body Categoria categorias);

    @PUT("categoria/{id}")
    Call<Categoria> putCategoria(@Path("id") long id, @Body Categoria categoria);

    //PRODUCTO

    @DELETE("producto/{id}")
    Call<Integer> deleteProducto(@Path("id") long id);

    @GET("producto/{id}")
    Call<Producto> getProducto(@Path("id") long id);

    @GET("producto")
    Call<ArrayList<Producto>> getProductos();

    @POST("producto")
    Call<Long> postProducto(@Body Producto producto);

    @PUT("producto/{id}")
    Call<Producto> putProducto(@Path("id") long id, @Body Producto producto);

    //IMÁGENES

    @Multipart
    @POST("upload")
    Call<String> fileUpload(@Part MultipartBody.Part file);

    //FACTURA

    @DELETE("factura/{id}")
    Call<Integer> deleteFactura(@Path("id") long id);

    @GET("factura/{id}")
    Call<Factura> getFactura(@Path("id") long id);

    @GET("factura")
    Call<ArrayList<Factura>> getFactura();

    @POST("factura")
    Call<Long> postFactura(@Body Factura factura);

    @PUT("factura/{id}")
    Call<Factura> putFactura(@Path("id") long id, @Body Factura factura);

    //COMANDA

    @DELETE("comanda/{id}")
    Call<Integer> deleteComanda(@Path("id") long id);

    @GET("comanda/{id}")
    Call<Comanda> getComanda(@Path("id") long id);

    @GET("comanda")
    Call<ArrayList<Comanda>> getComanda();

    @POST("comanda")
    Call<Long> postComanda(@Body Comanda comanda);

    @PUT("comanda/{id}")
    Call<Comanda> putComanda(@Path("id") long id, @Body Comanda comanda);
}
