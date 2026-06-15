package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad Profesor para la base de datos Room.
 * Representa información adicional de un usuario que es profesor.
 *
 * Relación:
 * - Cada Profesor está asociado a un Usuario (1 a 1)
 * - Si se elimina el Usuario, se elimina el Profesor (CASCADE)
 */
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

    /**
     * Identificador del profesor.
     * También actúa como clave foránea hacia Usuario.
     */
    @PrimaryKey
    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

    // Atributos
    @ColumnInfo(name = "Puede_Dar_Doma")
    public boolean puedeDarDoma;


    @ColumnInfo(name = "Nivel_Maximo_Doma")
    public String nivelMaximoDoma;

    @ColumnInfo(name = "Puede_Dar_Salto")
    public boolean puedeDarSalto;

    @ColumnInfo(name = "Nivel_Maximo_Salto")
    public String nivelMaximoSalto;

    // Información profesional
    @ColumnInfo(name = "Anios_Experiencia")
    public int aniosExperiencia;

    @ColumnInfo(name = "Activo")
    public boolean activo;

    /**
     * Constructor vacío requerido por Room.
     */

    public Profesor() {}

    // Constantes niveles
    public static final String PRINCIPIANTE = "Principiante";
    public static final String INTERMEDIO = "Intermedio";
    public static final String AVANZADO = "Avanzado";

    /**
     * Constructor básico para crear un profesor con los datos principales.
     */
    public Profesor(int idProfesor,
                    boolean puedeDarDoma,
                    boolean puedeDarSalto,
                    int aniosExperiencia) {

        this.idProfesor = idProfesor;
        this.puedeDarDoma = puedeDarDoma;
        this.puedeDarSalto = puedeDarSalto;
        this.aniosExperiencia = aniosExperiencia;
        this.activo = true;
    }
}
