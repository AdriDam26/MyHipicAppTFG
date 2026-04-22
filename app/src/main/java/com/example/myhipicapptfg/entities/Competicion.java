package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Competicion",
        indices = {
                @Index(value = {"Nombre"}, unique = true)
        }
)
public class Competicion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Competicion")
    public int idCompeticion;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Fecha")
    public long fecha;

    // 🔹 Constructor vacío (Room)
    public Competicion() {}

    // 🔹 Constructor recomendado
    public Competicion(String nombre, long fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
    }
}