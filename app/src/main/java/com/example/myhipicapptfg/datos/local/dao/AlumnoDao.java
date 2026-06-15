package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Alumno;

import java.util.List;


/**
 * DAO (Data Access Object) para la entidad Alumno.
 *
 * Define todas las operaciones de acceso a datos relacionadas
 * con la tabla Alumno de la base de datos Room.
 */
@Dao
public interface AlumnoDao {

    /**
     * Inserta un nuevo alumno en la base de datos.
     *
     * En caso de conflicto (por ejemplo, una clave primaria repetida),
     * la operación se abortará y lanzará una excepción.
     *
     * @param alumno Alumno a insertar.
     * @return ID de la fila insertada.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarAlumno(Alumno alumno);

    /**
     * Actualiza los datos de un alumno existente.
     *
     * @param alumno Alumno con los datos actualizados.
     * @return Número de filas modificadas.
     */
    @Update
    int actualizarAlumno(Alumno alumno);

    /**
     * Elimina un alumno de la base de datos.
     *
     * @param alumno Alumno a eliminar.
     * @return Número de filas eliminadas.
     */
    @Delete
    int eliminarAlumno(Alumno alumno);

    /**
     * Obtiene todos los alumnos almacenados en la base de datos.
     *
     * Se devuelve un LiveData para que la interfaz de usuario
     * pueda observar automáticamente los cambios en los datos.
     *
     * @return Lista observable de alumnos.
     */
    @Query("SELECT * FROM Alumno")
    LiveData<List<Alumno>> obtenerTodosAlumnos();

    /**
     * Busca un alumno por su identificador.
     *
     * La consulta devuelve un LiveData para que los cambios
     * en el registro se reflejen automáticamente en la UI.
     *
     * @param id Identificador del alumno.
     * @return Alumno encontrado o null si no existe.
     */
    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    LiveData<Alumno> buscarPorId(int id);

    /**
     * Busca un alumno por su identificador de forma síncrona.
     *
     * Este método suele utilizarse para validaciones internas
     * o lógica de negocio donde no es necesario observar cambios.
     *
     * Debe ejecutarse en un hilo secundario para evitar bloquear
     * el hilo principal de la aplicación.
     *
     * @param id Identificador del alumno.
     * @return Alumno encontrado o null si no existe.
     */
    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    Alumno buscarPorIdSync(int id);


}