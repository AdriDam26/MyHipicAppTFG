package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/**
 * Entidad que representa un caballo (equino) dentro de la aplicación
 *
 * Relaciones:
 * -Un equino puede estar asociado a un usuario propietario mediante
 *  *   la clave foránea ID_Usuario.
 * - Si el usuario propietario es eliminado, el campo ID_Usuario se
 *  *   establecerá a null (SET_NULL).
 *
 * Restricciones:
 * - El número de microchip debe ser único para cada equino.
 * - Se crea un índice sobre ID_Usuario para optimizar las búsquedas
 *
 *
 */

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

    /** Identificador único autogenerado del equino. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    /** Nombre del caballo. */
    @ColumnInfo(name = "Nombre")
    public String nombre;

    /** Raza del caballo. */
    @ColumnInfo(name = "Raza")
    public String raza;


    /**
     * Fecha de nacimiento almacenada como timestamp en milisegundos
     */
    @ColumnInfo(name = "Fecha_Nacimiento")
    public long fechaNacimiento;

    /** Sexo del caballo (MACHO o HEMBRA). */
    @ColumnInfo(name = "Sexo")
    public String sexo;

    /** Altura del caballo en metros. */
    @ColumnInfo(name = "Altura")
    public double altura;

    /** Peso del caballo en kilogramos. */
    @ColumnInfo(name = "Peso")
    public double peso;

    /**
     * Temperamento del caballo.
     * Valores:
     * FACIL, MANEJABLE o DIFICIL.
     */
    @ColumnInfo(name = "Temperamento")
    public String temperamento;

    /**
     * Estado general de salud del caballo.
     * Valores:
     * BUENO, REGULAR o MALO.
     */
    @ColumnInfo(name = "Estado_Salud")
    public String estadoSalud;

    /**
     * Número de microchip identificativo.
     * Debe ser único para cada equino.
     */
    @ColumnInfo(name = "Numero_Microchip")
    public String numeroMicrochip;

    /** Indica si el caballo está entrenado para salto. */
    @ColumnInfo(name = "Sabe_Salto")
    public boolean sabeSalto;


    /** Indica si el caballo está entrenado para doma. */
    @ColumnInfo(name = "Sabe_Doma")
    public boolean sabeDoma;

    /**
     * Identificador del propietario asociado.
     * Puede ser null si el caballo no tiene propietario asignado.
     */
    @ColumnInfo(name = "ID_Usuario")
    public Integer idUsuario; // puede ser nulo

    /** Número de cuadra donde se encuentra alojado el caballo. */
    @ColumnInfo(name = "Numero_Cuadra")
    public int numeroCuadra;

    /**
     * Ruta o URI de la imagen de perfil del caballo.
     */
    @ColumnInfo(name = "Foto_Perfil")
    public String fotoPerfil;


    // Constantes de dominio
    public static final String SEXO_MACHO = "MACHO";
    public static final String SEXO_HEMBRA = "HEMBRA";

    public static final String FACIL = "Facil";
    public static final String MANEJABLE = "Manejable";
    public static final String DIFICIL = "Dificil";

    public static final String BUENO = "Bueno";
    public static final String REGULAR = "Regular";
    public static final String MALO = "Malo";

    /**
     * Constructor vacío requerido por Room para
     * la creación automática de objetos.
     */
    public Equino() {}

    /**
     * Constructor principal utilizado para crear
     * nuevas instancias de Equino.
     *
     * @param nombre Nombre del caballo.
     * @param raza Raza del caballo.
     * @param fechaNacimiento Fecha de nacimiento en milisegundos.
     * @param sexo Sexo del caballo.
     * @param altura Altura en metros.
     * @param peso Peso en kilogramos.
     * @param temperamento Temperamento del caballo.
     * @param estadoSalud Estado de salud.
     * @param numeroMicrochip Número de microchip único.
     * @param sabeSalto Indica si practica salto.
     * @param sabeDoma Indica si practica doma.
     * @param idUsuario Identificador del propietario.
     * @param numeroCuadra Número de cuadra asignado.
     * @param fotoPerfil Ruta o URI de la imagen de perfil.
     */
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

    /**
     * Devuelve el nombre del caballo.
     * Se utiliza principalmente para mostrar el objeto
     * en componentes visuales como Spinner o ListView.
     */
    @Override
    public String toString() {
        return nombre;
    }
}