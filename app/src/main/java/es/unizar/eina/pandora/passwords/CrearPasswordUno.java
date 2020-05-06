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

public class CrearPasswordUno extends AppCompatActivity {

    private TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_uno);
        nombre = findViewById(R.id.crear_password_1_nombre);
    }

    public void goSiguiente(View view){
        String n =nombre.getText().toString();
        if(!n.equals("")){
            if(n.length()>30 || n.length()<1){
                Toast.makeText(getApplicationContext(),"La longitud del nombre debe estar entre 1 y 30", Toast.LENGTH_LONG).show();
            }else{
                SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_name",nombre.getText().toString());
                startActivity(new Intent(this,CrearPasswordDos.class));
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la contraseña", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
