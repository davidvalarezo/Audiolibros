package org.example.audiolibros;

import org.example.audiolibros.interfaces.LibroStorage;
import org.example.audiolibros.singleton.LibrosSingleton;

public class DetalleFragmentPresenter {
    private final LibroStorage libroStorage;
    private final View view;

    public DetalleFragmentPresenter(LibroStorage libroStorage, DetalleFragmentPresenter.View view) {
        this.libroStorage = libroStorage;
        this.view = view;
    }

    public void showDetailsBook(int idBooks) {
        if(idBooks >= 0){
            ponInfoLibro(positionBookInList, vista);
        }
        Libro book = LibrosSingleton.getListaLibros().get(idBooks);
        view.putDetailsBookView(book, view);
        view.prepararNotification(book);
        view.startMediaPlayer(book.urlAudio);
    }

    public interface View {
        void putDetailsBookView(Libro book, View view);
        void prepararNotification(Libro book);
        void startMediaPlayer(String urlAudio);
    }
}
