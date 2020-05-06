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
        String u =user.getText().toString();
        if(u.length()>30){
            Toast.makeText(getApplicationContext(),"La longitud del nombre se usuario no debe ser superior a 30", Toast.LENGTH_LONG).show();
        }else{
            SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_user",u);
            startActivity(new Intent(this,CrearPasswordTres.class));
        }
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
