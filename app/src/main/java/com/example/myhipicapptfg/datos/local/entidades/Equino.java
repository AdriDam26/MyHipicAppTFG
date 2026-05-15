package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Equino",
        foreignKeys = {
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "ID_Usuario",
                        childColumns = "ID_Usuario",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(value = {"Numero_Microchip"}, unique = true),
                @Index(value = {"ID_Usuario"})
        }
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
    public long fechaNacimiento;

    @ColumnInfo(name = "Sexo")
    public String sexo;

    @ColumnInfo(name = "Altura")
    public double altura;

    @ColumnInfo(name = "Peso")
    public double peso;

    @ColumnInfo(name = "Temperamento")
    public String temperamento;

    @ColumnInfo(name = "Estado_Salud")
    public String estadoSalud;

    @ColumnInfo(name = "Numero_Microchip")
    public String numeroMicrochip;

    @ColumnInfo(name = "Sabe_Salto")
    public boolean sabeSalto;

    @ColumnInfo(name = "Sabe_Doma")
    public boolean sabeDoma;

    @ColumnInfo(name = "ID_Usuario")
    public Integer idUsuario; // puede ser nulo

    @ColumnInfo(name = "Numero_Cuadra")
    public int numeroCuadra;

    @ColumnInfo(name = "Foto_Perfil")
    public String fotoPerfil;

    // 🔹 Constantes
    public static final String SEXO_MACHO = "MACHO";
    public static final String SEXO_HEMBRA = "HEMBRA";

    public static final String FACIL = "Facil";
    public static final String MANEJABLE = "Manejable";
    public static final String DIFICIL = "Dificil";

    public static final String BUENO = "Bueno";
    public static final String REGULAR = "Regular";
    public static final String MALO = "Malo";

    // 🔹 Constructor vacío (Room)
    public Equino() {}

    // 🔹 Constructor recomendado
    public Equino(String nombre,
                  String raza,
                  long fechaNacimiento,
                  String sexo,
                  double altura,
                  double peso,
                  String temperamento,
                  String estadoSalud,
                  String numeroMicrochip,
                  boolean sabeSalto,
                  boolean sabeDoma,
                  Integer idUsuario,
                  int numeroCuadra,
                  String fotoPerfil) {

        this.nombre = nombre;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.altura = altura;
        this.peso = peso;
        this.temperamento = temperamento;
        this.estadoSalud = estadoSalud;
        this.numeroMicrochip = numeroMicrochip;
        this.sabeSalto = sabeSalto;
        this.sabeDoma = sabeDoma;
        this.idUsuario = idUsuario;
        this.numeroCuadra = numeroCuadra;
        this.fotoPerfil = fotoPerfil;
    }

    @Override
    public String toString() {
        return nombre;
    }
}