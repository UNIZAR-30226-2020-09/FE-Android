package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenerarPassword extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/contrasenya/generar";
    private final OkHttpClient httpClient = new OkHttpClient();

    Boolean min = true;
    Boolean may = true;
    Boolean num = true;
    Boolean car = true;

    EditText l;
    Button generar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_password);

        generar = findViewById(R.id.generar_button);
        l = findViewById(R.id.generar_entrada_lon);
        l.setTransformationMethod(null);

    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
    public void generar(View view){
        if (!min && !may && !car && !num){
            Toast.makeText(getApplicationContext(),"Debes seleccionar al menos uno de estos: minúsculas, mayúsculas, números o caracteres especiales", Toast.LENGTH_LONG).show();
        }
        else{
            String lon = l.getText().toString();
            if(lon.equals("")){
                Toast.makeText(getApplicationContext(),"El campo longitud no puede estar vacío", Toast.LENGTH_LONG).show();
            }
            else if(Integer.parseInt(lon) > 40 || Integer.parseInt(lon) < 4 ){
                    Toast.makeText(getApplicationContext(),"El campo longitud debe tener un valor entre 4 y 40", Toast.LENGTH_LONG).show();
            }
            else {
                doPost(Integer.parseInt(lon));
            }
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.generar_check_min:
                min = checked;
                break;
            case R.id.generar_check_may:
                may = checked;
                break;
            case R.id.generar_check_num:
                num = checked;
                break;
            case R.id.generar_check_car:
                car = checked;
                break;
        }
    }

    private void doPost(final Integer lo) {
        Log.d("Minusculas", String.valueOf(min));
        Log.d("Mayusculas", String.valueOf(may));
        Log.d("Numeros", String.valueOf(num));
        Log.d("Especiales", String.valueOf(car));
        Log.d("Longitud", String.valueOf(lo));
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("minus",min);
            json.accumulate("mayus",may);
            json.accumulate("numbers",num);
            json.accumulate("specialCharacters",car);
            json.accumulate("length",lo);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", Objects.requireNonNull(formBody.contentType()).toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (response.isSuccessful()) {
                        String pass = json.getString("password");
                        PrintOnThread.show(getApplicationContext(), "Contraseña generada " + pass);
                        SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_pass",pass);
                        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
                        //Dependiendo de la actividad anterior, vamos a una actividad u otra
                        String origen = sharedPreferencesHelper.getString("origen");
                        if(origen.equals("crear")){
                            startActivity(new Intent(GenerarPassword.this, CrearPasswordCuatro.class));
                        }else{
                            SharedPreferencesHelper.getInstance(getApplicationContext()).put("generar",true);
                            SharedPreferencesHelper.getInstance(getApplicationContext()).put("generada",pass);
                            startActivity(new Intent(GenerarPassword.this, EditarPassword.class));
                        }
                        finish();
                    }
                    else {
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}
