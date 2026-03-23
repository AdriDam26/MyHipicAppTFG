package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "ProfesorDisciplina",
        primaryKeys = {"ID_Profesor", "ID_Disciplina"},
        foreignKeys = {
                @ForeignKey(
                        entity = Profesor.class,
                        parentColumns = "ID_Profesor",  // coincide con @ColumnInfo de Profesor
                        childColumns = "ID_Profesor",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Disciplina.class,
                        parentColumns = "ID_Disciplina", // coincide con @ColumnInfo de Disciplina
                        childColumns = "ID_Disciplina",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ProfesorDisciplina {

    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;
}
