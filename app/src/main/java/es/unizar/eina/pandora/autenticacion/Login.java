package es.unizar.eina.pandora.autenticacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class Login extends AppCompatActivity {



    TextView email;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_entrada_usuario);
        password = findViewById(R.id.login_entrada_clave);
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
