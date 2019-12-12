package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class SingleEmpleadoGerente extends AppCompatActivity {

    public MainViewModel viewModel;
    private TextInputEditText tvNombre, tvApellido, tvUsername, tvTelefono;
    private SwitchMaterial sGerente;
    private ImageView ivImagen;
    private Empleado empleado;
    private String url = "";
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_empleado_gerente);
        init();
    }

    private void init() {
        repository = new Repository();
        url = repository.getUrl();
        empleado = getIntent().getExtras().getParcelable("empleado");

        viewModel = EmpleadosGerente.viewModel;

        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.etApellidos);
        tvUsername = findViewById(R.id.etUsername);
        tvTelefono = findViewById(R.id.etTelefono);
        sGerente = findViewById(R.id.sGerente);
        ivImagen = findViewById(R.id.ivImagen);

        tvNombre.setText(empleado.getNombre());
        tvApellido.setText(empleado.getApellido());
        tvUsername.setText(empleado.getUsername());
        tvTelefono.setText(""+empleado.getTelefono());

        if(empleado.getGerente() != 0){
            sGerente.setChecked(true);
        }else{
            sGerente.setChecked(false);
        }

        Glide.with(SingleEmpleadoGerente.this)
                .load(url+"/upload/"+empleado.getImagen())
                .override(500, 500)
                .into(ivImagen);
    }
}
