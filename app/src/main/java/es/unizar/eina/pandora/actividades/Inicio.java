package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Inicio extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/estadisticas";
    private final OkHttpClient httpClient = new OkHttpClient();

    TextView nUsuarios;
    TextView nPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        nUsuarios = findViewById(R.id.inicio_nUsers);
        nPass = findViewById(R.id.inicio_nPass);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String _email = sharedPreferences.getString("email",null);
        String _password = sharedPreferences.getString("password",null);
        // Si ya tenemos estos datos, iniciamos sesión automáticamente
        if (_email != null && _password != null) {
            startActivity(new Intent(Inicio.this, Login.class));
            finish();
        }
        doPost();

    }

    public void goLogin(View view){
        startActivity(new Intent(Inicio.this,Login.class));
    }

    public void goRegistro(View view){
        startActivity(new Intent(Inicio.this, RegistroUno.class));
    }

    public void goContacto(View view){
        startActivity(new Intent(Inicio.this, ContactarUno.class));
    }

    private void doPost() {
        // Formamos la petición
        final okhttp3.Request request = new Request.Builder().url(url).build();

        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", response.body().string());
                    } else {
                        // Obtenemos la respuesta y creamos un JSON con la misma
                        final JSONObject json = new JSONObject(response.body().string());

                        // Como vamos a tocar elementos de la UI, tenemos que crear un Thread de UI
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    nUsuarios.setText(Integer.toString(json.getInt("nUsuarios")));
                                    nPass.setText(Integer.toString(json.getInt("nContraseñas")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                catch (IOException | JSONException e){
                    Log.d("EXCEPCION ", e.getMessage());
                }
            }
        });
        thread.start();
    }
}
