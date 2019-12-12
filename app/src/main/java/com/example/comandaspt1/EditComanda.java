package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditComanda extends AppCompatActivity {

    private MainViewModel viewModel;
    private Comanda comanda;

    private Producto producto;
    private ImageView ivImagen;
    private TextInputEditText etDescripcion, etNombre, etUnidades, etPrecio;
    private MaterialButton btEditar;
    private View fragment;

    private static final String STORAGE = "/upload/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comanda);
        init();
    }

    private void init() {
        comanda = getIntent().getExtras().getParcelable("comanda");
        producto = getIntent().getExtras().getParcelable("producto");

        fragment = findViewById(R.id.fEditComanda);
        fragment.setVisibility(View.INVISIBLE);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUnidades = findViewById(R.id.etUnidades);
        etPrecio = findViewById(R.id.etPrecio);
        ivImagen = findViewById(R.id.ivImagen);
        btEditar = findViewById(R.id.btEditar);

        etNombre.setText(producto.getNombre());
        etUnidades.setText(""+comanda.getUnidades());
        etDescripcion.setText(producto.getDescripcion());
        etPrecio.setText(""+comanda.getPrecio());
        Repository repository = new Repository();
        Glide.with(this)
                .load(repository.getUrl()+STORAGE+producto.getImagen())
                .override(400, 400)
                .into(ivImagen);
        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etUnidades.getText().toString().equalsIgnoreCase("") &&
                        !etUnidades.getText().toString().equalsIgnoreCase(""+comanda.getUnidades())){
                    crearComanda();
                }
            }
        });
    }

    private void crearComanda() {
        fragment.setVisibility(View.VISIBLE);
        Comanda c = new Comanda();
        c.setId(comanda.getId());
        c.setPrecio(producto.getPrecio());
        c.setEntregado(0);
        c.setIdProducto(comanda.getIdProducto());
        c.setIdEmpleado(comanda.getIdEmpleado());
        c.setIdFactura(comanda.getIdFactura());
        c.setUnidades(Integer.parseInt(etUnidades.getText().toString()));
        actualizarComanda(c);
    }

    private void actualizarComanda(Comanda comanda) {
        viewModel = Facturas.viewModel;
        viewModel.updateComanda(comanda.getId(), comanda, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                finish();
                fragment.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                finish();
                fragment.setVisibility(View.INVISIBLE);
            }
        });
    }
}
