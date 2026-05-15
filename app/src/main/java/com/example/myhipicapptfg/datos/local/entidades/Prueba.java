package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Prueba",
        foreignKeys = {
                @ForeignKey(
                        entity = Competicion.class,
                        parentColumns = "ID_Competicion",
                        childColumns = "ID_Competicion",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Juez.class,
                        parentColumns = "ID_Juez",
                        childColumns = "ID_Juez",
                        // SET_NULL porque si borras el juez
                        // la prueba no debería desaparecer
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(value = {"ID_Competicion"}),
                @Index(value = {"ID_Juez"})
        }
)
public class Prueba {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    // 🧑 Categoría del jinete
    @ColumnInfo(name = "Categoria")
    public String categoria;

    // 📊 Nivel técnico de la prueba
    @ColumnInfo(name = "Nivel")
    public String nivel;

    @ColumnInfo(name = "ID_Competicion")
    public int idCompeticion;

    @ColumnInfo(name = "ID_Juez")
    public Integer idJuez;

    @ColumnInfo(name = "Publicado")
    public boolean publicado = false;

    // 🔹 Categorías RFHE (simplificadas)
    public static final String ALEVIN = "Alevin";
    public static final String INFANTIL = "Infantil";
    public static final String JUVENIL_0 = "Juvenil 0*";
    public static final String JUVENIL_1 = "Juvenil 1*";
    public static final String JOVEN_JINETE = "Joven Jinete";
    public static final String U25 = "U25";
    public static final String ADULTO = "Adulto";

    // 🔹 Niveles técnicos de doma
    public static final String NIVEL_0 = "Nivel 0";
    public static final String NIVEL_1 = "Nivel 1";
    public static final String NIVEL_2 = "Nivel 2";
    public static final String NIVEL_3 = "Nivel 3";
    public static final String NIVEL_4 = "Nivel 4";
    public static final String SAN_JORGE = "San Jorge";
    public static final String INTERMEDIA = "Intermedia";
    public static final String GRAN_PREMIO = "Gran Premio";

    // Constructor vacío (Room)
    public Prueba() {}

    // Constructor recomendado
    public Prueba(String nombre, String categoria, String nivel,
                  int idCompeticion, Integer idJuez) {
        this.nombre        = nombre;
        this.categoria     = categoria;
        this.nivel         = nivel;
        this.idCompeticion = idCompeticion;
        this.idJuez        = idJuez;
    }
}