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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreerJeu extends AppCompatActivity {
    private String codePartie;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonCreerPartie = findViewById(R.id.button_creerpartie);
        buttonCreerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codePartie = generateUniqueCode();
                txtCodeCreer.setText(codePartie);

                String userId = mAuth.getCurrentUser().getUid();
                mDatabase.child("parties").child(codePartie).setValue(userId);

                Intent intent = new Intent(CreerJeu.this, ListeJoueurs.class);
                intent.putExtra("CODE_PARTIE", codePartie);
                intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
                startActivity(intent);
                finish();
            }
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreerJeu.this, MainActivity.class);
                startActivity(intent);
            }
        });

        RadioGroup radioGroupRounds = findViewById(R.id.radioGroupRounds);
        RadioButton radioButtonRd6 = findViewById(R.id.radioButtonRd6);
        RadioButton radioButtonRd12 = findViewById(R.id.radioButtonRd12);
        RadioButton radioButtonRd18 = findViewById(R.id.radioButtonRd18);

        /*
        radioGroupRounds.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
        Random random = new Random();
        int code = random.nextInt(90000) + 10000;
        return String.valueOf(code);
    }
}
