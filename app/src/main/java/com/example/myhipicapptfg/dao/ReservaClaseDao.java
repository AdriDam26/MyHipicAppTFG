package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.myhipicapptfg.entities.ReservaClase;
import java.util.List;

@Dao
public interface ReservaClaseDao {

    @Insert
    void insertar(ReservaClase reserva);

    @Delete
    void eliminar(ReservaClase reserva);

    @Query("SELECT * FROM ReservaClase")
    LiveData<List<ReservaClase>> obtenerTodas();

    // Verificación 1: ¿Es un Alumno válido?
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Alumno')")
    boolean esAlumnoValido(int id);

    // Verificación 2: ¿Existe la Clase?
    @Query("SELECT EXISTS(SELECT 1 FROM Clase WHERE ID_Clase = :id)")
    boolean existeClase(int id);

    // Verificación 3: ¿Ya tiene reserva en esta clase?
    @Query("SELECT EXISTS(SELECT 1 FROM ReservaClase WHERE ID_Alumno = :idAlu AND ID_Clase = :idCla)")
    boolean yaEstaReservado(int idAlu, int idCla);

    // Verificación 4: Contar alumnos actuales en la clase
    @Query("SELECT COUNT(*) FROM ReservaClase WHERE ID_Clase = :idClase")
    int contarAlumnosEnClase(int idClase);
}
