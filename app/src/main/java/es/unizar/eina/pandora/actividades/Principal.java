package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.unizar.eina.pandora.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Principal extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    final String urlListarPassword = "https://pandorapp.herokuapp.com/api/contrasenya/listar";
    private final OkHttpClient httpClient = new OkHttpClient();

    // Urls para peticiones: cambiar las de Yapper a algunas que tengan sentido en Pandora
    /*
    private String urlBorrar = "https://yapper-app.herokuapp.com/borrarUsuario";
    private String urlRecuperarUsuario = "https://yapper-app.herokuapp.com/recuperarUsuario";
    private String urlListarYaps = "https://yapper-app.herokuapp.com/listarYaps";
    private String urlListarYapsPorCategoria = "https://yapper-app.herokuapp.com/listarYapsPorCategoria?categoria=";
    private String urlListarCategorias = "https://yapper-app.herokuapp.com/getCategorias";
    private String urlRecuperarLikesDeUssuario = "https://yapper-app.herokuapp.com/listaDeLikesDeUnUsuario";
    */

    // Información del usuario.
    private String email;
    JSONArray lista_respuesta = new JSONArray();

    // Elementos de la interfaz.
    private Toolbar toolbar;
    private TextView drawerEmail;
    private DrawerLayout drawer;
    private NavigationView drawerView;
    private SwipeRefreshLayout swipeLayout;
    private View headerDrawer;
    private RecyclerView listaPass;

    private FloatingActionButton addMenu;
    private FloatingActionButton addCat;
    private FloatingActionButton addCon;

    private FrameLayout catButton;
    private FrameLayout conButton;

    boolean pulsado=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Log.d("Principal", "1");

        // Recuperar información del usuario.
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);

        //Botones de crear categoria y contraseña
        addMenu = findViewById(R.id.principal_add_button);
        addCat = findViewById(R.id.principal_add_cat_button);
        addCon = findViewById(R.id.principal_add_con_button);

        catButton = findViewById(R.id.principal_frame_cat);
        conButton = findViewById(R.id.principal_frame_con);

        // Menú desplegable.
        toolbar = findViewById(R.id.topbar_toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        drawerView = findViewById(R.id.principal_drawer);
        headerDrawer = drawerView.getHeaderView(0);
        drawerEmail = headerDrawer.findViewById(R.id.drawer_email);

        drawerEmail.setText(email);
        Log.d("Principal", "2");

        listaPass = (RecyclerView) findViewById(R.id.principal_recyclerview_password);
        try {
            doPostPassword();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Principal","3");
        Log.d("Prueba",Integer.toString(lista_respuesta.length()));
        PrincipalAdapter lista= new PrincipalAdapter(Principal.this,lista_respuesta);
        listaPass.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listaPass.setAdapter(lista);


    }

    public void cerrarSesion(MenuItem menuItem){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Borramos información de la sesión y del usuario
        editor.clear();
        editor.commit();
        startActivity(new Intent(Principal.this, Inicio.class));
    }

    public void contactar(MenuItem menuItem){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("guest",false);
        editor.commit();
        startActivity(new Intent(Principal.this, ContactarUno.class));
    }

    //Pulsar el boton inferior con el "+"
    public void pulsarAdd(View view){
        if(!pulsado){
            pulsado = true;
            catButton.setVisibility(View.VISIBLE);
            conButton.setVisibility(View.VISIBLE);
        }else{
            pulsado = false;
            catButton.setVisibility(View.INVISIBLE);
            conButton.setVisibility(View.INVISIBLE);
        }
    }

    public void addCategory(View view){
        Log.d("ADD CATEGORY","TODO OK");
    }

    public void addPassword(View view){
        startActivity(new Intent(Principal.this, CrearPasswordUno.class));
        Log.d("ADD PASSWORD","TODO OK");
    }

    public void doPostPassword() throws InterruptedException {
        String token = sharedPreferences.getString("token",null);
        Log.d("Crear password 4", token);


        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlListarPassword)
                .addHeader("Authorization", token)
                .build();

        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", response.body().string());
                    } else {
                        final JSONObject json = new JSONObject(response.body().string());
                        lista_respuesta = json.getJSONArray("passwords");
                        Log.d("AAAAAAAAAAAAA",Integer.toString(lista_respuesta.length()));
                    }
                }
                catch (IOException | JSONException e){
                    Log.d("EXCEPCION ", e.getMessage());
                }
            }
        });
        thread.start();
        thread.join();
    }
}
