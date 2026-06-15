package com.example.myhipicapptfg.ui.admin.clases;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;

import java.util.Calendar;
import java.util.List;

/**
 * Clase utilitaria encargada de realizar las validaciones
 * relacionadas con la gestión de clases.
 *
 * Sus responsabilidades son:
 * - Verificar si un profesor puede impartir una disciplina y nivel.
 * - Comprobar la disponibilidad de profesores.
 * - Comprobar la disponibilidad de pistas.
 * - Detectar solapamientos horarios.
 */
public class ClaseValidator {

    /**
     * Comprueba si un profesor está capacitado para impartir
     * una clase de una determinada disciplina y nivel.
     *
     * Para ello se valida:
     * - Que el profesor tenga habilitada la disciplina.
     * - Que su nivel máximo permitido sea igual o superior
     *   al nivel de la clase.
     *
     * @param profesor Profesor a validar.
     * @param disciplina Disciplina de la clase (Doma o Salto).
     * @param nivelClase Nivel de la clase.
     * @return true si puede impartirla, false en caso contrario.
     */
    public static boolean profesorPuedeDarClase(
            Profesor profesor,
            String disciplina,
            String nivelClase
    ) {

        if (disciplina.equals(Clase.DOMA)) {

            return profesor.puedeDarDoma &&
                    nivelAInt(profesor.nivelMaximoDoma)
                            >= nivelAInt(nivelClase);

        } else {

            return profesor.puedeDarSalto &&
                    nivelAInt(profesor.nivelMaximoSalto)
                            >= nivelAInt(nivelClase);
        }
    }

    /**
     * Comprueba si un profesor está disponible en el horario indicado.
     *
     * Recorre todas las clases existentes y verifica:
     * - Que no sea la propia clase que se está editando.
     * - Que pertenezca al mismo profesor.
     * - Que se celebre el mismo día.
     * - Que no exista solapamiento horario.
     *
     * @param idProfesor Profesor a comprobar.
     * @param inicio Hora de inicio de la nueva clase.
     * @param fin Hora de fin de la nueva clase.
     * @param clases Lista de clases existentes.
     * @param claseEditando Id de la clase que se está editando.
     *
     * @return true si está disponible, false si existe conflicto.
     */
    public static boolean profesorDisponible(
            int idProfesor,
            long inicio,
            long fin,
            List<Clase> clases,
            int claseEditando
    ) {

        for (Clase c : clases) {

            // Ignorar la propia clase cuando se edita
            if (c.idClase == claseEditando)
                continue;

            // Ignorar clases de otros profesores
            if (c.idProfesor != idProfesor)
                continue;

            // Ignorar clases de otros días
            if (!mismoDia(c.fecha, inicio))
                continue;

            // Si existe solapamiento, el profesor no está disponible
            if (horariosSolapados(
                    inicio,
                    fin,
                    c.horaInicio,
                    c.horaFin
            )) {
                return false;
            }
        }

        return true;
    }

    /**
     * Comprueba si una pista está disponible en el horario indicado.
     *
     * Recorre todas las clases existentes y verifica:
     * - Que no sea la propia clase que se está editando.
     * - Que utilice la misma pista.
     * - Que se celebre el mismo día.
     * - Que no exista solapamiento horario.
     *
     * @param idPista Pista a comprobar.
     * @param inicio Hora de inicio de la nueva clase.
     * @param fin Hora de fin de la nueva clase.
     * @param clases Lista de clases existentes.
     * @param claseEditando Id de la clase que se está editando.
     *
     * @return true si la pista está libre, false si existe conflicto.
     */
    public static boolean pistaDisponible(
            int idPista,
            long inicio,
            long fin,
            List<Clase> clases,
            int claseEditando
    ) {

        for (Clase c : clases) {

            // Ignorar la propia clase cuando se edita
            if (c.idClase == claseEditando)
                continue;

            // Ignorar clases de otras pistas
            if (c.idPista != idPista)
                continue;

            // Ignorar clases de otros días
            if (!mismoDia(c.fecha, inicio))
                continue;

            // Si existe solapamiento, la pista no está disponible
            if (horariosSolapados(
                    inicio,
                    fin,
                    c.horaInicio,
                    c.horaFin
            )) {
                return false;
            }
        }

        return true;
    }

    /**
     * Comprueba si dos intervalos horarios se solapan.
     *
     * Ejemplo:
     * - 10:00 - 11:00
     * - 10:30 - 11:30
     *
     * Resultado: true
     *
     * @param inicio1 Inicio del primer intervalo.
     * @param fin1 Fin del primer intervalo.
     * @param inicio2 Inicio del segundo intervalo.
     * @param fin2 Fin del segundo intervalo.
     *
     * @return true si existe solapamiento.
     */
    private static boolean horariosSolapados(
            long inicio1,
            long fin1,
            long inicio2,
            long fin2
    ) {

        return inicio1 < fin2 && fin1 > inicio2;
    }

    /**
     * Comprueba si dos fechas pertenecen al mismo día.
     *
     * Solo compara:
     * - Año
     * - Día del año
     *
     * @param fecha1 Primera fecha.
     * @param fecha2 Segunda fecha.
     *
     * @return true si corresponden al mismo día.
     */
    private static boolean mismoDia(long fecha1, long fecha2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTimeInMillis(fecha1);
        c2.setTimeInMillis(fecha2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR)
                == c2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Convierte un nivel de clase a un valor numérico
     * para facilitar las comparaciones.
     *
     * Equivalencias:
     * - Básico      -> 1
     * - Intermedio  -> 2
     * - Avanzado    -> 3
     *
     * @param nivel Nivel textual.
     * @return Valor numérico asociado.
     */
    private static int nivelAInt(String nivel) {

        if (nivel == null) {
            return 0;
        }

        switch (nivel) {

            case Clase.AVANZADO:
                return 3;

            case Clase.INTERMEDIO:
                return 2;

            default:
                return 1;
        }
    }
}