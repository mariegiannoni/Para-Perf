package com.example.paraperf;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

    // Boutons play, pause, avance, rembobiner, debut
    private ImageButton boutonPlay;
    private ImageButton boutonPause;
    private ImageButton boutonAvance;
    private ImageButton boutonRembobine;
    private ImageButton boutonDebut;

    // Temps de gestion de la vidéo
    private Handler handlerVideo = new Handler();
    private SeekBar.OnSeekBarChangeListener listenerCurseurAvancement;
    private double currentTime = 0;
    private double startTime = 0;
    private double finalTime = 0;
    private int avanceTime = 1000; //ms
    private int rembobineTime = 1000; //ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        vueVideo = (VideoView) findViewById(R.id.vueVideo);
        ;
        graphique = findViewById(R.id.graphique);
        curseurAvancement = (SeekBar) findViewById(R.id.curseurAvancement);
        titreResultat = (TextView) findViewById(R.id.titreResultat);
        titreVideo = (TextView) findViewById(R.id.titreVideo);
        titreGraphique = (TextView) findViewById(R.id.titreGraphique);
        boutonPause = (ImageButton) findViewById(R.id.pause);
        boutonPlay = (ImageButton) findViewById(R.id.play);
        boutonPause.setEnabled(false);
        boutonPlay.setEnabled(true);
        boutonAvance = (ImageButton) findViewById(R.id.avance);
        boutonRembobine = (ImageButton) findViewById(R.id.rembobiner);
        boutonDebut = (ImageButton) findViewById(R.id.debut);

        init();
    }

    private void init() {
        cheminDonnees = getIntent().getStringExtra("cheminDonnees");
        cheminVideo = getIntent().getStringExtra("cheminVideo");

        // Affichage de la vidéo
        try {
            vueVideo.setVideoPath(cheminVideo);
        } catch (Exception e) {
            Context context = getApplicationContext();
            Log.e("Error Play Local Video:", e.getMessage());
            Toast.makeText(context, "Une erreur est survenue en jouant la vidéo : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Récupération du temps de la vidéo
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(cheminVideo);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        finalTime = Double.parseDouble(time);
        startTime = vueVideo.getCurrentPosition();
        currentTime = vueVideo.getCurrentPosition();

        Log.d("Time", finalTime + " " + startTime + " " + currentTime);

        // Initialisation du curseur d'avancement
        if (finalTime != 0) {
            curseurAvancement.setMax((int) finalTime);
        }
        curseurAvancement.setProgress((int) currentTime);

        handlerVideo.postDelayed(changePositionTemps, 100);
        curseurAvancement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = (double) curseurAvancement.getProgress();
                    if (currentTime >= startTime && currentTime <= finalTime) {
                        vueVideo.seekTo((int) currentTime);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    /* Gestion de la vidéo */
    // Bouton play et pause
    public void pause(View v) {
        if (vueVideo.isPlaying() && vueVideo.canPause()) {
            vueVideo.pause();
            Log.d("cas 1", "ici");
            boutonPause.setEnabled(false);
            boutonPlay.setEnabled(true);
        }
    }

    public void play(View view) {
        if (currentTime <= finalTime) {
            vueVideo.start();
            Log.d("cas 2", "ici");
            boutonPause.setEnabled(true);
            boutonPlay.setEnabled(false);
        } else {
            vueVideo.seekTo((int) startTime);
            Log.d("cas 3", "ici");
            boutonPause.setEnabled(true);
            boutonPlay.setEnabled(false);
        }
    }

    // Bouton avance
    public void avance(View v) {
        if (vueVideo.canSeekForward()) {
            currentTime = currentTime + avanceTime;
            if (currentTime >= finalTime) {
                currentTime = finalTime;
            }
            curseurAvancement.setProgress((int) currentTime);
            vueVideo.seekTo((int) currentTime);
        }
    }

    // Bouton rembobine
    public void rembobine(View v) {
        if (vueVideo.canSeekBackward()) {
            currentTime = currentTime - rembobineTime;
            if (currentTime <= startTime) {
                currentTime = startTime;
            }
            curseurAvancement.setProgress((int) currentTime);
            vueVideo.seekTo((int) currentTime);
        }
    }

    // Bouton retour au début
    public void debut(View v) {
        if (vueVideo.canSeekBackward()) {
            currentTime = startTime;
            curseurAvancement.setProgress((int) currentTime);
            vueVideo.seekTo((int) currentTime);
        }
    }

    // Barre d'avancement : positionnement manuel de l'utilisateur

    // Avancement de la barre d'avancement
    private Runnable changePositionTemps = new Runnable() {
        @Override
        public void run() {
            currentTime = vueVideo.getCurrentPosition();
            curseurAvancement.setProgress((int) currentTime);
            handlerVideo.postDelayed(this, 100);
        }
    };
}
