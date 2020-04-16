package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class CrearPasswordTres extends AppCompatActivity {

    private TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_tres);
        password = findViewById(R.id.crear_password_3_password);
    }

    public void goSiguiente(View view){
        if(!password.getText().toString().equals("")){
            SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_pass",password.getText().toString());
            startActivity(new Intent(this,CrearPasswordCuatro.class));
        }
        else{
            Toast.makeText(getApplicationContext(),"El campo contraseña no puede estar vacío", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
