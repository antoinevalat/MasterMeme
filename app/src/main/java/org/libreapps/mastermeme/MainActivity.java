package org.libreapps.mastermeme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.libreapps.mastermeme.models.User;
import org.libreapps.mastermeme.network.ApiClient;
import org.libreapps.mastermeme.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    Toast.makeText(MainActivity.this, "Users fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonCreerJeu = findViewById(R.id.button_creerjeu);
        Button buttonRejoindre = findViewById(R.id.button_rejoindre);
        Button buttonRegles = findViewById(R.id.button_regles);
        Button buttonAPropos = findViewById(R.id.button_apropos);

        buttonCreerJeu.setOnClickListener(v -> {
            EditText editEntrezNom = findViewById(R.id.editEntrezNom);
            String nomUtilisateur = editEntrezNom.getText().toString();
            if (!nomUtilisateur.isEmpty()) {
                mAuth.createUserWithEmailAndPassword("test@example.com", "password")
                        .addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(MainActivity.this, CreerJeu.class);
                                intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRejoindre.setOnClickListener(v -> {
            EditText editEntrezNom = findViewById(R.id.editEntrezNom);
            String nomUtilisateur = editEntrezNom.getText().toString();
            if (!nomUtilisateur.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, RejoindreJeu.class);
                intent.putExtra("NOM_UTILISATEUR", nomUtilisateur);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            }
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
