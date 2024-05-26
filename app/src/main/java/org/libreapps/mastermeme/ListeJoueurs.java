package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeJoueurs extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<String> joueursListe;
    private ArrayAdapter<String> adapter;
    private String codePartie;
    private String nomUtilisateur;
    private int roundCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_joueurs);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ListView listViewJoueurs = findViewById(R.id.listViewJoueurs);
        joueursListe = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, joueursListe);
        listViewJoueurs.setAdapter(adapter);

        TextView txtCodePartie = findViewById(R.id.txtCodePartie);
        TextView txtRoundCount = findViewById(R.id.txtRoundCount);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            codePartie = extras.getString("CODE_PARTIE");
            nomUtilisateur = extras.getString("NOM_UTILISATEUR");
            roundCount = extras.getInt("ROUND_COUNT");

            txtCodePartie.setText(codePartie);
            txtRoundCount.setText("Nombre de rounds : " + roundCount);
            ajouterJoueur(nomUtilisateur);
        }

        mDatabase.child("parties").child(codePartie).child("joueurs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                joueursListe.clear();
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String joueur = playerSnapshot.getValue(String.class);
                    joueursListe.add(joueur);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        Button buttonStartGame = findViewById(R.id.button_start_game);
        buttonStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(ListeJoueurs.this, JeuPrincipal.class);
            intent.putExtra("CODE_PARTIE", codePartie);
            intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
            intent.putExtra("ROUND_COUNT", roundCount);
            startActivity(intent);
        });
    }

    private void ajouterJoueur(String nomUtilisateur) {
        mDatabase.child("parties").child(codePartie).child("joueurs").child(nomUtilisateur).setValue(nomUtilisateur);
    }
}
