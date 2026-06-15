package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Equino;

import java.util.List;


/**
 *
 * Esta interfaz define todas las operaciones de acceso a datos sobre
 * la tabla "Equino" utilizando Room como capa de persistencia.
 *
 * Incluye operaciones CRUD básicas (crear, leer, actualizar y eliminar),
 * consultas para la interfaz de usuario mediante LiveData y métodos
 * síncronos destinados a validaciones y lógica interna del repositorio.
 */

@Dao
public interface EquinoDao {

    /**
     * Inserta un nuevo equino en la base de datos.
     *
     * La estrategia ABORT provoca una excepción si se produce un conflicto,
     * por ejemplo, cuando se intenta insertar un número de microchip que ya existe.
     *
     * @param equino Equino a insertar.
     * @return Identificador generado para el nuevo registro.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarEquino(Equino equino);

    /**
     * Actualiza los datos de un equino existente.
     *
     * Room identifica el registro mediante su clave primaria.
     *
     * @param equino Equino con los datos actualizados.
     * @return Número de filas modificadas.
     */
    @Update
    int actualizarEquino(Equino equino);

    /**
     * Elimina un equino de la base de datos.
     *
     * @param equino Equino que se desea eliminar.
     * @return Número de filas eliminadas.
     */
    @Delete
    int eliminarEquino(Equino equino);

    /**
     * Recupera todos los equinos registrados.
     *
     * Los resultados se ordenan alfabéticamente por nombre y se devuelven
     * mediante LiveData para que la interfaz se actualice automáticamente
     * ante cualquier cambio en la base de datos.
     *
     * @return Lista observable de equinos.
     */
    @Query("SELECT * FROM Equino ORDER BY Nombre ASC")
    LiveData<List<Equino>> obtenerTodosEquinos();

    /**
     * Obtiene un equino a partir de su identificador.
     *
     * Devuelve un objeto LiveData para mantener sincronizada la interfaz.
     *
     * @param id Identificador del equino.
     * @return Equino encontrado o null si no existe.
     */
    @Query("SELECT * FROM Equino WHERE ID_Equino = :id LIMIT 1")
    LiveData<Equino> buscarEquinoPorId(int id);



    // Estos métodos están pensados para ejecutarse en segundo plano desde el repositorio para realizar validaciones de negocio

    /**
     * Comprueba si ya existe un equino con el número de microchip indicado.
     *
     * Se utiliza para garantizar la unicidad del microchip antes de insertar.
     *
     * @param microchip Número de microchip.
     * @return true si existe, false en caso contrario.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE Numero_Microchip = :microchip)")
    boolean existeMicrochip(String microchip);


    /**
     * Obtiene un equino por su identificador de forma síncrona.
     *
     * @param id Identificador del equino.
     * @return Equino encontrado o null.
     */
    @Query("SELECT * FROM Equino WHERE ID_Equino = :id LIMIT 1")
    Equino buscarPorIdSync(int id);

    /**
     * Obtiene un equino a partir de su número de microchip.
     *
     * Este método es utilizado por el sistema de realidad aumentada
     * tras escanear un código QR asociado al caballo.
     *
     * @param microchip Número de microchip.
     * @return Equino encontrado o null.
     */
    @Query("SELECT * FROM Equino WHERE Numero_Microchip = :microchip LIMIT 1")
    Equino buscarPorMicrochipSync(String microchip);


    /**
     * Verifica que el usuario indicado existe y tiene rol de propietario.
     *
     * Se utiliza antes de asociar un caballo a un usuario.
     *
     * @param id Identificador del usuario.
     * @return true si es un propietario válido.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'propietario')")
    boolean esPropietarioValido(int id);


    /**
     * Comprueba si una cuadra ya está ocupada por otro equino distinto.
     *
     * Se utiliza durante la modificación de un caballo para evitar
     * conflictos de asignación.
     *
     * @param numeroCuadra Número de cuadra.
     * @param idEquino Identificador del equino que se está editando.
     * @return true si existe otro caballo ocupando la cuadra.
     */
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Equino " +
            "WHERE Numero_Cuadra = :numeroCuadra " +
            "AND ID_Equino != :idEquino)")
    boolean esCuadraOcupadaPorOtro(int numeroCuadra, int idEquino);

    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE Numero_Cuadra = :numeroCuadra)")
    boolean esCuadraOcupada(int numeroCuadra);

    /**
     * Recupera todos los equinos entrenados en doma.
     *
     * @return Lista observable de caballos con la disciplina de doma.
     */
    @Query("SELECT * FROM Equino WHERE Sabe_Doma = 1")
    LiveData<List<Equino>> obtenerEquinosDoma();

    /**
     * Obtiene el nombre completo de un propietario concatenando
     * nombre y apellidos.
     *
     * El uso de COALESCE evita valores nulos en el segundo apellido.
     *
     * @param idUsuario Identificador del propietario.
     * @return Nombre completo del propietario.
     */
    @Query("SELECT Usuario.Nombre || ' ' || Usuario.Apellido1 || ' ' || COALESCE(Usuario.Apellido2, '') " +
            "FROM Usuario WHERE Usuario.ID_Usuario = :idUsuario LIMIT 1")
    String obtenerNombrePropietarioSync(int idUsuario);
}