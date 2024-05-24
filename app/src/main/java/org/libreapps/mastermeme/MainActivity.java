package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.libreapps.mastermeme.models.User;
import org.libreapps.mastermeme.network.ApiClient;
import org.libreapps.mastermeme.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer les utilisateurs depuis l'API
        fetchUsers();

        // Récupérer le bouton "Créer un jeu"
        Button buttonCreerJeu = findViewById(R.id.button_creerjeu);
        // Ajouter un écouteur de clic au bouton "Créer un jeu"
        buttonCreerJeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir CreerJeu
                Intent intent = new Intent(MainActivity.this, CreerJeu.class);
                // Démarrer CreerJeu
                startActivity(intent);
            }
        });

        // Récupérer le bouton "Rejoindre un jeu"
        Button buttonRejoindreJeu = findViewById(R.id.button_rejoindre);
        // Ajouter un écouteur de clic au bouton "Rejoindre un jeu"
        buttonRejoindreJeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir RejoindreJeu
                Intent intent = new Intent(MainActivity.this, RejoindreJeu.class);
                // Démarrer RejoindreJeu
                startActivity(intent);
            }
        });

        // Récupérer le bouton "À propos"
        Button buttonAPropos = findViewById(R.id.button_apropos);
        // Ajouter un écouteur de clic au bouton "À propos"
        buttonAPropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir APropos
                Intent intent = new Intent(MainActivity.this, APropos.class);
                // Démarrer APropos
                startActivity(intent);
            }
        });

        // Récupérer le bouton "Règles du jeu"
        Button buttonReglesJeu = findViewById(R.id.button_regles);
        // Ajouter un écouteur de clic au bouton "Règles du jeu"
        buttonReglesJeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour ouvrir ReglesJeu
                Intent intent = new Intent(MainActivity.this, ReglesJeu.class);
                // Démarrer ReglesJeu
                startActivity(intent);
            }
        });
    }


    private void fetchUsers() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    // Utilisez les données des utilisateurs ici
                    Toast.makeText(MainActivity.this, "Users fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Gestion des erreurs de la réponse
                    Toast.makeText(MainActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Gestion des erreurs de la requête
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
