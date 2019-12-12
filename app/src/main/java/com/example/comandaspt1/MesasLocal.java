package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MesasLocal extends AppCompatActivity implements ComponentCallbacks2 {

    public static MainViewModel viewModel;
    private MesaAdapter adapter;

    private FloatingActionButton fbMesa, fbEmpleado, fbCategoria;
    private View fragment;
    private SearchView svMesas;
    private SwipeRefreshLayout swipeMesasLocal;

    private static final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_local);

        init();
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MesaAdapter.IDFACTURA, 0);
        editor.commit();
        fragment = findViewById(R.id.fMesasLocal);
        adapter = new MesaAdapter(this);

        cargaViewModel();

        swipeMesasLocal = findViewById(R.id.swipeMesasLocal);
        swipeMesasLocal.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*Log.v("-------swipe--","donete");
                svMesas.setQuery("", false);
                fragment.setVisibility(View.VISIBLE);

                cargaViewModel();*/

                Intent i = new Intent(MesasLocal.this, MesasLocal.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void cargaViewModel(){
        viewModel =  ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveMesaList().observe(this, new Observer<List<Mesa>>() {
            @Override
            public void onChanged(List<Mesa> mesas) {
                adapter.setData(mesas);
                cogerFacturas();
            }
        });
    }

    public void cogerFacturas(){
        viewModel.getLiveFacturaList().observe(this, new Observer<List<Factura>>() {
            @Override
            public void onChanged(List<Factura> facturas) {
                adapter.setFacturas(facturas);
                Log.d("xyz", "Entra en facturas");
                finalizaRelleno();
            }
        });
    }

    private void finalizaRelleno() {
        fbMesa = findViewById(R.id.fbAddMesa);
        fbEmpleado = findViewById(R.id.fbRecyclerEmpleados);
        fbCategoria = findViewById(R.id.fbRecyclerCategoria);

        fbMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesasLocal.this, MesasGerente.class);
                startActivity(intent);
            }
        });

        fbEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesasLocal.this, EmpleadosGerente.class);
                startActivity(intent);
            }
        });

        fbCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesasLocal.this, Categorias.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = MesasLocal.this.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        if(sharedPref.getInt(MainActivity.GERENTE, 0) == 0){
            fbMesa.setVisibility(View.GONE);
            fbEmpleado.setVisibility(View.GONE);
            fbCategoria.setVisibility(View.GONE);

        }

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        svMesas = findViewById(R.id.svMesasLocal);
        svMesas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        fragment.setVisibility(View.INVISIBLE);
        swipeMesasLocal.setRefreshing(false);
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
            editor.commit();
            Intent i = new Intent(MesasLocal.this, MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void addFactura(Factura factura){

        viewModel.addFactura(factura, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
            }

            @Override
            public void onFailure(String message) {
                Log.v(ERROR_TAG,message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(svMesas != null) {
            svMesas.setQuery("", false);
        }
    }
}
