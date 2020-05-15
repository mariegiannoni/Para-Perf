package com.example.paraperf;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MainActivity extends AppCompatActivity {

    //Déclaration des widgets
    // EditText représentant chemin du fichier Excel et de la vidéo du test
    private EditText cheminDonnees;
    private EditText cheminVideo;

    // Boutons nécessaires pour chercher les chemins du fichier Excel et de la vidéo
    private Button boutonChargerDonnees;
    private Button boutonChargerVideo;
    private Button boutonCalculer;

    // TextView et EditView fournissants des informations à l'utilisteur
    private TextView bienvenue;
    private TextView paraPerf;
    private TextView titreDonnees;
    private TextView titreVideo;
    private EditText texteExplications;
    private EditText texteInvitation;

    // static pour différencier une requête de récupération du chemin des données et de la vidéo
    private static final int REQUETE_CHEMIN_DONNEES = 2;
    private static final int REQUETE_CHEMIN_VIDEO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialisation des widgets
        cheminDonnees = (EditText) findViewById(R.id.cheminDonnees);
        cheminVideo = (EditText) findViewById(R.id.cheminVideo);
        boutonChargerDonnees = (Button) findViewById(R.id.boutonChargerDonnees);
        boutonChargerVideo = (Button) findViewById(R.id.boutonChargerVideo);
        boutonCalculer = (Button) findViewById(R.id.boutonCalculer);
        texteExplications = (EditText) findViewById(R.id.texteExplications);
        texteInvitation = (EditText) findViewById(R.id.texteInvitation);
        bienvenue = (TextView) findViewById(R.id.bienvenue);
        paraPerf = (TextView) findViewById(R.id.paraPerf);
        titreDonnees = (TextView) findViewById(R.id.titreDonnees);
        titreVideo = (TextView) findViewById(R.id.titreVideo);

        // appel de la fonction d'initialisation
        init();
    }

    private void init() {
        // On vide les champs de données et de vidéo
        cheminDonnees.setText("");
        cheminVideo.setText("");
    }

    // Chargement du chemin d'un fichier Excel contenant les données
    public void chargeDonnees(View v) {
        // Explorateur de fichier
        Intent intent = new Intent(this, ExplorateurFichier.class);
        startActivityForResult(intent, REQUETE_CHEMIN_DONNEES);
    }

    // Chargement du chemin d'une vidéo
    public void chargeVideo(View v) {
        startActivityForResult(new Intent(this, ExplorateurFichier.class), REQUETE_CHEMIN_VIDEO);
    }


    // Resultats de la requête de chemin pour les données et la vidéo
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Chemin données
        if (requestCode == REQUETE_CHEMIN_DONNEES) {
            if (resultCode == RESULT_OK) {
                cheminDonnees.setText(data.getStringExtra("GetFilePath"));
            }
        }
        // Chemin vidéo
        if (requestCode == REQUETE_CHEMIN_VIDEO) {
            if (resultCode == RESULT_OK) {
                cheminVideo.setText(data.getStringExtra("GetFilePath"));
            }
        }
    }

    // Fonction de vérificaiton du format d'un fichier
    private boolean verifieFichier(String fichier, String fichierFormat) {
        try {
            // Pattern utilisé pour vérifier le format
            Pattern pattern = Pattern.compile(fichierFormat);

            // Si le fichier est null ou ne contient pas de caractère, alors on renvoie false
            if (fichier == null)
                return false;

            if (fichier.length() == 0)
                return false;

            // On procède à la vérifcation avec la matcher du pattern et on retourne le résultat
            return pattern.matcher(fichier).matches();
        }
        catch (PatternSyntaxException e) {
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Passage à l'activité Resultat
    public void calculeResultat(View v) {
        // Nom des fichiers à charger
        String filenameDonnees = cheminDonnees.getText().toString();
        String filenameVideo = cheminVideo.getText().toString();

        // Fichier à charger
        File fileDonnees = new File(filenameDonnees);
        File fileVideo = new File(filenameVideo);

        if(fileDonnees.exists() && fileVideo.exists()) {

            // Vérification que le fichier de données est du format : xls, xlsx, xlsm et la vidéo au format mp4
            if((verifieFichier(filenameDonnees, ".*.xls") || verifieFichier(filenameDonnees, ".*.xlsm")
                    || verifieFichier(filenameDonnees, ".*.xlsx")) && verifieFichier(filenameVideo, ".*.mp4")) {
                // Calcul des résultats
                // On ajoutera ici un appel de fonctions de calculs quand celles-ci seront créées

                // Afficher l'activité des résultats
                Intent intent = new Intent(this, ResultatActivity.class);

                // On met le chemin absolu des données dans un extra
                intent.putExtra("cheminDonnees", filenameDonnees );

                // On met le chemin absolu de la vidéo dans un extra
                intent.putExtra("cheminVideo", filenameVideo );

                // On passe le résultat de l'extra à l'activité
                setResult(RESULT_OK, intent);

                // Création de deux extras contenant le chemin du fichier excel et le chemin de la vidéo
                startActivity(intent);
            }

            // Si le fichier de données n'est pas au bon format
            // On affiche un toast pour prévenir l'utilisateur
            else if (!verifieFichier(filenameDonnees, ".*.xls") && !verifieFichier(filenameDonnees, ".*.xlsm")
                    && !verifieFichier(filenameDonnees, ".*.xlsx")){
                Context context = getApplicationContext();
                CharSequence text = "Le fichier contenant les données doit être au format .xls, .xlsx ou .xlsm.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            // Si la vidéo n'est pas au bon format
            // on affiche un toast pour prévenir l'utilisateur
            else if (!verifieFichier(filenameVideo, ".*.mp4")) {
                Context context = getApplicationContext();
                CharSequence text = "La vidéo doit être au format .mp4.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }
    }

}
