package es.unizar.eina.pandora.autenticacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;

public class RegistroUno extends AppCompatActivity {

    private Button siguiente;

    private EditText email;                     //Campo para escribir el email
    private Boolean emptyEmail = true;          //Campo del email vacío?
    private Boolean emailCheckLength = false;
    private Boolean emailCheckValue = false;
    private TextView limitEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_uno);

        siguiente = findViewById(R.id.registro1_texto_que_desea);
        email = findViewById(R.id.registro1_correo);
        limitEmail = findViewById(R.id.registro1_long_correo);

        email.addTextChangedListener(registerTextWatcher);

        siguiente.setEnabled(false);
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {

        private String correo;
        private String newLimitEmail;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            correo = email.getText().toString().trim();

            Integer mailchars = 100 - correo.length();
            if (mailchars < 0) {
                limitEmail.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitEmail.setTextColor(Color.rgb(128,128,128));
            }
            newLimitEmail = mailchars.toString();

            limitEmail.setText(newLimitEmail);

            emptyEmail = correo.isEmpty();
        }

        @Override
        public void afterTextChanged(Editable s) {
            emailCheckLength = (correo.length() <= 100 && correo.length() >= 6);
            emailCheckValue = correo.matches("[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+");

            siguiente.setEnabled(!emptyEmail);
        }
    };

    // Se registra el usuario si se cumplen todos los requisitos, si no se muestra el error.
    public void goSiguiente(View view){
        if (!emailCheckLength || !emailCheckValue) {
            Toast.makeText(getApplicationContext(),"La dirección de correo tiene que tener entre 3 y 100 caracteres y ser válida", Toast.LENGTH_LONG).show();
        }
        else {
            String emailIntroducido = email.getText().toString().trim();
            //Guardamos el correo del usuario para seguir con el registro
            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
            sharedPreferencesHelper.put("email",emailIntroducido);
            //Cambiamos de actividad
            startActivity(new Intent(this,RegistroDos.class));
        }
    }
}
