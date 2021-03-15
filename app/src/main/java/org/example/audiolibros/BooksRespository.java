package org.example.audiolibros;

import org.example.audiolibros.interfaces.LibroStorage;

import java.util.List;

import static org.example.audiolibros.singleton.LibrosSingleton.getListaLibros;

public class BooksRespository {
    private final LibroStorage libroStorage;

    public BooksRespository(LibroStorage libroStorage) {
        this.libroStorage = libroStorage;
    }
    public int getLastBook() {
        return libroStorage.getLastBook();
    }
    public String getLasBookSavedByName(){
        return libroStorage.getLastBookName();
    }

    public static int searchBookByName(String nameBook){
        List<Libro> libroList = getListaLibros();
        for(int i= 0; i < libroList.size(); i++ ) {
            //System.out.println(libroList.get(i).autor);
            if(libroList.get(i).titulo.equals(nameBook))
                return i;
        }
        return -1;
    }

    public static int getIdBookByName(String nameBook){
        List<Libro> libroList = getListaLibros();
        for(int id= 0; id < libroList.size(); id++ ) {
            if(libroList.get(id).titulo.equals(nameBook))
                return id;
        }
        return 0;
    }
}
