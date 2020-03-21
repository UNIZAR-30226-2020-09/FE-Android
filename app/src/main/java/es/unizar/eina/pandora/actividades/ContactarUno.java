package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.R;

public class ContactarUno extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar_uno);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        mensaje = findViewById(R.id.contactar1_entrada_comunicado);
    }

    public void goSiguiente(View view){
        if(!mensaje.getText().toString().equals("")){
            String mensaje_insertado = mensaje.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("mensaje",mensaje_insertado);
            editor.commit();
            startActivity(new Intent(this,ContactarDos.class));
        }
    }
}
