package org.libreapps.mastermeme;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class JeuPrincipal extends AppCompatActivity {

    private List<String> memesList;
    private List<String> situationDescriptions;
    private int roundCount;
    private int currentRound;
    private int judgeIndex = -1; // Initialiser à -1 pour indiquer une valeur non définie
    private List<String> players;
    private List<Integer> scores;
    private List<String> selectedMemes;
    private List<Integer> selectedMemeIndexes;

    private TextView txtSituationDescription;
    private ImageView[] memeImageViews;
    private Button[] memeSelectButtons;
    private Button btnValider;

    private DatabaseReference mDatabase;
    private String codePartie;
    private String nomUtilisateur;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_principal);

        initializeMemesList();
        initializeSituationDescriptions();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        codePartie = getIntent().getStringExtra("CODE_PARTIE");
        nomUtilisateur = getIntent().getStringExtra("NOM_UTILISATEUR");
        userId = generateUserId();

        roundCount = getIntent().getIntExtra("ROUND_COUNT", 6); // Par défaut 6 rounds si non spécifié
        currentRound = 1;
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

        btnValider = findViewById(R.id.btnValider);

        // Ajouter l'utilisateur à la partie avec un score initial de 0
        mDatabase.child("parties").child(codePartie).child("joueurs").child(userId).setValue(nomUtilisateur).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                players.add(userId);
                scores.add(0);
            } else {
                Log.w("JeuPrincipal", "Failed to add user to the game", task.getException());
            }
        });

        // Initialiser les données de la partie
        mDatabase.child("parties").child(codePartie).child("round").setValue(currentRound);

        // Vérifier et initialiser judgeIndex si nécessaire
        mDatabase.child("parties").child(codePartie).child("judgeIndex").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Initialiser judgeIndex à 0 si le nœud n'existe pas encore
                    mDatabase.child("parties").child(codePartie).child("judgeIndex").setValue(0);
                    judgeIndex = 0;
                } else {
                    judgeIndex = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("JeuPrincipal", "initialiserJudgeIndex:onCancelled", databaseError.toException());
            }
        });

        // Charger les joueurs
        mDatabase.child("parties").child(codePartie).child("joueurs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    players.add(playerSnapshot.getKey());
                    scores.add(0); // Initialiser le score à 0 pour chaque joueur
                }
                startRound();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("JeuPrincipal", "loadPlayers:onCancelled", databaseError.toException());
            }
        });

        btnValider.setOnClickListener(v -> {
            int bestMemeIndex = getBestMemeIndex();
            String winnerId = selectedMemeIndexes.get(bestMemeIndex).toString();
            int playerIndex = players.indexOf(winnerId);

            if (playerIndex >= 0 && playerIndex < scores.size()) {
                int currentScore = scores.get(playerIndex);
                scores.set(playerIndex, currentScore + 1);
                mDatabase.child("parties").child(codePartie).child("scores").child(winnerId).setValue(currentScore + 1);
            } else {
                Log.w("JeuPrincipal", "Invalid player index for winner: " + winnerId);
            }

            startNextRound();
        });

        Button btnNextRound = findViewById(R.id.btnNextRound);
        btnNextRound.setOnClickListener(v -> startNextRound());

        // Listener pour les changements de round
        mDatabase.child("parties").child(codePartie).child("round").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer round = dataSnapshot.getValue(Integer.class);
                if (round != null) {
                    currentRound = round;
                    startRound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("JeuPrincipal", "loadRound:onCancelled", databaseError.toException());
            }
        });

        // Listener pour les changements de situation description
        mDatabase.child("parties").child(codePartie).child("situationDescription").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String situationDescription = dataSnapshot.getValue(String.class);
                txtSituationDescription.setText(situationDescription);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("JeuPrincipal", "loadDescription:onCancelled", databaseError.toException());
            }
        });

        // Listener pour les changements de juge
        mDatabase.child("parties").child(codePartie).child("judgeIndex").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer index = dataSnapshot.getValue(Integer.class);
                if (index != null) {
                    judgeIndex = index;
                    updateJudgeUI();
                } else {
                    Log.w("JeuPrincipal", "judgeIndex n'existe pas encore dans la base de données");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("JeuPrincipal", "loadJudge:onCancelled", databaseError.toException());
            }
        });
    }

    private void initializeMemesList() {
        memesList = new ArrayList<>();
        memesList.add(String.valueOf(R.drawable.meme1));
        memesList.add(String.valueOf(R.drawable.meme2));
        memesList.add(String.valueOf(R.drawable.meme3));
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
    }

    private void initializeSituationDescriptions() {
        situationDescriptions = new ArrayList<>();
        situationDescriptions.add("Quand tu ouvres une carte d'anniversaire de ta mamie et de l'argent en tombe");
        situationDescriptions.add("Quand tu réalises que le son que tu sautes d'habitude est en fait un banger");
        situationDescriptions.add("Quand le cours est sur le point de se terminer et quelqu'un pose une question");
        situationDescriptions.add("Quand tu retournes ton oreiller sur le côté froid");
        situationDescriptions.add("Quand tu croyais avoir 4$ dans ton compte en banque mais que tu en as 30");
        situationDescriptions.add("Quand tu réalises que tu as oublié d'étudier pour l'examen demain");
        situationDescriptions.add("Quand tu mets tes vêtements fraîchement sortis du sèche-linge");
        situationDescriptions.add("Quand tu trouves enfin le titre d'une chanson que tu cherches depuis des lustres");
        situationDescriptions.add("Quand tu commandes quelque chose de nouveau au restaurant et que c'est délicieux");
        situationDescriptions.add("Quand tu retrouves de l'argent dans une vieille veste");
    }

    private void startRound() {
        if (currentRound > roundCount) {
            endGame();
            return;
        }

        // Sélectionner une situation aléatoire
        Random random = new Random();
        int situationIndex = random.nextInt(situationDescriptions.size());
        String situationDescription = situationDescriptions.get(situationIndex);

        // Mettre à jour la description de la situation dans la base de données
        mDatabase.child("parties").child(codePartie).child("situationDescription").setValue(situationDescription);

        // Sélectionner 4 mèmes aléatoires
        selectedMemes = new ArrayList<>(memesList);
        Collections.shuffle(selectedMemes);
        selectedMemes = selectedMemes.subList(0, 4);

        selectedMemeIndexes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int memeResId = getResources().getIdentifier(selectedMemes.get(i), "drawable", getPackageName());
            memeImageViews[i].setImageResource(memeResId);
            selectedMemeIndexes.add(i);
        }

        updateJudgeUI();
    }

    private void updateJudgeUI() {
        if (players.get(judgeIndex).equals(userId)) {
            Toast.makeText(JeuPrincipal.this, "Vous êtes le juge ce tour-ci", Toast.LENGTH_SHORT).show();
            btnValider.setEnabled(true);
            for (Button button : memeSelectButtons) {
                button.setEnabled(false);
            }
        } else {
            btnValider.setEnabled(false);
            for (Button button : memeSelectButtons) {
                button.setEnabled(true);
            }
        }
    }

    private int getBestMemeIndex() {
        // Cette méthode devrait implémenter la logique pour déterminer le meilleur mème
        // Pour l'instant, elle retourne un index aléatoire pour l'exemple
        Random random = new Random();
        return random.nextInt(4);
    }

    private void startNextRound() {
        if (currentRound < roundCount) {
            currentRound++;
            judgeIndex = (judgeIndex + 1) % players.size();
            mDatabase.child("parties").child(codePartie).child("round").setValue(currentRound);
            mDatabase.child("parties").child(codePartie).child("judgeIndex").setValue(judgeIndex);
            startRound();
        } else {
            endGame();
        }
    }

    private void endGame() {
        // Afficher le gagnant et terminer la partie
        int maxScore = Collections.max(scores);
        int winnerIndex = scores.indexOf(maxScore);
        String winnerId = players.get(winnerIndex);
        mDatabase.child("parties").child(codePartie).child("winner").setValue(winnerId);
        Toast.makeText(JeuPrincipal.this, "La partie est terminée ! Le gagnant est " + winnerId, Toast.LENGTH_LONG).show();
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
