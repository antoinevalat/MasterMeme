package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer le bouton "Créer un jeu"
        Button buttonCreerJeu = findViewById(R.id.button_creerjeu);

        // Ajouter un écouteur de clic au bouton "Créer un jeu"
        buttonCreerJeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir CreerJeu
                Intent intent = new Intent(MainActivity.this, CreerJeu.class);
                // Démarrer CreerJeu
                startActivity(intent);
            }
        });

        // Récupérer le bouton "Rejoindre un jeu"
        Button buttonRejoindreJeu = findViewById(R.id.button_rejoindre);

        // Ajouter un écouteur de clic au bouton "Rejoindre un jeu"
        buttonRejoindreJeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir RejoindreJeu
                Intent intent = new Intent(MainActivity.this, RejoindreJeu.class);
                // Démarrer RejoindreJeu
                startActivity(intent);
            }
        });


        // Récupérer le bouton "À propos"
        Button buttonAPropos = findViewById(R.id.button_apropos);
        // Ajouter un écouteur de clic au bouton "À propos"
        buttonAPropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir APropos
                Intent intent = new Intent(MainActivity.this, APropos.class);
                // Démarrer APropos
                startActivity(intent);
            }
        });

        // Récupérer le bouton "À propos"
        //Button buttonAPropos = findViewById(R.id.button_apropos);

        // Ajouter un écouteur de clic au bouton "À propos"
        buttonAPropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir APropos
                Intent intent = new Intent(MainActivity.this, APropos.class);
                // Démarrer APropos
                startActivity(intent);
            }
        });



    }
}