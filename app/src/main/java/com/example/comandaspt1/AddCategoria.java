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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AddCategoria extends AppCompatActivity {

    public MainViewModel viewModel;

    private ImageView ivCrearCategoria;
    private TextInputEditText etCrearCategoria;
    private Button btCrearCategoria;
    private View fragment;

    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;

    private Uri enlace;
    private String fotoCategoria, enlaceImagen;
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";
    public static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);
        initComponents();
    }

    private void initComponents() {
        fragment = findViewById(R.id.fAddCategoria);
        fragment.setVisibility(View.INVISIBLE);
        ivCrearCategoria = findViewById(R.id.ivCrearCategoria);
        etCrearCategoria = findViewById(R.id.etNombreCrearCategoria);
        btCrearCategoria = findViewById(R.id.btCrearCategoria);
        viewModel = Categorias.viewModel;

        ivCrearCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btCrearCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haRellenado()){
                    guardarCategoria();
                }
            }
        });
    }

    private void guardarCategoria() {
        saveSelectedImageInFile(enlace);
        if (fotoCategoria == null){
            Toast.makeText(this, "Error: No se ha podido subir la fotografia", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean haRellenado() {
        if (etCrearCategoria.length() == 0){
            Toast.makeText(this, getResources().getText(R.string.errorNombreC), Toast.LENGTH_SHORT).show();
            return false;
        }else if(enlace == null){
            Toast.makeText(this, getResources().getText(R.string.errorFotoC), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            enlaceImagen = uri.toString();
            fotoCategoria = uri.toString();
            Glide.with(this)
                    .load(uri)
                    .override(500, 500)
                    .into(ivCrearCategoria);
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

    private void insertData() {
        Categoria c = establecerCampos();
        viewModel.addCategoria(c, new OnRestListener() {
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

    private Categoria establecerCampos() {
        Categoria c = new Categoria();
        c.setNombre(etCrearCategoria.getText().toString());
        c.setImagen(fotoCategoria);
        return c;
    }

    private void insertDataFoto(File file){
        fragment.setVisibility(View.VISIBLE);
        btCrearCategoria.setClickable(false);
        ivCrearCategoria.setClickable(false);

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nomCategoriaAñadir", etCrearCategoria.getText().toString());
        outState.putString("imgCategoriaAñadir", enlaceImagen);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etCrearCategoria.setText(savedInstanceState.getString("nomCategoriaAñadir"));
        String imagen = savedInstanceState.getString("imgCategoriaAñadir");
        if(!(imagen.compareTo("") == 0)){
            Uri img = Uri.parse(imagen);
            enlaceImagen = imagen;
            enlace = img;
            Glide.with(AddCategoria.this)
                    .load(img)
                    .override(500,500)
                    .into(ivCrearCategoria);
        }
    }
}
