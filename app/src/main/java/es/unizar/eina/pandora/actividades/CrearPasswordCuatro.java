package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.unizar.eina.pandora.R;

public class CrearPasswordCuatro extends AppCompatActivity {

    private TextView dias;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_cuatro);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        dias = findViewById(R.id.crear_password_4_dias);
        siguiente = findViewById(R.id.crear_password_4_button_continuar);
    }

    public void goSiguiente(View view){
        //Puede ser vacío
        String dias_insertado = dias.getText().toString();
        Log.d("CrearDos", dias_insertado);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password_dias",dias_insertado);
        editor.commit();
        startActivity(new Intent(this,CrearPasswordCinco.class));
    }

}
