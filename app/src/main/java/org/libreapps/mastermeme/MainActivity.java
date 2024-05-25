package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button buttonCreerJeu = findViewById(R.id.button_creerjeu);
        Button buttonRejoindre = findViewById(R.id.button_rejoindre);
        Button buttonRegles = findViewById(R.id.button_regles);
        Button buttonAPropos = findViewById(R.id.button_apropos);

        buttonCreerJeu.setOnClickListener(v -> {
            EditText editEntrezNom = findViewById(R.id.editEntrezNom);
            String nomUtilisateur = editEntrezNom.getText().toString().trim();
            if (!nomUtilisateur.isEmpty()) {
                // Vérifie si l'utilisateur est déjà connecté
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Enregistrer le nom de l'utilisateur dans Firebase Database
                    enregistrerNomUtilisateur(currentUser.getUid(), nomUtilisateur);
                    // Lancer l'activité pour créer un jeu en passant le nom de l'utilisateur
                    lancerActiviteCreerJeu(nomUtilisateur);
                } else {
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Enregistrer le nom de l'utilisateur dans Firebase Database
                                        enregistrerNomUtilisateur(user.getUid(), nomUtilisateur);
                                        // Lancer l'activité pour créer un jeu en passant le nom de l'utilisateur
                                        lancerActiviteCreerJeu(nomUtilisateur);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to retrieve user.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(MainActivity.this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRejoindre.setOnClickListener(v -> {
            EditText editEntrezNom = findViewById(R.id.editEntrezNom);
            String nomUtilisateur = editEntrezNom.getText().toString().trim();
            if (!nomUtilisateur.isEmpty()) {
                // Lancer l'activité pour rejoindre un jeu en passant le nom de l'utilisateur
                lancerActiviteRejoindreJeu(nomUtilisateur);
            } else {
                Toast.makeText(MainActivity.this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegles.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReglesJeu.class);
            startActivity(intent);
        });

        buttonAPropos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, APropos.class);
            startActivity(intent);
        });
    }

    // Méthode pour enregistrer le nom de l'utilisateur dans Firebase Database
    private void enregistrerNomUtilisateur(String uid, String nomUtilisateur) {
        mDatabase.child("users").child(uid).setValue(nomUtilisateur);
    }

    // Méthode pour lancer l'activité de création de jeu en passant le nom de l'utilisateur
    private void lancerActiviteCreerJeu(String nomUtilisateur) {
        Intent intent = new Intent(MainActivity.this, CreerJeu.class);
        intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
        startActivity(intent);
    }

    // Méthode pour lancer l'activité de rejoindre un jeu en passant le nom de l'utilisateur
    private void lancerActiviteRejoindreJeu(String nomUtilisateur) {
        Intent intent = new Intent(MainActivity.this, RejoindreJeu.class);
        intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
        startActivity(intent);
    }
}
