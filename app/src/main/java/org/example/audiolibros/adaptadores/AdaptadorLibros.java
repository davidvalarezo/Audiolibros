package org.example.audiolibros.adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.example.audiolibros.EmptyClickAction;
import org.example.audiolibros.Libro;
import org.example.audiolibros.R;
import org.example.audiolibros.interfaces.ClickAction;
import org.example.audiolibros.singleton.VolleySingleton;

import java.util.List;

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder> {
    private LayoutInflater inflador; //Crea Layouts a partir del XML
    protected List<Libro> listaLibros; //Lista de libros a visualizar
    private Context contexto;
    private ClickAction clickAction = new EmptyClickAction();
    private ClickAction clickLongAction = new EmptyClickAction();
    private Libro libro;

    public AdaptadorLibros(Context contexto, List<Libro> listaLibros) {
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listaLibros = listaLibros;
        this.contexto = contexto;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView portada;
        public TextView titulo;
        public ViewHolder(View itemView) {
            super(itemView);
            portada = (ImageView) itemView.findViewById(R.id.portada);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
        }
    }

    // Creamos el ViewHolder con las vista de un elemento sin personalizar
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.elemento_selector, null);
        return new ViewHolder(v);
    }
    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(@NonNull final AdaptadorLibros.ViewHolder holder, final int position) {
        libro = listaLibros.get(position);
        //holder.portada.setImageResource(libro.recursoImagen);
        VolleySingleton volleySingleton = VolleySingleton.getInstance(contexto);
        volleySingleton.getLectorImagenes().get(libro.urlImagen, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                //Ejercicio: Extraer paleta de colores de una imagen.
                Bitmap bitmap = response.getBitmap();
                //holder.portada.setImageBitmap(bitmap);
                if (bitmap != null) {
                    holder.portada.setImageBitmap(bitmap);
                    if(libro.colorApagado != -1 && libro.colorVibrante != -1){
                        //Palette palette = Palette.from(bitmap).generate();
                        holder.itemView.setBackgroundColor(libro.colorApagado);
                        holder.titulo.setBackgroundColor(libro.colorVibrante);
                    }else{
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                libro.colorApagado = palette.getLightMutedColor(0);
                                libro.colorVibrante = palette.getLightVibrantColor(0);
                                holder.itemView.setBackgroundColor(libro.colorApagado);
                                holder.titulo.setBackgroundColor(libro.colorVibrante);
                            }
                        });
                    }
                    holder.portada.invalidate();
                }
            } @Override
            public void onErrorResponse(VolleyError error) {
                holder.portada.setImageResource(R.drawable.books);
            }
                });
        holder.titulo.setText(libro.titulo);
        //Ejercicio: Animación de propiedades borrado en Audiolibros
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                clickAction.execute(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickLongAction.execute(position);
                return true;
            }
        });
    }
    // Indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public void setOnItemLongClickListener(ClickAction clickLongAction) { this.clickLongAction = clickLongAction; }

    public void setClickAction(ClickAction clickAction) { this.clickAction = clickAction; }
}
