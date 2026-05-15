package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Clase",
        foreignKeys = {
                @ForeignKey(
                        entity = Pista.class,
                        parentColumns = "ID_Pista",
                        childColumns = "ID_Pista",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Profesor.class,
                        parentColumns = "ID_Profesor",
                        childColumns = "ID_Profesor",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Clase {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Clase")
    public int idClase;

    @ColumnInfo(name = "Hora_Inicio")
    public long horaInicio;

    @ColumnInfo(name = "Hora_Fin")
    public long horaFin;

    @ColumnInfo(name = "Fecha")
    public long fecha;

    @ColumnInfo(name = "Nivel")
    public String nivel;

    @ColumnInfo(name = "Disciplina")
    public String disciplina;

    @ColumnInfo(name = "ID_Pista")
    public int idPista;

    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

    // 🔹 Disciplina
    public static final String DOMA = "Doma";
    public static final String SALTO = "Salto";


    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";


    public Clase() {}


    public Clase(long horaInicio, long horaFin, long fecha,
                 String nivel, String disciplina,
                 int idPista, int idProfesor) {

        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.fecha = fecha;
        this.nivel = nivel;
        this.disciplina = disciplina;
        this.idPista = idPista;
        this.idProfesor = idProfesor;
    }
}