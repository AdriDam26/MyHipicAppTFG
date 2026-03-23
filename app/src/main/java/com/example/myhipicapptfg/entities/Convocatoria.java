package com.example.myhipicapptfg.entities;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Convocatoria",
        foreignKeys = {
                @ForeignKey(
                        entity = PlantillaPrueba.class,
                        parentColumns = "ID_Prueba",
                        childColumns = "ID_Prueba",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Competicion.class,
                        parentColumns = "ID_Competicion",
                        childColumns = "ID_Competicion",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Convocatoria {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Convocatoria")
    public int idConvocatoria;

    @ColumnInfo(name = "Categoria")
    public String categoria;

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    @ColumnInfo(name = "ID_Competicion")
    public int idCompeticion;

    // Constantes categorías
    public static final String POTROS = "Potros";
    public static final String ALEVIN = "Alevin";
    public static final String INFANTIL = "Infantil";
    public static final String JUVENIL = "Juvenil";
    public static final String JUNIOR = "Junior";
    public static final String SENIOR = "Senior";
}
