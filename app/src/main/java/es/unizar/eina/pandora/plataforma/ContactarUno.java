package es.unizar.eina.pandora.plataforma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class ContactarUno extends AppCompatActivity {

    TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar_uno);
        mensaje = findViewById(R.id.contactar1_entrada_comunicado);
    }

    public void goSiguiente(View view){
        String m = mensaje.getText().toString();
        if(!m.equals("")){
            if(m.length()<5 || m.length()>250){
                Toast.makeText(getApplicationContext(),"La longitud del mensaje debe estar entre 5 y 250 caracteres", Toast.LENGTH_LONG).show();
            }else{
                SharedPreferencesHelper.getInstance(getApplicationContext()).put("mensaje",mensaje.getText().toString());
                startActivity(new Intent(this,ContactarDos.class));
            }
        }else{
            Toast.makeText(getApplicationContext(),"El mensaje no puede estar vacío", Toast.LENGTH_LONG).show();
        }
    }
}
