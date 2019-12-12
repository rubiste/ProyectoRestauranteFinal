package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.View.MainViewModel;
import com.example.comandaspt1.View.MesaGerenteAdapter;

import java.util.List;

public class MesasGerente extends AppCompatActivity {

    public static MainViewModel viewModel;
    private MesaGerenteAdapter adapter;
    private Button btAdd;
    private SearchView svMesas;
    private View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_gerente);
        init();
    }

    private void init() {
        fragment = findViewById(R.id.fMesasGerente);

        adapter = new MesaGerenteAdapter(this);
        viewModel =  ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveMesaList().observe(this,  new Observer<List<Mesa>>() {
            @Override
            public void onChanged(List<Mesa> mesas) {
                adapter.setData(mesas);
                finalizarRelleno();
            }
        });
    }

    private void finalizarRelleno() {
        btAdd = findViewById(R.id.btAdd);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesasGerente.this, AddMesaGerente.class);
                startActivity(intent);
            }
        });

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        svMesas = findViewById(R.id.svMesasGerente);
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
            Intent i = new Intent(MesasGerente.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MesasGerente.this, MesasLocal.class);
        startActivity(intent);
        finish();
    }
}
