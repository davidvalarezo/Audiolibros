package org.example.audiolibros.casosusos;

import org.example.audiolibros.Libro;
import org.example.audiolibros.fragments.DetalleFragment;

public class PrepareNotification {
    private final DetalleFragment fragment;

    public PrepareNotification(DetalleFragment fragment) {
        this.fragment = fragment;
    }

    public void execute(Libro libro) {
        fragment.prepararNotification(libro);
    }
}
