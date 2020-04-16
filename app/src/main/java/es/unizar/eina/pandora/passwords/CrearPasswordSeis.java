package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class CrearPasswordSeis extends AppCompatActivity {
    final String url = "https://pandorapp.herokuapp.com/api/contrasenya/insertar";
    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView note;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_seis);
        note = findViewById(R.id.crear_password_6_note);

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        password = sharedPreferencesHelper.getString("password",null);
        Log.d("LOGIN OK",password);
    }

    public void crear(View view){
        //Recuperamos los datos introducidos
        String nombre_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_name");
        String user_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_user");
        String pass_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_pass");
        String nota_insertada = note.getText().toString().trim();
        int dias_insertados = SharedPreferencesHelper.getInstance(getApplicationContext()).getInt("password_dias");
        int cartegory_insertada = SharedPreferencesHelper.getInstance(getApplicationContext()).getInt("password_cat");
        doPost(nombre_insertado,user_insertado,pass_insertado,nota_insertada,dias_insertados,cartegory_insertada);
    }

    private void doPost(final String name, final String user, final String pass, final String note,
                        final int dias, final int categoria) {
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("masterPassword",password);
            json.accumulate("passwordName",name);
            json.accumulate("password",pass);
            json.accumulate("expirationTime",dias);
            json.accumulate("passwordCategoryId",categoria);
            json.accumulate("optionalText",note);
            json.accumulate("userName",user);
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
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        startActivity(new Intent(CrearPasswordSeis.this, Principal.class));
                        finishAffinity();
                    }
                    else {
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
                    }
                } catch (JSONException e){
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
