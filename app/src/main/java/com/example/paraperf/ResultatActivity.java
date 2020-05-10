package com.example.paraperf;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


/*
    Cette activité est celle affichant les résultats.
    Les résultats sont un graphique calculé en backend et synchronisé à la vidéo, elle-même affichée.
    La synchronisation est visuelle grâce à un curseur d'avancement.
*/

public class ResultatActivity extends AppCompatActivity {

    // Déclaration des widgets
    // Vidéo
    private VideoView vueVideo;

    // Vue du graphique
    private View graphique;

    // Curseur d'avancement de la vidéo et du graphique
    private SeekBar curseurAvancement;

    // Titres
    private TextView titreResultat;
    private TextView titreVideo;
    private TextView titreGraphique;

    // Chemin données et vidéo
    private String cheminDonnees;
    private String cheminVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        vueVideo = (VideoView) findViewById(R.id.vueVideo);;
        graphique = findViewById(R.id.graphique);
        curseurAvancement = (SeekBar) findViewById(R.id.curseurAvancement);
        titreResultat =  (TextView) findViewById(R.id.titreResultat);
        titreVideo =  (TextView) findViewById(R.id.titreVideo);
        titreGraphique =  (TextView) findViewById(R.id.titreGraphique);

        init();
    }

    private void init() {
        cheminDonnees = getIntent().getStringExtra("cheminDonnees");
        cheminVideo = getIntent().getStringExtra("cheminVideo");

        // Affichage de la vidéo
        try {
            vueVideo.setVideoPath(cheminVideo);
            vueVideo.start();
        } catch(Exception e) {
            Context context = getApplicationContext();
            Log.e("Error Play Local Video:", e.getMessage());
            Toast.makeText(context,"Une erreur est survenue en jouant la vidéo : "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Permet l'affichage du menu sur la page de résultats
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.resultat_menu, menu);
        return true;
    }

    // Permet la gestion des menus
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accueil:
                // Retourner à l'activité principale
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.telechargement:
                // écrire les résultats dans un fichier excel et l'enregistrer
                // dans l'endroit choisi par l'utilisateur
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        vueVideo.start();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        vueVideo.suspend();
//    }
}
