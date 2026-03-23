package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Competicion",
        foreignKeys = @ForeignKey(
                entity = Disciplina.class,
                parentColumns = "ID_Disciplina",
                childColumns = "ID_Disciplina",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"Nombre"}, unique = true)}
)
public class Competicion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Competicion")
    public int idCompeticion;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Fecha")
    public String fecha; // TEXT 'YYYY-MM-DD'

    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;
}
