package com.example.paraperf;

import com.jjoe64.graphview.CursorMode;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


/*
    Cette activité est celle affichant les résultats.
    Les résultats sont un graphique calculé en backend et synchronisé à la vidéo, elle-même affichée.
    La synchronisation est visuelle grâce à un curseur d'avancement.

    Les images du media player sont issus du site icone8.fr
    Play
    <a target="_blank" href="https://icones8.fr/icons/set/play--v1">Jouer icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
    Pause
    <a target="_blank" href="https://icones8.fr/icons/set/pause--v1">Pause icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
    Avance
    <a target="_blank" href="https://icones8.fr/icons/set/fast-forward--v1">Avance rapide icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
    Rembobine
    <a target="_blank" href="https://icones8.fr/icons/set/rewind">Rembobiner icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
    Retour au début
    <a target="_blank" href="https://icones8.fr/icons/set/rotate--v1">Faire tourner icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>

    Utilisation de la librairie opensource GraphView pour l'affichage du graphique.
*/

public class ResultatActivity extends AppCompatActivity {

    // Déclaration des widgets
    // Vidéo
    private VideoView vueVideo;

    // Vue du graphique
    private GraphView graphique;

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
    private double currentTime = 0;
    private double startTime = 0;
    private double finalTime = 0;
    private int avanceTime = 1000; //ms
    private int rembobineTime = 1000; //ms

    // Lecture du fichier excel
    private LectureExcel donneesLues;

    // Radiobutton pour gérer le graphique
    private RadioGroup choixPos;
    private RadioButton posX;
    private RadioButton posY;
    private RadioButton posZ;

    // Elements pour le graphique
    private LineGraphSeries<DataPoint> serieX;
    private LineGraphSeries<DataPoint> serieY;
    private LineGraphSeries<DataPoint> serieZ;
    private BarGraphSeries<DataPoint> serieVirage;
    private Handler handlerGraphique = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat);

        vueVideo = (VideoView) findViewById(R.id.vueVideo);
        graphique = (GraphView) findViewById(R.id.graphique);
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
        choixPos = (RadioGroup) findViewById(R.id.choix_pos);
        posX = (RadioButton) findViewById(R.id.pos_x);
        posY = (RadioButton) findViewById(R.id.pos_y);
        posZ = (RadioButton) findViewById(R.id.pos_z);

        initVideo();
        initGraph();
    }

    /* VIDEO */

    private void initVideo() {
        cheminVideo = getIntent().getStringExtra("cheminVideo");

        // Affichage de la vidéo
        try {
            vueVideo.setVideoPath(cheminVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Récupération du temps de la vidéo
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(cheminVideo);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        finalTime = Double.parseDouble(time);
        startTime = vueVideo.getCurrentPosition();
        currentTime = vueVideo.getCurrentPosition();

        // Initialisation du curseur d'avancement
        if (finalTime != 0) {
            curseurAvancement.setMax((int) finalTime);
        }
        curseurAvancement.setProgress((int) currentTime);

        handlerVideo.postDelayed(changePositionTemps, 100);

        curseurAvancement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Quand l'utilisateur appuie sur la barre de progression, le temps de la vidéo est mis à jour
                // par rapport à la position du curseur
                if (fromUser) {
                    currentTime = curseurAvancement.getProgress();
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

    // Bouton pause
    public void pause(View v) {
        if (vueVideo.isPlaying() && vueVideo.canPause()) {
            vueVideo.pause();
            boutonPause.setEnabled(false);
            boutonPlay.setEnabled(true);
        }
    }

    // Bouton play
    public void play(View view) {
        if (currentTime <= finalTime) {
            vueVideo.start();
            boutonPause.setEnabled(true);
            boutonPlay.setEnabled(false);
        } else {
            vueVideo.seekTo((int) startTime);
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

    // Avancement du curseur au fil du temps
    private Runnable changePositionTemps = new Runnable() {
        @Override
        public void run() {
            currentTime = vueVideo.getCurrentPosition();
            curseurAvancement.setProgress((int) currentTime);
            handlerVideo.postDelayed(this, 100);
        }
    };

    /* GRAPHIQUE */

    private void initGraph() {
        cheminDonnees = getIntent().getStringExtra("cheminDonnees");

        // Récupération des données dans le fichier Excel
        donneesLues = new LectureExcel(cheminDonnees);

        // Chargement des séries de données pour les trois axes
        if (donneesLues.getDonnees() != null) {
            // axe x
            serieX = calculSerie(0);
            serieX.setTitle("Positions sur l'axe x en fonction du temps");
            serieX.setDrawDataPoints(true);
            serieX.setColor(Color.argb(255, 30, 125, 135));

            // axe y
            serieY = calculSerie(1);
            serieY.setTitle("Positions sur l'axe y en fonction du temps");
            serieY.setDrawDataPoints(true);
            serieY.setColor(Color.argb(255, 30, 125, 135));

            // axe z
            serieZ = calculSerie(2);
            serieZ.setTitle("Positions sur l'axe z en fonction du temps");
            serieZ.setDrawDataPoints(true);
            serieZ.setColor(Color.argb(255, 30, 125, 135));

            // Position par défaut : X
            graphique.addSeries(serieX);
            graphique.setTitle("Position du capteur sur l'axe x en fonction du temps");
            graphique.getViewport().setScrollable(true);
            posX.setChecked(true);

            // Ajout des virages
            serieVirage = calculVirage(graphique.getViewport().getMaxY(true));
            serieVirage.setDataWidth(0.5);
            serieVirage.setSpacing(10);
            graphique.addSeries(serieVirage);
        }

        // Choix de position
        choixPos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                choixDePosition(group, checkedId);
            }
        });

        handlerGraphique.postDelayed(changeAxeTemps, 100);
    }

    // Calcul d'une série de données (choix : x - 0, y - 1 ou z - 2)
    private LineGraphSeries<DataPoint> calculSerie(int choix){
        LineGraphSeries<DataPoint> serie = new LineGraphSeries<>();
        if(donneesLues.getDonnees() != null) {
            if (donneesLues.getDonnees().size() > 0) {
                if (choix == 0) { // X
                    for (int i = 0; i < donneesLues.getDonnees().size(); i++) {
                        serie.appendData(new DataPoint(donneesLues.getDonnees().get(i).getTemps(), donneesLues.getDonnees().get(i).getX()), true, donneesLues.getDonnees().size());
                    }
                } else if (choix == 1) { // Y
                    for (int i = 0; i < donneesLues.getDonnees().size(); i++) {
                        serie.appendData(new DataPoint(donneesLues.getDonnees().get(i).getTemps(), donneesLues.getDonnees().get(i).getY()), true, donneesLues.getDonnees().size());
                    }
                } else if (choix == 2) { // Z
                    for (int i = 0; i < donneesLues.getDonnees().size(); i++) {
                        serie.appendData(new DataPoint(donneesLues.getDonnees().get(i).getTemps(), donneesLues.getDonnees().get(i).getZ()), true, donneesLues.getDonnees().size());
                    }
                }
            }
        }
        return serie;
    }

    // Calcule une série de données contenant les virages (bar chart)
    private BarGraphSeries<DataPoint> calculVirage(double max){
        BarGraphSeries<DataPoint> serie = new BarGraphSeries<>();
        if(donneesLues.getDonnees() != null) {
            if (donneesLues.getDonnees().size() > 0) {
                for (int i = 0; i < donneesLues.getDonnees().size(); i++) {
                    if (donneesLues.getDonnees().get(i).isVirage()) {
                        serie.appendData(new DataPoint(donneesLues.getDonnees().get(i).getTemps(), max), true, 500);
                    }
                }
            }
        }
        return serie;
    }

    //  Boutons permettant de choisir l'axe visualisé sur le graphique
    private void choixDePosition(RadioGroup group, int checkIde) {
        int radioId = group.getCheckedRadioButtonId();

       if(radioId == posX.getId()){
            // dessin de x par rapport au temps
            if(serieX != null) {
                graphique.removeAllSeries();
                graphique.addSeries(serieX);
                graphique.addSeries(serieVirage);
                graphique.setTitle("Position du capteur sur l'axe x en fonction du temps");
            }
        }
        else if (radioId == posY.getId()){
            // dessin de y par rapport au temps
            if(serieY != null) {
                graphique.removeAllSeries();
                graphique.addSeries(serieY);
                graphique.addSeries(serieVirage);
                graphique.setTitle("Position du capteur sur l'axe y en fonction du temps");
            }
        }
        else if (radioId == posZ.getId()){
            // dessin de z par rapport au temps
            if(serieZ != null) {
                graphique.removeAllSeries();
                graphique.addSeries(serieZ);
                graphique.addSeries(serieVirage);
                graphique.setTitle("Position du capteur sur l'axe z en fonction du temps");
            }
        }
    }

    private Runnable changeAxeTemps = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            currentTime = vueVideo.getCurrentPosition();

            // Synchronisation avec le graphique
            if(donneesLues.getDonnees() != null) {
                double min;
                double max;

                // Calcul du min et du max en fonction du temps
                min = currentTime/1000 - 0.15 * (finalTime - startTime)/1000;
                max = currentTime/1000 + 0.15 * (finalTime - startTime)/1000;

                graphique.getViewport().setXAxisBoundsManual(true);
                graphique.getViewport().setMinX(min);
                graphique.getViewport().setMaxX(max);
                graphique.setCursorMode(true);
                graphique.invalidate();
            }

            handlerGraphique.postDelayed(this, 100);
        }
    };

    /* MENU */

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
}
