package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    @ColumnInfo(name = "Ejercicio")
    public String ejercicio; // Ej: "Trote de trabajo"

    @ColumnInfo(name = "Letra")
    public String letra; // Ej: "A-C"

    @ColumnInfo(name = "Orden")
    public int orden; // Importante para el ranking y la vista del juez (1, 2, 3...)

    @ColumnInfo(name = "Coeficiente")
    public double coeficiente; // Normalmente 1 o 2

    @ColumnInfo(name = "Directriz")
    public String directriz; // Notas de ayuda para el juez

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba; // Relación con la Prueba

    // 🔹 Constructor vacío para Room
    public Movimiento() {}

    // 🔹 Constructor para usar en tu Panel de Admin
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