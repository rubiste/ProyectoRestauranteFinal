package com.example.comandaspt1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaAdapter;
import com.example.comandaspt1.View.ProductoAdapter;

import java.util.ArrayList;
import java.util.List;

public class VerProductos extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String KEY_INTENT_EDITAR = "Categoria";
    public static MainViewModel viewModel;
    private ProductoAdapter productoAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static VerProductos contextMain;
    private RecyclerView recyclerProductos;
    private SearchView svProductos;
    private Categoria categoria;
    private long idCategoria;
    private Button btCrearProducto;
    private View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_productos);

        btCrearProducto = findViewById(R.id.btNuevoProducto);

        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);

        if(idFactura > 0){
            btCrearProducto.setVisibility(View.GONE);
        }

        btCrearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerProductos.this, AddProducto.class);
                intent.putExtra(ID, idCategoria);
                escribirShared();
                startActivity(intent);
            }
        });

        initComponents();
    }

    public void escribirShared(){
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_INTENT_EDITAR, idCategoria);
        editor.commit();
    }

    private void initComponents() {
        fragment = findViewById(R.id.fProductos);
        contextMain = this;

        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idAux = sharedPreferences.getLong(KEY_INTENT_EDITAR, 0);
        if(idAux == 0){
            categoria = getIntent().getParcelableExtra(KEY_INTENT_EDITAR);
            idCategoria = categoria.getId();
        }else{
            idCategoria = idAux;
        }

        productoAdapter = new ProductoAdapter(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveDataProductosList().observe(this, new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                ArrayList<Producto> productosAux = new ArrayList<>();
                for (int i = 0; i < productos.size(); i++) {
                    if (productos.get(i).getIdCategoria() == idCategoria) {
                        productosAux.add(productos.get(i));
                    }
                }
                productoAdapter.setData(productosAux);
                finalizarRelleno();
            }
        });
    }

    private void finalizarRelleno() {
        recyclerProductos = findViewById(R.id.rvProducto);
        layoutManager = new LinearLayoutManager(this);

        recyclerProductos.setLayoutManager(layoutManager);
        recyclerProductos.setAdapter(productoAdapter);

        svProductos = findViewById(R.id.svProductos);
        svProductos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productoAdapter.getFilter().filter(newText);
                return false;
            }
        });

        fragment.setVisibility(View.INVISIBLE);
    }

    public static VerProductos getContext() {
        return contextMain;
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
            Intent i = new Intent(VerProductos.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

