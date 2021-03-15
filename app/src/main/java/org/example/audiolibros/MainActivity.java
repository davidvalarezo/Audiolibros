package org.example.audiolibros;

import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.example.audiolibros.adaptadores.AdaptadorLibrosFiltro;
import org.example.audiolibros.casosusos.GetLastBook;
import org.example.audiolibros.casosusos.SaveLastBook;
import org.example.audiolibros.fragments.DetalleFragment;
import org.example.audiolibros.fragments.PreferenciasFragment;
import org.example.audiolibros.fragments.SelectorFragment;
import org.example.audiolibros.interfaces.LibroStorage;
import org.example.audiolibros.singleton.LibrosSingleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {
    //private LibroSharedPreferenceStorage libroSharedPreferenceStorage;
    public boolean dosFragments;
    private AdaptadorLibrosFiltro adaptador;

    //Ejercicio: Ocultar / mostrar elementos al cambiar
    private AppBarLayout appBarLayout;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    //Fin Ejercicio: Ocultar / mostrar elementos al cambiar
    private Bundle savedState;
    private int mayorTamañoDisp;
    private LibroStorage libroStorage;
    //private MainController controller;
    private MainPresenter presenter;
    private LibrosSingleton libros;
    private SaveLastBook saveBook;
    private GetLastBook getLastBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        libros = LibrosSingleton.getInstance(this);
        //libroSharedPreferenceStorage = new LibroSharedPreferenceStorage(this);
        libroStorage = LibroSharedPreferenceStorage.getInstance(this);
        //controller = new MainController( new LibroSharedPreferenceStorage(this));
        saveBook = new SaveLastBook(libroStorage);
        getLastBook = new GetLastBook(new BooksRespository(libroStorage));
        presenter = new MainPresenter(saveBook, getLastBook,this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        savedState = savedInstanceState;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.clickFavoriteButton();
            }
        });

        //Ejercicio: Ocultar / mostrar elementos al cambiar de fragment
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Fin Ejercicio: Ocultar / mostrar elementos al cambiar de fragment

        View detalleFragment = findViewById(R.id.detalle_fragment);
        if(detalleFragment != null){
            getSupportFragmentManager().popBackStackImmediate();
        }
        dosFragments = detalleFragment != null &&
                detalleFragment.getVisibility() == View.VISIBLE;
        int width = this.getResources().getConfiguration().screenWidthDp;
        int height = this.getResources().getConfiguration().screenHeightDp;
        mayorTamañoDisp = width>height ? width:height;
        if(mayorTamañoDisp>600){
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
            getSupportFragmentManager().popBackStackImmediate();
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null && !fragment.equals(f)) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        }

        ponFragmentIzquierdo(new SelectorFragment());
        //Fin Ejercicio

        // Ejercicio: Añadir pestañas con TabLayout.
        adaptador = libros.getAdaptador();
        //Pestañas
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Todos"));
        tabs.addTab(tabs.newTab().setText("Nuevos"));
        tabs.addTab(tabs.newTab().setText("Leidos"));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //Todos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(false);
                        break;
                    case 1: //Nuevos
                        adaptador.setNovedad(true);
                        adaptador.setLeido(false);
                        break;
                    case 2: //Leidos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(true);
                        break;
                }
                adaptador.notifyDataSetChanged();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        //Añadir un Navigation Drawer en Audiolibros
        // Navigation Drawer
        drawer = (DrawerLayout) findViewById( R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_preferencias) {
            //(1) Reemplazar Intent i = new Intent(this, PreferenciasActivity.class); startActivity(i); por:
            this.reemplazaFragmentIzquierdo(new PreferenciasFragment());
            // Fin (1)
            return true;
        }
        else if (id == R.id.menu_acerca) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void irUltimoVisitado() {
        presenter.clickFavoriteButton();
    }

    public void mostrarDetalle(int id) {
        presenter.openDetalle(id);
    }

    @Override
    public void mostrarNoUltimaVisita() {
        Toast.makeText(this, "Sin última vista",Toast.LENGTH_LONG).show();
    }
    @Override
    public void mostrarFragmentDetalle(int id) {
        DetalleFragment detalleFragment = (DetalleFragment)
                getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (dosFragments) {
            detalleFragment.ponInfoLibro(id);
        } else {
            detalleFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id);
            detalleFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor_pequeno, detalleFragment)
                    .addToBackStack(null).commit();
        }
    }

    public void mostrarBarraAmpliada(boolean mostrar) {
        appBarLayout.setExpanded(mostrar);
        toggle.setDrawerIndicatorEnabled(mostrar);
        if (mostrar) {
            //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            tabs.setVisibility(View.VISIBLE);
        }
        else { tabs.setVisibility(View.GONE);
            //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //add preferencias
        if (id == R.id.nav_preferencias) {
           // Intent i = new Intent(this, PreferenciasActivity.class); startActivity(i);
            this.reemplazaFragmentIzquierdo(new PreferenciasFragment());
        }
        else if (id == R.id.nav_compartir) { }
        else {
            filterBooksByGenre(id);
        }
        closeDrawer();
        return true;
    }
    private void closeDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void filterBooksByGenre(int idGenre){
        switch (idGenre){
            case R.id.nav_todos:
                adaptador.setGenero("");
                adaptador.notifyDataSetChanged();
                break;
            case  R.id.nav_epico:
                adaptador.setGenero(Libro.G_EPICO);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_XIX:
                adaptador.setGenero(Libro.G_S_XIX);
                adaptador.notifyDataSetChanged();
                break;
            case  R.id.nav_suspense:
                adaptador.setGenero(Libro.G_SUSPENSE);
                adaptador.notifyDataSetChanged();
                break;
        }

    }

    @Override public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //(1)
    public void ponFragmentIzquierdo(Fragment fragment) {
        int idContenedor = dosFragments ? R.id.contenedor_izquierdo
                                        : R.id.contenedor_pequeno;
        if(mayorTamañoDisp<600){
            if( fragment != null && !dosFragments ){
                if(savedState != null){
                    return;
                }
            }
        }
            getSupportFragmentManager().beginTransaction()
                    .add(idContenedor, fragment).commit();
    }

    public void reemplazaFragmentIzquierdo(Fragment fragment) {
        int idContenedor = dosFragments ? R.id.contenedor_izquierdo
                : R.id.contenedor_pequeno;
        getSupportFragmentManager().beginTransaction()
                .replace(idContenedor, fragment).addToBackStack(null).commit();
    }//(1) Fin

    @Override
    public void onStop() {
/*        Fragment f = getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getSupportFragmentManager().popBackStackImmediate(); // Sin esta linea logro que 2 fragmen vaya al fragmen detalle, pero no aparece selector fragme y hay 2 voces (excluyendo el borrado de selector fragmen)
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null && fragment.equals(f)) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        }*/
        super.onStop();
    }

}
