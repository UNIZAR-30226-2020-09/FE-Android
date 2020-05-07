package es.unizar.eina.pandora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import es.unizar.eina.pandora.adaptadores.PrincipalAdapter;
import es.unizar.eina.pandora.categorias.CrearCategoria;
import es.unizar.eina.pandora.categorias.ListadoCategorias;
import es.unizar.eina.pandora.plataforma.ContactarUno;
import es.unizar.eina.pandora.passwords.CrearPasswordUno;
import es.unizar.eina.pandora.plataforma.SobrePandora;
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
    private final String urlEliminarPasswordCompartida = "https://pandorapp.herokuapp.com/api/grupo/eliminar";
    private final String urlListarCategorias = "https://pandorapp.herokuapp.com/api/categorias/listar";
    private final String urlListarPasswordsOfACategory = "https://pandorapp.herokuapp.com/api/contrasenya/listarPorCategoria";
    private final String urlListarPasswordsCompartidas = "https://pandorapp.herokuapp.com/api/grupo/listar";

    private final OkHttpClient httpClient = new OkHttpClient();

    // Información del usuario.
    private String email;
    private String password;
    private ArrayList<JSONObject> lista_respuesta = new ArrayList<>();
    JSONArray lista = new JSONArray();
    JSONArray listaCategories = new JSONArray();
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

    private FrameLayout catButton;
    private FrameLayout conButton;

    boolean pulsado = false;

    String categoriaFiltrada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Recuperar información del usuario.
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        email = sharedPreferencesHelper.getString("email",null);
        password = sharedPreferencesHelper.getString("password",null);

        listaPass = findViewById(R.id.principal_recyclerview_password);

        categoriaFiltrada = "Todas";

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
                    lista_respuesta.clear();
                    doPostPasswordsOfACategory(categoriaFiltrada);
                    doPostCategory();
                    toArrayList();
                    listaAdapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Para hacer notificaciones de contraseñas expiradas
        String CHANNEL_ID = "Pandora";
        createNotificationChannel(CHANNEL_ID);

        try {
            doPostPasswordsOfACategory(categoriaFiltrada);
            doPostCategory();
            toArrayList();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

        //Creación del adaptador de contraseñas
        listaAdapter = new PrincipalAdapter(Principal.this,lista_respuesta);
        listaPass.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listaPass.setAdapter(listaAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(listaPass);
    }

    public void mostrarFiltradoCategorias(View v) throws InterruptedException, JSONException {
        doPostCategory();

        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Todas");
        for(int i = 0; i < listaCategories.length(); i++){
            popup.getMenu().add(listaCategories.getJSONObject(i).getString("categoryName"));
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                categoriaFiltrada = item.getTitle().toString();
                try {
                    lista_respuesta.clear();
                    doPostPasswordsOfACategory(categoriaFiltrada);
                    doPostCategory();
                    toArrayList();
                    listaAdapter.notifyDataSetChanged();
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        popup.show();
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
            try {
                if(deleted_password.getString("categoryName").equals("Compartida")) {
                    lista_respuesta.remove(position);
                    listaAdapter.notifyItemRemoved(position);
                    String name = deleted_password.getString("passwordName");
                    Integer id_pass = deleted_password.getInt("passId");
                    borrarPassword(id_pass, name, position, true);
                }
                else{
                    lista_respuesta.remove(position);
                    listaAdapter.notifyItemRemoved(position);
                    String name = deleted_password.getString("passwordName");
                    Integer id_pass = deleted_password.getInt("passId");
                    borrarPassword(id_pass, name, position, false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //Pasar JSONArray a un ArrayList<JSONObject>
    protected void toArrayList() throws JSONException {
        for (int i = 0; i < lista.length(); i++){
            lista_respuesta.add(lista.getJSONObject(i));
        }
    }

    public void administrarCategorias(MenuItem menuItem){
        startActivity(new Intent(Principal.this, ListadoCategorias.class));
    }

    public void cerrarSesion(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
        startActivity(new Intent(Principal.this, Inicio.class));
        finishAffinity();
    }

    public void contactar(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("guest",false);
        startActivity(new Intent(Principal.this, ContactarUno.class));
    }

    public void sobrePandora(MenuItem menuItem){
        startActivity(new Intent(Principal.this, SobrePandora.class));
    }

    public void eliminarCuenta(MenuItem menuItem){
        // Confirmar que queremos eliminar la cuenta
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("¿Está seguro de que quiere borrar su cuenta?");
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

    protected void borrarPassword(final Integer id, String name, final int position, final boolean compartida){
        // Confirmar que queremos eliminar la contraseña
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("¿Eliminar contraseña: " + name +"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (compartida){
                    doPostEliminarPasswordCompartida(id);
                }
                else{
                    doPostEliminarPassword(id);
                }
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
        }
        else{
            pulsado = false;
            catButton.setVisibility(View.INVISIBLE);
            conButton.setVisibility(View.INVISIBLE);
        }
    }

    public void addCategory(View view){
        startActivity(new Intent(Principal.this, CrearCategoria.class));
    }

    public void addPassword(View view){
        startActivity(new Intent(Principal.this, CrearPasswordUno.class));
    }

    public void doPostPassword() throws InterruptedException {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

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
        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlListarPassword)
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();
        // Hacemos la petición SÍNCRONA
        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        lista = json.getJSONArray("passwords");
                        notificarPasswordsExpiradas(lista);
                    }else{
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                }
                catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    public void doPostSharedPasswords() throws InterruptedException {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlListarPasswordsCompartidas)
                .addHeader("Authorization", token)
                .get()
                .build();

        // Hacemos la petición SÍNCRONA
        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        lista = json.getJSONArray("passwords");
                        notificarPasswordsExpiradas(lista);
                    }
                    else{
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                }
                catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    private void doPostPasswordsOfACategory(String category) throws InterruptedException, JSONException {
        Log.d("Category",category);
        int idCat;

        if(category.equals("Todas")){
            doPostPassword();
            return;
        }
        else if(category.equals("Compartida")){
            doPostSharedPasswords();
            return;
        }
        else{
            idCat = getCategoryId(category);
        }

        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        JSONObject json = new JSONObject();
        try{
            json.accumulate("masterPassword",password);
            json.accumulate("idCat",idCat);
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
                .url(urlListarPasswordsOfACategory)
                .addHeader("Content-Type", formBody.contentType().toString())
                .addHeader("Authorization", token)
                .post(formBody)
                .build();
        // Hacemos la petición SÍNCRONA
        // Enviamos la petición en un thread nuevo y actuamos en función de la respuesta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        lista = json.getJSONArray("passwords");
                        notificarPasswordsExpiradas(lista);
                    }
                    else{
                        PrintOnThread.show(getApplicationContext(), json.getString("statusText"));
                    }
                }
                catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    private int getCategoryId(String category) throws JSONException {
        for(int i = 0; i < listaCategories.length(); i++){
            if(listaCategories.getJSONObject(i).getString("categoryName").equals(category)){
                return listaCategories.getJSONObject(i).getInt("catId");
            }
        }
        return -1;
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
                    PrintOnThread.show(getApplicationContext(), "Contraseña eliminada");
                    Log.d("PASSWORD ELIMINADA","OK");
                }
                else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void doPostEliminarPasswordCompartida(Integer id_pass) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");
        String urlAux = urlEliminarPasswordCompartida+"?id=" + Integer.toString(id_pass);
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
                    PrintOnThread.show(getApplicationContext(), "Contraseña eliminada");
                    Log.d("PASSWORD ELIMINADA","OK");
                }
                else{
                    Log.d("fallo","mal");
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void doPostCategory() throws InterruptedException {
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");

        // Formamos la petición con el cuerpo creado
        final Request request = new Request.Builder()
                .url(urlListarCategorias)
                .addHeader("Authorization", token)
                .build();

        // Enviamos la petición SÍNCRONA
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.d("ERROR ", response.body().string());
                    } else {
                        final JSONObject json = new JSONObject(response.body().string());
                        listaCategories = json.getJSONArray("categories");
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

    private void notificarPasswordsExpiradas(JSONArray passwords){
        try{
            for(int i = 0; i < passwords.length(); i++) {

                JSONObject password = passwords.getJSONObject(i);
                int days = password.getInt("noDaysBeforeExpiration");

                if (days < 0) {

                    String texto_notificacion = "Tu contraseña \"" +  password.getString("passwordName") + "\" ha expirado. Cámbiala para dejar de recibir esta notificación";

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("Pandora", "Pandora", NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription("Notificaciones");
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                            .setSmallIcon(R.drawable.logo) // notification icon
                            .setContentTitle("Pandora") // title for notification
                            .setContentText(texto_notificacion)// message for notification
                            .setAutoCancel(true)    // clear notification after click
                            .setChannelId("Pandora");
                    Intent intent = new Intent(getApplicationContext(), Principal.class);
                    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    mNotificationManager.notify(i, mBuilder.build());
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pandora";
            String description = "Notificaciones";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
