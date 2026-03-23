package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import com.example.myhipicapptfg.entities.Disciplina;
import java.util.List;

@Dao
public interface DisciplinaDao {

    // Usamos ABORT para que el Repositorio maneje el error si hay un conflicto inesperado
    @Insert
    long insertarDisciplina(Disciplina disciplina);

    @Update
    void actualizarDisciplina(Disciplina disciplina);

    @Delete
    void eliminarDisciplina(Disciplina disciplina);

    @Query("SELECT * FROM Disciplina")
    LiveData<List<Disciplina>> obtenerTodasDisciplinas();

    @Query("SELECT * FROM Disciplina WHERE ID_Disciplina = :id LIMIT 1")
    LiveData<Disciplina> buscarPorId(int id);

    // MÉTODO SÍNCRONO: Fundamental para la validación en el Repositorio
    @Query("SELECT * FROM Disciplina WHERE Nombre = :nombre LIMIT 1")
    Disciplina buscarPorNombreSync(String nombre);

    @Query("SELECT COUNT(*) FROM Disciplina")
    LiveData<Integer> contarDisciplinas();
}
