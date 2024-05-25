package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListeJoueurs extends AppCompatActivity {

    private List<String> joueursListe;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_joueurs);

        ListView listViewJoueurs = findViewById(R.id.listViewJoueurs);
        joueursListe = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, joueursListe);
        listViewJoueurs.setAdapter(adapter);

        // Récupérer le nom d'utilisateur passé en extra
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nomUtilisateur = extras.getString("NOM_UTILISATEUR");
            if (nomUtilisateur != null) {
                ajouterJoueur(nomUtilisateur);
            }
        }

        Button buttonStartGame = findViewById(R.id.button_start_game);
        buttonStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(ListeJoueurs.this, JeuPrincipal.class);
            intent.putStringArrayListExtra("LISTE_JOUEURS", new ArrayList<>(joueursListe));
            startActivity(intent);
        });
    }

    private void ajouterJoueur(String nomUtilisateur) {
        joueursListe.add(nomUtilisateur);
        adapter.notifyDataSetChanged();
    }
}
