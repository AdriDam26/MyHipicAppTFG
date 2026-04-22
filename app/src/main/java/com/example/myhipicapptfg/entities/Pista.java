package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Pista",
        indices = {@Index(value = {"Nombre"}, unique = true)} // ⚡ índice único en Nombre
)
public class Pista {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Pista")
    public int idPista;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Ancho")
    public double ancho;

    @ColumnInfo(name = "Largo")
    public double largo;


    @Override
    public String toString() {
        return nombre;
    }

    public Pista() {
        // Constructor vacío obligatorio para Room
    }

    public Pista(String nombre, double ancho, double largo) {
        this.nombre = nombre;
        this.ancho = ancho;
        this.largo = largo;
    }



}
