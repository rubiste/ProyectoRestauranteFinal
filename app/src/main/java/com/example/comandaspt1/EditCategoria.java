package com.example.comandaspt1;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class EditCategoria extends AppCompatActivity {

    public MainViewModel viewModel;

    private ImageView ivEditarCategoria;
    private TextInputEditText etEditarCategoria;
    private Button btEditarCategoria;
    private View fragment;

    private final String IMGS_CATEGORIA = "/upload/";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;
    private long idEditar;

    private String fotoCategoria="", url="", enlaceCategoria;
    private Uri enlace;
    private Categoria c;

    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    public static final String KEY_INTENT_EDITAR = "Categoria";
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";
    public static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categoria);

        initComponents();
    }
    private void initComponents() {
        Repository repository = new Repository();
        url = repository.getUrl();
        viewModel = Categorias.viewModel;
        fragment = findViewById(R.id.fEditCategoria);
        fragment.setVisibility(View.INVISIBLE);
        ivEditarCategoria = findViewById(R.id.ivEditarCategoria);
        etEditarCategoria = findViewById(R.id.etNombreEditarCategoria);
        btEditarCategoria = findViewById(R.id.btEditarCategoria);

        c = getIntent().getParcelableExtra(KEY_INTENT_EDITAR);

        etEditarCategoria.setText(c.getNombre());
        enlaceCategoria = url+IMGS_CATEGORIA+c.getImagen();
        Glide.with(EditCategoria.this)
                .load(enlaceCategoria)
                .override(500, 500)
                .into(ivEditarCategoria);

        btEditarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRellenado()) {
                    idEditar = c.getId();
                    if(enlace != null){
                        saveSelectedImageInFile(enlace);
                    }
                    insertData();
                }
            }
        });

        ivEditarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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
            enlaceCategoria = uri.toString();
            Glide.with(this)
                    .load(uri)
                    .override(500, 500)
                    .into(ivEditarCategoria);
        }
    }

    private boolean isRellenado() {
        if(etEditarCategoria.getText().toString().length() == 0){
            Toast.makeText(this, getResources().getText(R.string.errorNombreC), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        fotoCategoria = randomString(15)+".jpg";
        File file = new File(getFilesDir(), fotoCategoria);
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

    private void establecerCampos(){
        c.setNombre(etEditarCategoria.getText().toString());
        if(!fotoCategoria.equalsIgnoreCase("")){
            c.setImagen(fotoCategoria);
        }
    }

    private void insertData() {
        fragment.setVisibility(View.VISIBLE);
        establecerCampos();
        viewModel.updateCategoria(idEditar, c, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                fragment.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onFailure(String message) {
                fragment.setVisibility(View.INVISIBLE);
                finish();
            }
        });
    }

    private void insertDataFoto(File file){
        fragment.setVisibility(View.VISIBLE);
        btEditarCategoria.setClickable(false);
        ivEditarCategoria.setClickable(false);

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nomCategoriaEditar", etEditarCategoria.getText().toString());
        outState.putString("fotoCategoriaEditar", enlaceCategoria);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etEditarCategoria.setText(savedInstanceState.getString("nomCategoriaEditar"));
        String imagen = savedInstanceState.getString("fotoCategoriaEditar");
        if(!imagen.equalsIgnoreCase("")){
            Uri imgUri=Uri.parse(imagen);
            enlaceCategoria = imagen;
            enlace = imgUri;
            Glide.with(EditCategoria.this)
                    .load(imgUri)
                    .override(500,500)
                    .into(ivEditarCategoria);
        }
    }
}

