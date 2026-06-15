package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entidad Juez para la base de datos Room.
 *
 * Representa a un usuario con rol de juez dentro del sistema hípico.
 * Está vinculado a la entidad Usuario (relación 1 a 1).
 */
@Entity(
        tableName = "Juez",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Juez",
                onDelete = ForeignKey.CASCADE
        )
)
public class Juez {

    /**
     * Identificador del juez.
     * Coincide con el ID del usuario asociado.
     */
    @PrimaryKey
    @ColumnInfo(name = "ID_Juez")
    public int idJuez;

    // Atributos
    @ColumnInfo(name = "Numero_Licencia")
    public String numeroLicencia;

    @ColumnInfo(name = "Federacion")
    public String federacion;


    @ColumnInfo(name = "Activo")
    public boolean activo;

    // Constantes federaciones posibles
    public static final String ESPAÑOLA = "Real Federación Hípica Española (RFHE)";
    public static final String FRANCESA = "Fédération Française d'Équitation (FFE)";
    public static final String BRITANICA = "British Equestrian (BEF)";
    public static final String INTERNACIONAL = "Fédération Equestre Internationale (FEI)";

    /**
     * Constructor vacío requerido por Room.
     */
    public Juez() {}

    /**
     * Constructor básico para crear un juez.
     */
    public Juez(int idJuez, String numeroLicencia, String federacion) {
        this.idJuez = idJuez;
        this.numeroLicencia = numeroLicencia;
        this.federacion = federacion;
    }
}