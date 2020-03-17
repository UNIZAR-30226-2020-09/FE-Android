package es.unizar.eina.pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistroDos extends AppCompatActivity {

    String url = "https://pandorapp.herokuapp.com/api/users/registro";


    private Button confirmar;

    SharedPreferences sharedpreferences;

    String savedEmail;

    private EditText password;          // Campo de contraseña.
    private EditText confirmPassword;   // Campo de confirmar contraseña.

    private Boolean mostrandoPass1 = false;
    private Boolean mostrandoPass2 = false;

    //Comprobación longitud contraseñas
    private Boolean passwordCheckLength = false;

    // Comprobación de que las dos contraseñas son iguales.
    private Boolean confirmCheck = false;

    // Comprobaciones de patrones y caracteres de los campos del registro.
    private Boolean passwordCheckValue = false;

    // Comprobación de que los campos de las contraseñas no están vacíos
    private Boolean passwordEmpty = true;

    private TextView limitPass;     // Límite de caracteres de la contraseña.
    private TextView limitPass2;    // Límite de caracteres de confirmar contraseña.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_dos);

        confirmar = findViewById(R.id.registro2_confirmar);
        password = findViewById(R.id.registro2_clave1);
        confirmPassword = findViewById(R.id.registro2_clave2);
        limitPass = findViewById(R.id.registro2_long_clave);
        limitPass2 = findViewById(R.id.registro2_long_clave);

        password.addTextChangedListener(registerTextWatcher);
        confirmPassword.addTextChangedListener(registerTextWatcher);

        sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        savedEmail = sharedpreferences.getString("email","");

    }

    private TextWatcher registerTextWatcher = new TextWatcher() {

        private String _password;
        private String _confirmpassword;

        private String newLimitpassword;
        private  String newLimitpassword2;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            _password= password.getText().toString().trim();
            _confirmpassword = password.getText().toString().trim();

            Integer passchars = 40 - _password.length();
            if (passchars < 0) {
                limitPass.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitPass.setTextColor(Color.rgb(128,128,128));
            }

            newLimitpassword = passchars.toString();

            Integer pass2chars = 40 - _confirmpassword.length();
            if (passchars < 0) {
                limitPass2.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitPass2.setTextColor(Color.rgb(128,128,128));
            }

            newLimitpassword2 = pass2chars.toString();

            limitPass.setText(newLimitpassword);
            limitPass2.setText(newLimitpassword2);

            passwordEmpty = _password.isEmpty();
        }

        @Override // Soloo puede tener letras mayusculas, minusci,as si n acentuar numeros y _-.
        public void afterTextChanged(Editable s) {
            passwordCheckLength = (_password.length() <= 40 && _password.length() >= 8);
            passwordCheckValue = _password.matches("[a-zA-Z0-9_]+");

            //Comprueba que las dos claves son iguales
            confirmCheck = _password.equals(_confirmpassword);
            confirmar.setEnabled(!passwordEmpty);
            if (confirmar.isEnabled()) {
                confirmar.setBackgroundTintList(AppCompatResources.getColorStateList(confirmar.getContext(), R.color.colorButtonEnabled));
            }
            else {
                confirmar.setBackgroundTintList(AppCompatResources.getColorStateList(confirmar.getContext(), R.color.colorButtonDisabled));
            }
        }
    };

    public void password_visible(View view){
        if (mostrandoPass1){
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrandoPass1 = false;
        }
        else{
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrandoPass1 = true;
        }
    }

    public void password2_visible(View view){
        if (mostrandoPass2){
            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrandoPass2 = false;
        }
        else{
            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrandoPass2 = true;
        }
    }

    // Se registra el usuario si se cumplen todos los requisitos, si no se muestra el error.
    public void registrar(View view){
        if (!passwordCheckLength) {
            Toast.makeText(getApplicationContext(),"LLa contraseña tiene que tener entre 8 y 40 caracteres.", Toast.LENGTH_LONG).show();
        }
        else if(!passwordCheckValue) {
            Toast.makeText(getApplicationContext(),"La contraseña solo puede tener letras mayúsculas o minúsculas sin acentuar, números, y los caracteres _ y -.", Toast.LENGTH_LONG).show();
        }
        else if (!confirmCheck) {
            Toast.makeText(getApplicationContext(),"Contraseñas no coinciden.", Toast.LENGTH_LONG).show();
        }
        else {
            String password_in = password.getText().toString().trim();
            //Guardamos la contraseña
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.putString("password",password_in);

            confirmar.setEnabled(false);

            doPost(password_in,savedEmail);
        }
    }


    private void doPost(final String contrasena, final String correo) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final org.json.JSONObject jsonBody = new org.json.JSONObject();

            jsonBody.put("mail",correo);
            jsonBody.put("masterPassword",contrasena);

            final String requestBody = jsonBody.toString();

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // TODO Auto-generated method stub
                            // hazle un print al object o lo que gustes
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.putString("email",correo);
                            editor.putString("password",contrasena);
                            editor.commit();
                            startActivity(new Intent(RegistroDos.this, Login.class));
                            finish();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

            RequestManager.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
