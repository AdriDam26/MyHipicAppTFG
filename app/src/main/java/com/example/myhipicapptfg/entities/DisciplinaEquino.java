package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "Disciplina_Equino",
        primaryKeys = {"ID_Equino", "ID_Disciplina"},
        foreignKeys = {
                @ForeignKey(
                        entity = Equino.class,
                        parentColumns = "ID_Equino",
                        childColumns = "ID_Equino",
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
public class DisciplinaEquino {

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;
}

