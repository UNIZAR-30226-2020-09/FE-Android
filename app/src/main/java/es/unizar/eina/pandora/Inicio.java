package es.unizar.eina.pandora;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import es.unizar.eina.pandora.autenticacion.Login;
import es.unizar.eina.pandora.autenticacion.RegistroUno;
import es.unizar.eina.pandora.plataforma.ContactarUno;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Inicio extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/estadisticas";
    private final OkHttpClient httpClient = new OkHttpClient();


    TextView nUsuarios;
    TextView nPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        nUsuarios = findViewById(R.id.inicio_nUsers);
        nPass = findViewById(R.id.inicio_nPass);

        doPost();
    }

    public void goLogin(View view){
        startActivity(new Intent(Inicio.this, Login.class));
    }

    public void goRegistro(View view){
        startActivity(new Intent(Inicio.this, RegistroUno.class));
    }

    public void goContacto(View view){
        startActivity(new Intent(Inicio.this, ContactarUno.class));
    }

    private void doPost() {
        // Formamos la petición
        final okhttp3.Request request = new Request.Builder().url(url).build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        PrintOnThread.changeValue(getApplicationContext(),nUsuarios,Integer.toString(json.getInt("nUsuarios")));
                        PrintOnThread.changeValue(getApplicationContext(),nPass,Integer.toString(json.getInt("nContraseñas")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}
