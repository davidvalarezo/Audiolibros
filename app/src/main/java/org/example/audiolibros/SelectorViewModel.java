package org.example.audiolibros;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SelectorViewModel extends ViewModel {
    private List<Libro> libroList;
    private MutableLiveData<List<Libro>> libros;

    public LiveData<List<Libro>> getLibros() {
        if (libros == null) {
            libros = new MutableLiveData<List<Libro>>();
            loadLibros();
        }
        return libros;
    }

    private void loadLibros() {
        // Acciones asíncronas para cargar los libros.
        libroList = Libro.ejemploLibros();//LibrosSingleton.getListaLibros(); //Libro.ejemploLibros();
        libros.setValue(libroList);
    }

    public Libro get(int id){
        return libroList.get(id);
    }

    public void delete(int id){
        libroList.remove(id);
        libros.setValue(libroList);
    }

    public void insert(Libro libro){
        libroList.add(0, libro);
        libros.setValue(libroList);
    }
}
