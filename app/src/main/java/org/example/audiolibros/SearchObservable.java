package org.example.audiolibros;

import androidx.appcompat.widget.SearchView;

import java.util.Observable;

public class SearchObservable extends Observable implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        setChanged();
        notifyObservers(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
