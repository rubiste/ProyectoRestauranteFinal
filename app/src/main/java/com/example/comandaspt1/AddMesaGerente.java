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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AddMesaGerente extends AppCompatActivity {

    public MainViewModel viewModel;
    private EditText etMesa;
    private Button btCrear;
    private ImageView ivImagen;
    private SwitchMaterial sDisponible;
    private View fragment;

    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";
    public static Random RANDOM = new Random();

    private Uri enlace;
    private String imagen = "";
    private String enlaceImagen = "";
    private String nuevaImagen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mesa_gerente);
        init();
    }

    private void init() {
        viewModel = MesasGerente.viewModel;
        fragment = findViewById(R.id.fAddMesa);
        fragment.setVisibility(View.INVISIBLE);
        sDisponible = findViewById(R.id.sDisponible);
        etMesa = findViewById(R.id.etMesa);
        btCrear = findViewById(R.id.btCrear);
        ivImagen = findViewById(R.id.ivImagen);

        ivImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMesa.getText().toString().equalsIgnoreCase("") ||
                        enlace == null){
                    Toast.makeText(getApplicationContext(), R.string.vacio, Toast.LENGTH_SHORT).show();
                }else {
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
            enlaceImagen = uri.toString();
            Glide.with(this)
                    .load(uri)
                    .override(500, 500)
                    .into(ivImagen);
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
        imagen = randomString(15)+".jpg";
        File file = new File(getFilesDir(), imagen);
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
        Mesa m = establecerCampos();
        viewModel.addMesa(m, new OnRestListener() {
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

    private Mesa establecerCampos() {
        Mesa m = new Mesa();
        m.setNumero(Integer.parseInt(etMesa.getText().toString()));
        m.setImagen(imagen);
        if(sDisponible.isChecked()){
            m.setDisponible(1);
        }else{
            m.setDisponible(0);
        }

        return m;
    }

    private void insertDataFoto(File file){
        fragment.setVisibility(View.VISIBLE);
        btCrear.setClickable(false);
        ivImagen.setClickable(false);

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
        outState.putString("numMesa",etMesa.getText().toString());
        outState.putString("imagenMesa",enlaceImagen);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etMesa.setText(savedInstanceState.getString("numMesa"));
        String imagen = savedInstanceState.getString("imagenMesa");
        if(!imagen.equalsIgnoreCase("")){
            Uri imgUri=Uri.parse(imagen);
            enlaceImagen = imagen;
            enlace = imgUri;
            ivImagen.setImageURI(null);
            ivImagen.setImageURI(imgUri);
        }
    }

}
