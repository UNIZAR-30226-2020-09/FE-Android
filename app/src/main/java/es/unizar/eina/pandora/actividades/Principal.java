package es.unizar.eina.pandora.actividades;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import es.unizar.eina.pandora.R;

public class Principal extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    // Urls para peticiones.
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

    // Elementos de la interfaz.
    private Toolbar toolbar;
    private TextView drawerEmail;
    private DrawerLayout drawer;
    private NavigationView drawerView;
    private SwipeRefreshLayout swipeLayout;
    private View headerDrawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Recuperar información del usuario.
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);

        // Menú desplegable.
        toolbar = findViewById(R.id.topbar_toolbar);
        drawer = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.principal_drawer);
        //drawerView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        headerDrawer = drawerView.getHeaderView(0);
        drawerEmail = headerDrawer.findViewById(R.id.drawer_email);

        // Layout refresh pass.
        swipeLayout = findViewById(R.id.principal_refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                // algo habrá que hacer -> petición
                swipeLayout.setRefreshing(false);
            }
        });

        // algo habrá que hacer -> petición
    }
}
