// model/HojaCalificacionesData.java
package com.example.myhipicapptfg.model;

import java.util.List;

public class HojaCalificacionesData {
    public List<MovimientoConNota> movimientos;
    public double  correccion;
    public boolean eliminado;

    public HojaCalificacionesData(List<MovimientoConNota> movimientos,
                                  double correccion, boolean eliminado) {
        this.movimientos = movimientos;
        this.correccion  = correccion;
        this.eliminado   = eliminado;
    }
}