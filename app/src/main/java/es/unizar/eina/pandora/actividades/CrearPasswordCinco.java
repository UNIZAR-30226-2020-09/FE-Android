package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import es.unizar.eina.pandora.R;

public class CrearPasswordCinco extends AppCompatActivity {

    //private Spinner categorias;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_cinco);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        //dias = findViewById(R.id.crear_password_4_dias);
        siguiente = findViewById(R.id.crear_password_5_button_continuar);
    }

    public void goSiguiente(View view){
        //Puede ser vacío
        //String dias_insertado = dias.getText().toString();
        //Log.d("CrearDos", dias_insertado);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("password_user",dias_insertado);
        //editor.commit();
        startActivity(new Intent(CrearPasswordCinco.this,CrearPasswordSeis.class));
    }
}
