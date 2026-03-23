package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Reprisa",
        foreignKeys = @ForeignKey(
                entity = PlantillaPrueba.class,
                parentColumns = "ID_Prueba",
                childColumns = "ID_Prueba",
                onDelete = ForeignKey.CASCADE
        )
)
public class Reprisa {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Reprisa")
    public int idReprisa;

    @ColumnInfo(name = "Peso_Ejercicio")
    public double pesoEjercicio; // decimal(5,2) > 0

    @ColumnInfo(name = "Directriz")
    public String directriz; // not null

    @ColumnInfo(name = "Movimiento")
    public String movimiento; // not null

    @ColumnInfo(name = "Letra")
    public String letra; // char(1)

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba; // FK

    @ColumnInfo(name = "Orden")
    public int orden; // >=1
}
