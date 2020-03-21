package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.RequestManager;

public class Login extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/usuarios/login";


    SharedPreferences sharedPreferences;
    TextView email;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        email = findViewById(R.id.login_entrada_usuario);
        password = findViewById(R.id.login_entrada_clave);
    }

    public void entrar(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("email", email.getText().toString().trim());
        editor.putString("password", password.getText().toString().trim());
        editor.commit();
        doPost(sharedPreferences.getString("email",null),
                sharedPreferences.getString("password",null));
    }

    private void doPost(final String correo, final String contrasena) {
        try {
            final org.json.JSONObject jsonBody = new org.json.JSONObject();
            jsonBody.put("mail", correo);
            jsonBody.put("masterPassword", contrasena);

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // TODO Auto-generated method stub
                            Log.d("OK: ","Se ha podido iniciar sesión");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("email", correo);
                            editor.putString("password", contrasena);
                            editor.commit();
                            startActivity(new Intent(Login.this, Inicio.class));
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("Error: ", error.getMessage());
                        }
                    });
            RequestManager.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
