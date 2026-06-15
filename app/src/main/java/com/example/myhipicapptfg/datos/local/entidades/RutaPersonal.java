package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa una ruta personalizada realizada por un usuario.
 *
 * Una ruta contiene información general del recorrido:
 * nombre, fecha, duración, distancia y su propietario.
 */
@Entity(
        tableName = "RutaPersonal",
        /**
         * Relación con Usuario:
         * Cada ruta pertenece a un usuario (propietario).
         *
         * Si se elimina el usuario, se eliminan todas sus rutas (CASCADE).
        */
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Propietario",
                onDelete = ForeignKey.CASCADE
        )
)
public class RutaPersonal {


    /**
     * ID único de la ruta (clave primaria).
     * Se genera automáticamente.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Ruta_Personal")
    public int idRutaPersonal;

    /**
     * Nombre descriptivo de la ruta.
     */
    @ColumnInfo(name = "Nombre")
    public String nombre;

    /**
     * Duración total de la ruta.
     * Formato recomendado: HH:MM
     */
    @ColumnInfo(name = "Duracion")
    public String duracion;

    /**
     * Hora en la que se realizó la ruta.
     * Formato: HH:MM
     */
    @ColumnInfo(name = "Hora")
    public String hora;

    /**
     * Fecha en la que se realizó la ruta.
     * Formato: YYYY-MM-DD
     */
    @ColumnInfo(name = "Fecha")
    public String fecha;

    /**
     * Distancia total recorrida en la ruta (en kilómetros).
     */
    @ColumnInfo(name = "Distancia_Recorrida")
    public double distanciaRecorrida;

    /**
     * ID del usuario propietario de la ruta.
     * Relación con la tabla Usuario.
     */
    @ColumnInfo(name = "ID_Propietario")
    public int idPropietario;
}
