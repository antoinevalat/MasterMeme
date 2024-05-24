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
    private int bestMemeIndex;

    private List<String> players; // Liste des joueurs
    private List<Integer> scores; // Scores des joueurs
    private List<String> selectedMemes; // Liste des memes sélectionnés pour le round actuel
    private List<Integer> selectedMemeIndexes; // Liste des indexes des memes sélectionnés
    private List<String> selectedMemesByPlayers; // Liste des memes sélectionnés par les joueurs

    private TextView txtSituationDescription; // TextView pour afficher la description de la situation
    private ImageView[] memeImageViews; // Tableau pour afficher les memes
    private Button[] memeSelectButtons; // Tableau pour les boutons de sélection des memes
    private Button btnJudgeSelect; // Bouton pour le juge de sélectionner le meilleur mème


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_principal);

        initializeMemesList();
        initializeSituationDescriptions();
        btnJudgeSelect = findViewById(R.id.btnJudgeSelect); // Associer le bouton du juge
        btnJudgeSelect.setVisibility(View.INVISIBLE); // Le rendre invisible au début


        roundCount = 5; // Nombre de rounds
        currentRound = 1;
        judgeIndex = 0; // Le premier joueur est le juge
        currentPlayerIndex = 0; // Le premier joueur (hors juge) est le premier de la liste
        players = new ArrayList<>();
        scores = new ArrayList<>();

        players.add("Joueur 1");
        players.add("Joueur 2");
        players.add("Joueur 3");
        players.add("Joueur 4");
        //
        //Initialisez les scores à 0 pour chaque joueur
        for (int i = 0; i < players.size(); i++) {
            scores.add(0);
        }

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

        btnJudgeSelect = findViewById(R.id.btnJudgeSelect); // Associer le bouton du juge
        btnJudgeSelect.setVisibility(View.INVISIBLE); // Le rendre invisible au début

        startRound();

        Button btnNextRound = findViewById(R.id.btnNextRound);
        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextRound();
            }
        });
        setupJudgeSelectButton();
    }

    private void initializeMemesList() {
        memesList = new ArrayList<>();
        memesList.add(String.valueOf(R.drawable.meme1));
        memesList.add(String.valueOf(R.drawable.meme2));
        memesList.add(String.valueOf(R.drawable.meme3));
        memesList.add(String.valueOf(R.drawable.meme4));
        memesList.add(String.valueOf(R.drawable.meme4));
        memesList.add(String.valueOf(R.drawable.meme5));
        memesList.add(String.valueOf(R.drawable.meme6));
        memesList.add(String.valueOf(R.drawable.meme7));
        memesList.add(String.valueOf(R.drawable.meme8));
        memesList.add(String.valueOf(R.drawable.meme9));
        memesList.add(String.valueOf(R.drawable.meme10));
        memesList.add(String.valueOf(R.drawable.meme11));
        memesList.add(String.valueOf(R.drawable.meme12));
        memesList.add(String.valueOf(R.drawable.meme13));
        memesList.add(String.valueOf(R.drawable.placeholder_meme));

    }

    private void initializeSituationDescriptions() {
        situationDescriptions = new ArrayList<>();
        situationDescriptions.add("Quant tu ouvres une carte d'anniversaire de ta mamie et de l'argent en tombe");
        situationDescriptions.add("Quand tu réalises que le son que tu sautes d'habitude est en fait un banger");
        situationDescriptions.add("Quand le cours est sur le point de se terminer et quelqu'un pose une question");
        situationDescriptions.add("Quand tu retournes ton oreiller sur le côté froid");
        situationDescriptions.add("Quand tu croyais avoir 4$ dans ton compte en banque mais que tu en as 30");
        situationDescriptions.add("Quand tu réalises que tu as oublié d'étudier pour l'examen demain matin.");
        situationDescriptions.add("Quand c'est la 5ième fois qu'on te répète qqc et que tu fais semblant d'avoir compris");
        situationDescriptions.add("Quand tu te réveilles et réalises que c'est encore le week-end.");
        situationDescriptions.add("Quand tu vois les prix des billets d'avion pour tes vacances");
        situationDescriptions.add("Quand tu cherches tes clés de voiture et que tu les trouves dans le frigo.");

    }

    private void startRound() {
        String situationDescription = getRandomSituationDescription();
        txtSituationDescription.setText(situationDescription);

        selectRandomMemes();
        displaySelectedMemes();
        selectedMemesByPlayers = new ArrayList<>(); // Réinitialiser les sélections des joueurs pour le round
        for (int j = 0; j < players.size(); j++) {
            selectedMemesByPlayers.add(""); // Initialiser avec des chaînes vides
        //Rendre les boutons de sélection visibles et configurables pour les joueurs
            for (int i = 0; i < memeSelectButtons.length; i++) {
                final int memeIndex = i;
                memeSelectButtons[i].setVisibility(View.VISIBLE);
                memeSelectButtons[i].setEnabled(true);
                memeSelectButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!players.get(currentPlayerIndex).equals(players.get(judgeIndex))) {
                            selectedMemesByPlayers.set(currentPlayerIndex, selectedMemes.get(memeIndex));
                            memeSelectButtons[memeIndex].setEnabled(false); // Désactiver le bouton une fois sélectionné
                            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                            while (currentPlayerIndex == judgeIndex) {
                                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                            }
                        }
                        // Vérifier si tous les joueurs sauf le juge ont sélectionné
                        if (allPlayersSelected()) {
                            btnJudgeSelect.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }
    private boolean allPlayersSelected() {
        for (int i = 0; i < selectedMemesByPlayers.size(); i++) {
            if (i != judgeIndex && selectedMemesByPlayers.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
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
            int resId = Integer.parseInt(selectedMemes.get(i));
            memeImageViews[i].setImageResource(resId);
        }
    }

    private void startNextRound() {
        currentRound++;

        if (currentRound <= roundCount) {
            judgeIndex = (judgeIndex + 1) % players.size(); // Passer au prochain juge
            currentPlayerIndex = (judgeIndex + 1) % players.size(); // Débuter avec le joueur suivant après le juge
            startRound();
        } else {
            displayFinalScores();
        }
    }
    private String getRandomSituationDescription() {
        Random random = new Random();
        int index = random.nextInt(situationDescriptions.size());
        return situationDescriptions.get(index);
    }

    private void displayFinalScores() {
        // Affichez les scores finaux des joueurs
    }
    private void setupJudgeSelectButton() {
        btnJudgeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le juge sélectionne le meilleur mème
// Après que le juge ait sélectionné le meilleur mème, vous pouvez mettre à jour les scores
                // Par exemple, vous pouvez attribuer un point au joueur dont le mème est choisi comme le meilleur
                String bestMeme = selectedMemesByPlayers.get(bestMemeIndex);
                for (int i = 0; i < selectedMemesByPlayers.size(); i++) {
                    if (i != judgeIndex && selectedMemesByPlayers.get(i).equals(bestMeme)) {
                        scores.set(i, scores.get(i) + 1); // Mettre à jour le score du joueur dont le mème est choisi comme le meilleur
                        break; // Sortir de la boucle une fois le score mis à jour
                    }
                }

                // Après la sélection du meilleur mème et la mise à jour des scores, passer au round suivant
                btnJudgeSelect.setVisibility(View.INVISIBLE); // Cacher le bouton de sélection du juge
                startNextRound();
            }
        });
    }
}