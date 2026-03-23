package com.example.myhipicapptfg.entities;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Equino",
        foreignKeys = {
                @ForeignKey(
                        entity = Propietario.class,
                        parentColumns = "ID_Propietario",
                        childColumns = "ID_Propietario",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Cuadra.class,
                        parentColumns = "ID_Cuadra",
                        childColumns = "ID_Cuadra",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"Numero_Microchip"}, unique = true)}
)
public class Equino {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Raza")
    public String raza;

    @ColumnInfo(name = "Fecha_Nacimiento")
    public String fechaNacimiento; // TEXT 'YYYY-MM-DD'

    @ColumnInfo(name = "Sexo")
    public String sexo; // 'M' o 'H'

    @ColumnInfo(name = "Altura")
    public double altura; // en metros

    @ColumnInfo(name = "Peso")
    public double peso; // en Kg

    @ColumnInfo(name = "Temperamento")
    public String temperamento;

    @ColumnInfo(name = "Estado_Salud")
    public String estadoSalud;

    @ColumnInfo(name = "Numero_Microchip")
    public String numeroMicrochip;

    @ColumnInfo(name = "ID_Propietario")
    public Integer idPropietario; // opcional

    @ColumnInfo(name = "ID_Cuadra")
    public int idCuadra;


    // Constantes Sexo
    public static final String SEXO_MACHO = "M";
    public static final String SEXO_HEMBRA = "H";

    // Constantes Temperamento
    public static final String FACIL = "Fácil";
    public static final String MANEJABLE = "Manejable";
    public static final String DIFICIL = "Dificil";

    // Constantes Estado Salud
    public static final String BUENO = "Bueno";
    public static final String REGULAR = "Regular";
    public static final String MALO = "Malo";
}
