package com.example.myhipicapptfg.datos.local.entidades;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad Alumno para la base de datos Room.
 *
 * Representa a un usuario con rol de alumno dentro del sistema hípico.
 * Está vinculado a la entidad Usuario (relación 1 a 1).
 */
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

    /**
     * Identificador del alumno.
     * Coincide con el ID del usuario asociado.
     */
    @PrimaryKey
    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    // Atributos
    @ColumnInfo(name = "Practica_Doma")
    public boolean practicaDoma;

    @ColumnInfo(name = "Practica_Salto")
    public boolean practicaSalto;

    @ColumnInfo(name = "Nivel_Doma")
    public String nivelDoma;

    @ColumnInfo(name = "Nivel_Salto")
    public String nivelSalto;

    // Constante de nivel
    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";


    /**
     * Constructor vacío requerido por Room.
     */
    public Alumno() {}


    /**
     * Constructor vacío requerido por Room.
     */
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

