package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.View.ComandasAdapter;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Facturas extends AppCompatActivity {

    public static MainViewModel viewModel;
    private ComandasAdapter adapter;

    private FloatingActionButton btImpresion;
    private Button btAdd;
    private SearchView svComandas;
    private View fragment;
    private SwipeRefreshLayout swipeFactura;

    public static final String IDFACTURA = "idFactura";

    private long idFactura;
    private boolean avanzar = true, vacio = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturas);
        init();
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        idFactura = sharedPreferences.getLong(IDFACTURA, 0);

        fragment = findViewById(R.id.fFacturas);

        btAdd = findViewById(R.id.btAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Facturas.this, Categorias.class);
                startActivity(intent);
            }
        });
        adapter = new ComandasAdapter(this);

        btImpresion = findViewById(R.id.btImpresion);
        btImpresion.setVisibility(View.GONE);


        cargaViewModel();

        swipeFactura = findViewById(R.id.swipeFacturas);
        swipeFactura.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                svComandas.setQuery("", false);
                fragment.setVisibility(View.VISIBLE);
                cargaViewModel();
            }
        });
    }

    private void prepararBoton(List<Comanda> comandas) {
        boolean cerradas = true;
        int cont = 0;

        for (int i = 0; i < comandas.size(); i++) {
            if (comandas.get(i).getEntregado() == 0 && comandas.get(i).getIdFactura() == idFactura){
                cerradas = false;
            }
            if(comandas.get(i).getIdFactura() == idFactura){
                cont++;
            }
        }

        if(cont > 0 && cerradas){
            btImpresion.setVisibility(View.VISIBLE);
        }
    }

    private void cargaViewModel(){
        viewModel =  ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveDataProductosList().observe(this, new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                adapter.setProductos(productos);
                cogerComandas();
            }
        });
    }

    private void cogerComandas() {
        viewModel.getLiveComandaList().observe(this, new Observer<List<Comanda>>() {
            @Override
            public void onChanged(List<Comanda> comandas) {
                adapter.setData(comandas);
                prepararBoton(comandas);
                rellenarRecycler();
            }
        });
    }

    private void rellenarRecycler() {
        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
        initSearchPrint();
    }

    private void initSearchPrint() {
        svComandas = findViewById(R.id.svComandas);
        svComandas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        btImpresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Facturas.this);
                builder.setMessage(R.string.dialogoMensajeImpresion)
                        .setTitle(R.string.dialogoTituloImpresion);
                builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragment.setVisibility(View.VISIBLE);
                        btImpresion.setClickable(false);
                        Intent intent = new Intent(Facturas.this, MainImpresion.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        fragment.setVisibility(View.INVISIBLE);
        swipeFactura.setRefreshing(false);
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
            Intent i = new Intent(Facturas.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(IDFACTURA, 0);
        editor.commit();
        Intent intent = new Intent(this, MesasLocal.class);
        startActivity(intent);
        finish();
    }
}


