package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myhipicapptfg.entities.ProfesorDisciplina;
import java.util.List;

@Dao
public interface ProfesorDisciplinaDao {

    @Insert
    void insertar(ProfesorDisciplina pd);

    @Delete
    void eliminar(ProfesorDisciplina pd);

    @Query("SELECT * FROM ProfesorDisciplina")
    LiveData<List<ProfesorDisciplina>> obtenerTodas();

    // Verificación: ¿Existe el ID y el usuario es de tipo 'Profesor'?
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Profesor')")
    boolean esProfesorValido(int id);

    // Verificación: ¿Existe la Disciplina?
    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplina(int id);

    @Query("SELECT * FROM ProfesorDisciplina WHERE ID_Profesor = :idProfesor")
    List<ProfesorDisciplina> obtenerDisciplinasPorProfesor(int idProfesor);
}
