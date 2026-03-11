package com.example.myapplication;

import java.util.List;

public class Rule {
    private List<Gejala> premises; // Daftar gejala dalam rule
    private String conclusion; // Kesimpulan penyakit atau hama
    private String penanganan;
    private String gambarUrl;

    public Rule(List<Gejala> premises, String conclusion, String penanganan, String gambarUrl) {
        this.premises = premises;
        this.conclusion = conclusion;
        this.penanganan = penanganan;
        this.gambarUrl = gambarUrl;
    }

    // Getter untuk daftar gejala (premises)
    public List<Gejala> getPremises() {
        return premises;
    }

    // Getter untuk kesimpulan
    public String getConclusion() {
        return conclusion;
    }

    public String getPenanganan() {
        return penanganan;
    }

    public void setPenanganan(String penanganan) {
        this.penanganan = penanganan;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }
}
