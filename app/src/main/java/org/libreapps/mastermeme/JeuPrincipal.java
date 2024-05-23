package org.libreapps.mastermeme;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class JeuPrincipal extends AppCompatActivity {

    private List<String> memesList; // Liste des chemins d'accès aux images des memes
    private List<String> situationDescriptions; // Liste des descriptions de situations
    private int roundCount; // Nombre de rounds de jeu
    private int currentRound; // Round actuel
    private int judgeIndex; // Index du joueur qui est le juge actuellement
    private int currentPlayerIndex; // Index du joueur actuel (hors juge)
    private List<String> players; // Liste des joueurs
    private List<Integer> scores; // Scores des joueurs
    private List<String> selectedMemes; // Liste des memes sélectionnés pour le round actuel
    private List<Integer> selectedMemeIndexes; // Liste des indexes des memes sélectionnés

    private TextView txtSituationDescription; // TextView pour afficher la description de la situation
    private ImageView[] memeImageViews; // Tableau pour afficher les memes
    private Button[] memeSelectButtons; // Tableau pour les boutons de sélection des memes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_principal);

        initializeMemesList();
        initializeSituationDescriptions();

        roundCount = 5; // Nombre de rounds
        currentRound = 1;
        judgeIndex = 0; // Le premier joueur est le juge
        currentPlayerIndex = 0; // Le premier joueur (hors juge) est le premier de la liste
        players = new ArrayList<>();
        scores = new ArrayList<>();

        txtSituationDescription = findViewById(R.id.txtSituationDescription);

        memeImageViews = new ImageView[4];
        memeImageViews[0] = findViewById(R.id.imageView1);
        memeImageViews[1] = findViewById(R.id.imageView2);
        memeImageViews[2] = findViewById(R.id.imageView3);
        memeImageViews[3] = findViewById(R.id.imageView4);

        memeSelectButtons = new Button[4];
        memeSelectButtons[0] = findViewById(R.id.btnSelectMeme1);
        memeSelectButtons[1] = findViewById(R.id.btnSelectMeme2);
        memeSelectButtons[2] = findViewById(R.id.btnSelectMeme3);
        memeSelectButtons[3] = findViewById(R.id.btnSelectMeme4);

        startRound();

        Button btnNextRound = findViewById(R.id.btnNextRound);
        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextRound();
            }
        });
    }

    private void initializeMemesList() {
        memesList = new ArrayList<>();
        // Ajoutez les chemins d'accès aux images des memes à la liste
        memesList.add("path/to/meme1.jpg");
        memesList.add("path/to/meme2.jpg");
        memesList.add("path/to/meme3.jpg");
        // Ajoutez d'autres memes si nécessaire
    }

    private void initializeSituationDescriptions() {
        situationDescriptions = new ArrayList<>();
        // Ajoutez les descriptions de situations à la liste
        situationDescriptions.add("Description 1");
        situationDescriptions.add("Description 2");
        situationDescriptions.add("Description 3");
        // Ajoutez d'autres descriptions si nécessaire
    }

    private void startRound() {
        String situationDescription = getRandomSituationDescription();
        txtSituationDescription.setText(situationDescription);

        selectRandomMemes();
        displaySelectedMemes();
    }

    private String getRandomSituationDescription() {
        Random random = new Random();
        int index = random.nextInt(situationDescriptions.size());
        return situationDescriptions.get(index);
    }

    private void selectRandomMemes() {
        selectedMemes = new ArrayList<>();
        selectedMemeIndexes = new ArrayList<>();

        Collections.shuffle(memesList);
        for (int i = 0; i < 4; i++) {
            selectedMemes.add(memesList.get(i));
            selectedMemeIndexes.add(i);
        }
    }

    private void displaySelectedMemes() {
        for (int i = 0; i < 4; i++) {
            // Affichez les memes sélectionnés sur la page
            // Vous devez charger les images depuis les chemins d'accès
            // Utilisez une bibliothèque comme Picasso ou Glide pour le chargement d'images à partir des URL
            // Par exemple:
            // Picasso.get().load(selectedMemes.get(i)).into(memeImageViews[i]);
        }
    }

    private void startNextRound() {
        currentRound++;

        if (currentRound <= roundCount) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            if (currentPlayerIndex == judgeIndex) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
            startRound();
        } else {
            displayFinalScores();
        }
    }

    private void displayFinalScores() {
        // Affichez les scores finaux des joueurs
    }
}
