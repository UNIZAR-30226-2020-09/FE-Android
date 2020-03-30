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

    String _user;

    //Constructor
    public PrincipalAdapter(Context _context, JSONArray _password){
        context = _context;
        password = _password;
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", context.MODE_PRIVATE);
        _user = sharedPreferences.getString("user",null);
        Log.d("Usuario", _user);
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public PrincipalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View passwordView = inflater.inflate(R.layout.item_principal, parent, false);

        ViewHolder viewHolder = new ViewHolder(passwordView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull PrincipalAdapter.ViewHolder holder, int position) {
        try {
            JSONObject pass = password.getJSONObject(position);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
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

    /*
    private List<Contact> mContacts;

    // Pass in the contact array into the constructor
    public PrincipalAdapter(List<Contact> contacts) {
        mContacts = contacts;
    }

    @Override
    public PrincipalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PrincipalAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Contact contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());
        Button button = viewHolder.messageButton;
        button.setText(contact.isOnline() ? "Message" : "Offline");
        button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }*/
}