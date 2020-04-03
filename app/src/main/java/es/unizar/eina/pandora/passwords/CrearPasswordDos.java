package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class CrearPasswordDos extends AppCompatActivity {

    private TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_dos);
        user = findViewById(R.id.crear_password_2_user);
    }

    public void goSiguiente(View view){
        //Puede ser vacío
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_user",user.getText().toString());
        startActivity(new Intent(this,CrearPasswordTres.class));
    }
}
