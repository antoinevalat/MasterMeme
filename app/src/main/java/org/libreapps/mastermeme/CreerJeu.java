package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class CreerJeu extends AppCompatActivity {
    private String codePartie;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_jeu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        Button buttonCreerPartie = findViewById(R.id.button_creerpartie);
        buttonCreerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Générer un code de partie unique (vous pouvez utiliser une fonction pour cela)
                codePartie = generateUniqueCode();
                // Enregistrer le code de la partie dans SharedPreferences ou une base de données
                saveCodePartie(codePartie);
                // Rediriger vers l'activité principale du jeu
                redirectToMainActivity();
            }
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour revenir à MainActivity
                Intent intent = new Intent(CreerJeu.this, MainActivity.class);
                // Démarrer MainActivity
                startActivity(intent);
            }
        });

        // Identifier le groupe radio et les boutons radio
        RadioGroup radioGroupRounds = findViewById(R.id.radioGroupRounds);
        RadioButton radioButtonRd6 = findViewById(R.id.radioButtonRd6);
        RadioButton radioButtonRd12 = findViewById(R.id.radioButtonRd12);
        RadioButton radioButtonRd18 = findViewById(R.id.radioButtonRd18);


        /*
        // Ajouter un écouteur pour gérer la sélection des boutons radio
        radioGroupRounds.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Gérer les actions en fonction du bouton radio sélectionné
                switch (checkedId) {
                    case R.id.radioButtonRd6:
                        // Actions pour 6 Rounds sélectionnés
                        break;
                    case R.id.radioButtonRd12:
                        // Actions pour 12 Rounds sélectionnés
                        break;
                    case R.id.radioButtonRd18:
                        // Actions pour 18 Rounds sélectionnés
                        break;
                }
            }
        });
        */

    }
    private String generateUniqueCode() {
        // Implémentez votre logique pour générer un code unique
        // Par exemple, vous pouvez utiliser une combinaison aléatoire de chiffres et de lettres
        return "ABC123"; // Code de test
    }

    private void saveCodePartie(String code) {
        // Utilisez SharedPreferences ou une base de données pour enregistrer le code de la partie
    }

    private void redirectToMainActivity() {
        // Rediriger vers l'activité principale du jeu
        Intent intent = new Intent(CreerJeu.this, MainActivity.class);
        startActivity(intent);
        finish(); // Facultatif : pour fermer l'activité actuelle après la redirection
    }
}
