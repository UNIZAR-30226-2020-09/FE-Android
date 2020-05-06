package es.unizar.eina.pandora.autenticacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Login2FA extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/usuarios/loginCon2FA";
    private final OkHttpClient httpClient = new OkHttpClient();

    private String codigo;
    private String email;

    private EditText _codigo;
    private Button entrar;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2_f);

        _codigo = findViewById(R.id.login_2FA_entrada_codigo);
        entrar = findViewById(R.id.login_2FA_entrar);

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        email = sharedPreferencesHelper.getString("email");
        password = sharedPreferencesHelper.getString("password");
    }

    public void entrar(View view){
        codigo = _codigo.getText().toString();
        if(codigo.equals("")){
            Toast.makeText(getApplicationContext(),"El campo codigo no puede estar vacío", Toast.LENGTH_LONG).show();
        }else{

            doPost();
        }
    }

    private void doPost() {
        Log.d("correo", email);
        Log.d("contrasena", password);
        Log.d("codigo", codigo);
        Log.d("++++++++++++++","ok");
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try {
            json.accumulate("mail", email);
            json.accumulate("masterPassword", password);
            json.accumulate("verificationCode", codigo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", formBody.contentType().toString())
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        Log.d("Ok","OK");
                        String token = json.getString("token");
                        Log.d("token", token);
                        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
                        sharedPreferencesHelper.put("token", token);
                        startActivity(new Intent(Login2FA.this, Principal.class));
                        finishAffinity();
                    } else {
                        Log.d("No Ok",json.getString("statusText"));
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
