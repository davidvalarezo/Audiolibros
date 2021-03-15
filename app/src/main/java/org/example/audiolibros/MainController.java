package org.example.audiolibros;

import org.example.audiolibros.interfaces.LibroStorage;

public class MainController {

    LibroStorage libroStorage;

    public MainController(LibroStorage libroStorage) {
        this.libroStorage = libroStorage;
    }

    public void saveLastBook(int id) {
        libroStorage.saveLastBookId(id);
    }

    public void saveLastBookName(String bookName) {
        libroStorage.saveLastBookName(bookName);
    }

    public int getLastBook() {
        return libroStorage.getLastBook();
    }

    public String getLastBookName() {
        return libroStorage.getLastBookName();
    }

    public boolean hasLastBook() {
        return libroStorage.hasLastBook();
    }
}
