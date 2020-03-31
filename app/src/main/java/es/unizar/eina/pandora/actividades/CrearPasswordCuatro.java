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
import android.widget.Toast;

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
        String aux = dias.getText().toString();
        if(!dias.getText().toString().equals("") && isNumeric(aux)) {
            Integer aux2 = Integer.parseInt(aux);
            if(aux2 >= 1 && aux2<=365){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("password_dias", aux2);
                editor.commit();
                startActivity(new Intent(this, CrearPasswordCinco.class));
            }else{
                Toast.makeText(getApplicationContext(),"El periodo de validez debe estar entre 1 y 365", Toast.LENGTH_LONG).show();
            }
        }else if(!dias.getText().toString().equals("") && !isNumeric(aux)){
            Toast.makeText(getApplicationContext(),"El periodo de validez debe ser un número entre 1 y 365", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Debe introducir un periodo de validez para la contraseña", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

}
