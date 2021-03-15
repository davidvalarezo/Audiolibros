package org.example.audiolibros.singleton;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private Context context;
    private RequestQueue colaPeticiones;
    private ImageLoader lectorImagenes;

    private VolleySingleton(Context context) {
        this.context = context;
        colaPeticiones = Volley.newRequestQueue(context);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });
    }

    private static VolleySingleton instance;
    public static VolleySingleton getInstance(Context context) {
        if(instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getColaPeticiones() {
        return colaPeticiones;
    }

    public ImageLoader getLectorImagenes() {
        return lectorImagenes;
    }
}
