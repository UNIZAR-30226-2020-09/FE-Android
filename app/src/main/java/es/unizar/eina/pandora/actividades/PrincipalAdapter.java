package es.unizar.eina.pandora.actividades;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.unizar.eina.pandora.R;

public class PrincipalAdapter extends
        RecyclerView.Adapter<PrincipalAdapter.ViewHolder> {

    private Context context;
    private JSONArray password = new JSONArray();

    //Constructor
    public PrincipalAdapter(Context _context, JSONArray _password){
        context = _context;
        password = _password;
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", context.MODE_PRIVATE);
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
    public void onBindViewHolder(@NonNull PrincipalAdapter.ViewHolder holder, int position) {
        try {
            JSONObject pass = password.getJSONObject(position);

            String name = pass.getString("passwordName");
            TextView passwd = holder.name;
            passwd.setText(name);
            Log.d("+++++++",name);

            String user = pass.getString("userName");
            TextView _user= holder.user;
            _user.setText(user);
            Log.d("+++++++",user);

            String cat = pass.getString("categoryName");
            TextView category = holder.category;
            category.setText(cat);
            Log.d("+++++++",cat);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return password.length();
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
    }

}