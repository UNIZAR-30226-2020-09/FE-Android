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

public class CrearPasswordDos extends AppCompatActivity {

    private TextView user;
    private Button siguiente;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_dos);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        user = findViewById(R.id.crear_password_2_user);
        siguiente = findViewById(R.id.crear_password_2_button_continuar);
    }

    public void goSiguiente(View view){
        //Puede ser vacío
        String user_insertado = user.getText().toString();
        Log.d("CrearDos", user_insertado);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password_user",user_insertado);
        editor.commit();
        startActivity(new Intent(this,CrearPasswordTres.class));
    }
}
