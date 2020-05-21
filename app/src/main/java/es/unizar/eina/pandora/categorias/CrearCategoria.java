package es.unizar.eina.pandora.categorias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrearCategoria extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/categorias/insertar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView name;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_categoria);
        name = findViewById(R.id.crear_categoria_1_nombre);
        button = findViewById(R.id.crear_categoria_1_button_confirmar);
    }

    public void confirmar(View view){
        String nombre_introducido = name.getText().toString();
        if(!nombre_introducido.equals("")){
            if(nombre_introducido.length()>30 ){
                Toast.makeText(getApplicationContext(),"Longitud de categoria entre 1 y 30", Toast.LENGTH_LONG).show();
            }else {
                Log.d("n",nombre_introducido);
                button.setEnabled(false);
                doPostCategoria(nombre_introducido);
            }

        }else{
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la categoría", Toast.LENGTH_LONG).show();
        }
    }

    private void doPostCategoria(final String name) {
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("categoryName",name);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", Objects.requireNonNull(formBody.contentType()).toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (response.isSuccessful()) {
                        PrintOnThread.show(getApplicationContext(), "Categoría creada");
                        String activity = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("addCategory");
                        if(activity.equals("password")){
                            startActivity(new Intent(CrearCategoria.this, Principal.class));
                        }
                        else if(activity.equals("category")){
                            startActivity(new Intent(CrearCategoria.this, ListadoCategorias.class));
                        }
                        finishAffinity();
                    }
                    else {
                        PrintOnThread.setEnabled(getApplicationContext(), button);
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
