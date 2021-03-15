package org.example.audiolibros.fragments;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.example.audiolibros.BooksRespository;
import org.example.audiolibros.DetalleFragmentPresenter;
import org.example.audiolibros.Libro;
import org.example.audiolibros.LibroSharedPreferenceStorage;
import org.example.audiolibros.casosusos.GetLastBook;
import org.example.audiolibros.casosusos.SaveLastBook;
import org.example.audiolibros.interfaces.LibroStorage;
import org.example.audiolibros.singleton.LibrosSingleton;
import org.example.audiolibros.MainActivity;
import org.example.audiolibros.R;
import org.example.audiolibros.singleton.VolleySingleton;
import org.example.audiolibros.ZoomSeekBar;

import java.io.IOException;

public class DetalleFragment extends Fragment implements DetalleFragmentPresenter.View, View.OnTouchListener,
         MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl, ZoomSeekBar.OnDesplazarListener{
    public static String ARG_ID_LIBRO = "id_libro";
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    //SharedPreferences pref;
    private Libro libro;
    private ZoomSeekBar zoomBar;
    private AudioThread taskZoomBar;
    private boolean isBar = false;
    //Notificación
    private static final int ID_NOTIFICACION = 1;
    static final String ID_CANAL = "channel_id";
    private NotificationManager notificManager;
    private NotificationCompat.Builder notificacion;
    private RemoteViews remoteViews;
    private LibroStorage libroStorage;
    private LibrosSingleton libros;
    private GetLastBook getLastBook;
    private SaveLastBook saveBook;

    private DetalleFragmentPresenter dFragmentPresenter;

    public static final String ACCION_DEMO = "org.example.audiolibros.ACCION_DEMO";
    public static final String EXTRA_PARAM = "org.example.audiolibros.EXTRA_PARAM";
    public static final String ACCION_INCR = "org.example.audiolibros.ACCION_INCR";

    @Override public View onCreateView(LayoutInflater inflador,
                                       ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle, contenedor, false);
        Bundle args = getArguments();

        libros = LibrosSingleton.getInstance(getContext());
        libroStorage = LibroSharedPreferenceStorage.getInstance( getContext());

        dFragmentPresenter = new DetalleFragmentPresenter(libroStorage,this);
        saveBook = new SaveLastBook(libroStorage);
        getLastBook = new GetLastBook(new BooksRespository(libroStorage));
        zoomBar = vista.findViewById(R.id.zoomSeekBar);
        zoomBar.setOnDesplazarListener(this);
        taskZoomBar = new AudioThread();
        int positionBookInList = 0;

        if (args != null) {
            positionBookInList = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(positionBookInList, vista);
        }
        else {
            if (saveBook.hasLastBook())
                positionBookInList = getLastBook.executeByName();
            ponInfoLibro(positionBookInList, vista);
        }

        zoomBar.setVisibility(View.INVISIBLE);
        IntentFilter filtro = new IntentFilter(ACCION_DEMO);
        ((MainActivity) getActivity()).registerReceiver(new ReceptorAnuncio(), filtro);

        return vista;
    }


    public void putDetailsBookView(Libro book, View view) {
        ((TextView) view.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) view.findViewById(R.id.autor)).setText(libro.autor);
        VolleySingleton volleySingleton = VolleySingleton.getInstance(getContext());
        ((NetworkImageView) view.findViewById(R.id.portada)).setImageUrl(
                libro.urlImagen, volleySingleton.getLectorImagenes());
        view.setOnTouchListener(this);
    }

    private void ponInfoLibro(int id, View vista) {
        libro = libros.getListaLibros().get(id);
        putDetailsBookView(libro, vista);
        prepararNotification(libro);
        startMediaPlayer(libro.urlAudio);
    }

    public void startMediaPlayer(String urlAudio){
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        Uri audio = Uri.parse(urlAudio);
        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio, e);
        }
    }

    public void ponInfoLibro(int id) { ponInfoLibro(id, getView()); }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        //Añadir dinámicamente PreferenciasFragment
        // Reemplazar: mediaPlayer.start(); por:
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            //Añadir la última posición del audiolibro
            int id = libroStorage.getLastBookPosition(libro.titulo);
            if(id>0) mediaPlayer.seekTo(id);
            mediaPlayer.start();
        }
        //zoomBar.setValMax(mediaPlayer.getDuration()/1000);
        //Fin Reemplazo. Añadir dinámicamente PreferenciasFragment
        mediaController.setMediaPlayer(this);
        View v = getView().findViewById(R.id.fragment_detalle);
        if (v != null) {
            mediaController.setAnchorView(v);
        }
        else {
            mediaController.setAnchorView(getView()
                    .findViewById(R.id.detalle_fragment));
        }

        settingZoomSeekBar(mediaPlayer.getCurrentPosition(), 15);
        taskZoomBar.start();//execute();
        mediaController.setEnabled(true);
        mediaController.show();

        lanzarNotificacion();
    }

    public void guardarUltimaPosicionAudioLibro(){
        if (mediaPlayer != null) {
            int pos = mediaPlayer.getCurrentPosition();
            saveBook.saveLastBookPosition(libro.titulo, pos);
        }
    }

    public void settingZoomSeekBar(int id, int scale){
        id = id/1000;
        zoomBar.setValMin(0);
        int maxValue = mediaPlayer.getDuration()/1000;
        zoomBar.setValMax(maxValue);
        zoomBar.setEscalaRaya(1);
        zoomBar.setEscalaRayaLarga(10);
        //scale = scale*60;
       if(id == 0){
            zoomBar.setVal(id);
            zoomBar.setValMin(0);
            zoomBar.setEscalaIni(id);
            zoomBar.setEscalaMin(id);
            zoomBar.setEscalaMax(scale*2);
        }else{
            zoomBar.setVal(id);
            //zoomBar.setValMin(id);
           zoomBar.setEscalaIni(id-2*scale);
            zoomBar.setEscalaMin(id-scale);
            zoomBar.setEscalaMax(id+scale);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        isBar = true;
        //zoomBar.setVisibility(View.VISIBLE);
        settingZoomSeekBar(mediaPlayer.getCurrentPosition(), 15);
        mediaController.show();
        if(mediaController.isShowing())
            zoomBar.setVisibility(View.VISIBLE);
        return false;
    }

    @Override public void onDestroy() {
        try {
            mediaController.hide();
            mediaPlayer.stop();
            mediaPlayer.release();
           // taskZoomBar.cancel(true);
            taskZoomBar.destroy();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity actividad = (MainActivity) getActivity();
        if (!actividad.dosFragments)
            actividad.mostrarBarraAmpliada(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        guardarUltimaPosicionAudioLibro();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) { return 0; }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    @Override
    public void onDesplazar(boolean movimiento, int val) {
    }

    @Override
    public void onDesplazando(boolean movimiento, int val) {
        zoomBar.setVal(val);
        zoomBar.setEscalaMin(val-15);
        zoomBar.setEscalaMax(val+15);
        val = val*1000;
        mediaPlayer.seekTo(val);
    }

    @Override
    public void onError(String mensage) {

    }

    public void prepararNotification(Libro libro) {
        Uri uriLibroImg = Uri.parse(Libro.SERVIDOR+libro.urlImagen);
        remoteViews = new RemoteViews(((MainActivity) getActivity()).getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notificacion_reproducir, R.drawable.ic_img_play);
        remoteViews.setImageViewUri(R.id.caratula_libro, uriLibroImg);
        remoteViews.setTextViewText(R.id.notificacion_titulo, libro.titulo);
        remoteViews.setTextColor(R.id.notificacion_titulo, Color.BLACK);
        remoteViews.setTextViewText(R.id.notificacion_autor, libro.autor);
        remoteViews.setTextColor(R.id.notificacion_autor, Color.BLACK);
    }

    class AudioThread extends Thread {
        public void run() {
            while (mediaPlayer.isPlaying()) {
                int segundos = mediaPlayer.getCurrentPosition();
                zoomBar.setVal(segundos/1000);
            }
        }
    }

    private void lanzarNotificacion() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion = new NotificationCompat.Builder(getContext(), ID_CANAL)
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("Notificación personalizada");
        notificManager = (NotificationManager)(((MainActivity) getActivity()).
                getSystemService( Context.NOTIFICATION_SERVICE));

        Intent i = new Intent();
        i.setAction(ACCION_DEMO);
        i.putExtra(EXTRA_PARAM, "otro parámetro");
        PendingIntent pendingI = PendingIntent.getBroadcast(getContext(),
                0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificacion_reproducir, pendingI);

        if (Build.VERSION.SDK_INT >= 26){
            NotificationChannel channel = new NotificationChannel(ID_CANAL,
                    "Nombre del canal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Descripción del canal");
            notificManager.createNotificationChannel(channel);
        }
        notificManager.notify(ID_NOTIFICACION, notificacion.build());
    }

    public class ReceptorAnuncio extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            boolean isPlaying = mediaPlayer.isPlaying();
            if(isPlaying){
                mediaPlayer.pause();
                remoteViews.setImageViewResource(R.id.notificacion_reproducir, R.drawable.ic_img_pause);
                remoteViews.setImageViewResource(R.id.widget_play, R.drawable.ic_img_pause);
            }else{
                mediaPlayer.start();
                remoteViews.setImageViewResource(R.id.notificacion_reproducir, R.drawable.ic_img_play);
                remoteViews.setImageViewResource(R.id.widget_play, R.drawable.ic_img_play);

            }
            notificManager.notify(ID_NOTIFICACION, notificacion.build());
        }
    }

}
