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
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AddEmpleadoGerente extends AppCompatActivity {

    public MainViewModel viewModel;
    private EditText etNombre, etApellido, etUsername, etPassword, etRepeat, etTelefono;
    private SwitchMaterial sGerente;
    private Button btCrear;
    private ImageView ivImagen;
    private View fragment;

    private Uri enlace;
    private String imagen = "";
    private String enlaceImagen = "";
    private String nuevaImagen = "";

    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";
    public static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_empleado_gerente);
        init();
    }

    private void init() {
        viewModel = EmpleadosGerente.viewModel;

        fragment = findViewById(R.id.fAddEmpleado);
        fragment.setVisibility(View.INVISIBLE);

        etNombre = findViewById(R.id.tvNombre);
        etApellido = findViewById(R.id.etApellidos);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etTelefono = findViewById(R.id.etTelefono);
        sGerente = findViewById(R.id.sGerente);
        etRepeat = findViewById(R.id.etRepeat);
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
                if(etNombre.getText().toString().equalsIgnoreCase("") ||
                        etApellido.getText().toString().equalsIgnoreCase("") ||
                        etUsername.getText().toString().equalsIgnoreCase("") ||
                        etPassword.getText().toString().equalsIgnoreCase("") ||
                        etRepeat.getText().toString().equalsIgnoreCase("") ||
                        etTelefono.getText().toString().equalsIgnoreCase("") ||
                        enlace == null){
                    Toast.makeText(getApplicationContext(), R.string.vacio, Toast.LENGTH_SHORT).show();
                }else {
                    if(!etPassword.getText().toString().equalsIgnoreCase(etRepeat.getText().toString())){
                        Toast.makeText(getApplicationContext(), R.string.noCoinciden, Toast.LENGTH_SHORT).show();
                    }else{
                        saveSelectedImageInFile(enlace);
                    }

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

    private Empleado establecerCampos(){
        Empleado e = new Empleado();
        e.setNombre(etNombre.getText().toString());
        e.setApellido(etApellido.getText().toString());
        e.setUsername(etUsername.getText().toString());
        e.setPassword(MainActivity.sha1(etPassword.getText().toString()));
        e.setTelefono(Integer.parseInt(etTelefono.getText().toString()));

        if(sGerente.isChecked()){
            e.setGerente(1);
        }else{
            e.setGerente(0);
        }

        e.setImagen(imagen);

        return e;
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
        Empleado e = establecerCampos();
        viewModel.addEmpleado(e, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                fragment.setVisibility(View.INVISIBLE);
                /*Toast.makeText(AddEmpleadoGerente.this, getResources().getText(R.string.successAddE),
                        Toast.LENGTH_SHORT).show();*/
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
                Intent intent = new Intent(AddEmpleadoGerente.this, EmpleadosGerente.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("empNombre",etNombre.getText().toString());
        outState.putString("empApellidos",etApellido.getText().toString());
        outState.putString("empUsername",etUsername.getText().toString());
        outState.putString("empTelefono",etTelefono.getText().toString());
        outState.putString("empPass",etPassword.getText().toString());
        outState.putString("empRepeat",etRepeat.getText().toString());

        if(sGerente.isChecked()){
            outState.putString("empGerente",""+1);
        }else{
            outState.putString("empGerente",""+0);
        }

        outState.putString("imagenEmpleado",enlaceImagen);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etNombre.setText(savedInstanceState.getString("empNombre"));
        etApellido.setText(savedInstanceState.getString("empApellidos"));
        etUsername.setText(savedInstanceState.getString("empUsername"));
        etPassword.setText(savedInstanceState.getString("empPass"));
        etRepeat.setText(savedInstanceState.getString("empRepeat"));
        etTelefono.setText(savedInstanceState.getString("empTelefono"));

        String gerente = savedInstanceState.getString("empGerente");

        if(gerente.equalsIgnoreCase("0")){
            sGerente.setChecked(false);
        }else{
            sGerente.setChecked(true);
        }

        String imagen = savedInstanceState.getString("imagenEmpleado");
        if(!imagen.equalsIgnoreCase("")){
            Uri imgUri=Uri.parse(imagen);
            enlaceImagen = imagen;
            enlace = imgUri;
            ivImagen.setImageURI(null);
            ivImagen.setImageURI(imgUri);
        }
    }

}