package com.example.myapplication;

import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class HistoryItem {
    private String name;
    private String subtext;
    private Date timestamp;
    private List<Map<String, String>> diagnosisList;  // Menyimpan data diagnosis (gejala dan keyakinan)
    private String gambar;
    private String penanganan;

    // Constructor
    public HistoryItem(String name, String subtext, Date timestamp, List<Map<String, String>> diagnosisList, String gambar, String penanganan) {
        this.name = name;
        this.subtext = subtext;
        this.timestamp = timestamp;
        this.diagnosisList = diagnosisList;
        this.gambar = gambar;
        this.penanganan = penanganan;
    }

    // Getter dan Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<Map<String, String>> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(List<Map<String, String>> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getPenanganan() {
        return penanganan;
    }

    public void setPenanganan(String penanganan) {
        this.penanganan = penanganan;
    }
}
