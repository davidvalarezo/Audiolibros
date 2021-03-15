package org.example.audiolibros;

import android.content.Context;
import android.content.SharedPreferences;

import org.example.audiolibros.interfaces.LibroStorage;

public class LibroSharedPreferenceStorage  implements LibroStorage {
    public static final String PREF_AUDIOLIBROS = "org.example.audiolibros_internal";
    public static final String PREF_POSICION_LIBRO = "org.example.audiolibros_posicion";
    public static final String KEY_ULTIMO_LIBRO = "ultimo";
    private final Context context;

    private LibroSharedPreferenceStorage(Context context) {
        this.context = context;
    }
    @Override
    public boolean hasLastBook() {
        return getPreference().contains(KEY_ULTIMO_LIBRO);
    }
    @Override
    public SharedPreferences getPreference() {
        return context.getSharedPreferences( PREF_AUDIOLIBROS, Context.MODE_PRIVATE);
    }
    @Override
    public int getLastBook() {
        return getPreference().getInt(KEY_ULTIMO_LIBRO, -1);
    }
    @Override
    public String getLastBookName() {
        return getPreference().getString(KEY_ULTIMO_LIBRO, "");
    }
    @Override
    public void saveLastBookId(int id) {
        SharedPreferences pref = getPreference();
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_ULTIMO_LIBRO, id);
        editor.commit();
    }
    @Override
    public void saveLastBookName(String bookName) {
        SharedPreferences pref = getPreference();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_ULTIMO_LIBRO, bookName);
        editor.commit();
    }
    @Override
    public int getLastBookPosition(String book) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_POSICION_LIBRO, Context.MODE_PRIVATE);
        return pref.getInt(book, 0);
    }
    @Override
    public void saveLastBookPosition(String titleBook, int positionBook) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_POSICION_LIBRO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(titleBook, positionBook);
        editor.commit();
    }
    @Override
    public void removeBookByTitle(String titleBook) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_POSICION_LIBRO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(titleBook);
        editor.commit();;
    }

    private static LibroSharedPreferenceStorage instance;
    public static LibroStorage getInstance(Context context) {
        if(instance == null) {
            instance = new LibroSharedPreferenceStorage(context);
        }
        return instance;
    }
}
