package com.example.comandaspt1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.View.EmpleadoAdapter;
import com.example.comandaspt1.View.MainViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARCHIVO = "Comandas";
    public static final String EMPLEADOREMEMBER = "idempleadoRecordado";
    public static final String GERENTEREMEMBER = "gerenteRecordado";
    public static final String IDEMPLEADO = "idEmpleado";
    public static final String GERENTE = "valorGerente";
    private String url="";
    private boolean con = false;

    private static MainActivity mContext;

    private TextView tvLogo;
    private EditText etUsername, etPassword;
    private Button btLogin;
    private EmpleadoAdapter adapter;
    private List<Empleado> empleadoList;
    private CheckBox cbRecordar;
    private View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init(){
        tvLogo = findViewById(R.id.tvLogo);
        aplicarFuente();
        fragment = findViewById(R.id.fLogin);
        fragment.setVisibility(View.INVISIBLE);

        cbRecordar = findViewById(R.id.cbRecordar);
        adapter = new EmpleadoAdapter(this);
        mContext = this;
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);

        MainViewModel viewModel =  ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);

        viewModel.getLiveEmpleadoList().observe(this, new Observer<List<Empleado>>() {
            @Override
            public void onChanged(List<Empleado> empleados) {
                fragment.setVisibility(View.VISIBLE);
                checkRemember();
                adapter.setData(empleados);
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        empleadoList = adapter.getData();
        fragment.setVisibility(View.INVISIBLE);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setVisibility(View.VISIBLE);
                btLogin.setClickable(false);

                if(etPassword.getText().toString().length()<=30) {
                    if (!etUsername.getText().toString().equalsIgnoreCase("") && !etPassword.getText().toString().equalsIgnoreCase("")) {
                        boolean found = false;
                        boolean match = false;
                        for (int i = 0; i < empleadoList.size(); i++) {
                            if (empleadoList.get(i).getUsername().equalsIgnoreCase(etUsername.getText().toString())) {
                                found = true;
                                if (empleadoList.get(i).getPassword().equalsIgnoreCase(sha1(etPassword.getText().toString()))) {
                                    match = true;
                                    SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putLong(IDEMPLEADO, empleadoList.get(i).getId());
                                    editor.putInt(GERENTE, empleadoList.get(i).getGerente());
                                    editor.commit();
                                    if (cbRecordar.isChecked()) {
                                        editor.putLong(EMPLEADOREMEMBER, empleadoList.get(i).getId());
                                        editor.putInt(GERENTEREMEMBER, empleadoList.get(i).getGerente());
                                        editor.commit();
                                    }
                                    fragment.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(MainActivity.this, MesasLocal.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                        if (!found || !match) {
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.vacio, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, R.string.passwordLength, Toast.LENGTH_SHORT).show();
                }
                fragment.setVisibility(View.INVISIBLE);
                btLogin.setClickable(true);
            }
        });
    }

    public void checkRemember(){
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long id = sharedPreferences.getLong(EMPLEADOREMEMBER, 0);
        if(id != 0){
            Intent intent = new Intent(MainActivity.this, MesasLocal.class);
            startActivity(intent);
            finish();
            fragment.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Conexion.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.contentProvider) {
            Intent intent = new Intent(MainActivity.this, MainProvider.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static MainActivity getContext() {
        return mContext;
    }

    public static String sha1(String txt) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("SHA1");
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username",etUsername.getText().toString());
        outState.putString("password",etPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etUsername.setText(savedInstanceState.getString("username"));
        etPassword.setText(savedInstanceState.getString("password"));
    }

    private void aplicarFuente(){
        String fuente = "fuentes/prueba.ttf";
        Typeface tpFace = Typeface.createFromAsset(getAssets(), fuente);

        tvLogo.setTypeface(tpFace);
    }
}
