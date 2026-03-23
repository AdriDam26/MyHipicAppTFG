package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "PlantillaPrueba",
        foreignKeys = @ForeignKey(
                entity = Disciplina.class,
                parentColumns = "ID_Disciplina",
                childColumns = "ID_Disciplina",
                onDelete = CASCADE
        ),
        indices = {@Index(value = "ID_Disciplina")}
)
public class PlantillaPrueba {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    // Clave foránea hacia Disciplina
    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;

}
