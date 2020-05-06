package es.unizar.eina.pandora.autenticacion;

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

public class Login extends AppCompatActivity {



    TextView email;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_entrada_usuario);
        password = findViewById(R.id.login_entrada_clave);


        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        sharedPreferencesHelper.put("email", "javierreraul@gmail.com");
        sharedPreferencesHelper.put("password", "raulito1!A");
        startActivity(new Intent(Login.this, Principal.class));
    }

    public void entrar(View view) {
        if(email.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Los campos email y contraseña no pueden estar vacíos", Toast.LENGTH_LONG).show();
        }
        else{
            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
            sharedPreferencesHelper.put("email", email.getText().toString().trim());
            sharedPreferencesHelper.put("password", password.getText().toString().trim());

            startActivity(new Intent(Login.this, Login2FA.class));
        }
    }
}
