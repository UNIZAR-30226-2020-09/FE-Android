package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrearCategoria extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/categorias/insertar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView name;
    private Button confirmar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_categoria);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        name = findViewById(R.id.crear_categoria_1_nombre);
        confirmar = findViewById(R.id.crear_categoria_1_button_confirmar);
    }

    public void confirmar(View view){
        String nombre_introducido = name.getText().toString();
        if(!nombre_introducido.equals("")){
            Log.d("InsertarCategory","IN");
            doPostCategoria(nombre_introducido);
        }else{
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la categoría", Toast.LENGTH_LONG).show();
        }
    }

    private void doPostCategoria(final String name) {
        String token = sharedPreferences.getString("token",null);
        Log.d("Crear password 4", token);
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("categoryName",name);
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
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
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
                        final JSONObject json = new JSONObject(response.body().string());
                        startActivity(new Intent(CrearCategoria.this, Principal.class));
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
