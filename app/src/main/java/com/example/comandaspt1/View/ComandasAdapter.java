package com.example.comandaspt1.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Conexion;
import com.example.comandaspt1.EditComanda;
import com.example.comandaspt1.Facturas;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;

import java.util.ArrayList;
import java.util.List;

public class ComandasAdapter  extends RecyclerView.Adapter <ComandasAdapter.ItemHolder> implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<com.example.comandaspt1.Model.Data.Comanda> comandaList = new ArrayList<>(), listaAuxC;
    private ArrayList<Producto> productoList = new ArrayList<>(), listAuxP;
    public MainViewModel viewModel;
    private Context miContexto;
    private static final String STORAGE = "/upload/";
    public static final String IDFACTURA = "idFactura";

    public ComandasAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public ComandasAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_comanda,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComandasAdapter.ItemHolder holder, int position) {
        if(this.comandaList != null){
            holder.setIsRecyclable(false);
            final int posicion = position;
            final Comanda current = comandaList.get(position);
            Repository repository = new Repository();
            holder.tvUnidades.setText(""+current.getUnidades());

            holder.tvPrecio.setText(""+current.getPrecio());

            for (int i = 0; i < productoList.size(); i++) {
                if(current.getIdProducto() == productoList.get(i).getId()){
                    holder.tvNombreProducto.setText(productoList.get(i).getNombre());
                    final Producto producto = productoList.get(i);
                    Glide.with(miContexto)
                            .load(repository.getUrl()+STORAGE+productoList.get(i).getImagen())
                            .override(400, 400)
                            .into(holder.ivProducto);
                    if(current.getEntregado() == 1){
                        holder.btEditar.setVisibility(View.GONE);
                        holder.cvComandas.setCardBackgroundColor(Color.parseColor("#AAAAAA"));
                    }else{
                        holder.btEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(miContexto, EditComanda.class);
                                intent.putExtra("comanda",current);
                                intent.putExtra("producto", producto);
                                miContexto.startActivity(intent);
                            }
                        });
                    }
                }
            }

            viewModel = Facturas.viewModel;

            holder.btBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(miContexto);
                    builder.setMessage(R.string.dialogoMensaje)
                            .setTitle(R.string.dialogoTitulo);
                    builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            viewModel.deleteComanda(current.getId());
                            notifyItemRemoved(posicion);
                            comandaList.remove(posicion);
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
        }
    }

    private Filter comandasFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Comanda> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAuxC);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Comanda comanda : listaAuxC) {
                    for (Producto producto : listAuxP) {
                        if (comanda.getIdProducto() == producto.getId() && producto.getNombre()
                                .toLowerCase().contains(filterPattern)) {
                            filteredList.add(comanda);
                        }
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            comandaList.clear();
            comandaList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return comandasFilter;
    }

    @Override
    public int getItemCount() {
        int elements = 0;
        if(comandaList != null){
            elements = comandaList.size();
        }
        return elements;
    }

    public List<Comanda> getComandas(){
        return this.comandaList;
    }

    public void setData(List<com.example.comandaspt1.Model.Data.Comanda> comandaList){
        this.comandaList = new ArrayList<>();
        SharedPreferences sharedPreferences = miContexto.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idFactura = sharedPreferences.getLong(IDFACTURA, 0);
        this.comandaList = new ArrayList<Comanda>();
        for (int i = 0; i < comandaList.size(); i++) {
            if(comandaList.get(i).getIdFactura() == idFactura){
                this.comandaList.add(comandaList.get(i));
            }
        }
        listaAuxC = new ArrayList<>(this.comandaList);
        notifyDataSetChanged();
    }

    public void setProductos(List<Producto> productoList){
        this.productoList = new ArrayList<>();
        if(productoList.size() > 0){
            for (int i = 0; i < productoList.size(); i++) {
                this.productoList.add(productoList.get(i));
            }
        }
        listAuxP = new ArrayList<>(this.productoList);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreProducto, tvPrecio, tvUnidades;
        private CardView cvComandas;
        private ImageView ivProducto;
        private ConstraintLayout cl;
        private ImageButton btBorrar, btEditar;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvUnidades = itemView.findViewById(R.id.tvUnidades);
            btBorrar = itemView.findViewById(R.id.btBorrar);
            btEditar = itemView.findViewById(R.id.btEditar);
            cvComandas = itemView.findViewById(R.id.cvComandas);
            ivProducto = itemView.findViewById(R.id.ivProducto);
        }
    }
}
