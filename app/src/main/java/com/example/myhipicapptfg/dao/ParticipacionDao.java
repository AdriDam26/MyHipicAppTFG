package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Participacion;
import java.util.List;

@Dao
public interface ParticipacionDao {

    @Insert
    long insertarParticipacion(Participacion participacion);

    @Update
    void actualizarParticipacion(Participacion participacion);

    @Delete
    void eliminarParticipacion(Participacion participacion);

    @Query("SELECT * FROM Participacion WHERE ID_Convocatoria = :idConv ORDER BY Hora ASC")
    LiveData<List<Participacion>> obtenerPorConvocatoriaLiveData(int idConv);

    // --- VALIDACIONES DE ADUANA ---
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Alumno')")
    boolean existeAlumno(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE ID_Equino = :id)")
    boolean existeEquino(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Convocatoria WHERE ID_Convocatoria = :id)")
    boolean existeConvocatoria(int id);

    // Evitar que un mismo binomio se inscriba dos veces en la misma convocatoria
    @Query("SELECT EXISTS(SELECT 1 FROM Participacion WHERE ID_Alumno = :idAlu AND ID_Equino = :idEq AND ID_Convocatoria = :idConv)")
    boolean existeBinomioEnConvocatoria(int idAlu, int idEq, int idConv);
}
