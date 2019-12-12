package com.example.comandaspt1.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaspt1.Model.Data.Contacto;
import com.example.comandaspt1.R;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter <ContactoAdapter.ItemHolder> implements PopupMenu.OnMenuItemClickListener {

    private LayoutInflater inflater;
    private List<Contacto> contactosList;
    public MainViewModel viewModel;
    private Context miContexto;
    private String direccion;

    public ContactoAdapter(Context context) {
        inflater= LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public ContactoAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Toast.makeText(miContexto, "hola", Toast.LENGTH_SHORT).show();
        View itemView= inflater.inflate(R.layout.item_contacto,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactoAdapter.ItemHolder holder, int position) {

        final Contacto contacto = contactosList.get(position);

        if (contacto != null){

            holder.tvNombre.setText(contacto.getNombre());
            holder.tvTelefono.setText(contacto.getEmail().toString());
        }

        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                direccion = contacto.getEmail();
                showPopup(holder.cl);
            }
        });
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(contactosList !=null){
            elements= contactosList.size();
        }
        return elements;
    }

    public void setData(List<Contacto> contactoList){
        this.contactosList = contactoList;
        notifyDataSetChanged();
    }

    public List<Contacto> getData(){
        return this.contactosList;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvTelefono;
        private  Button btEnviar;
        private CardView cl;
        private ImageView ivImagen;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre=itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            btEnviar=itemView.findViewById(R.id.btBorrar);
            cl = itemView.findViewById(R.id.cvContacto);
            ivImagen = itemView.findViewById(R.id.ivProducto);
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(miContexto, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_contacto);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.email:

                mandarCorreo(direccion);
                return true;
            default:
                return true;
        }
    }

    private void mandarCorreo(String direccion) {

        String[] TO = {direccion};
        String[] CC = {""};


        //crear intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: "));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PRUEBA APP ENVIAR CORREO");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);


        //crear chooset
        String title = "Mandar este mail con...";

        Intent chooser = Intent.createChooser(emailIntent, title);
        if (emailIntent.resolveActivity(miContexto.getPackageManager()) != null){
            miContexto.startActivity(chooser);
        }

    }
}
