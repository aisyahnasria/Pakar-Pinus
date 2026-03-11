package com.example.myapplication;

import java.util.List;

public class Gejala {
    private String namaGejala;
    private boolean isSelected; // untuk menyimpan apakah gejala dipilih oleh user
    private String keyakinan;
    private double bobot; // tambahan untuk CF
    private double cfUser;

    // Constructor
    public Gejala(String namaGejala) {
        this.namaGejala = namaGejala;
        this.isSelected = false;
        this.keyakinan = null;
    }

    // Constructor dengan bobot
    public Gejala(String namaGejala, double bobot) {
        this.namaGejala = namaGejala;
        this.isSelected = false;
        this.bobot = bobot;
    }

    // Getter dan Setter untuk nama gejala
    public String getNamaGejala() {
        return namaGejala;
    }

    public void setNamaGejala(String namaGejala) {
        this.namaGejala = namaGejala;
    }

    // Getter dan Setter untuk status pemilihan
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Getter dan Setter untuk bobot
    public double getBobot() {
        return bobot;
    }

    public void setBobot(double bobot) {
        this.bobot = bobot;
    }


    public String getKeyakinan() {
        return keyakinan;
    }

    public void setKeyakinan(String keyakinan) {
        this.keyakinan = keyakinan;
    }

    public double getCfUser() {
        return cfUser;
    }

    public void setCfUser(double cfUser) {
        this.cfUser = cfUser;
    }
}

