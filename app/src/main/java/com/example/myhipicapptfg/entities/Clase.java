package com.example.myhipicapptfg.entities;



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
                        entity = Disciplina.class,
                        parentColumns = "ID_Disciplina",
                        childColumns = "ID_Disciplina",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Profesor.class,
                        parentColumns = "ID_Profesor",  // coincide con Usuario
                        childColumns = "ID_Profesor",  // coincide con columna en Clase
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Clase {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Clase")
    public int idClase;

    @ColumnInfo(name = "Nivel_Recomendado")
    public String nivelRecomendado;

    @ColumnInfo(name = "Hora_Fin")
    public String horaFin;

    @ColumnInfo(name = "Hora_Inicio")
    public String horaInicio;

    @ColumnInfo(name = "Fecha")
    public String fecha;

    @ColumnInfo(name = "ID_Pista")
    public int idPista;

    @ColumnInfo(name = "ID_Disciplina")
    public int idDisciplina;

    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

    // Constantes
    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";
}
