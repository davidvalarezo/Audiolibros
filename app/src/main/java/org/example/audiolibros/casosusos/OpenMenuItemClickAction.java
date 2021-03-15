package org.example.audiolibros.casosusos;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import org.example.audiolibros.Libro;
import org.example.audiolibros.LibroSharedPreferenceStorage;
import org.example.audiolibros.R;
import org.example.audiolibros.SelectorViewModel;
import org.example.audiolibros.adaptadores.AdaptadorLibrosFiltro;
import org.example.audiolibros.interfaces.ClickAction;
import org.example.audiolibros.interfaces.LibroStorage;
import org.example.audiolibros.singleton.LibrosSingleton;

import java.util.List;

public class OpenMenuItemClickAction implements ClickAction {
    private Activity actividad;
    private List<Libro> listaLibros;
    private AdaptadorLibrosFiltro adaptador;
    private Animator.AnimatorListener animator;
    private LibrosSingleton libros;
    private LibroStorage libroStorage;
    private View view;
    private SelectorViewModel model;

    public OpenMenuItemClickAction(Activity actividad, View view,
                                   Animator.AnimatorListener animator,
                                   SelectorViewModel model) {
        this.actividad = actividad;
        this.animator = animator;
        this.libroStorage = LibroSharedPreferenceStorage.getInstance(actividad.getBaseContext());
        this.view = view;
        libros = LibrosSingleton.getInstance(actividad.getBaseContext());
        this.adaptador = libros.getAdaptador();
        this.listaLibros = libros.getListaLibros();
        this.model = model;
    }
    @Override
    public void execute(final int position) {

        AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
        CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int opcion) {
                switch (opcion) {
                    case 0: //Compartir
                        //Práctica: Animación de propiedades compartir en Audiolibros.
                        Animator anim = AnimatorInflater.loadAnimator(actividad, R.animator.animacion1);
                        anim.addListener(animator);
                        anim.setTarget(view);
                        anim.start();
                        //
                        //Libro libro = listaLibros.get(position);
                        Libro libro = model.get(position);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                        i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                        actividad.startActivity(Intent.createChooser(i, "Compartir"));
                        break;
                    case 1: //Borrar
                        Snackbar.make(view,"¿Estás seguro?", Snackbar.LENGTH_LONG)
                                .setAction("SI", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Ejercicio: Animación de propiedades borrado en Audiolibros
                                        Animator anim = AnimatorInflater.loadAnimator(actividad,
                                                R.animator.menguar);
                                        anim.addListener(animator);
                                        anim.setTarget(view);
                                        anim.start();
                                        adaptador.borrar(position);
                                        Libro libro = libros.getListaLibros().get(position);
                                        libroStorage.removeBookByTitle(libro.titulo);
                                    }
                                }) .show();
                        break;
                    case 2: //Insertar
                        adaptador.insertar((Libro) adaptador.getItem(position));
                        adaptador.notifyItemInserted(0);
                        Snackbar.make(view,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) { }
                                }) .show();
                        break;
                }
            }
        });
        menu.create().show();
    }
}
