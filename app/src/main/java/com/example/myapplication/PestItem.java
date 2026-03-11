package com.example.myapplication;

public class PestItem {
    private String name;
    private String gambar ;
    private  String penanganan;


    public PestItem (String name){
        this.name = name;
        this.gambar = gambar;
        this.penanganan = penanganan;


    }

    public String getName() {
        return name;
    }

    public String getGambar() {return gambar;}

    public String getPenanganan() {return penanganan;}

    public void setName(String name){
        this.name = name;
    }
}
