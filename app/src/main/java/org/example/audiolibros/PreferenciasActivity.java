package org.example.audiolibros;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.example.audiolibros.fragments.PreferenciasFragment;

public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        new PreferenciasFragment())
                .commit();
    }
}
