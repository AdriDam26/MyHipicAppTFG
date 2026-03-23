package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cuadra", indices = {@Index(value = {"Numero_Cuadra"}, unique = true)})
public class Cuadra {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Cuadra")
    public int idCuadra;

    @ColumnInfo(name = "Numero_Cuadra")
    public int numeroCuadra;
}
