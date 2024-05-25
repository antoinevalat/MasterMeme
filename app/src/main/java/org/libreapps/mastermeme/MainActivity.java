package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.activity_main);

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

        // Set up button listeners
        Button buttonCreerJeu = findViewById(R.id.button_creerjeu);
        Button buttonRejoindre = findViewById(R.id.button_rejoindre);
        Button buttonRegles = findViewById(R.id.button_regles);
        Button buttonAPropos = findViewById(R.id.button_apropos);

        buttonCreerJeu.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreerJeu.class);
            startActivity(intent);
        });

        buttonRejoindre.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RejoindreJeu.class);
            startActivity(intent);
        });

        buttonRegles.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReglesJeu.class);
            startActivity(intent);
        });

        buttonAPropos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, APropos.class);
            startActivity(intent);
        });
    }
}
