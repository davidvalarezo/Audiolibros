package org.example.audiolibros.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.example.audiolibros.MainActivity;
import org.example.audiolibros.R;

public class PreferenciasFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferencias, rootKey);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity actividad = (MainActivity) getActivity();
        if (!actividad.dosFragments)
            actividad.mostrarBarraAmpliada(false);
    }
}
