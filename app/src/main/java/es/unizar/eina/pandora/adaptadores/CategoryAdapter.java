package es.unizar.eina.pandora.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.categorias.EditarCategoria;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<JSONObject> categories = new ArrayList<>();


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
        ViewHolder viewHolder = new ViewHolder(categoryView);
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
            name = (TextView) itemView.findViewById(R.id.item_category_name);
        }

        public String getName(){
            return name.getText().toString();
        }

        public void bind(final JSONObject JSONitem) throws JSONException {
            name.setText(JSONitem.getString("categoryName"));
            if(!name.getText().toString().equals("Sin categoría")) {
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
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                    }
                });
            }
        }
    }
}