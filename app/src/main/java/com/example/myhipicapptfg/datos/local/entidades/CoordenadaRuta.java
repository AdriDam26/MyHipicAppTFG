package com.example.myhipicapptfg.datos.local.entidades;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un punto GPS dentro de una ruta personal.
 *
 * Cada fila de esta tabla es una coordenada (latitud, longitud, altitud)
 * que forma parte de un recorrido más grande.
 *
 * Se relaciona con la tabla RutaPersonal mediante una clave foránea.
 */

@Entity(
        tableName = "CoordenadaRuta",
        /**
         * Clave foránea:
         * Cada coordenada pertenece a una única ruta personal.
         *
         * Si se elimina una ruta (RutaPersonal),
         * automáticamente se eliminan todas sus coordenadas (CASCADE).
        */
        foreignKeys = @ForeignKey(
                entity = RutaPersonal.class,
                parentColumns = "ID_Ruta_Personal",
                childColumns = "ID_Ruta_Personal",
                onDelete = ForeignKey.CASCADE
        )
)
public class CoordenadaRuta {

    /**
     * ID único de cada coordenada (clave primaria).
     * Se genera automáticamente.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Coordenada")
    public int idCordenada;

    /**
     * Orden del punto dentro de la ruta.
     *
     * IMPORTANTE:
     * Permite reconstruir la ruta en el orden correcto
     */
    @ColumnInfo(name = "Orden")
    public int orden;

    /**
     * Coordenada de latitud (posición norte-sur).
     */
    @ColumnInfo(name = "Latitud")
    public double latitud;

    /**
     * Coordenada de longitud (posición este-oeste).
     */
    @ColumnInfo(name = "Longitud")
    public double longitud;

    /**
     * Altitud del punto GPS (metros sobre el nivel del mar).
     */
    @ColumnInfo(name = "Altitud")
    public double altitud;

    /**
     * Marca de tiempo del momento en el que se registró el punto.
     * Se guarda normalmente en milisegundos (System.currentTimeMillis()).
     */
    @ColumnInfo(name = "Marca_Tiempo")
    public long marcaTiempo;

    /**
     * ID de la ruta a la que pertenece este punto GPS.
     *
     * Es la clave foránea que conecta con RutaPersonal.
     */
    @ColumnInfo(name = "ID_Ruta_Personal")
    public int idRutaPersonal;
}