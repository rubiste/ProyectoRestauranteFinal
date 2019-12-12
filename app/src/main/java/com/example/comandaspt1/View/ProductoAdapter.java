package com.example.comandaspt1.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.Comanda;
import com.example.comandaspt1.Conexion;
import com.example.comandaspt1.EditProducto;
import com.example.comandaspt1.Facturas;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;
import com.example.comandaspt1.VerProductos;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter <ProductoAdapter.ItemHolder> implements Filterable {

    private static String TAG = "xzy";
    private LayoutInflater inflater;
    private List<Producto> productoList = new ArrayList<>(), listaAux;
    private MainViewModel viewModel;
    private Context miContexto;
    public static final String PRODUCTO = "productoParcelable";
    public static final String KEY_INTENT_EDITAR = "Categoria";
    private static final String STORAGE = "/upload/";

    public ProductoAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public ProductoAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_productos, parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.ItemHolder holder, int position) {
        if (productoList != null) {
            final int posicion = position;
            final Producto current = productoList.get(position);

            SharedPreferences sharedPreferences = miContexto.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
            long idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);

            holder.tvNombre.setText(current.getNombre());
            holder.tvprecio.setText(String.valueOf(current.getPrecio()));

            Repository repository = new Repository();
            Glide.with(miContexto)
                    .load(repository.getUrl()+STORAGE+current.getImagen())
                    .override(400, 400)
                    .into(holder.ivImagen);

            viewModel = VerProductos.viewModel;
            holder.ibEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(miContexto, EditProducto.class);
                    intent.putExtra(KEY_INTENT_EDITAR, current);
                    escribirShared(current.getIdCategoria());
                    miContexto.startActivity(intent);
                }
            });

            holder.ibBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(miContexto);
                    builder.setMessage(R.string.dialogoMensaje)
                            .setTitle(R.string.dialogoTitulo);
                    builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            try {
                                viewModel.deleteProducto(current.getId());
                                notifyItemRemoved(posicion);
                                productoList.remove(posicion);
                            } catch (Exception e){
                                Toast.makeText(miContexto, "espere un momento", Toast.LENGTH_SHORT).show();
                            }
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

            if(idFactura > 0){
                holder.ibBorrar.setVisibility(View.GONE);
                holder.ibEditar.setVisibility(View.GONE);
                holder.cvProducto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(miContexto, Comanda.class);
                        intent.putExtra(KEY_INTENT_EDITAR, current.getId());
                        intent.putExtra(PRODUCTO,current);
                        miContexto.startActivity(intent);
                    }
                });
            }
        }
    }

    public void escribirShared(long idAux){
        SharedPreferences sharedPreferences = miContexto.getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_INTENT_EDITAR, idAux);
        editor.commit();
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Producto> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAux);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Producto producto : listaAux){
                    if (producto.getNombre().toLowerCase().contains(filterPattern)){
                        filteredList.add(producto);
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productoList.clear();
            productoList.addAll((List) results.values);
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
        if(productoList !=null){
            elements= productoList.size();
        }
        return elements;
    }

    public void setData(List<Producto> equipoList){
        this.productoList = equipoList;
        listaAux = new ArrayList<>(equipoList);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre, tvprecio;
        private final ImageButton ibBorrar, ibEditar;
        private CardView cvProducto;
        private ImageView ivImagen;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre=itemView.findViewById(R.id.tvNombreCategoria);
            tvprecio=itemView.findViewById(R.id.tvPrecio);
            ibBorrar =itemView.findViewById(R.id.ibBorrarProductos);
            ibEditar =itemView.findViewById(R.id.ibEditarProductos);
            cvProducto = itemView.findViewById(R.id.cvProductos);
            ivImagen = itemView.findViewById(R.id.ivProducto);
        }
    }
}