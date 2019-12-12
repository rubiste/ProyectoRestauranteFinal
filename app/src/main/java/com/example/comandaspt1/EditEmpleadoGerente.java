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
import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Contract.OnRestListenerFoto;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.View.MainViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class EditEmpleadoGerente extends AppCompatActivity {

    public MainViewModel viewModel;
    private volatile boolean avanzar = true;

    private TextInputEditText etNombre, etApellido, etUsername, etPassword, etRepeat, etTelefono;
    private SwitchMaterial sGerente;
    private Button btEditar;
    private ImageView ivImagen;
    private View fragment;

    private String url = "";
    private Uri enlace;

    private String imagen = "";
    private String rutaImagen = "";

    private final String SUCCEED_TAG = "SUCCEED", ERROR_TAG="ERROR";
    private static final int SELECT_IMAGE_TO_UPLOAD = 1;
    public static final String DATA = "012UV34BCDEFGHIMNOPQ567JKL89ARSTWXYZ";
    public static Random RANDOM = new Random();

    private Empleado empleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_empleado_gerente);
        initComponents();
    }

    private void initComponents() {
        Repository repository = new Repository();
        url = repository.getUrl();

        fragment = findViewById(R.id.fEditEmpleado);
        fragment.setVisibility(View.INVISIBLE);

        empleado = getIntent().getExtras().getParcelable("empleado");
        viewModel = EmpleadosGerente.viewModel;
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellidos);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etTelefono = findViewById(R.id.etTelefono);
        sGerente = findViewById(R.id.sGerente);
        etRepeat = findViewById(R.id.etRepeat);

        btEditar = findViewById(R.id.btEditar);
        ivImagen = findViewById(R.id.ivImagen);

        etApellido.setText(empleado.getApellido());
        etNombre.setText(empleado.getNombre());
        etUsername.setText(empleado.getUsername());
        etTelefono.setText(""+empleado.getTelefono());
        if(empleado.getGerente() != 0){
            sGerente.setChecked(true);
        }else{
            sGerente.setChecked(false);
        }

        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNombre.getText().toString().equalsIgnoreCase("") ||
                        etApellido.getText().toString().equalsIgnoreCase("") ||
                        etUsername.getText().toString().equalsIgnoreCase("") ||
                        etTelefono.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), R.string.vacio, Toast.LENGTH_SHORT).show();
                } else {
                    if(enlace != null){
                        saveSelectedImageInFile(enlace);
                    }  else {
                        insertData();
                    }
                }
            }
        });

        if(enlace == null && rutaImagen.equalsIgnoreCase("")){
            Glide.with(EditEmpleadoGerente.this)
                    .load(url+"/upload/"+empleado.getImagen())
                    .override(500, 500)// prueba de escalado
                    .into(ivImagen);
        }else{
            Uri imageUri = Uri.parse(rutaImagen);
            Glide.with(EditEmpleadoGerente.this)
                    .load(imageUri)
                    .override(500, 500)// prueba de escalado
                    .into(ivImagen);
        }

        ivImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private Empleado establecerCampos(){
        Empleado e = new Empleado();
        e.setId(empleado.getId());
        if(!etNombre.getText().toString().equalsIgnoreCase("")){
            e.setNombre(etNombre.getText().toString());
        }else{
            e.setNombre(empleado.getNombre());
        }

        if(!etApellido.getText().toString().equalsIgnoreCase("")){
            e.setApellido(etApellido.getText().toString());
        }else{
            e.setApellido(empleado.getApellido());
        }

        if(!etUsername.getText().toString().equalsIgnoreCase("")){
            e.setUsername(etUsername.getText().toString());
        }else{
            e.setUsername(empleado.getUsername());
        }

        if(etPassword.getText().toString().equals(etRepeat.getText().toString())){
            e.setPassword(MainActivity.sha1(etPassword.getText().toString()));
        }else{
            e.setPassword(empleado.getPassword());
        }

        if(!etTelefono.getText().toString().equalsIgnoreCase("")){
            e.setTelefono(Integer.parseInt(etTelefono.getText().toString()));
        }else{
            e.setTelefono(empleado.getTelefono());
        }

        if(sGerente.isChecked()){
            e.setGerente(1);
        }else{
            e.setGerente(0);
        }

        if(!imagen.equalsIgnoreCase("")){
            e.setImagen(imagen);
        }

        return e;
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
            rutaImagen = uri.toString();
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
        fragment.setVisibility(View.VISIBLE);
        Empleado e = establecerCampos();
        viewModel.updateEmpleado(empleado.getId(), e, new OnRestListener() {
            @Override
            public void onSuccess(long id) {
                fragment.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                fragment.setVisibility(View.INVISIBLE);
                if(avanzar){
                    /*Intent intent = new Intent(EditEmpleadoGerente.this, EmpleadosGerente.class);
                    startActivity(intent);*/
                    avanzar = false;
                    finish();
                }
            }
        });
    }

    private void insertDataFoto(File file){
        fragment.setVisibility(View.VISIBLE);
        btEditar.setClickable(false);
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

        if(sGerente.isChecked()){
            outState.putString("empGerente",""+1);
        }else{
            outState.putString("empGerente",""+0);
        }

        outState.putString("empRepeat",etRepeat.getText().toString());

        outState.putString("imagenEmpleado",rutaImagen);
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
            enlace = imgUri;
            rutaImagen = imagen;
            Glide.with(EditEmpleadoGerente.this)
                    .load(imgUri)
                    .override(500,500)
                    .into(ivImagen);
        }
    }

}
