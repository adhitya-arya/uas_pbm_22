package com.example.firestore.model;

public class Mahasiswa {

    private String id, namaMatkul, namaHari, namaKelas, jamKelas;
    public Mahasiswa(){

    }

    public Mahasiswa(String namaMatkul, String namaHari, String namaKelas, String jamKelas) {
        this.namaMatkul = namaMatkul;
        this.namaHari = namaHari;
        this.namaKelas = namaKelas;
        this.jamKelas = jamKelas;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getNamaMatkul() {

        return namaMatkul;
    }

    public void setNamaMatkul(String namaMatkul) {

        this.namaMatkul = namaMatkul;
    }

    public String getNamaHari() {

        return namaHari;
    }

    public void setNamaHari(String namaHari) {

        this.namaHari = namaHari;
    }

    public String getNamaKelas() {

        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {

        this.namaKelas = namaKelas;
    }

    public String getJamKelas() {

        return jamKelas;
    }

    public void setJamKelas(String jamKelas) {

        this.jamKelas = jamKelas;
    }

}
