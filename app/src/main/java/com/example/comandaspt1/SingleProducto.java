package com.example.comandaspt1;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;

public class SingleProducto extends AppCompatActivity {

    public MainViewModel viewModel;
    private static String TAG = "xzy";
    private static final String ID = "ID";
    private ImageView ivProducto, imgProducto;
    private String imgRuta, url="", enlaceProducto;
    private EditText etNombre, etPrecio, etDescripcion;
    private TextView tvDestino, tvIdCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_producto);

        initComponents();
    }

    private void initComponents() {

        viewModel = VerProductos.viewModel;
        ivProducto = findViewById(R.id.ivProducto);
        imgProducto = findViewById(R.id.ivProducto);
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        tvDestino = findViewById(R.id.tvDestino);
        etDescripcion = findViewById(R.id.etDescripcion);
        Repository repository = new Repository();
        url = repository.getUrl();
        final long id = getIntent().getLongExtra(ID, 0);

        viewModel.productoLiveData(id).observe(SingleProducto.this, new Observer<Producto>() {
            @Override
            public void onChanged(Producto producto) {

                Producto productoEdit = producto;

                etNombre.setText(productoEdit.getNombre());
                etPrecio.setText(String.valueOf(productoEdit.getPrecio()));
                tvDestino.setText(productoEdit.getDestino());
                tvIdCategoria.setText(String.valueOf(productoEdit.getIdCategoria()));
                etDescripcion.setText(productoEdit.getDescripcion());

                Glide.with(SingleProducto.this)
                        .load(url+"/upload/"+producto.getImagen())
                        .override(500, 500)
                        .into(ivProducto);
            }
        });
    }
}
