package com.example.paraperf;

/*
    Cette classe contient toutes les information d'un item
    Son nom, les données qu'il contient, la date de dernière modification, le chemin absolue et une icon
    On affichera toutes ses données représentative d'un fichier ou d'un répertoire dans notre explorateur de fichier
 */

public class Item implements Comparable<Item> {
    private String name;
    private String data;
    private String date;
    private String path;
    private String icon;

    public Item(String name, String data, String date, String path, String icon) {
        this.name = name;
        this.data = data;
        this.date = date;
        this.path = path;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public String getIcon() {
        return icon;
    }

    public int compareTo(Item item) {
        if(this.name != null) {
            return this.name.toLowerCase().compareTo(item.getName().toLowerCase());
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
