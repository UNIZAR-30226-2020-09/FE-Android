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

        /*
        Por si vuelven a cambiar el token, necesitaremos iniciar sesión para que nos den uno nuevo
        //String _email = sharedPreferences.getString("email",null);
        //String _password = sharedPreferences.getString("password",null);
        // Si ya tenemos estos datos, iniciamos sesión automáticamente

        if (_email != null && _password != null) {
            doPost(sharedPreferences.getString("email",null),
                    sharedPreferences.getString("password",null));
            finish();
        }
        */

        email = findViewById(R.id.login_entrada_usuario);
        password = findViewById(R.id.login_entrada_clave);
    }

    public void entrar(View view) {
        if(email.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Los campos email y contraseña no pueden estar vacíos", Toast.LENGTH_LONG).show();
        }else{
            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
            sharedPreferencesHelper.put("email", email.getText().toString().trim());
            sharedPreferencesHelper.put("password", password.getText().toString().trim());

            startActivity(new Intent(Login.this, Login2FA.class));
        }


    }


}
