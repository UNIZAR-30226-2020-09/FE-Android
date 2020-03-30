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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrearPasswordSeis extends AppCompatActivity {
    final String url = "https://pandorapp.herokuapp.com/api/contrasenya/insertar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView note;
    private Button compartir;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_seis);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        note = findViewById(R.id.crear_password_6_note);
        siguiente = findViewById(R.id.crear_password_6_button_confirmar);
        compartir = findViewById(R.id.crear_password_6_button_compartir);
    }

    public void crear(View view){
        //Recuperamos los datos introducidos
        String nombre_insertado = sharedPreferences.getString("password_name",null);
        Log.i("Crear note 4",nombre_insertado);
        String user_insertado = sharedPreferences.getString("password_user",null);
        Log.i("Crear note 4",user_insertado);
        String pass_insertado = sharedPreferences.getString("password_pass",null);
        Log.i("Crear note 4",pass_insertado);
        String nota_insertada = note.getText().toString().trim();
        Log.i("Crear note 4",nota_insertada);
        String aux = sharedPreferences.getString("password_dias",null);
        Log.d("Crear note 4", aux);
        Integer dias_insertados=Integer.parseInt(aux);
        //Corregir
        Integer cartegory_insertada=2;
        doPost(nombre_insertado,user_insertado,pass_insertado,nota_insertada,dias_insertados,cartegory_insertada);
    }

    private void doPost(final String name, final String user, final String pass, final String note,
                        final Integer dias, final Integer categoria) {
        String token = sharedPreferences.getString("token",null);
        Log.d("Crear password 4", token);
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("passwordName",name);
            json.accumulate("password",pass);
            json.accumulate("expirationTime",dias);
            json.accumulate("passwordCategoryId",categoria);
            json.accumulate("optionalText",note);
            json.accumulate("userName",user);
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
                        startActivity(new Intent(CrearPasswordSeis.this, Principal.class));
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
