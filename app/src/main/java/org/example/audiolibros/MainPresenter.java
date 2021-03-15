package org.example.audiolibros;

import org.example.audiolibros.casosusos.GetLastBook;
import org.example.audiolibros.casosusos.SaveLastBook;

public class MainPresenter {
    private final View view;
    private final SaveLastBook saveLastBook;
    private final GetLastBook getLastBook;

    public MainPresenter(SaveLastBook saveLastBook, GetLastBook getLastBook, MainPresenter.View view ) {
        this.saveLastBook = saveLastBook;
        this.view = view;
        this.getLastBook = getLastBook;
    }
    public void clickFavoriteButton() {
        if (saveLastBook.hasLastBook()) {
            int indexBook = getLastBook.executeByName();
            view.mostrarFragmentDetalle(indexBook);
        }
        else { view.mostrarNoUltimaVisita(); }
    }
    public void openDetalle(int id) {
        saveLastBook.execute(id);
        view.mostrarFragmentDetalle(id);
    }
    public interface View {
        void mostrarFragmentDetalle(int lastBook);
        void mostrarNoUltimaVisita();
    }
}
