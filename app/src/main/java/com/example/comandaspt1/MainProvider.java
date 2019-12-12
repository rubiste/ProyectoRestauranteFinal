package com.example.comandaspt1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaspt1.Model.Data.Contacto;
import com.example.comandaspt1.View.ContactoAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class MainProvider extends AppCompatActivity {

    private ContactoAdapter contactoAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static VerProductos contextMain;
    private RecyclerView recyclerContactos;

    protected final int SOLICITUD_PERMISO_CONTACTOS=0;
    private static final String A1 ="ABC" ;
    ArrayList<Contacto> datos_contactos = new ArrayList<Contacto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_provider);

        initComponents();

    }

    private void initComponents() {

        recyclerContactos = findViewById(R.id.rvContactos);
        layoutManager = new LinearLayoutManager(this);
        contactoAdapter = new ContactoAdapter(this);

        recyclerContactos.setLayoutManager(layoutManager);
        recyclerContactos.setAdapter(contactoAdapter);

        checkPermissions(Manifest.permission.READ_CONTACTS,
                R.string.tituloExplicacion2, R.string.mensajeExplicacion2);
    }


    private void checkPermissions(String permiso, int titulo, int mensaje) {
        if (ContextCompat.checkSelfPermission(this, permiso)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                explain(R.string.tituloExplicacion, R.string.mensajeExplicacion, permiso);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permiso},
                        SOLICITUD_PERMISO_CONTACTOS);
            }
        } else {
            contactoAdapter.setData(MostrarAgenda(MainProvider.this));
        }
    }

    private void explain(int title, int message, final String permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainProvider.this, new String[]{permissions}, SOLICITUD_PERMISO_CONTACTOS);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISO_CONTACTOS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permiso concedido
                    contactoAdapter.setData(MostrarAgenda(MainProvider.this));

                } else {
                    Toast.makeText(this,"Permiso no concedido", Toast.LENGTH_LONG);
                    finish();
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public ArrayList<Contacto> MostrarAgenda(Context context){

            ArrayList<Contacto> contactosList = new ArrayList<>();
            HashSet<String> emlRecsHS = new HashSet<>();
            //Context context = getActivity();
            ContentResolver cr = context.getContentResolver();
            String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
            String order = "CASE WHEN "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + ", "
                    + ContactsContract.CommonDataKinds.Email.DATA
                    + " COLLATE NOCASE";
            String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
            if (cur.moveToFirst()) {
                do {
                    // names comes in hand sometimes
                    String name = cur.getString(1);
                    String emlAddr = cur.getString(3);
                    Contacto contacto = new Contacto();
                    contacto.setNombre(name);
                    contacto.setEmail(emlAddr);
                    Log.v("xzy", "name: "+name);
                    Log.v("xzy", "emlAddr: "+emlAddr);
                    Log.v("xzy", "contacto: "+contacto.toString());

                        contactosList.add(contacto);
                } while (cur.moveToNext());
            }

            cur.close();
            return contactosList;
    }


}
