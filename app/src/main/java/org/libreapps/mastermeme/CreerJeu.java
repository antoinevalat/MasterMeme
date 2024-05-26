package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreerJeu extends AppCompatActivity {
    private String codePartie;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private int selectedRoundCount = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_jeu);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        String nomUtilisateur = getIntent().getStringExtra("NOM_UTILISATEUR");

        TextView txtCodeCreer = findViewById(R.id.txtCodeCreer);

        codePartie = generateUniqueCode();
        txtCodeCreer.setText(codePartie);

        RadioGroup radioGroupRounds = findViewById(R.id.radioGroupRounds);
        radioGroupRounds.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonRd6) {
                selectedRoundCount = 6;
            } else if (checkedId == R.id.radioButtonRd12) {
                selectedRoundCount = 12;
            } else if (checkedId == R.id.radioButtonRd18) {
                selectedRoundCount = 18;
            }
        });

        Button buttonCreerPartie = findViewById(R.id.button_creerpartie);
        buttonCreerPartie.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();

            mDatabase.child("parties").child(codePartie).setValue(userId);
            mDatabase.child("parties").child(codePartie).child("round").setValue(1);
            mDatabase.child("parties").child(codePartie).child("situationDescription").setValue("Début du jeu");
            mDatabase.child("parties").child(codePartie).child("judgeIndex").setValue(0);

            Intent intent = new Intent(CreerJeu.this, ListeJoueurs.class);
            intent.putExtra("CODE_PARTIE", codePartie);
            intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
            intent.putExtra("ROUND_COUNT", selectedRoundCount);
            startActivity(intent);
            finish();
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(v -> {
            Intent intent = new Intent(CreerJeu.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private String generateUniqueCode() {
        Random random = new Random();
        int code = random.nextInt(90000) + 10000;
        return String.valueOf(code);
    }
}
