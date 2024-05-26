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

    private static final String TAG = "JeuPrincipal";

    private List<String> memesList;
    private List<String> situationDescriptions;
    private int roundCount;
    private int currentRound;
    private Integer judgeIndex;
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

        try {
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

            // Ajouter l'utilisateur à la partie
            mDatabase.child("parties").child(codePartie).child("joueurs").child(userId).setValue(nomUtilisateur);

            // Initialiser les données de la partie
            mDatabase.child("parties").child(codePartie).child("round").setValue(currentRound);

            mDatabase.child("parties").child(codePartie).child("joueurs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        players.add(playerSnapshot.getKey());
                        scores.add(0);
                    }
                    startRound();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadPlayers:onCancelled", databaseError.toException());
                }
            });

            btnValider.setOnClickListener(v -> {
                try {
                    int bestMemeIndex = getBestMemeIndex();
                    if (bestMemeIndex >= 0 && bestMemeIndex < selectedMemeIndexes.size()) {
                        String winnerId = selectedMemeIndexes.get(bestMemeIndex).toString();
                        int currentScore = scores.get(players.indexOf(winnerId));
                        mDatabase.child("parties").child(codePartie).child("scores").child(winnerId).setValue(currentScore + 1);
                        startNextRound();
                    } else {
                        Log.w(TAG, "Invalid bestMemeIndex: " + bestMemeIndex);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in btnValider click listener: ", e);
                }
            });

            Button btnNextRound = findViewById(R.id.btnNextRound);
            btnNextRound.setOnClickListener(v -> startNextRound());

            // Listener pour les changements de round
            mDatabase.child("parties").child(codePartie).child("round").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer roundValue = dataSnapshot.getValue(Integer.class);
                    if (roundValue != null) {
                        currentRound = roundValue;
                        startRound();
                    } else {
                        Log.w(TAG, "roundValue is null");
                        Toast.makeText(JeuPrincipal.this, "Erreur : round est nul", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadRound:onCancelled", databaseError.toException());
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
                    Log.w(TAG, "loadDescription:onCancelled", databaseError.toException());
                }
            });

            // Listener pour les changements de juge
            mDatabase.child("parties").child(codePartie).child("judgeIndex").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer judgeValue = dataSnapshot.getValue(Integer.class);
                    if (judgeValue != null) {
                        judgeIndex = judgeValue;
                        updateJudgeUI();
                    } else {
                        Log.w(TAG, "judgeValue is null");
                        Toast.makeText(JeuPrincipal.this, "Erreur : judgeIndex est nul", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadJudge:onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            finish(); // Fermer l'activité en cas d'erreur
        }
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
        situationDescriptions.add("Quand tu réalises que tu as oublié d'étudier pour l'examen demain matin.");
        situationDescriptions.add("Quand c'est la 5ème fois qu'on te répète qqc et que tu fais semblant d'avoir compris");
        situationDescriptions.add("Quand tu te réveilles et réalises que c'est encore le week-end.");
        situationDescriptions.add("Quand tu vois les prix des billets d'avion pour tes vacances");
        situationDescriptions.add("Quand tu cherches tes clés de voiture et que tu les trouves dans le frigo.");
    }

    private void startRound() {
        try {
            // Vérifiez si l'utilisateur est le juge
            mDatabase.child("parties").child(codePartie).child("judgeIndex").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    judgeIndex = dataSnapshot.getValue(Integer.class);
                    if (judgeIndex != null && judgeIndex >= 0 && judgeIndex < players.size()) {
                        if (userId.equals(players.get(judgeIndex))) {
                            // Si l'utilisateur est le juge, affichez un message approprié
                            Toast.makeText(getApplicationContext(), "Vous êtes le juge de ce round", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sinon, affichez un message indiquant aux autres joueurs d'attendre
                            Toast.makeText(getApplicationContext(), "Attendez que les autres joueurs fassent leur choix", Toast.LENGTH_SHORT).show();
                        }

                        // Récupérer et afficher la description de la situation aléatoire
                        String situationDescription = getRandomSituationDescription();
                        mDatabase.child("parties").child(codePartie).child("situationDescription").setValue(situationDescription);

                        // Sélectionner les mèmes aléatoires pour ce tour
                        selectRandomMemes();

                        // Afficher les mèmes sélectionnés par les autres joueurs pour le juge
                        mDatabase.child("parties").child(codePartie).child("memes").child(String.valueOf(currentRound)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Effacez les anciens mèmes sélectionnés, puis récupérez les nouveaux mèmes sélectionnés
                                selectedMemes.clear();
                                selectedMemeIndexes.clear();

                                // Parcourez les données pour obtenir les mèmes sélectionnés
                                for (DataSnapshot memeSnapshot : dataSnapshot.getChildren()) {
                                    String memeIndex = memeSnapshot.getValue(String.class);
                                    selectedMemes.add(memeIndex);
                                    selectedMemeIndexes.add(Integer.parseInt(memeIndex));
                                }

                                // Mettez à jour l'interface utilisateur du juge avec les nouveaux mèmes sélectionnés
                                displaySelectedMemes();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Gérez les erreurs de récupération des données depuis Firebase
                                Log.w(TAG, "loadMemes:onCancelled", databaseError.toException());
                            }
                        });
                    } else {
                        Log.w(TAG, "judgeIndex is invalid");
                        Toast.makeText(JeuPrincipal.this, "Erreur : judgeIndex est invalide", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Gérez les erreurs de récupération des données depuis Firebase
                    Log.w(TAG, "loadJudge:onCancelled", databaseError.toException());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in startRound: ", e);
        }
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
        if (selectedMemes.size() < 4) {
            Log.w(TAG, "selectedMemes size is less than 4");
            return;
        }
        for (int i = 0; i < 4; i++) {
            int resId = Integer.parseInt(selectedMemes.get(i));
            memeImageViews[i].setImageResource(resId);
        }
    }

    private int getBestMemeIndex() {
        // Implémentez la logique pour obtenir l'index du meilleur meme ici
        return 0;
    }

    private void startNextRound() {
        try {
            if (currentRound < roundCount) {
                currentRound++;
                mDatabase.child("parties").child(codePartie).child("round").setValue(currentRound);
                startRound();
            } else {
                endGame();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in startNextRound: ", e);
        }
    }

    private void endGame() {
        Toast.makeText(this, "Le jeu est terminé !", Toast.LENGTH_SHORT).show();
    }

    private void updateJudgeUI() {
        if (judgeIndex != null && judgeIndex >= 0 && judgeIndex < players.size()) {
            String judgeName = players.get(judgeIndex);
            Toast.makeText(this, "Le juge de ce round est : " + judgeName, Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "judgeIndex is invalid in updateJudgeUI");
        }
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
