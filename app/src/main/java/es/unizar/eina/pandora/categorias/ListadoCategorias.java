package es.unizar.eina.pandora.categorias;

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.unizar.eina.pandora.Inicio;
import es.unizar.eina.pandora.Principal;
import es.unizar.eina.pandora.R;
import es.unizar.eina.pandora.adaptadores.CategoryAdapter;
import es.unizar.eina.pandora.adaptadores.PrincipalAdapter;
import es.unizar.eina.pandora.plataforma.ContactarUno;
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

public class ListadoCategorias extends AppCompatActivity {

    private final String urlEliminarCategoria = "https://pandorapp.herokuapp.com/api/categorias/eliminar";
    private final String urlListarCategorias = "https://pandorapp.herokuapp.com/api/categorias/listar";
    private final String urlEliminarCuenta = "https://pandorapp.herokuapp.com/api/usuarios/eliminar";

    private final OkHttpClient httpClient = new OkHttpClient();

    CategoryAdapter listaAdapter;
    private ArrayList<JSONObject> lista_respuesta = new ArrayList<>();
    JSONArray lista = new JSONArray();
    private RecyclerView listaCategories;
    JSONObject deleted_category;

    private Toolbar toolbar;
    private TextView drawerEmail;
    private DrawerLayout drawer;
    private NavigationView drawerView;
    private SwipeRefreshLayout swipeLayout;
    private View headerDrawer;


    //Necesario para el borrado de categorias deslizando hacia el lateral
    ItemTouchHelper.SimpleCallback touchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                int position = viewHolder.getAdapterPosition();
                deleted_category = lista_respuesta.get(position);
                String name = deleted_category.getString("categoryName");
                if(name.equals("Sin categoría")){
                    listaAdapter.notifyItemChanged(position);
                    Toast.makeText(getApplicationContext(),"No se puede eliminar la categoría \"Sin categoría\"", Toast.LENGTH_LONG).show();
                }else if(name.equals("Compartida")){
                    listaAdapter.notifyItemChanged(position);
                    Toast.makeText(getApplicationContext(),"No se puede eliminar la categoría \"Compartida\"", Toast.LENGTH_LONG).show();
                }
                else{
                    lista_respuesta.remove(position);
                    listaAdapter.notifyItemRemoved(position);
                    Integer id_category = deleted_category.getInt("catId");
                    Log.d("categoryName",name);
                    Log.d("catId",id_category.toString());
                    borrarCategory(id_category,name,position);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_categorias);

        //Creación del adaptador de categorías
        try{
            doPostCategory();
            toArrayList();
        }
        catch (InterruptedException | JSONException e){
            e.printStackTrace();
        }
        listaAdapter = new CategoryAdapter(ListadoCategorias.this,lista_respuesta);
        listaCategories = findViewById(R.id.categories_recyclerview);
        listaCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listaCategories.setAdapter(listaAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(listaCategories);

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
        drawerEmail.setText(SharedPreferencesHelper.getInstance(getApplicationContext()).getString("email"));

        // Swipe refresh
        swipeLayout = findViewById(R.id.categories_refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                try {
                    lista_respuesta.clear();
                    doPostCategory();
                    toArrayList();
                    listaAdapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void borrarCategory(final Integer id, String name, final int position){
        // Confirmar que queremos eliminar la categoría
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("¿Eliminar categoría: " + name +"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doPostEliminarCategoria(id);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Volvemos a colocar la categoría en su lugar
                lista_respuesta.add(position,deleted_category);
                listaAdapter.notifyItemInserted(position);
            }
        });
        builder.show();
    }

    public void doPostEliminarCategoria(Integer id_category) {
        // Recogemos el token
        String token = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("token");
        String urlAux = urlEliminarCategoria+"?id=" + Integer.toString(id_category);
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
                    Log.d("CATEGORIA ELIMINADA","OK");
                }else{
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
                        lista = json.getJSONArray("categories");
                        Log.d("Categorias", lista.toString());
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

    public void administrarContrasenyas(MenuItem menuItem){
        startActivity(new Intent(ListadoCategorias.this, Principal.class));
    }

    public void cerrarSesion(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).clear();
        startActivity(new Intent(ListadoCategorias.this, Inicio.class));
        finishAffinity();
    }

    public void contactar(MenuItem menuItem){
        SharedPreferencesHelper.getInstance(getApplicationContext()).put("guest",false);
        startActivity(new Intent(ListadoCategorias.this, ContactarUno.class));
    }

    public void sobrePandora(MenuItem menuItem){
        startActivity(new Intent(ListadoCategorias.this, SobrePandora.class));
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
                    startActivity(new Intent(ListadoCategorias.this, Inicio.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
        });
    }

    public void addCategory(View view){
        startActivity(new Intent(ListadoCategorias.this, CrearCategoria.class));
    }
}
