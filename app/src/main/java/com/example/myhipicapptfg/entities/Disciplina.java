package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Disciplina",
        indices = {@Index(value = {"Nombre"}, unique = true)}
)
public class Disciplina {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;

    @ColumnInfo(name = "Nombre")
    public String nombre;

}
