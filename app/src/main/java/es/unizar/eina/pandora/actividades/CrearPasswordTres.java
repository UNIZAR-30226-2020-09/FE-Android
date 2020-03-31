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

public class CrearPasswordTres extends AppCompatActivity {

    private TextView password;
    private Button generar;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_tres);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        password = findViewById(R.id.crear_password_3_password);
        siguiente = findViewById(R.id.crear_password_3_button_continuar);
        generar = findViewById(R.id.crear_password_3_button_generar);
    }

    public void goSiguiente(View view){
        if(!password.getText().toString().equals("")){
            String password_insertado = password.getText().toString();
            Log.d("Crear password 3",password_insertado);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("password_pass",password_insertado);
            editor.commit();
            startActivity(new Intent(this,CrearPasswordCuatro.class));
        }
        else{
            Toast.makeText(getApplicationContext(),"El campo contraseña no puede estar vacío", Toast.LENGTH_LONG).show();
        }

    }
}
