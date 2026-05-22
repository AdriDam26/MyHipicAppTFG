package com.example.myhipicapptfg.ui.admin.clases;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;

import java.util.Calendar;
import java.util.List;

public class ClaseValidator {

    public static boolean profesorPuedeDarClase(
            Profesor profesor,
            String disciplina,
            String nivelClase
    ) {

        if (disciplina.equals(Clase.DOMA)) {

            return profesor.puedeDarDoma &&
                    nivelAInt(profesor.nivelMaximoDoma) >= nivelAInt(nivelClase);

        } else {

            return profesor.puedeDarSalto &&
                    nivelAInt(profesor.nivelMaximoSalto) >= nivelAInt(nivelClase);
        }
    }

    public static boolean profesorDisponible(
            int idProfesor,
            long inicio,
            long fin,
            List<Clase> clases,
            int claseEditando
    ) {

        for (Clase c : clases) {

            if (c.idClase == claseEditando)
                continue;

            if (c.idProfesor != idProfesor)
                continue;

            if (!mismoDia(c.fecha, inicio))
                continue;

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

    public static boolean pistaDisponible(
            int idPista,
            long inicio,
            long fin,
            List<Clase> clases,
            int claseEditando
    ) {

        for (Clase c : clases) {

            if (c.idClase == claseEditando)
                continue;

            if (c.idPista != idPista)
                continue;

            if (!mismoDia(c.fecha, inicio))
                continue;

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

    private static boolean horariosSolapados(
            long inicio1,
            long fin1,
            long inicio2,
            long fin2
    ) {

        return inicio1 < fin2 && fin1 > inicio2;
    }

    private static boolean mismoDia(long fecha1, long fecha2) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTimeInMillis(fecha1);
        c2.setTimeInMillis(fecha2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private static int nivelAInt(String nivel) {

        if (nivel == null)
            return 0;

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
