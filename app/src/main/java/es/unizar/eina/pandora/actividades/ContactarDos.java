package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.*;

public class ContactarDos extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/mensaje";
    private final OkHttpClient httpClient = new OkHttpClient();

    SharedPreferences sharedPreferences;
    TextView remitente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar_dos);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        remitente = findViewById(R.id.contactar2_entrada_comunicado);
    }

    public void contactar(View view){
        if(!remitente.getText().toString().equals("")){
            String remitente_insertado = remitente.getText().toString().trim();
            String mensaje_insertado = sharedPreferences.getString("mensaje",null);
            doPost(remitente_insertado, mensaje_insertado);
        }
    }

    private void doPost(final String remitente, final String mensaje) {
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("mail",remitente);
            json.accumulate("body",mensaje);
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
                        startActivity(new Intent(ContactarDos.this, ContactarTres.class));
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
