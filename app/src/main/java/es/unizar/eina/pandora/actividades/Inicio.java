package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.unizar.eina.pandora.R;

public class Inicio extends AppCompatActivity {
    private Button login;
    private Button registro;
    private Button contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        login = findViewById(R.id.inicio_login);
        registro = findViewById(R.id.inicio_registro);
        contacto = findViewById(R.id.inicio_contacto);
    }

    public void goLogin(View view){
        startActivity(new Intent(Inicio.this,Login.class));
    }

    public void goRegistro(View view){
        startActivity(new Intent(Inicio.this, Registro.class));
    }

    public void goContacto(View view){
        startActivity(new Intent(Inicio.this, Contacto.class));
    }
}
