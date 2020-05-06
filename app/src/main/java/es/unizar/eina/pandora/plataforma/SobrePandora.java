package es.unizar.eina.pandora.plataforma;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.utiles.MiRunnable;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SobrePandora extends AppCompatActivity {

    final String url = "https://pandorapp.herokuapp.com/api/estadisticas";
    private final OkHttpClient httpClient = new OkHttpClient();

    // Para hacer las peticiones continuas de las stats
    final Handler handler = new Handler();
    MiRunnable recargarEstadisticas = new MiRunnable() {
        @Override
        public void run() {
            if(!isKilled()) {
                doPost();
                Log.d("Pido estadisticas", "Sobre Pandora");
                handler.postDelayed(this, 60000);
            }
        }
    };

    TextView nUsuarios;
    TextView nPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_pandora);
        nUsuarios = findViewById(R.id.sobre_pandora_nUsers);
        nPass = findViewById(R.id.sobre_pandora_nPass);
        handler.post(recargarEstadisticas);
    }

    public void contactar(View item){
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("guest",false);
        recargarEstadisticas.killRunnable();
        startActivity(new Intent(SobrePandora.this, ContactarUno.class));
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
