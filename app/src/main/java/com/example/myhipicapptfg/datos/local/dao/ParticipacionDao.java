package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Participacion;

import com.example.myhipicapptfg.model.ConteoParticipantes;
import com.example.myhipicapptfg.model.ParticipacionDetalle;
import com.example.myhipicapptfg.model.PruebaAlumno;
import com.example.myhipicapptfg.model.RankingItem;

import java.util.List;

/**
 * DAO de Participacion.
 *
 * Gestiona la inscripción de alumnos y equinos en pruebas,
 * así como los resultados, rankings y consultas complejas del sistema.
 */
@Dao
public interface ParticipacionDao {

    /**
     * Inserta una participación en una prueba.
     * Evita duplicados mediante constraint definida en entidad.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarParticipacion(Participacion participacion);


    @Update
    int actualizarParticipacion(Participacion participacion);


    @Delete
    int eliminarParticipacion(Participacion participacion);

    /**
     * Obtiene todas las participaciones registradas.
     */
    @Query("SELECT * FROM Participacion")
    LiveData<List<Participacion>> obtenerTodasParticipaciones();

    /**
     * Obtiene las participaciones de una prueba ordenadas por salida.
     */
    @Query("SELECT * FROM Participacion WHERE ID_Prueba = :idPrueba ORDER BY Orden_Salida ASC")
    LiveData<List<Participacion>> obtenerPorPrueba(int idPrueba);

    /**
     * Obtiene participaciones de un alumno concreto.
     */
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Alumno = :idAlumno")
    LiveData<List<Participacion>> obtenerPorAlumno(int idAlumno);

    /**
     * Obtiene participaciones de un equino concreto.
     */
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Equino = :idEquino")
    LiveData<List<Participacion>> obtenerPorEquino(int idEquino);

    /**
     * Busca una participación por ID.
     */
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Participacion = :id LIMIT 1")
    LiveData<Participacion> buscarPorId(int id);

    /**
     * Verifica si existe un alumno.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Alumno WHERE ID_Alumno = :id)")
    boolean existeAlumno(int id);

    /**
     * Verifica si existe un equino.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE ID_Equino = :id)")
    boolean existeEquino(int id);

    /**
     * Verifica si existe una prueba.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Prueba WHERE ID_Prueba = :id)")
    boolean existePrueba(int id);

    /**
     * Evita duplicar inscripciones:
     * un mismo alumno + equino + prueba no puede repetirse.
     */
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Participacion " +
            "WHERE ID_Alumno = :idAlumno " +
            "AND ID_Equino = :idEquino " +
            "AND ID_Prueba = :idPrueba)")
    boolean existeParticipacion(int idAlumno, int idEquino, int idPrueba);

    /**
     * Obtiene el siguiente orden de salida disponible en una prueba.
     */
    @Query("SELECT IFNULL(MAX(orden_Salida), 0) + 1 FROM Participacion WHERE id_Prueba = :idPrueba")
    LiveData<Integer> obtenerSiguienteOrden(int idPrueba);



    /**
     * Devuelve todos los participantes de una Prueba con
     * nombre del jinete (Alumno→Usuario) y nombre del caballo (Equino),
     * ordenados por Orden_Salida.
     */
    @Query("SELECT " +
            "  pa.ID_Participacion                          AS idParticipacion, " +
            "  pa.Orden_Salida                              AS ordenSalida, " +
            "  pa.Correccion                                AS correccion, " +  // ← añadir
            "  (u.Nombre || ' ' || u.Apellido1)             AS nombreJinete, " +
            "  e.Nombre                                     AS nombreCaballo " +
            "FROM Participacion pa " +
            "INNER JOIN Alumno   a ON pa.ID_Alumno = a.ID_Alumno " +
            "INNER JOIN Usuario  u ON a.ID_Alumno  = u.ID_Usuario " +
            "INNER JOIN Equino   e ON pa.ID_Equino = e.ID_Equino " +
            "WHERE pa.ID_Prueba = :idPrueba " +
            "ORDER BY pa.Orden_Salida ASC")
    LiveData<List<ParticipacionDetalle>> getParticipantesByPrueba(int idPrueba);

    /**
     * Actualiza resultados finales de una participación.
     */
    @Query("UPDATE Participacion SET Nota_Final = :notaFinal, Porcentaje = :porcentaje, " +
            "Correccion = :correccion, Eliminado = :eliminado WHERE ID_Participacion = :id")
    void updateResultado(int id, double notaFinal, double porcentaje,
                         double correccion, boolean eliminado);



    /**
     * Obtiene pruebas publicadas en las que ha participado un alumno.
     */
    @Query("SELECT " +
            "  pa.ID_Participacion   AS idParticipacion, " +
            "  pr.ID_Prueba          AS idPrueba, " +
            "  pr.Nombre             AS nombrePrueba, " +
            "  c.Nombre              AS nombreCompeticion, " +
            "  pr.Categoria          AS categoria, " +
            "  pr.Nivel              AS nivel, " +
            "  pr.Publicado          AS publicado " +
            "FROM Participacion pa " +
            "INNER JOIN Prueba       pr ON pa.ID_Prueba        = pr.ID_Prueba " +
            "INNER JOIN Competicion  c  ON pr.ID_Competicion   = c.ID_Competicion " +
            "WHERE pa.ID_Alumno = :idAlumno AND pr.Publicado = 1 " +
            "ORDER BY c.Fecha DESC, pr.Nombre ASC")
    LiveData<List<PruebaAlumno>> getPruebasPublicadasByAlumno(int idAlumno);


    /**
     * Calcula ranking de una prueba:
     * ordena por porcentaje y coloca eliminados al final.
     */
    @Query(
            "SELECT " +
                    "  pa.ID_Participacion AS idParticipacion, " +
                    "  (u.Nombre || ' ' || u.Apellido1) AS nombreJinete, " +
                    "  e.Nombre AS nombreCaballo, " +
                    "  pa.Porcentaje AS porcentaje, " +
                    "  pa.Nota_Final AS notaFinal, " +
                    "  pa.Eliminado AS eliminado, " +

                    "  CASE WHEN pa.Eliminado = 1 THEN -1 " +
                    "  ELSE (SELECT COUNT(p2.ID_Participacion) " +  // ← Cuenta filas, no porcentajes distintos
                    "        FROM Participacion p2 " +
                    "        WHERE p2.ID_Prueba = pa.ID_Prueba " +
                    "        AND p2.Eliminado = 0 " +
                    "        AND p2.Porcentaje > pa.Porcentaje) + 1 " +
                    "  END AS posicion " +

                    "FROM Participacion pa " +
                    "INNER JOIN Alumno a ON pa.ID_Alumno = a.ID_Alumno " +
                    "INNER JOIN Usuario u ON a.ID_Alumno = u.ID_Usuario " +
                    "INNER JOIN Equino e ON pa.ID_Equino = e.ID_Equino " +
                    "WHERE pa.ID_Prueba = :idPrueba " +
                    "ORDER BY pa.Eliminado ASC, pa.Porcentaje DESC"
    )
    LiveData<List<RankingItem>> getRankingByPrueba(int idPrueba);


    /**
     * Cuenta participantes por prueba.
     */
    @Query("SELECT ID_Prueba, COUNT(*) AS total FROM Participacion GROUP BY ID_Prueba")
    LiveData<List<ConteoParticipantes>> contarParticipantesPorTodasLasPruebas();


}