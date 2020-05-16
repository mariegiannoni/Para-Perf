package com.example.paraperf;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;

public class LectureExcel {

    private String chemin;
    private ArrayList<PointDonnee> donnees;

    public LectureExcel() {}

    public LectureExcel(String chemin) {
        Workbook workbook = null;
        try {
            // Sécurisation afin d'être sûr que donnees est vide
            if(donnees != null) {
                if (donnees.size() != 0) {
                    donnees.clear();
                }
            }
            else {
                donnees = new ArrayList<PointDonnee>();
            }

            // Récupération du classeur
            workbook = Workbook.getWorkbook(new File(chemin));

            // Récupération de la première feuille
            Sheet sheet = workbook.getSheet(0);

            // Récupération des données
            int nbRows = sheet.getRows();
            int nbColumns = sheet.getColumns();
            if(nbRows > 0 && nbColumns == 4) {
                // x, y, temps, virage
                int id_x = 0;
                int id_y = 0;
                int id_temps = 0;
                int id_virage = 0;

                // Lecture de la première ligne
                String strC1 = sheet.getCell(0,0).getContents();
                String strC2 = sheet.getCell(1,0).getContents();
                String strC3 = sheet.getCell(2,0).getContents();
                String strC4 = sheet.getCell(3,0).getContents();

                // Attribution d'un identifiant
                // id_x
                id_x = identifiantXMultiCell(strC1, strC2, strC3, strC4, "");
                // id_y
                id_y = identifiantYMultiCell(strC1, strC2, strC3, strC4, "");
                // id_temps
                id_temps = identifiantTempsMultiCell(strC1, strC2, strC3, strC4, "");
                // id_virage
                id_virage = identifiantVirageMultiCell(strC1, strC2, strC3, strC4, "");

                if(id_x != -1 && id_y != -1 && id_temps != -1 && id_virage != -1) {
                    // Parcours de la feuille
                    for (int i = 1; i < nbRows; i++) {
                        // Récupération du contenu de chaque column pour une ligne donnée
                        Double temps = Double.valueOf(sheet.getCell(id_temps, i).getContents());
                        Double x = Double.valueOf(sheet.getCell(id_x, i).getContents());
                        Double y = Double.valueOf(sheet.getCell(id_y, i).getContents());
                        // Conversion du virage en boolean
                        boolean virage = false;
                        if(sheet.getCell(id_virage, i).getContents().compareToIgnoreCase("true") == 0
                                || sheet.getCell(id_virage, i).getContents().compareToIgnoreCase("vrai") == 0) {
                            virage = true;
                        }

                        // Ajout d'un noyau de données à la liste
                        donnees.add(new PointDonnee(x, y, temps, virage));
                    }
                }
            }
            else if(nbRows > 0 && nbColumns == 5) {
                // x, y, z, temps, virage
                int id_x = 0;
                int id_y = 0;
                int id_z = 0;
                int id_temps = 0;
                int id_virage = 0;

                // Lecture de la première ligne
                String strC1 = sheet.getCell(0,0).getContents();
                String strC2 = sheet.getCell(1,0).getContents();
                String strC3 = sheet.getCell(2,0).getContents();
                String strC4 = sheet.getCell(3,0).getContents();
                String strC5 = sheet.getCell(4,0).getContents();

                // Attribution d'un identifiant
                // id_x
                id_x = identifiantXMultiCell(strC1, strC2, strC3, strC4, strC5);
                // id_y
                id_y = identifiantYMultiCell(strC1, strC2, strC3, strC4, strC5);
                // id_z
                id_z = identifiantZMultiCell(strC1, strC2, strC3, strC4, strC5);
                // id_temps
                id_temps = identifiantTempsMultiCell(strC1, strC2, strC3, strC4, strC5);
                // id_virage
                id_virage = identifiantVirageMultiCell(strC1, strC2, strC3, strC4, strC5);

                if(id_x != -1 && id_y != -1 && id_z != -1 && id_temps != -1 && id_virage != -1) {
                    // Parcours de la feuille
                    for (int i = 1; i < nbRows; i++) {
                        // Récupération du contenu de chaque column pour une ligne donnée
                        Double temps = Double.valueOf(sheet.getCell(id_temps, i).getContents());
                        Double x = Double.valueOf(sheet.getCell(id_x, i).getContents());
                        Double y = Double.valueOf(sheet.getCell(id_y, i).getContents());
                        Double z = Double.valueOf(sheet.getCell(id_z, i).getContents());

                        // Conversion du virage en boolean
                        boolean virage = false;
                        if(sheet.getCell(id_virage, i).getContents().compareToIgnoreCase("true") == 0
                                || sheet.getCell(id_virage, i).getContents().compareToIgnoreCase("vrai") == 0) {
                            virage = true;
                        }

                        // Ajout d'un noyau de données à la liste
                        PointDonnee point = new PointDonnee(x, y, z, temps, virage);
                        donnees.add(point);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (BiffException e) {
            e.printStackTrace();
        }
        finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public ArrayList<PointDonnee> getDonnees() {
        return donnees;
    }

    public void setDonnees(ArrayList<PointDonnee> donnees) {
        this.donnees = donnees;
    }

    private boolean identifiantX(String cell) {
        return cell.compareToIgnoreCase("x") == 0;
    }

    private int identifiantXMultiCell(String strC1, String strC2, String strC3, String strC4, String strC5) {
        if(identifiantX(strC1)) {
            return 0;
        }
        else if (identifiantX(strC2)) {
            return 1;
        }
        else if (identifiantX(strC3)) {
            return 2;
        }
        else if (identifiantX(strC4)) {
            return 3;
        }
        else if (identifiantX(strC5)) {
            return 4;
        }
        else {
            return -1;
        }
    }

    private boolean identifiantY(String cell) {
        return cell.compareToIgnoreCase("y") == 0;
    }

    private int identifiantYMultiCell(String strC1, String strC2, String strC3, String strC4, String strC5) {
        if(identifiantY(strC1)) {
            return 0;
        }
        else if (identifiantY(strC2)) {
            return 1;
        }
        else if (identifiantY(strC3)) {
            return 2;
        }
        else if (identifiantY(strC4)) {
            return 3;
        }
        else if (identifiantY(strC5)) {
            return 4;
        }
        else {
            return -1;
        }
    }

    private boolean identifiantZ(String cell) {
        return cell.compareToIgnoreCase("z") == 0;
    }

    private int identifiantZMultiCell(String strC1, String strC2, String strC3, String strC4, String strC5) {
        if(identifiantZ(strC1)) {
            return 0;
        }
        else if (identifiantZ(strC2)) {
            return 1;
        }
        else if (identifiantZ(strC3)) {
            return 2;
        }
        else if (identifiantZ(strC4)) {
            return 3;
        }
        else if (identifiantZ(strC5)) {
            return 4;
        }
        else {
            return -1;
        }
    }

    private boolean identifiantTemps(String cell) {
        return cell.compareToIgnoreCase("temps") == 0;
    }

    private int identifiantTempsMultiCell(String strC1, String strC2, String strC3, String strC4, String strC5) {
        if(identifiantTemps(strC1)) {
            return 0;
        }
        else if (identifiantTemps(strC2)) {
            return 1;
        }
        else if (identifiantTemps(strC3)) {
            return 2;
        }
        else if (identifiantTemps(strC4)) {
            return 3;
        }
        else if (identifiantTemps(strC5)) {
            return 4;
        }
        else {
            return -1;
        }
    }

    private boolean identifiantVirage(String cell) {
        return cell.compareToIgnoreCase("virage") == 0;
    }

    private int identifiantVirageMultiCell(String strC1, String strC2, String strC3, String strC4, String strC5) {
        if(identifiantVirage(strC1)) {
            return 0;
        }
        else if (identifiantVirage(strC2)) {
            return 1;
        }
        else if (identifiantVirage(strC3)) {
            return 2;
        }
        else if (identifiantVirage(strC4)) {
            return 3;
        }
        else if (identifiantVirage(strC5)) {
            return 4;
        }
        else {
            return -1;
        }
    }

}
