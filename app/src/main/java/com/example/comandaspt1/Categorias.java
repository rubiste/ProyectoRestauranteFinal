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
import com.example.comandaspt1.View.CategoriaAdapter;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaAdapter;

import java.util.List;

public class Categorias extends AppCompatActivity {

    public static MainViewModel viewModel;
    private CategoriaAdapter categoriaAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static Categorias contextMain;
    
    private RecyclerView recyclerCategorias;
    private SearchView svCategorias;
    private Button btAddcategoria;
    private View fragment;

    public static final String KEY_INTENT_EDITAR = "Categoria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        initComponents();
    }

    private void borrarShared(){
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_INTENT_EDITAR, 0);
        editor.commit();
    }

    private void initComponents() {
        contextMain = this;
        fragment = findViewById(R.id.fCategorias);
        borrarShared();
        categoriaAdapter = new CategoriaAdapter(this, new CategoriaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria categoria, Class myClass) {
                Intent i = new Intent(Categorias.this, myClass);
                i.putExtra(KEY_INTENT_EDITAR, categoria);
                startActivity(i);
            }
        });

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveDataCategoriasList().observe(this, new Observer<List<Categoria>>() {
            @Override
            public void onChanged(List<Categoria> categorias) {
                categoriaAdapter.setData(categorias);
                finalizaRelleno();
            }
        });
    }

    public void finalizaRelleno(){
        btAddcategoria = findViewById(R.id.btAñadirCategoria);

        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);

        if(idFactura > 0){
            btAddcategoria.setVisibility(View.GONE);
        }

        btAddcategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, AddCategoria.class);
                startActivity(i);
            }
        });

        recyclerCategorias = findViewById(R.id.rvCategorias);

        layoutManager = new LinearLayoutManager(this);
        recyclerCategorias.setLayoutManager(layoutManager);
        recyclerCategorias.setAdapter(categoriaAdapter);

        svCategorias = findViewById(R.id.svCategorias);
        svCategorias.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                categoriaAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                categoriaAdapter.getFilter().filter(newText);
                return false;
            }
        });

        fragment.setVisibility(View.INVISIBLE);
    }

    public static Categorias getContext() {
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
            Intent i = new Intent(Categorias.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);
        if(idFactura == 0){
            super.onBackPressed();
        }
    }
}
