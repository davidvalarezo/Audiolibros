package org.example.audiolibros;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.example.audiolibros.interfaces.LibroStorage;
import org.example.audiolibros.singleton.LibrosSingleton;

import static org.example.audiolibros.fragments.DetalleFragment.ACCION_DEMO;

public class MiAppWidgetProvider extends AppWidgetProvider {
    public static final String ACCION_INCR = "org.example.audiolibros.ACCION_INCR";
    public static final String EXTRA_PARAM = "org.example.audiolibros.EXTRA_ID";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        for (int widgetId: widgetIds) {
            actualizaWidget(context, widgetId);
        }
    }

    public static void actualizaWidget(Context context, int widgetId) {
        int cont = incrementaContador(context, widgetId);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        Libro libro = returnLibro(context);
        String autor = "Autor", titulo = "Titulo";
        if(libro != Libro.LIBRO_EMPTY){
            autor = libro.autor;
            titulo = libro.titulo;
        }
        remoteViews.setTextViewText(R.id.widget_autor, autor+" " + cont);
        remoteViews.setTextViewText(R.id.widget_titulo, titulo+" " + cont);
        remoteViews.setImageViewResource(R.id.widget_lista, R.drawable.ic_img_list);
        remoteViews.setImageViewResource(R.id.widget_play, R.drawable.ic_img_play);
        //
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_lista, pendingIntent);
        //
        intent = new Intent(context, MiAppWidgetProvider.class);
        intent.setAction(ACCION_INCR);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.putExtra(EXTRA_PARAM, "otro parámetro");
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_titulo, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widget_autor, pendingIntent);
//
        Intent i = new Intent(); i.setAction(ACCION_DEMO);
        i.putExtra(EXTRA_PARAM, "otro parámetro");
        PendingIntent pendingI = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_play, pendingI);


        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
    }

    private static int incrementaContador(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences("contadores", Context.MODE_PRIVATE);
        int cont = prefs.getInt("cont_" + widgetId, 0);
        cont++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("cont_" + widgetId, cont);
        editor.commit();
        return cont;
    }

    @Override public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(ACCION_INCR)) {
            int widgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            String param = intent.getStringExtra(EXTRA_PARAM);
            Toast.makeText(context,"Parámetro: "+param, Toast.LENGTH_LONG).show();
            actualizaWidget(context, widgetId);

        }
        super.onReceive(context, intent);
    }

    private static Libro returnLibro(Context context){
        LibroStorage libroStorage = LibroSharedPreferenceStorage.getInstance(context);
        LibrosSingleton libros = LibrosSingleton.getInstance(context);
        if (libroStorage.hasLastBook()){
            String bookName = libroStorage.getLastBookName();
            if (!Libro.LIBRO_EMPTY.titulo.equals(bookName)){
                return libros.getListaLibros()
                        .get(BooksRespository.searchBookByName(bookName));
            }
        }
        return Libro.LIBRO_EMPTY;
    }
}
