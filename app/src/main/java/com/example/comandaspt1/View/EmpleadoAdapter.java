package com.example.comandaspt1.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaspt1.Model.Data.Empleado;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmpleadoAdapter extends RecyclerView.Adapter <EmpleadoAdapter.ItemHolder> {

    private LayoutInflater inflater;
    private List<Empleado> empleadoList;
    public MainViewModel viewModel;
    private Context miContexto;
    private static final String ID = "Empleado.ID";
    private static final String STORAGE = "/upload/";

    public EmpleadoAdapter(Context context) {
        inflater= LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoAdapter.ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(empleadoList !=null){
            elements= empleadoList.size();
        }
        return elements;
    }

    public void setData(List<Empleado> equipoList){
        this.empleadoList = equipoList;
        notifyDataSetChanged();
    }

    public List<Empleado> getData(){
        return this.empleadoList;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        /*private final EditText tvNombre, tvCiudad;
        private final Button btBorrar, btEditar;
        private ConstraintLayout cl;
        private ImageView ivImagen;*/

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            /*tvNombre=itemView.findViewById(R.id.tvNombre);
            tvCiudad=itemView.findViewById(R.id.etCiudad);
            btBorrar=itemView.findViewById(R.id.btBorrar);
            btEditar=itemView.findViewById(R.id.btEditar);
            cl = itemView.findViewById(R.id.cl);
            ivImagen = itemView.findViewById(R.id.ivImagen);*/
        }
    }

}
