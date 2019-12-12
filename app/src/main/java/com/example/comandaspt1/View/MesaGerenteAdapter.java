package com.example.comandaspt1.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.EditMesaGerente;
import com.example.comandaspt1.MesasGerente;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;

import java.util.ArrayList;
import java.util.List;

public class MesaGerenteAdapter  extends RecyclerView.Adapter <MesaGerenteAdapter.ItemHolder> implements Filterable {

    private LayoutInflater inflater;
    private List<Mesa> mesaList = new ArrayList<>(), listaAux;
    public MainViewModel viewModel;
    private Context miContexto;
    private static final String STORAGE = "/upload/";

    public MesaGerenteAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public MesaGerenteAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_mesas_gerente,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MesaGerenteAdapter.ItemHolder holder, int position) {
        if(mesaList !=null){
            final int posicion = position;
            final Mesa current = mesaList.get(position);
            holder.tvMesa.setText(miContexto.getResources().getText(R.string.mesa)+" "+current.getNumero());

            Repository repository = new Repository();
            Glide.with(miContexto)
                    .load(repository.getUrl()+STORAGE+current.getImagen())
                    .override(400, 400)// prueba de escalado
                    .into(holder.ivImagen);

            viewModel = MesasGerente.viewModel;
            holder.btBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(miContexto);
                    builder.setMessage(R.string.dialogoMensaje)
                            .setTitle(R.string.dialogoTitulo);
                    builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            viewModel.deleteMesa(current.getId());
                            notifyItemRemoved(posicion);
                            mesaList.remove(posicion);
                        }
                    });
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            holder.btEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mesa mesa = new Mesa(current.getId(), current.getNumero(), current.getImagen(), current.getDisponible());
                    Intent intent = new Intent(miContexto, EditMesaGerente.class);
                    intent.putExtra("mesa",mesa);
                    miContexto.startActivity(intent);

                }
            });
        }
    }

    private Filter exampleFilter = new Filter() {
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
        return exampleFilter;
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(mesaList !=null){
            elements= mesaList.size();
        }
        return elements;
    }

    public void setData(List<Mesa> mesaList){
        this.mesaList = mesaList;
        listaAux = new ArrayList<>(mesaList);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMesa;
        private ImageView ivImagen;
        private ImageButton btEditar, btBorrar;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvMesa = itemView.findViewById(R.id.tvMesa);
            btBorrar = itemView.findViewById(R.id.btBorrar);
            btEditar = itemView.findViewById(R.id.btEditar);
            ivImagen = itemView.findViewById(R.id.ivImagen);
        }
    }
}
