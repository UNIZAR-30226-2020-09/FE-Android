package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CrearPasswordCinco extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/categorias/listar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private Spinner categorias;
    ArrayList<String> name_category = new ArrayList<>();
    ArrayList<Integer> id_category = new ArrayList<>();

    Integer id_cat;

    JSONArray cat = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_cinco);

        categorias = findViewById(R.id.crear_password_5_category);
        try {
            doPostCategory();
            getCategoryNameAndId();
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, name_category);
        categorias.setAdapter(categoriesAdapter);
    }

    public void goSiguiente(View view){
        String cat_name = categorias.getSelectedItem().toString();
        //Buscamos su id:
        boolean encontrado=false;
        String aux;
        int i = 0;
        while (!id_category.isEmpty() && !encontrado ){
            aux = name_category.get(i);
            if (aux.equals(cat_name)){
                Log.d("Encontrado","OK");
                encontrado = true;
                id_cat = id_category.get(i);
            }else{
                i++;
            }
        }
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_cat",id_cat);
        startActivity(new Intent(CrearPasswordCinco.this,CrearPasswordSeis.class));
    }

    protected void getCategoryNameAndId() throws JSONException {
        JSONObject aux;
        String name;
        Integer id;
        for (int i=0; i < cat.length();i++){
            aux = cat.getJSONObject(i);
            name = aux.getString("categoryName");
            id = aux.getInt("catId");
            Log.d("Category",name + "" + Integer.toString(id));
            name_category.add(name);
            id_category.add(id);
        }
        Log.d("OK",Integer.toString(cat.length()));
    }

    public void doPostCategory() throws InterruptedException {
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        // Enviamos la petición SÍNCRONA
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", response.body().string());
                    } else {
                        final JSONObject json = new JSONObject(response.body().string());
                        cat = json.getJSONArray("categories");
                    }
                }
                catch (IOException | JSONException e){
                    Log.d("EXCEPCION ", e.getMessage());
                }
            }
        });
        thread.start();
        thread.join();
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
