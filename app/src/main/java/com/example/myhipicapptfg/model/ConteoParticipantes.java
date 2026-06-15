package com.example.myhipicapptfg.model;

import androidx.room.ColumnInfo;

public class ConteoParticipantes {
    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    @ColumnInfo(name = "total")
    public int total;
}