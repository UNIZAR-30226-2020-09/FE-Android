package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class CrearPasswordCompartida extends AppCompatActivity {

    EditText mailsEditText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_compartida);
        mailsEditText = findViewById(R.id.crear_contraseña_correos);
        button = findViewById(R.id.button_compartir);
    }

    public void confirmar(View view){
        button.setEnabled(false);
        String mails = mailsEditText.getText().toString();
        Log.d("Mails", mails);
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_mails", mails);
        finish();
    }

    public void cancel(View view){
        finish();
    }
}
