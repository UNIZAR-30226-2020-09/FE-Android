package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import es.unizar.eina.pandora.R;

public class ContactarTres extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar_tres);
    }

    public void terminar(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean guest = sharedPreferences.getBoolean("guest",true);
        // Si no habíamos iniciado sesión, vamos a la pantalla de inicio
        if(guest){
            startActivity(new Intent(ContactarTres.this, Inicio.class));
            finish();
        }
        // En caso contrario, volvemos a la pantalla principal
        else{
            startActivity(new Intent(ContactarTres.this, Principal.class));
            finish();
        }
    }
}
