package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class CrearPasswordCuatro extends AppCompatActivity {

    private EditText dias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_cuatro);
        dias = findViewById(R.id.crear_password_4_dias);
        dias.setTransformationMethod(null);
    }

    public void goSiguiente(View view){
        String aux = dias.getText().toString();
        if(!dias.getText().toString().equals("")) {
            int aux2 = Integer.valueOf(aux);
            if(aux2 >= 1 && aux2 <= 365){
                SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_dias",aux2);
                startActivity(new Intent(this, CrearPasswordCinco.class));
            }
            else{
                Toast.makeText(getApplicationContext(),"El periodo de validez debe estar entre 1 y 365", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Debe introducir un periodo de validez para la contraseña", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }

}
