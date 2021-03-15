package org.example.audiolibros.casosusos;

import org.example.audiolibros.interfaces.LibroStorage;

public class SaveLastBook {
    private final LibroStorage libroStorage;
    public SaveLastBook(LibroStorage libroStorage) {
        this.libroStorage = libroStorage;
    }
    public void execute(int id) {
        libroStorage.saveLastBookId(id);
    }
    public boolean hasLastBook(){
        return this.libroStorage.hasLastBook();
    }
    public void saveLastBookPosition(String nameBook, int pos){
        this.libroStorage.saveLastBookPosition(nameBook,pos);
        this.libroStorage.saveLastBookName(nameBook);
    }
}
