package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/**
 * Entidad Competicion para la base de datos Room.
 *
 * Representa un evento o competición hípica dentro del sistema.
 */
@Entity(
        tableName = "Competicion",
        indices = {
                @Index(value = {"Nombre"}, unique = true)
        }
)
public class Competicion {

    /**
     * Identificador único de la competición (clave primaria autogenerada).
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Competicion")
    public int idCompeticion;

    // Atributos
    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Fecha")
    public long fecha;

    /**
     * Constructor vacío requerido por Room.
     */
    public Competicion() {}


    /**
     * Constructor recomendado para crear una competición.
     */
    public Competicion(String nombre, long fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
    }
}