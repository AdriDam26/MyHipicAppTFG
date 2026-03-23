package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myhipicapptfg.entities.Calificacion;
import java.util.List;

@Dao
public interface CalificacionDao {

    @Insert
    long insertarCalificacion(Calificacion calificacion);

    @Update
    void actualizarCalificacion(Calificacion calificacion);

    @Delete
    void eliminarCalificacion(Calificacion calificacion);

    @Query("SELECT * FROM Calificacion WHERE ID_Participacion = :idPart")
    LiveData<List<Calificacion>> obtenerPorParticipacionLiveData(int idPart);

    // --- VALIDACIONES ---
    @Query("SELECT EXISTS(SELECT 1 FROM Reprisa WHERE ID_Reprisa = :id)")
    boolean existeReprisa(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Participacion WHERE ID_Participacion = :id)")
    boolean existeParticipacion(int id);

    // Evitar que el juez califique dos veces el mismo movimiento en la misma salida a pista
    @Query("SELECT EXISTS(SELECT 1 FROM Calificacion WHERE ID_Participacion = :idPart AND ID_Reprisa = :idRep)")
    boolean yaEstaCalificado(int idPart, int idRep);
}
