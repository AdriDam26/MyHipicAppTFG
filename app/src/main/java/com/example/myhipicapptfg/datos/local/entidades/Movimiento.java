package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/**
 * Entidad Movimiento para la base de datos Room.
 *
 * Representa un ejercicio o movimiento dentro de una prueba ecuestre.
 * Cada movimiento pertenece a una única prueba.
 */
@Entity(
        tableName = "Movimiento",
        foreignKeys = @ForeignKey(
                entity = Prueba.class,
                parentColumns = "ID_Prueba",
                childColumns = "ID_Prueba",
                onDelete = ForeignKey.CASCADE // Si borras la prueba, se borran sus movimientos
        ),
        indices = {@Index(value = {"ID_Prueba"})} // Índice para mejorar la velocidad de búsqueda
)
public class Movimiento {

    /**
     * Identificador único del movimiento (clave primaria autogenerada).
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    // Atributos
    @ColumnInfo(name = "Ejercicio")
    public String ejercicio;

    /**
     * Letra o tramo del ejercicio dentro del recorrido.
     * Ejemplo: "A-C"
     */
    @ColumnInfo(name = "Letra")
    public String letra;

    /**
     * Orden del movimiento dentro de la prueba.
     * Importante para mostrar la secuencia (1, 2, 3...).
     */
    @ColumnInfo(name = "Orden")
    public int orden;

    /**
     * Coeficiente de puntuación del movimiento.
     * Normalmente 1 o 2, afecta al cálculo final.
     */
    @ColumnInfo(name = "Coeficiente")
    public double coeficiente;

    /**
     * Directriz o indicación para el juez.
     */
    @ColumnInfo(name = "Directriz")
    public String directriz;

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    /**
     * Constructor vacío requerido por Room.
     */
    public Movimiento() {}

    /**
     * Constructor para crear movimientos desde la interfaz de administración.
     */
    public Movimiento(String ejercicio, String letra, int orden,
                      double coeficiente, String directriz, int idPrueba) {
        this.ejercicio   = ejercicio;
        this.letra       = letra;
        this.orden       = orden;
        this.coeficiente = coeficiente;
        this.directriz   = directriz;
        this.idPrueba    = idPrueba;
    }
}