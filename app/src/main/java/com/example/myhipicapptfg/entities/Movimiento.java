package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Movimiento")
public class Movimiento {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    @ColumnInfo(name = "Ejercicio")
    public String ejercicio;

    @ColumnInfo(name = "Letra")
    public String letra;

    @ColumnInfo(name = "Orden")
    public int orden;

    @ColumnInfo(name = "Coeficiente")
    public double coeficiente;

    @ColumnInfo(name = "Directriz")
    public String directriz;

    // 🔹 Constructor vacío
    public Movimiento() {}

    // 🔹 Constructor recomendado
    public Movimiento(String ejercicio,
                      String letra,
                      int orden,
                      double coeficiente,
                      String directriz) {

        this.ejercicio = ejercicio;
        this.letra = letra;
        this.orden = orden;
        this.coeficiente = coeficiente;
        this.directriz = directriz;
    }
}