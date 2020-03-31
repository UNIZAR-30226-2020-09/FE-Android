package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.R;

public class CrearPasswordUno extends AppCompatActivity {

    private TextView nombre;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_uno);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        nombre = findViewById(R.id.crear_password_1_nombre);
        siguiente = findViewById(R.id.crear_password_1_button_continuar);
    }

    public void goSiguiente(View view){
        if(!nombre.getText().toString().equals("")){
            String nombre_insertado = nombre.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("password_name",nombre_insertado);
            editor.commit();
            startActivity(new Intent(this,CrearPasswordDos.class));
        }else{
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la contraseña", Toast.LENGTH_LONG).show();
        }
    }
}
