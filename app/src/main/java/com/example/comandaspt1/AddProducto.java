package com.example.comandaspt1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static android.provider.SyncStateContract.Columns.DATA;

public class AddProducto extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    public MainViewModel viewModel;
    private TextInputEditText etNombre, etPrecio, etDescripcion;
    private TextView tvDestino;
    private ImageView imgProducto;
    private Button btCrear;
    private View fragment;

    public static final String KEY_INTENT_EDITAR = "Categoria";
    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";

    private String imgRuta = "", enlaceProducto="";
    private Uri enlace;
    private long idCategoria;

    public static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producto);
        initComponents();
        initEvents();
    }

    private void initComponents() {
        viewModel = VerProductos.viewModel;
        fragment = findViewById(R.id.fAddProducto);
        fragment.setVisibility(View.INVISIBLE);

        idCategoria = getIntent().getLongExtra(VerProductos.ID, 0);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        tvDestino = findViewById(R.id.tvDestino);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgProducto = findViewById(R.id.ivCrearProducto);
        btCrear = findViewById(R.id.btCrearProducto);
    }

    private void initEvents() {

        imgProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etNombre.getText().toString().equalsIgnoreCase("") ||
                        etPrecio.getText().toString().equalsIgnoreCase("") ||
                        tvDestino.getText().toString().equalsIgnoreCase("") ||
                        etDescripcion.getText().toString().equalsIgnoreCase("") ||
                        enlace == null){
                    Toast.makeText(getApplicationContext(), R.string.vacio, Toast.LENGTH_SHORT).show();
                } else {
                    saveSelectedImageInFile(enlace);
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*/*");
        String[] types = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
        startActivityForResult(intent, SELECT_IMAGE_TO_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_TO_UPLOAD && resultCode == Activity.RESULT_OK && null != data) {
            Uri uri = data.getData();
            enlace = uri;
            enlaceProducto = uri.toString();
            Glide.with(this)
                    .load(uri)
                    .override(500, 500)
                    .into(imgProducto);
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.producto_destino_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cocina:
                tvDestino.setText("Cocina");
                return true;
            case R.id.barra:
                tvDestino.setText("Barra");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void saveSelectedImageInFile(Uri uri) {
        Bitmap bitmap = null;
        if(Build.VERSION.SDK_INT < 28) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                bitmap = null;
            }
        } else {
            try {
                final InputStream in = this.getContentResolver().openInputStream(uri);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                bitmap = null;
            }
        }
        if(bitmap != null) {
            File file = saveBitmapInFile(bitmap);
            if(file != null) {
                insertDataFoto(file);
            }
        }
    }

    private File saveBitmapInFile(Bitmap bitmap) {
        imgRuta = randomString(15)+".jpg";
        File file = new File(getFilesDir(), imgRuta);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            file = null;
        }
        return file;
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        return sb.toString();
    }

    private void insertData() {
        Producto p = establecerCampos();
        viewModel.addProducto(p, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                fragment.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Log.d(ERROR_TAG, message);
                fragment.setVisibility(View.INVISIBLE);
                finish();
            }
        });
    }

    private Producto establecerCampos() {
        Producto producto = new Producto();
        producto.setNombre(etNombre.getText().toString());
        producto.setPrecio(Float.parseFloat(etPrecio.getText().toString()));
        producto.setDestino(tvDestino.getText().toString());
        producto.setIdCategoria(idCategoria);
        producto.setDescripcion(etDescripcion.getText().toString());
        producto.setImagen(imgRuta);

        return producto;
    }

    private void insertDataFoto(File file){
        fragment.setVisibility(View.VISIBLE);
        btCrear.setClickable(false);
        imgProducto.setClickable(false);

        viewModel.upload(file, new OnRestListenerFoto() {
            @Override
            public void onSuccess(String message) {
                Log.d(SUCCEED_TAG, message);
                insertData();
            }

            @Override
            public void onFailure(String message) {
                Log.d(ERROR_TAG, message);
                fragment.setVisibility(View.INVISIBLE);
                finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle guardarEstado) {
        super.onSaveInstanceState(guardarEstado);
        guardarEstado.putString("nombre", etNombre.getText().toString());
        guardarEstado.putString("precio", etPrecio.getText().toString());
        guardarEstado.putString("destino", tvDestino.getText().toString());
        guardarEstado.putString("descripcion", etDescripcion.getText().toString());
        guardarEstado.putString("imagenProducto", enlaceProducto);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etNombre.setText(savedInstanceState.getString("nombre"));
        etPrecio.setText(savedInstanceState.getString("precio"));
        tvDestino.setText(savedInstanceState.getString("destino"));
        etDescripcion.setText(savedInstanceState.getString("descripcion"));
        String imagen = savedInstanceState.getString("imagenProducto");
        if(!imagen.equalsIgnoreCase("")){
            Uri imgUri=Uri.parse(imagen);
            enlaceProducto = imagen;
            enlace = imgUri;
            imgProducto.setImageURI(null);
            imgProducto.setImageURI(imgUri);
        }
    }
}
