package org.example.audiolibros.casosusos;

import org.example.audiolibros.BooksRespository;

public class GetLastBook {
    private final BooksRespository booksRespository;
    public GetLastBook(BooksRespository booksRespository) {
        this.booksRespository = booksRespository;
    }
    public int execute() {
        return booksRespository.getLastBook();
    }
    public int executeByName(){
        return BooksRespository.searchBookByName(
                booksRespository.getLasBookSavedByName());
    }
}
