package org.example.audiolibros.casosusos;

import org.example.audiolibros.MainActivity;
import org.example.audiolibros.interfaces.ClickAction;

public class OpenDetailClickAction implements ClickAction {
    private final MainActivity mainActivity;

    public OpenDetailClickAction(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    @Override
    public void execute(int position) {
        mainActivity.mostrarDetalle(position);
    }
}
