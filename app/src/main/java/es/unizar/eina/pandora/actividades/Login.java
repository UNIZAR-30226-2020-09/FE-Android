package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/usuarios/login";
    private final OkHttpClient httpClient = new OkHttpClient();

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
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("mail",correo);
            json.accumulate("masterPassword",contrasena);
        }
        catch (Exception e){
            Log.d("EXCEPCION", e.getMessage());
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        // Formamos la petición con el cuerpo creado
        final okhttp3.Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", formBody.contentType().toString())
                .post(formBody)
                .build();

        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", response.body().string());
                    } else {
                        Log.d("OK ", response.body().string());
                        startActivity(new Intent(Login.this, Principal.class));
                        finish();
                    }
                }
                catch (IOException e){
                    Log.d("EXCEPCION ", e.getMessage());
                }
            }
        });
        thread.start();
    }
}