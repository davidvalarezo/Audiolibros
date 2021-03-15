package org.example.audiolibros.singleton;

import android.content.Context;

import org.example.audiolibros.adaptadores.AdaptadorLibrosFiltro;
import org.example.audiolibros.Libro;

import java.util.List;

public class LibrosSingleton {
    private Context context;
    private static List<Libro> listaLibros;
    private AdaptadorLibrosFiltro adaptador;

    private  LibrosSingleton(Context context) {
        this.context = context;
        this.listaLibros = Libro.ejemploLibros();
        adaptador = new AdaptadorLibrosFiltro (context, listaLibros);
    }
    private static LibrosSingleton instance;
    public static LibrosSingleton getInstance(Context context) {
        if(instance == null) {
            instance = new LibrosSingleton(context);
        }
        return instance;
    }

    public static List<Libro> getListaLibros() { return listaLibros; }
    public AdaptadorLibrosFiltro getAdaptador() { return adaptador; }

}
