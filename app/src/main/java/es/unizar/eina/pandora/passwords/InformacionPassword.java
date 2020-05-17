package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class InformacionPassword extends AppCompatActivity {

    private TextView nombre;
    private TextView usuario;
    private TextView spassword;
    private TextView categoria;
    private TextView validez;
    private TextView nota;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_password);

        nombre = findViewById(R.id.info_nombre);
        usuario = findViewById(R.id.info_user);
        spassword = findViewById(R.id.info_password);
        categoria = findViewById(R.id.info_categoria);
        validez= findViewById(R.id.info_validez);
        nota = findViewById(R.id.info_nota);

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        JSONObject password = sharedPreferencesHelper.getJSONObject("Password_info");
        try {
            String _name = password.getString("passwordName");
            String _user = password.getString("userName");
            String _password = password.getString("password");
            int dias = password.getInt("noDaysBeforeExpiration");
            String _validez = dias + " días";
            String _nota = password.getString("optionalText");
            String category_name = password.getString("categoryName");

            nombre.setText(_name);
            usuario.setText(_user);
            spassword.setText(_password);
            validez.setText(_validez);
            nota.setText(_nota);
            categoria.setText(category_name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
