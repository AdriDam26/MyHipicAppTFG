package com.example.myhipicapptfg.datos.local.entidades;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Alumno",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Alumno",
                onDelete = ForeignKey.CASCADE
        )
)
public class Alumno {

    @PrimaryKey
    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "Practica_Doma")
    public boolean practicaDoma;

    @ColumnInfo(name = "Practica_Salto")
    public boolean practicaSalto;

    @ColumnInfo(name = "Nivel_Doma")
    public String nivelDoma;

    @ColumnInfo(name = "Nivel_Salto")
    public String nivelSalto;

    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";

    public Alumno() {}

    public Alumno(int idAlumno,
                  boolean practicaDoma,
                  boolean practicaSalto,
                  String nivelDoma,
                  String nivelSalto) {

        this.idAlumno = idAlumno;
        this.practicaDoma = practicaDoma;
        this.practicaSalto = practicaSalto;
        this.nivelDoma = nivelDoma;
        this.nivelSalto = nivelSalto;
    }


}

