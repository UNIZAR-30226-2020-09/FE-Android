package es.unizar.eina.pandora.autenticacion;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;
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

public class Login extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/usuarios/login";
    private final OkHttpClient httpClient = new OkHttpClient();

    TextView email;
    TextView password;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_entrada_usuario);
        password = findViewById(R.id.login_entrada_clave);
        button = findViewById(R.id.login_entrar);
    }

    public void entrar(View view) {
        if(email.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Los campos email y contraseña no pueden estar vacíos", Toast.LENGTH_LONG).show();
        }
        else{
            button.setEnabled(false);
            doPost();
        }
    }

    private void doPost() {
        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try {
            json.accumulate("mail", email.getText().toString());
            json.accumulate("masterPassword", password.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", Objects.requireNonNull(formBody.contentType()).toString())
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (response.isSuccessful()) {
                        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
                        sharedPreferencesHelper.put("email", email.getText().toString().trim());
                        sharedPreferencesHelper.put("password", password.getText().toString().trim());
                        startActivity(new Intent(Login.this, Login2FA.class));
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
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
