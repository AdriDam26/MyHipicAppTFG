package com.example.myhipicapptfg.dao;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.myhipicapptfg.entities.Propietario;
import java.util.List;

@Dao
public interface PropietarioDao {

    @Insert
    void insertarPropietario(Propietario propietario);

    @Update
    void actualizarPropietario(Propietario propietario);

    @Delete
    void eliminarPropietario(Propietario propietario);

    // LiveData para observar cambios desde la UI
    @Query("SELECT * FROM Propietario")
    LiveData<List<Propietario>> obtenerTodosPropietarios();

    @Query("SELECT * FROM Propietario WHERE ID_Propietario = :id LIMIT 1")
    LiveData<Propietario> buscarPorId(int id);

    @Query("SELECT COUNT(*) FROM Propietario")
    LiveData<Integer> contarPropietarios();

    // Método síncrono para lógica de fondo
    @Query("SELECT * FROM Propietario WHERE ID_Propietario = :id LIMIT 1")
    Propietario buscarPorIdSync(int id);
}
