package com.example.comandaspt1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Conexion extends AppCompatActivity {
    private EditText etConexion;
    private Button btConexion;
    public static final String URL = "URL";
    public static final String TAG = "comandaspt1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion);

        etConexion = findViewById(R.id.etConexion);
        btConexion = findViewById(R.id.btConexion);
        btConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etConexion.getText().toString().equalsIgnoreCase("")){
                    SharedPreferences sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(URL, etConexion.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(Conexion.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Conexion.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
