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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaspt1.EditEmpleadoGerente;
import com.example.comandaspt1.EmpleadosGerente;
import com.example.comandaspt1.Model.Data.Empleado;
import com.example.comandaspt1.Model.Repository;
import com.example.comandaspt1.R;
import com.example.comandaspt1.SingleEmpleadoGerente;

import java.util.ArrayList;
import java.util.List;

public class EmpleadoGerenteAdapter  extends RecyclerView.Adapter <EmpleadoGerenteAdapter.ItemHolder> implements Filterable {

    private LayoutInflater inflater;
    private List<Empleado> empleadoList, listaAux;
    public MainViewModel viewModel;
    private Context miContexto;
    private static final String STORAGE = "/upload/";

    public EmpleadoGerenteAdapter(Context context) {
        inflater=LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public EmpleadoGerenteAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_empleados_gerente,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoGerenteAdapter.ItemHolder holder, int position) {
        if(empleadoList !=null){
            final int posicion = position;
            final Empleado current = empleadoList.get(position);
            holder.tvNombre.setText(current.getNombre());
            holder.tvApellidos.setText(current.getApellido());

            Repository repository = new Repository();
            Glide.with(miContexto)
                    .load(repository.getUrl()+STORAGE+current.getImagen())
                    .override(400, 400)// prueba de escalado
                    .into(holder.ivImagen);

            viewModel = EmpleadosGerente.viewModel;
            holder.btBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(miContexto);
                    builder.setMessage(R.string.dialogoMensaje)
                            .setTitle(R.string.dialogoTitulo);
                    builder.setPositiveButton(R.string.confirmacion, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            viewModel.deleteEmpleado(current.getId());
                            notifyItemRemoved(posicion);
                            empleadoList.remove(posicion);
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
                    Empleado empleado = new Empleado(current.getId(), current.getNombre(), current.getApellido(),current.getImagen()
                    ,current.getUsername(), current.getPassword(),current.getTelefono(),current.getGerente());
                    Intent intent = new Intent(miContexto, EditEmpleadoGerente.class);
                    intent.putExtra("empleado",empleado);
                    miContexto.startActivity(intent);
                }
            });

            holder.cvEmpleado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Empleado empleado = new Empleado(current.getId(), current.getNombre(), current.getApellido(),current.getImagen()
                            ,current.getUsername(), current.getPassword(),current.getTelefono(),current.getGerente());
                    Intent intent = new Intent(miContexto, SingleEmpleadoGerente.class);
                    intent.putExtra("empleado",empleado);
                    miContexto.startActivity(intent);
                }
            });
        }
    }

    private Filter empleadoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Empleado> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listaAux);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Empleado empleado : listaAux){
                    if (empleado.getNombre().toLowerCase().contains(filterPattern)){
                        filteredList.add(empleado);
                    }
                }
            }
            FilterResults results =  new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            empleadoList.clear();
            empleadoList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return empleadoFilter;
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(empleadoList !=null){
            elements= empleadoList.size();
        }
        return elements;
    }

    public void setData(List<Empleado> empleadoList){
        this.empleadoList = empleadoList;
        listaAux = new ArrayList<>(empleadoList);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvApellidos;
        private CardView cvEmpleado;
        private ImageView ivImagen;
        private ImageButton btEditar, btBorrar;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvApellidos = itemView.findViewById(R.id.tvApellidos);
            btBorrar = itemView.findViewById(R.id.btBorrar);
            btEditar = itemView.findViewById(R.id.btEditar);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            cvEmpleado = itemView.findViewById(R.id.cvEmpleados);
        }
    }

}