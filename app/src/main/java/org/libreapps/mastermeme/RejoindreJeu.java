package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class RejoindreJeu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rejoindre_jeu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        Button buttonRejoindrePartie = findViewById(R.id.button_rejoindrepartie);
        buttonRejoindrePartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codePartie = getCodePartie();
                if (codePartie != null && !codePartie.isEmpty()) {
                    redirectToMainActivity(codePartie);
                } else {
                    Toast.makeText(RejoindreJeu.this, "Code de partie invalide", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonRetour = findViewById(R.id.button_retour);
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour revenir à MainActivity
                Intent intent = new Intent(RejoindreJeu.this, MainActivity.class);
                // Démarrer MainActivity
                startActivity(intent);
            }
        });

    }
    private String getCodePartie() {
        EditText editCodeRejoindre = findViewById(R.id.edit_code_rejoindre);
        return editCodeRejoindre.getText().toString().trim();
    }

    private void redirectToMainActivity(String codePartie) {
        Intent intent = new Intent(RejoindreJeu.this, MainActivity.class);
        intent.putExtra("CODE_PARTIE", codePartie);
        startActivity(intent);
        finish();
    }
}