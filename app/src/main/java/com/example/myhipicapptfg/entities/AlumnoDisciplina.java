package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "AlumnoDisciplina",
        primaryKeys = {"ID_Alumno", "ID_Disciplina"},
        foreignKeys = {
                @ForeignKey(
                        entity = Alumno.class,
                        parentColumns = "ID_Alumno",
                        childColumns = "ID_Alumno",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Disciplina.class,
                        parentColumns = "ID_Disciplina",
                        childColumns = "ID_Disciplina",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class AlumnoDisciplina {

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;

    @ColumnInfo(name = "Nivel")
    public String nivel;

    // Constantes de Nivel
    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";
}