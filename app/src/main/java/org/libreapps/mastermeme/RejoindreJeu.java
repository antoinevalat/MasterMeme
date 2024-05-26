package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RejoindreJeu extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String nomUtilisateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_jeu);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Récupérer le nom d'utilisateur de l'intent
        nomUtilisateur = getIntent().getStringExtra("NOM_UTILISATEUR");

        Button buttonRejoindrePartie = findViewById(R.id.button_rejoindrepartie);
        buttonRejoindrePartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codePartie = getCodePartie();
                if (codePartie != null && !codePartie.isEmpty()) {
                    mDatabase.child("parties").child(codePartie).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                redirectToListeJoueurs(codePartie);
                            } else {
                                Toast.makeText(RejoindreJeu.this, "Code de partie invalide", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                } else {
                    Toast.makeText(RejoindreJeu.this, "Code de partie invalide", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RejoindreJeu.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getCodePartie() {
        EditText editCodeRejoindre = findViewById(R.id.edit_code_rejoindre);
        return editCodeRejoindre.getText().toString().trim();
    }

    private void redirectToListeJoueurs(String codePartie) {
        Intent intent = new Intent(RejoindreJeu.this, ListeJoueurs.class);
        intent.putExtra("CODE_PARTIE", codePartie);
        intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
        startActivity(intent);
        finish();
    }
}
