package com.example.myhipicapptfg.datos.local.entidades;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Pista para la base de datos Room.
 * Representa una pista de equitación dentro del sistema.
 */
@Entity(
        tableName = "Pista",
        indices = {@Index(value = {"Nombre"}, unique = true)}
)
public class Pista {

    /**
     * Identificador único de la pista (clave primaria autogenerada).
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Pista")
    public int idPista;

    // Atributos
    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Ancho")
    public double ancho;

    @ColumnInfo(name = "Largo")
    public double largo;


    /**
     * Representación en texto de la pista (se muestra su nombre).
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Constructor vacío requerido por Room.
     */
    public Pista() {
    }

    /**
     * Constructor para crear una pista con sus dimensiones básicas.
     */
    public Pista(String nombre, double ancho, double largo) {
        this.nombre = nombre;
        this.ancho = ancho;
        this.largo = largo;
    }



}
