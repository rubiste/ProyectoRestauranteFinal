package com.example.comandaspt1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaAdapter;
import com.example.comandaspt1.View.ProductoAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Comanda extends AppCompatActivity {

    public static MainViewModel viewModel;
    private Producto producto;
    private Repository repository;

    private TextInputEditText etNombre, etDescripcion, etPrecio, etUnidades;
    private MaterialButton btCrear;
    private ImageView ivImagen;
    private View fragment;

    private static final String STORAGE = "/upload/";
    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        init();
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        repository = new Repository();

        producto = getIntent().getExtras().getParcelable(ProductoAdapter.PRODUCTO);
        fragment = findViewById(R.id.fCrearComandas);
        fragment.setVisibility(View.INVISIBLE);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etUnidades = findViewById(R.id.etUnidades);
        btCrear = findViewById(R.id.btCrear);
        ivImagen = findViewById(R.id.ivImagen);

        etNombre.setText(producto.getNombre());
        etDescripcion.setText(producto.getDescripcion());
        etPrecio.setText(""+producto.getPrecio());
        Glide.with(this)
                .load(repository.getUrl()+STORAGE+producto.getImagen())
                .override(400, 400)
                .into(ivImagen);

        btCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etUnidades.getText().toString().equalsIgnoreCase("")){
                    rellenarDatos();
                }else{
                    Toast.makeText(Comanda.this, R.string.vacio, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void rellenarDatos(){
        btCrear.setClickable(false);
        fragment.setVisibility(View.VISIBLE);
        com.example.comandaspt1.Model.Data.Comanda comanda = new com.example.comandaspt1.Model.Data.Comanda();

        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idfactura = sharedPreferences.getLong(MesaAdapter.IDFACTURA, 0);
        long idEmpleado = sharedPreferences.getLong(MainActivity.IDEMPLEADO, 0);
        comanda.setIdEmpleado(idEmpleado);
        comanda.setIdFactura(idfactura);
        comanda.setIdProducto(producto.getId());
        comanda.setPrecio(producto.getPrecio());
        comanda.setUnidades(Integer.parseInt(etUnidades.getText().toString()));
        comanda.setEntregado(0);

        crearComanda(comanda);
    }

    private void crearComanda(com.example.comandaspt1.Model.Data.Comanda comanda) {
        viewModel.addComanda(comanda, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                finish();
                fragment.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                Log.d(ERROR_TAG, message);
                finish();
                fragment.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mesas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(MainActivity.EMPLEADOREMEMBER, 0);
            editor.putInt(MainActivity.GERENTEREMEMBER, 0);
            editor.putInt(MesaAdapter.IDFACTURA, 0);
            editor.commit();
            Intent i = new Intent(Comanda.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("unidadesComanda", etUnidades.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etUnidades.setText(savedInstanceState.getString("unidadesComanda"));
    }
}
