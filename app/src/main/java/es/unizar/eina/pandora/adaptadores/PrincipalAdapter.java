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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.passwords.EditarPassword;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class PrincipalAdapter extends
        RecyclerView.Adapter<PrincipalAdapter.ViewHolder> {


    private Context context;
    private ArrayList<JSONObject> password = new ArrayList<>();


    //Constructor
    public PrincipalAdapter(Context _context, ArrayList<JSONObject> _password){
        context = _context;
        password = _password;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public PrincipalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View passwordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_principal, parent, false);
        ViewHolder viewHolder = new ViewHolder(passwordView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull final PrincipalAdapter.ViewHolder holder, int position) {
        try {
            holder.bind(password.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSharedPreferences(JSONObject item){
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        sharedPreferencesHelper.put("Password_info",item);
    }
    @Override
    public int getItemCount() {
        return password.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public TextView category;
        public TextView user;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_password_name);
            category = (TextView) itemView.findViewById(R.id.item_password_category);
            user = (TextView) itemView.findViewById(R.id.item_password_user);
        }

        public String getName(){
            return name.getText().toString();
        }

        public void bind(final JSONObject JSONitem) throws JSONException {
            name.setText(JSONitem.getString("passwordName"));
            category.setText(JSONitem.getString("categoryName"));
            user.setText(JSONitem.getString("userName"));

            //Desplegar el menú cuando hacemos click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(final View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_editar_info_principal);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_editar:
                                    //handle menu1 click
                                    Log.d("EDIT MENU","selected");
                                    setSharedPreferences(JSONitem);
                                    Intent act = new Intent(v.getContext(),EditarPassword.class);
                                    v.getContext().startActivity(act);
                                    return true;
                                case R.id.menu_info:
                                    Log.d("INFO MENU","OK");
                                    //handle menu2 click
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