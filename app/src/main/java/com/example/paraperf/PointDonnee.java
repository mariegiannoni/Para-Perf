package com.example.paraperf;

public class PointDonnee {

    private Double x;
    private Double y;
    private Double z;
    private Double temps;
    private boolean virage;

    public PointDonnee() {}

    public PointDonnee(Double x, Double y, Double z, Double temps, boolean virage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.temps = temps;
        this.virage = virage;
    }

    public PointDonnee(Double x, Double y, Double temps, boolean virage) {
        this.x = x;
        this.y = y;
        this.z = 0.0;
        this.temps = temps;
        this.virage = virage;
    }

    /* GETTER */

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getTemps() {
        return temps;
    }

    public boolean isVirage() {
        return virage;
    }

    /* SETTER */

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setTemps(double temps) {
        this.temps = temps;
    }

    public void setVirage(boolean virage) {
        this.virage = virage;
    }
}
