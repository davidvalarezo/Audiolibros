package org.example.audiolibros.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.example.audiolibros.adaptadores.AdaptadorLibrosFiltro;
import org.example.audiolibros.singleton.LibrosSingleton;
import org.example.audiolibros.MainActivity;
import org.example.audiolibros.casosusos.OpenDetailClickAction;
import org.example.audiolibros.casosusos.OpenMenuItemClickAction;
import org.example.audiolibros.R;
import org.example.audiolibros.SearchObservable;
import org.example.audiolibros.SelectorViewModel;

public class SelectorFragment extends Fragment implements Animator.AnimatorListener{
    private Activity actividad;
    private RecyclerView recyclerView;
    //private List<Libro> listaLibros;
    private AdaptadorLibrosFiltro adaptador;
    private LibrosSingleton librosSingleton;
    private SelectorViewModel model;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.actividad = getActivity();
        librosSingleton = LibrosSingleton.getInstance(context);
        adaptador = librosSingleton.getAdaptador();
        model = ViewModelProviders.of((FragmentActivity) actividad).get(SelectorViewModel.class);
        model.getLibros().observe(this, libros -> {
            // actualizamos IU
            adaptador.setData(libros);
            //adaptador.notifyDataSetChanged();
        });
    }

    @Override public View onCreateView(LayoutInflater inflador,
                                       ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById( R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad,2)); //aqui para que se muestre como una lista
        recyclerView.setAdapter(adaptador);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setMoveDuration(2000);
        recyclerView.setItemAnimator(animator);
        //Añadir un menú desde un fragment. <opcional>
        setHasOptionsMenu(true);
        adaptador.setClickAction(new OpenDetailClickAction((MainActivity) getActivity()));
        adaptador.setOnItemLongClickListener(new OpenMenuItemClickAction(actividad, vista,
                (Animator.AnimatorListener) SelectorFragment.this, model ));
        return vista;
    }

    //Añadir un menú desde un fragment. <opcional>
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Esto pare que se vuelva crear de nuevo todos los item del menú
        if (menu.findItem(R.id.menu_buscar)==null) {
            inflater.inflate(R.menu.menu_selector, menu);
        }
        //Ejercicio: Incorporando búsquedas dinámicamente
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
            SearchView searchView = (SearchView) searchItem.getActionView();
            SearchObservable searchObservable = new SearchObservable();
            searchObservable.addObserver(adaptador);
            searchView.setOnQueryTextListener(searchObservable);
            searchItem.setOnActionExpandListener( new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    adaptador.setBusqueda("");
                    adaptador.notifyDataSetChanged();
                    return true; // Para permitir cierre
                }
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true; // Para permitir expansión
                }
            });
        //Fin del Ejercicio: Incorporando búsquedas dinámicamente
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        }
        else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) actividad).mostrarBarraAmpliada(true);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
