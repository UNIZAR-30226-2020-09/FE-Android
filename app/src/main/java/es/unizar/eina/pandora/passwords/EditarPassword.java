package es.unizar.eina.pandora.passwords;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class EditarPassword extends AppCompatActivity {

    String url = "https://pandorapp.herokuapp.com/api/categorias/listar";
    String urlEditar = "https://pandorapp.herokuapp.com/api/contrasenya/modificar";
    String urlEditarCompartida = "https://pandorapp.herokuapp.com/api/grupo/modificar";

    private final OkHttpClient httpClient = new OkHttpClient();

    private EditText nombre;
    private EditText usuario;
    private EditText password;
    private EditText validez;
    private EditText nota;

    private Integer _id;
    private String _nombre;
    private String _usuario;
    private String _password;
    private String _validez;
    private String _nota;
    private String category_name;
    private int rol;
    private String spassword;

    //Para el Spinner con las categorias
    private Spinner categorias;
    ArrayList<String> name_category = new ArrayList<>();
    ArrayList<Integer> id_category = new ArrayList<>();
    Integer id_cat;
    JSONArray cat = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_password);

        nombre = findViewById(R.id.editar_password_nombre);
        usuario = findViewById(R.id.editar_password_usuario);
        password = findViewById(R.id.editar_password_pass);
        validez = findViewById(R.id.editar_password_dias);
        validez.setTransformationMethod(null);
        nota = findViewById(R.id.editar_password_nota);
        categorias=findViewById(R.id.editar_password_cat);

        //Conseguimos las categorias para el spinner
        try {
            doPostCategory();
            getCategoryNameAndId();
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        //Recuperamos la información de la contraseña
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        JSONObject password = new JSONObject();
        spassword = sharedPreferencesHelper.getString("password",null);
        Log.d("EDITAR OK",spassword);

        password = sharedPreferencesHelper.getJSONObject("Password_info");
        Log.d("PruebaPassword",password.toString());
        try {
            _id = password.getInt("passId");
            _nombre = password.getString("passwordName");
            _usuario = password.getString("userName");
            _password = password.getString("password");
            int dias = password.getInt("noDaysBeforeExpiration");
            _validez = Integer.toString(dias);
            _nota = password.getString("optionalText");
            category_name = password.getString("categoryName");
            rol = password.getInt("rol");
        } catch (JSONException ignored) { }

        nombre.setText(_nombre);
        usuario.setText(_usuario);
        Boolean g = SharedPreferencesHelper.getInstance(getApplicationContext()).getBoolean("generar");
        Log.d("¿Generar?", String.valueOf(g));
        if(g){
            //Después de generar, mostrar la contraseña generada
            _password = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("generada");
            Log.d("Password generada", _password);
            SharedPreferencesHelper.getInstance(getApplicationContext()).put("generar",false);
        }

        (this.password).setText(_password);
        validez.setText(_validez);
        nota.setText(_nota);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, name_category);
        categorias.setAdapter(categoriesAdapter);
        if (category_name!= null) {
            int spinnerPosition = categoriesAdapter.getPosition(category_name);
            categorias.setSelection(spinnerPosition);
        }
    }

    public void generar(View view){
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("origen","editar");
        startActivity(new Intent(this,GenerarPassword.class));
    }

    public void goConfirmar(View view){
       getCategory();
       _nombre = nombre.getText().toString();
       _usuario = usuario.getText().toString();
       _password = password.getText().toString();
       _validez = validez.getText().toString();
       int dias = 0;
       boolean empty = false;
       if(_validez.equals("")){
           empty=true;
       }else{
           dias = Integer.parseInt(_validez);
       }
       _nota = nota.getText().toString();
       if(checkParameters(_nombre,_password,dias,empty)){
           doPostEditar(_id, _nombre, _usuario, _password,_nota, dias, id_cat);
       }
    }

    //Devuelve true si se cumplen todas las restricciones de los parametros de las contraseñas
    private Boolean checkParameters(String name, String pass, Integer dias,Boolean empty){
        boolean isOK = false;
        if(name.equals("")){
            Toast.makeText(getApplicationContext(),"Debe introducir un nombre para la contraseña", Toast.LENGTH_LONG).show();
        }else if(pass.equals("")){
            Toast.makeText(getApplicationContext(),"El campo contraseña no puede estar vacío", Toast.LENGTH_LONG).show();
        }else if(pass.length() < 4){
            Toast.makeText(getApplicationContext(), "La longitud de la contraseña de usuario no debe ser inferior a 4", Toast.LENGTH_LONG).show();
        }else if(pass.length() > 40){
            Toast.makeText(getApplicationContext(), "La longitud de la contraseña de usuario no debe ser superior a 40", Toast.LENGTH_LONG).show();
        }
        else if(empty){
            Toast.makeText(getApplicationContext(),"El periodo de validez no puede estar vacío", Toast.LENGTH_LONG).show();
        }else if(dias<1 || dias>365){
            Toast.makeText(getApplicationContext(),"El periodo de validez debe estar entre 1 y 365", Toast.LENGTH_LONG).show();
        }else{
            isOK=true;
        }
        return isOK;
    }

    //Conseguir el id de la categoria seleccionada
    private void getCategory(){
        String cat_name = categorias.getSelectedItem().toString();
        //Buscamos su id:
        boolean encontrado=false;
        String aux;
        int i = 0;
        while (!id_category.isEmpty() && !encontrado ){
            aux = name_category.get(i);
            if (aux.equals(cat_name)){
                Log.d("Encontrado","OK");
                encontrado = true;
                id_cat = id_category.get(i);
            }else{
                i++;
            }
        }
    }

    protected void getCategoryNameAndId() throws JSONException {
        JSONObject aux;
        String name;
        int id;
        for (int i=0; i < cat.length();i++){
            aux = cat.getJSONObject(i);
            name = aux.getString("categoryName");
            //Eliminar "Compartida" de la lista para evitar problemas
            if(!name.equals("Compartida")) {
                id = aux.getInt("catId");
                Log.d("Category", name + "" + id);
                name_category.add(name);
                id_category.add(id);
            }
        }
        Log.d("OK",Integer.toString(cat.length()));
    }

    public void doPostCategory() throws InterruptedException {
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        // Enviamos la petición SÍNCRONA
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", Objects.requireNonNull(response.body()).string());
                    } else {
                        final JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                        cat = json.getJSONArray("categories");
                    }
                }
                catch (IOException | JSONException e){
                    Log.d("EXCEPCION ", Objects.requireNonNull(e.getMessage()));
                }
            }
        });
        thread.start();
        thread.join();
    }

    private void doPostEditar(final Integer _id,final String name, final String user, final String pass, final String note,
                        final int dias, final int categoria) {
        // Recuperamos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos un JSON con los parámetros
        JSONObject json = new JSONObject();
        try{
            json.accumulate("masterPassword",spassword);
            json.accumulate("id",_id);
            json.accumulate("passwordName",name);
            json.accumulate("password",pass);
            json.accumulate("expirationTime",dias);
            json.accumulate("passwordCategoryId",categoria);
            json.accumulate("optionalText",note);
            json.accumulate("userName",user);
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
        final Request request;

        if(!category_name.equals("Compartida")){
            request = new Request.Builder()
                    .url(urlEditar)
                    .addHeader("Content-Type", Objects.requireNonNull(formBody.contentType()).toString())
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .build();
        }
        else{
            request = new Request.Builder()
                    .url(urlEditarCompartida)
                    .addHeader("Content-Type", Objects.requireNonNull(formBody.contentType()).toString())
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .build();
        }

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (response.isSuccessful()) {
                        PrintOnThread.show(getApplicationContext(), "Contraseña editada");
                        startActivity(new Intent(EditarPassword.this, Principal.class));
                        finishAffinity();
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

    public void cancel(View view){
        startActivity(new Intent(this, Principal.class));
        finishAffinity();
    }
}
