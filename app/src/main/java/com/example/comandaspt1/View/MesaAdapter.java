package com.example.comandaspt1.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Conexion;
import com.example.comandaspt1.Facturas;
import com.example.comandaspt1.MainActivity;
import com.example.comandaspt1.MesasLocal;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MesaAdapter  extends RecyclerView.Adapter <MesaAdapter.ItemHolder> implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<Mesa> mesaList = new ArrayList<>(), listaAux;
    private ArrayList<Factura> facturaList = new ArrayList<>();
    public MainViewModel viewModel;
    private Context miContexto;
    private static final String STORAGE = "/upload/";
    public static final String IDFACTURA = "idFactura";

    public MesaAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public MesaAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_mesa,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MesaAdapter.ItemHolder holder, int position) {
        if(mesaList !=null){
            final int posicion = position;
            final Mesa current = mesaList.get(position);
            if(current.getDisponible() != 0){
                holder.tvMesa.setText(miContexto.getResources().getText(R.string.mesa)+" "+current.getNumero());

                final long idFactura = encontrarFactura(current.getId());

                Repository repository = new Repository();
                Glide.with(miContexto)
                        .load(repository.getUrl()+STORAGE+current.getImagen())
                        .override(400, 400)
                        .into(holder.ivImagen);

                if(idFactura == 0){
                    holder.ivPersona.setVisibility(View.INVISIBLE);
                }else{
                    holder.ivPersona.setVisibility(View.VISIBLE);
                }

                holder.cvMesas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(idFactura == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(miContexto);
                            builder.setMessage(R.string.facturaMensaje)
                                    .setTitle(R.string.facturaTitulo);
                            builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Factura factura = new Factura();
                                    SharedPreferences sharedPreferences = miContexto.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
                                    long idEmpleado = sharedPreferences.getLong(MainActivity.EMPLEADOREMEMBER, 0);
                                    factura.setNumeroMesa(current.getId());
                                    factura.setIdEmpleadoInicio(idEmpleado);
                                    factura.setHoraInicio(obtenerHora());
                                    factura.setIdEmpleadoCierre(idEmpleado);

                                    MesasLocal.addFactura(factura);
                                }
                            });
                            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{
                            SharedPreferences sharedPreferences = miContexto.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putLong(IDFACTURA, idFactura);
                            Log.v("xzy", "mesa"+idFactura);
                            editor.commit();
                            Intent intent = new Intent(miContexto, Facturas.class);
                            miContexto.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    private Filter mesaLocalFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Mesa> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAux);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Mesa mesa : listaAux){
                    if (String.valueOf(mesa.getNumero()).toLowerCase().contains(filterPattern)){
                        filteredList.add(mesa);
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mesaList.clear();
            mesaList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return mesaLocalFilter;
    }

    private String obtenerHora() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        return currentTime;
    }

    private long encontrarFactura(long idMesa) {
        long idF = 0;
        if(this.facturaList.size() > 0){
            for (int i = 0; i < this.facturaList.size(); i++) {
                if(this.facturaList.get(i).getNumeroMesa() == idMesa){
                    idF = this.facturaList.get(i).getId();
                }
            }
        }
        return idF;
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(mesaList !=null){
            elements = mesaList.size();
        }
        return elements;
    }

    public void setData(List<Mesa> mesaList){
        Log.d("xyz", mesaList.toString());
        this.mesaList = new ArrayList<>();
        if(mesaList != null) {
            for (int i = 0; i < mesaList.size(); i++) {
                if (mesaList.get(i).getDisponible() != 0) {
                    this.mesaList.add(mesaList.get(i));
                }
            }
        }
        else {
            Toast.makeText(miContexto, "no recarga mesas", Toast.LENGTH_SHORT).show();
        }
        listaAux = new ArrayList<>(this.mesaList);
        notifyDataSetChanged();
    }

    public void setFacturas(List<Factura> facturaList){
        if(facturaList != null) {
            if(facturaList.size() > 0) {
                for (int i = 0; i < facturaList.size(); i++) {
                    if (facturaList.get(i).getHoraCierra() == null || facturaList.get(i).getHoraCierra()
                            .equalsIgnoreCase("")) {
                        this.facturaList.add(facturaList.get(i));
                    }
                }
            }
        }else {
            Toast.makeText(miContexto, "no recarga facturas", Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMesa;
        private CardView cvMesas;
        private ImageView ivImagen, ivPersona;
        private ConstraintLayout cl;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvMesa = itemView.findViewById(R.id.tvMesa);
            cvMesas = itemView.findViewById(R.id.cvMesas);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            ivPersona = itemView.findViewById(R.id.ivPersona);
        }
    }
}
