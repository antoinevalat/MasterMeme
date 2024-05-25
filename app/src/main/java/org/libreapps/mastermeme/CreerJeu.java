package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Random;

public class CreerJeu extends AppCompatActivity {
    private String codePartie;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_jeu); // Charger le layout XML en premier
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String nomUtilisateur = getIntent().getStringExtra("NOM_UTILISATEUR");

        // Trouver et initialiser les éléments UI après avoir chargé le layout
        TextView txtCodeCreer = findViewById(R.id.txtCodeCreer);

        // Générer un code aléatoire à afficher dans txtCodeCreer
        codePartie = generateUniqueCode();
        txtCodeCreer.setText(codePartie); // Mettre à jour le TextView avec le code généré

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonCreerPartie = findViewById(R.id.button_creerpartie);
        buttonCreerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Générer un nouveau code de partie unique avant de créer la partie
                codePartie = generateUniqueCode();

                // Mettre à jour le TextView avec le nouveau code généré
                txtCodeCreer.setText(codePartie);

                String codePartie = generateUniqueCode();
                String userId = mAuth.getCurrentUser().getUid();
                mDatabase.child("parties").child(codePartie).setValue(userId);
                // Enregistrer le code de la partie dans SharedPreferences ou une base de données (non implémenté ici)

                // Créer une intention pour ouvrir JeuPrincipal
                Intent intent = new Intent(CreerJeu.this, ListeJoueurs.class);
                intent.putExtra("CODE_PARTIE", codePartie);
                intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
                startActivity(intent);
                finish(); // Facultatif : pour fermer l'activité actuelle après la redirection
            }
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour revenir à MainActivity
                Intent intent = new Intent(CreerJeu.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Identifier le groupe radio et les boutons radio
        RadioGroup radioGroupRounds = findViewById(R.id.radioGroupRounds);
        RadioButton radioButtonRd6 = findViewById(R.id.radioButtonRd6);
        RadioButton radioButtonRd12 = findViewById(R.id.radioButtonRd12);
        RadioButton radioButtonRd18 = findViewById(R.id.radioButtonRd18);

        // Ajouter un écouteur pour gérer la sélection des boutons radio
        /*
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
        // Générer un code aléatoire à 5 chiffres
        Random random = new Random();
        int code = random.nextInt(90000) + 10000; // pour assurer que le code a 5 chiffres

        return String.valueOf(code);
    }

    private void saveCodePartie(String code) {
        // Utilisez SharedPreferences ou une base de données pour enregistrer le code de la partie
    }
}