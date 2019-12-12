package com.example.comandaspt1.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Categorias;
import com.example.comandaspt1.Conexion;
import com.example.comandaspt1.EditCategoria;
import com.example.comandaspt1.Facturas;
import com.example.comandaspt1.Model.Data.Categoria;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;
import com.example.comandaspt1.VerProductos;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> implements Filterable {

    private OnItemClickListener listener;
    private LayoutInflater inflater;
    private List<Categoria> listaCategorias = new ArrayList<>(), listaAux;
    private MainViewModel viewModel;
    private Context context;
    private Repository repository;
    private String url;

    private static final String CATEGORIAS_IMGS_FOLDER = "/upload/";
    public static final String KEY_INTENT_EDITAR = "idCategoria";

    public CategoriaAdapter(Context context, OnItemClickListener listener){
        inflater = LayoutInflater.from(context);
        this.context = context;
        repository = new Repository();
        url = repository.getUrl();
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(Categoria categoria, Class myClass);
    }

    @NonNull
    @Override
    public CategoriaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_categorias, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapter.ViewHolder holder, final int position) {
        final Categoria categoriaActual = listaCategorias.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        long idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);

        holder.tvNombreCategoria.setText(categoriaActual.getNombre());

        Glide.with(context)
                .load(url+ CATEGORIAS_IMGS_FOLDER+categoriaActual.getImagen())
                .override(400, 400)
                .into(holder.ivCategoria);

        viewModel = Categorias.viewModel;

        holder.ibEditarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.onItemClick(categoriaActual, EditCategoria.class);
            }
        });

        holder.ibBorrarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.mensajeBorrarC)
                        .setTitle(R.string.titleBorrarC);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewModel.deleteCategoria(categoriaActual.getId());
                        notifyItemRemoved(position);
                        listaCategorias.remove(position);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if(idFactura > 0){
            holder.ibBorrarCategoria.setVisibility(View.GONE);
            holder.ibEditarCategoria.setVisibility(View.GONE);
        }

        holder.cvCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(categoriaActual, VerProductos.class);
            }
        });
    }

    private Filter categoriasFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Categoria> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAux);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Categoria categoria : listaAux){
                    if (categoria.getNombre().toLowerCase().contains(filterPattern)){
                        filteredList.add(categoria);
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaCategorias.clear();
            listaCategorias.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return categoriasFilter;
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(listaCategorias !=null){
            elements= listaCategorias.size();
        }
        return elements;
    }

    public void setData(List<Categoria> listaCategorias){
        this.listaCategorias = listaCategorias;
        listaAux = new ArrayList<>(listaCategorias);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cvCategorias;
        TextView tvNombreCategoria;
        ImageView ivCategoria;
        ImageButton ibEditarCategoria, ibBorrarCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvCategorias = itemView.findViewById(R.id.cvCategorias);
            ivCategoria = itemView.findViewById(R.id.ivCategorias);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
            ibEditarCategoria = itemView.findViewById(R.id.ibEditarCategorias);
            ibBorrarCategoria = itemView.findViewById(R.id.ibBorrarCategorias);
        }
    }
}
