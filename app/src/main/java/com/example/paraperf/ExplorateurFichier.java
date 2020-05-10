package com.example.paraperf;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;

/*
    Cette activité permet l'affichage des fichiers contenus dans un dossier
    Le dossier par défaut est celui de la carte sd.
    Icon fichier :
    <a target="_blank" href="https://icones8.fr/icons/set/file--v2">Fichier icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
    Icon dossier :
    <a target="_blank" href="https://icones8.fr/icons/set/folder-invoices--v1">Dossier icon</a> icône par <a target="_blank" href="https://icones8.fr">Icons8</a>
 */

public class ExplorateurFichier extends ListActivity {

    // Fichier contenant le dossier courant
    private File currentRepertory;
    // Adaptateur permettant d'afficher chaque fichier selon une nomenclature précisée dans vue_fichier.xml
    private FileArrayAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRepertory = new File(Environment.getExternalStorageDirectory().getPath());
        fill(currentRepertory);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle("Dossier courant " + f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item> files = new ArrayList<Item>();
        try {
            for (File ff: dirs) {
                // On récupère la date de modification et on la formate en chaîne de caractères
                Date lastModifiedDate = new Date(ff.lastModified());
                DateFormat formatDate = DateFormat.getDateTimeInstance();
                String modifiedDate = formatDate.format(lastModifiedDate);

                // On regarde si l'élément du dossier est lui-même un dossier
                // si oui on regarde s'il contient des éléments et on les compte
                if (ff.isDirectory()) {
                    File[] fnb = ff.listFiles();
                    int nb = 0;
                    if (fnb != null) {
                        nb = fnb.length;
                    }
                    else {
                        nb = 0;
                    }

                    // On affiche le nombre d'items contenu dans le dossier
                    String nbItem = String.valueOf(nb);
                    if (nb == 0) {
                        nbItem = nbItem + " élément";
                    }
                    else {
                        nbItem = nbItem + " éléments";
                    }
                    // On affiche le dossier avec un icon de dossier et le nombre d'items contenus dans le dossier en guise de taille
                    dir.add(new Item(ff.getName(), nbItem, modifiedDate, ff.getAbsolutePath(), "dossier_icon"));
                }
                else {
                    // Si c'est un fichier, on utilise un icon pour fichier et on affiche sa taille en byte
                    files.add(new Item(ff.getName(), ff.length() + " byte", modifiedDate, ff.getAbsolutePath(), "fichier_icon"));
                }
            }
        }
        catch (Exception e) {
            // Erreur exception
        }
        Collections.sort(dir);
        Collections.sort(files);
        dir.addAll(files);

        if(!f.getName().equalsIgnoreCase("sdcard")) {
            dir.add(0, new Item("..", "Dossier parent", "", f.getParent(), "parent_icon"));
        }

        adapter = new FileArrayAdapter(ExplorateurFichier.this, R.layout.vue_fichier, dir);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Item item = adapter.getItem(position);
        // En cas de clique sur un item, le dossier sélectionné devient le nouveau dossier courant

        if(new File(item.getPath()).isDirectory()) {
            currentRepertory = new File(item.getPath());
            fill(currentRepertory);
        }
        // Si on clique sur un fichier, on appelle la méthode onFileClick
        else {
            onFileClick(item);
        }
    }

    private void onFileClick(Item item) {
        // Création d'une Intent
        Intent intent = new Intent();

        // On met le chemin absolu du fichier dans un extra
        intent.putExtra("GetFilePath", currentRepertory.toString() + "/" + item.getName() );

        // On passe le résultat de l'extra à l'activité
        setResult(RESULT_OK, intent);

        // On termine l'activité ExplorateurFichier pour retourner à MainActivity
        finish();
    }
}
