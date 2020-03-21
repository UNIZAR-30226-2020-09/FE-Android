package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        startActivity(new Intent(ContactarTres.this, Inicio.class));
        finish();
    }
}
