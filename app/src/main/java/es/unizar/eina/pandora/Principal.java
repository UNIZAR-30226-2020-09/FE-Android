package es.unizar.eina.pandora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.unizar.eina.pandora.adaptadores.PrincipalAdapter;
import es.unizar.eina.pandora.categorias.CrearCategoria;
import es.unizar.eina.pandora.contacto.ContactarUno;
import es.unizar.eina.pandora.passwords.CrearPasswordUno;
import es.unizar.eina.pandora.passwords.EditarPassword;
import es.unizar.eina.pandora.utiles.PrintOnThread;
import es.unizar.eina.pandora.utiles.SharedPreferencesHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Principal extends AppCompatActivity {

    private final String urlListarPassword = "https://pandorapp.herokuapp.com/api/contrasenya/listar";
    private final String urlEliminarCuenta = "https://pandorapp.herokuapp.com/api/usuarios/eliminar";
    private final String urlEliminarPassword = "https://pandorapp.herokuapp.com/api/contrasenya/eliminar";

    private final OkHttpClient httpClient = new OkHttpClient();

    // Información del usuario.
    private String email;
    private String password;
    private ArrayList<JSONObject> lista_respuesta = new ArrayList<>();
    JSONArray lista = new JSONArray();
    JSONObject deleted_password;

    PrincipalAdapter listaAdapter;
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

    boolean pulsado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Log.d("Principal", "1");

        // Recuperar información del usuario.
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        email = sharedPreferencesHelper.getString("email",null);
        password = sharedPreferencesHelper.getString("password",null);
        Log.d("LOGIN OK",password);

        listaPass = findViewById(R.id.principal_recyclerview_password);



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

        // Swipe refresh
        swipeLayout = findViewById(R.id.principal_refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                try {
                    doPostPassword();
                    toArrayList();
                    swipeLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            doPostPassword();
            toArrayList();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Longitud lista password",Integer.toString(lista_respuesta.size()));

        //Creación del adaptador de contraseñas
        listaAdapter= new PrincipalAdapter(Principal.this,lista_respuesta);
        listaPass.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listaPass.setAdapter(listaAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(listaPass);
    }

    //Necesario para el borrado de contraseñas deslizando hacia el lateral
    ItemTouchHelper.SimpleCallback touchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleted_password = lista_respuesta.get(position);
            lista_respuesta.remove(position);
            listaAdapter.notifyItemRemoved(position);
            try {
                String name = deleted_password.getString("passwordName");
                Log.d("Nombre password",name);
                Integer id_pass = deleted_password.getInt("passId");
                Log.d("Id password", Integer.toString(id_pass));
                borrarPassword(id_pass,name,position);
                Log.d("BORRADO","OK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    //Pasar JSONArray a un ArrayList<JSONObject>
    protected void toArrayList() throws JSONException {
        ArrayList<JSONObject> listaAux = new ArrayList<>();
        Log.d("ToArrayList","IN");
        JSONObject aux;
        for (int i=0;i<lista.length();i++){
            aux = lista.getJSONObject(i);
            listaAux.add(aux);
            Log.d("Bucle", "Bucle");
        }
        lista_respuesta = listaAux;
        Log.d("ToArrayList",Integer.toString(lista_respuesta.size()));
        Log.d("ToArrayList","IN");
    }

    public void cerrarSesion(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
        startActivity(new Intent(Principal.this, Inicio.class));
        finish();
    }

    public void contactar(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("guest",false);
        startActivity(new Intent(Principal.this, ContactarUno.class));
    }

    public void eliminarCuenta(MenuItem menuItem){
        // Confirmar que queremos eliminar la cuenta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Está seguro de que quiere borrar su cuenta?");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doPostEliminarCuenta();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    protected void borrarPassword(final Integer id, String name, final int position){
        // Confirmar que queremos eliminar la contraseña
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Eliminar contraseña: " + name +"?");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doPostEliminarPassword(id);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Volvemos a colocar la pass word en su contraseña
                lista_respuesta.add(position,deleted_password);
                listaAdapter.notifyItemInserted(position);
            }
        });
        builder.show();
    }

    // Pulsar el boton inferior con el "+"
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
        startActivity(new Intent(Principal.this, CrearCategoria.class));
    }

    public void addPassword(View view){
        startActivity(new Intent(Principal.this, CrearPasswordUno.class));
        Log.d("ADD PASSWORD","TODO OK");
    }

    public void doPostPassword() throws InterruptedException {
        // Recogemos el token
        Log.d("dentro","OKK");
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");
        Log.d("dentro","OKK");

        JSONObject json = new JSONObject();
        try{
            json.accumulate("masterPassword",password);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Formamos el cuerpo de la petición con el JSON creado
        RequestBody formBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );
        Log.d("dentro","OKK");
        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlListarPassword)
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();
        Log.d("dentro","OKK3");
        // Hacemos la petición SÍNCRONA
        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        Log.d("dentro","OKK3");
                        Log.d("LISTAR",json.toString());
                        lista = json.getJSONArray("passwords");
                    }else{
                        Log.d("NO OK","F");
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
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

    public void doPostEliminarCuenta() {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos la petición con el token (IMPORTANTE VER QUE ES DE TIPO DELETE)
        final Request request = new Request.Builder()
                .url(urlEliminarCuenta)
                .addHeader("Authorization", token)
                .delete()
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
                    startActivity(new Intent(Principal.this, Inicio.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void doPostEliminarPassword(Integer id_pass) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");
        String urlAux = urlEliminarPassword+"?id=" + Integer.toString(id_pass);
        Log.d("URL",urlAux);

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlAux)
                .addHeader("Authorization", token)
                .delete()
                .build();

        // Hacemos la petición
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d("PASSWORD ELIMINADA","OK");
                }else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }
}
