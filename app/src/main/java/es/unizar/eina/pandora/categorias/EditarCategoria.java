package es.unizar.eina.pandora.categorias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

public class EditarCategoria extends AppCompatActivity {

    final String urlEditarCategoria = "https://pandorapp.herokuapp.com/api/categorias/modificar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView name;
    private JSONObject category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_categoria);
        name = findViewById(R.id.editar_categoria_1_nombre);
        category = SharedPreferencesHelper.getInstance(getApplicationContext()).getJSONObject("Category_info");
        TextView texto = findViewById(R.id.editar_categoria_1_texto);
        try {
            texto.setText(texto.getText() + " " + category.getString("categoryName") + "?");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void confirmar(View view) throws JSONException {
        String nombre_introducido = name.getText().toString();
        if(!nombre_introducido.equals("")){
            if(nombre_introducido.length()>30 || nombre_introducido.length()<1){
                Toast.makeText(getApplicationContext(),"Longitud de cateoria entre 1 y 30", Toast.LENGTH_LONG).show();
            }else {
                doPostCategoria(name.getText().toString(), category.getInt("catId"));
            }
        }else{
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la categoría", Toast.LENGTH_LONG).show();
        }
    }

    private void doPostCategoria(final String name, final int id) {
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("categoryName",name);
            json.accumulate("id",id);
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
                .url(urlEditarCategoria)
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        PrintOnThread.show(getApplicationContext(), "Categoría editada");
                        startActivity(new Intent(EditarCategoria.this, ListadoCategorias.class));
                        finishAffinity();
                    }
                    else {
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}
