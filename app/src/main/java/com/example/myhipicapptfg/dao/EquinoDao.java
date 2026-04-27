package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Equino;

import java.util.List;

@Dao
public interface EquinoDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarEquino(Equino equino);

    // 🔹 UPDATE
    @Update
    int actualizarEquino(Equino equino);

    // 🔹 DELETE
    @Delete
    int eliminarEquino(Equino equino);

    // 🔹 LISTADO GENERAL (UI)
    @Query("SELECT * FROM Equino ORDER BY Nombre ASC")
    LiveData<List<Equino>> obtenerTodosEquinos();

    // 🔹 BUSCAR POR ID (UI)
    @Query("SELECT * FROM Equino WHERE ID_Equino = :id LIMIT 1")
    LiveData<Equino> buscarEquinoPorId(int id);

    // 🔹 BUSCAR POR USUARIO (UI)
    @Query("SELECT * FROM Equino WHERE ID_Usuario = :idUsuario")
    LiveData<List<Equino>> buscarEquinosPorUsuario(int idUsuario);

    // 🔹 CONTAR (UI)
    @Query("SELECT COUNT(*) FROM Equino")
    LiveData<Integer> contarEquinos();

    // ----------------------------------------------------
    // 🔹 MÉTODOS SYNC (lógica en repositorio / background)
    // ----------------------------------------------------

    // ✔ validar microchip único
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE Numero_Microchip = :microchip)")
    boolean existeMicrochip(String microchip);

    // ✔ validar propietario
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'propietario')")
    boolean esPropietarioValido(int id);


    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE Numero_Cuadra = :numeroCuadra)")
    boolean esCuadraOcupada(int numeroCuadra);
}