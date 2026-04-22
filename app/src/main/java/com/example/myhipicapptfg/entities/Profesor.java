package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Profesor",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Profesor",
                onDelete = ForeignKey.CASCADE
        )
)
public class Profesor {

    @PrimaryKey
    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

    @ColumnInfo(name = "Puede_Dar_Doma")
    public boolean puedeDarDoma;

    @ColumnInfo(name = "Puede_Dar_Salto")
    public boolean puedeDarSalto;

    @ColumnInfo(name = "Anios_Experiencia")
    public int aniosExperiencia;

    public Profesor() {}

    public Profesor(int idProfesor,
                    boolean puedeDarDoma,
                    boolean puedeDarSalto,
                    int aniosExperiencia) {

        this.idProfesor = idProfesor;
        this.puedeDarDoma = puedeDarDoma;
        this.puedeDarSalto = puedeDarSalto;
        this.aniosExperiencia = aniosExperiencia;
    }
}
