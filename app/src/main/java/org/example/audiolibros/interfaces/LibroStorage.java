package org.example.audiolibros.interfaces;

import android.content.SharedPreferences;

public interface LibroStorage {
    boolean hasLastBook();
    int getLastBook();
    String getLastBookName();
    SharedPreferences getPreference();
    //
    void saveLastBookId(int id);
    void saveLastBookName(String bookName);
    int getLastBookPosition(String book);
    void saveLastBookPosition(String titleBook, int positionBook);
    void removeBookByTitle(String titleBook);
}
