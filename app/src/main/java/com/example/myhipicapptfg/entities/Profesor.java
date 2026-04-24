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


    @ColumnInfo(name = "Nivel_Maximo_Doma")
    public String nivelMaximoDoma;

    @ColumnInfo(name = "Puede_Dar_Salto")
    public boolean puedeDarSalto;

    @ColumnInfo(name = "Nivel_Maximo_Salto")
    public String nivelMaximoSalto;

    @ColumnInfo(name = "Anios_Experiencia")
    public int aniosExperiencia;

    @ColumnInfo(name = "Activo")
    public boolean activo;

    public Profesor() {}


    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";

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
