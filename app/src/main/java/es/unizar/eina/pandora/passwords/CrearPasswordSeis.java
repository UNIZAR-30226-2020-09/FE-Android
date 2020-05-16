package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

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

public class CrearPasswordSeis extends AppCompatActivity {
    String url = "https://pandorapp.herokuapp.com/api/contrasenya/insertar";
    String urlCompartida = "https://pandorapp.herokuapp.com/api/grupo/insertar";

    private final OkHttpClient httpClient = new OkHttpClient();

    private TextView note;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password_seis);
        note = findViewById(R.id.crear_password_6_note);

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        password = sharedPreferencesHelper.getString("password",null);
        Log.d("LOGIN OK",password);
    }

    public void crear(View view){
        String nota_insertada = note.getText().toString().trim();
        if(nota_insertada.length()>100){
            Toast.makeText(getApplicationContext(),"La longitud de la nota no pude superar 100 caracteres", Toast.LENGTH_LONG).show();
        }else{
            //Recuperamos los datos introducidos
            String nombre_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_name");
            String user_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_user");
            String pass_insertado = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_pass");
            int dias_insertados = SharedPreferencesHelper.getInstance(getApplicationContext()).getInt("password_dias");
            int category_insertada = SharedPreferencesHelper.getInstance(getApplicationContext()).getInt("password_cat");
            doPost(nombre_insertado,user_insertado,pass_insertado,nota_insertada,dias_insertados,category_insertada);
        }
    }

    public void compartir(View view){
        startActivity(new Intent(CrearPasswordSeis.this, CrearPasswordCompartida.class));
    }

    private void doPost(final String name, final String user, final String pass, final String note,
                        final int dias, final int categoria) {
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("passwordName",name);
            json.accumulate("password",pass);
            json.accumulate("expirationTime",dias);
            json.accumulate("passwordCategoryId",categoria);
            json.accumulate("optionalText",note);
            json.accumulate("userName",user);
            json.accumulate("usuarios", getEmails(SharedPreferencesHelper.getInstance(getApplicationContext()).getString("password_mails")));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            if(json.getJSONArray("usuarios").length() > 0){
                url = urlCompartida;
            }
        } catch (JSONException e) {
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
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (response.isSuccessful()) {
                        if(url.equals(urlCompartida)) {
                            JSONArray usuarios = json.getJSONArray("usuariosErroneos");
                            String listaErroneos = "";
                            for (int i = 0; i < usuarios.length(); i++) {
                                listaErroneos = "\n" + usuarios.get(i).toString();
                            }
                            String _response = "Contraseña grupal creada.";
                            if(usuarios.length() > 0) {
                                _response += " La contraseña no pudo ser compartida con:" + listaErroneos;
                            }

                            PrintOnThread.show(getApplicationContext(), _response);
                        }
                        else {
                            PrintOnThread.show(getApplicationContext(), "Contraseña creada.");
                        }

                        // Vaciamos los datos recogidos de los correos a compartir para no reutilizarlos otra vez
                        SharedPreferencesHelper.getInstance(getApplicationContext()).put("password_mails", "");

                        startActivity(new Intent(CrearPasswordSeis.this, Principal.class));
                        finishAffinity();
                    }
                    else {
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    private JSONArray getEmails(String emails){
        if(emails != null && emails.equals("")){
            return new JSONArray();
        }
        JSONArray respuesta = new JSONArray();
        String[] partes = emails.replaceAll(" ", "").split(",");
        for (String email : partes) {
            if(hasEmailFormat(email)){
                Log.d("Parte", email);
                respuesta.put(email);
            }
        }
        return respuesta;
    }

    private boolean hasEmailFormat(String candidato){
        String emailRegex = "^(.+)@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);

        return pat.matcher(candidato).matches();
    }

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
