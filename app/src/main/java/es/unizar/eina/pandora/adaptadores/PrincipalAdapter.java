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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.passwords.EditarPassword;
import es.unizar.eina.pandora.passwords.InformacionPassword;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrincipalAdapter extends
        RecyclerView.Adapter<PrincipalAdapter.ViewHolder> {

    private final String urlEliminarPassword = "https://pandorapp.herokuapp.com/api/contrasenya/eliminar";
    private final String urlEliminarPasswordCompartida = "https://pandorapp.herokuapp.com/api/grupo/eliminar";

    private final OkHttpClient httpClient = new OkHttpClient();

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
        ViewHolder(View itemView) {
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

        void bind(final JSONObject JSONitem) throws JSONException {
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
                                    try {
                                        if(JSONitem.getInt("rol") == 1){
                                            setSharedPreferences(JSONitem);
                                            SharedPreferencesHelper.getInstance(v.getContext()).put("generar",false);
                                            Intent act = new Intent(v.getContext(),EditarPassword.class);
                                            v.getContext().startActivity(act);
                                        }
                                        else{
                                            PrintOnThread.show(context, "No puedes editar esta contraseña porque no eres el dueño");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case R.id.menu_info:
                                    setSharedPreferences(JSONitem);
                                    Intent act2 = new Intent(v.getContext(), InformacionPassword.class);
                                    v.getContext().startActivity(act2);
                                    return true;
                                case R.id.menu_delete:
                                    try {
                                        if(JSONitem.getString("categoryName").equals("Compartida")){
                                            String name = JSONitem.getString("passwordName");
                                            Integer id_pass = JSONitem.getInt("passId");
                                            borrarPassword(id_pass, name,true);
                                        }else{
                                            String name = JSONitem.getString("passwordName");
                                            Integer id_pass = JSONitem.getInt("passId");
                                            borrarPassword(id_pass, name, false);
                                        }
                                        notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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

    private void borrarPassword(final Integer id, String name, final boolean compartida){
        // Confirmar que queremos eliminar la contraseña
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("¿Eliminar contraseña: " + name +"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (compartida){
                    doPostEliminarPasswordCompartida(id);
                }
                else{
                    doPostEliminarPassword(id);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Volvemos a colocar la pass word en su contraseña
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void doPostEliminarPassword(Integer id_pass) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(context).getString("token");
        String urlAux = urlEliminarPassword+"?id=" + id_pass;
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
                    PrintOnThread.show(context, "Contraseña eliminada");
                    Log.d("PASSWORD ELIMINADA","OK");
                }
                else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void doPostEliminarPasswordCompartida(Integer id_pass) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(context).getString("token");
        String urlAux = urlEliminarPasswordCompartida+"?id=" + Integer.toString(id_pass);
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
                    PrintOnThread.show(context, "Contraseña eliminada");
                    Log.d("PASSWORD ELIMINADA","OK");
                }
                else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}