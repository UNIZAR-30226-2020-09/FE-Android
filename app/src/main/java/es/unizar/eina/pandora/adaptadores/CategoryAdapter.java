package es.unizar.eina.pandora.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.categorias.EditarCategoria;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final String urlEliminarCategoria = "https://pandorapp.herokuapp.com/api/categorias/eliminar";

    private final OkHttpClient httpClient = new OkHttpClient();

    private Context context;
    private ArrayList<JSONObject> categories;


    //Constructor
    public CategoryAdapter(Context _context, ArrayList<JSONObject> _categories){
        Log.d("CategoryAdapter", _categories.toString());
        context = _context;
        categories = _categories;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(categoryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, int position) {
        try {
            holder.bind(categories.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSharedPreferencesCategory(JSONObject item){
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        sharedPreferencesHelper.put("Category_info",item);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            name = itemView.findViewById(R.id.item_category_name);
        }

        public String getName(){
            return name.getText().toString();
        }

        void bind(final JSONObject JSONitem) throws JSONException {
            name.setText(JSONitem.getString("categoryName"));
            if(!name.getText().toString().equals("Sin categoría") && !name.getText().toString().equals("Compartida")  ) {
                //Desplegar el menú cuando hacemos click
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        PopupMenu popup = new PopupMenu(context, v);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menu_editar_categories);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_editar:
                                        Log.d("EDIT MENU", "selected");
                                        setSharedPreferencesCategory(JSONitem);
                                        Intent act = new Intent(v.getContext(), EditarCategoria.class);
                                        v.getContext().startActivity(act);
                                        return true;
                                    case R.id.menu_borrar_cat:
                                        String c= null;
                                        try {
                                            c = JSONitem.getString("categoryName");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Integer id_category = null;
                                        try {
                                            id_category = JSONitem.getInt("catId");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        borrarCategory(id_category,c, JSONitem);

                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }
        }
    }

    private void borrarCategory(final Integer id, String name, final JSONObject JSONitem){
        // Confirmar que queremos eliminar la categoría
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("¿Eliminar categoría: " + name +"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categories.remove(JSONitem);
                notifyDataSetChanged();
                doPostEliminarCategoria(id);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void doPostEliminarCategoria(Integer id_category) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(context).getString("token");
        String urlAux = urlEliminarCategoria+"?id=" + id_category;
        Log.d("URL",urlAux);

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlAux)
                .addHeader("Authorization", token)
                .delete()
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    PrintOnThread.show(context, "Categoría eliminada");
                }else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}