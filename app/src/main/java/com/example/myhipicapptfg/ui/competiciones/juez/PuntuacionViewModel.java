package com.example.myhipicapptfg.ui.competiciones.juez;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntuacionViewModel extends AndroidViewModel {

    private final JuezRepository repo;

    private LiveData<List<Movimiento>>     movimientosLive;
    private LiveData<List<NotaMovimiento>> notasLive;

    private final MediatorLiveData<List<MovimientoConNota>> combinado =
            new MediatorLiveData<>();

    private List<Movimiento>     movimientosCached = new ArrayList<>();
    private List<NotaMovimiento> notasCached       = new ArrayList<>();

    // Corrección guardada previamente en BD (para restaurar el RadioButton)
    private double correccionGuardada = 0.0;

    public PuntuacionViewModel(@NonNull Application application) {
        super(application);
        repo = new JuezRepository(application);
    }

    public void init(int idPrueba, int idParticipacion, double correccionPrevia) {
        this.correccionGuardada = correccionPrevia;

        movimientosLive = repo.getMovimientosByPrueba(idPrueba);
        notasLive       = repo.getNotasByParticipacion(idParticipacion);

        combinado.addSource(movimientosLive, movs -> {
            movimientosCached = (movs != null) ? movs : new ArrayList<>();
            combinado.setValue(combinar());
        });
        combinado.addSource(notasLive, notas -> {
            notasCached = (notas != null) ? notas : new ArrayList<>();
            combinado.setValue(combinar());
        });
    }

    private List<MovimientoConNota> combinar() {
        Map<Integer, NotaMovimiento> mapaNotas = new HashMap<>();
        for (NotaMovimiento n : notasCached) {
            mapaNotas.put(n.idMovimiento, n);
        }

        List<MovimientoConNota> resultado = new ArrayList<>();
        for (Movimiento m : movimientosCached) {
            MovimientoConNota mcn = new MovimientoConNota();
            mcn.idMovimiento = m.idMovimiento;
            mcn.letra        = m.letra;
            mcn.ejercicio    = m.ejercicio;
            mcn.coeficiente  = m.coeficiente;
            mcn.directriz    = m.directriz;
            mcn.orden        = m.orden;

            NotaMovimiento n = mapaNotas.get(m.idMovimiento);
            mcn.nota        = (n != null) ? n.nota        : 0.0;
            mcn.observacion = (n != null) ? n.observacion : "";
            resultado.add(mcn);
        }
        return resultado;
    }

    public LiveData<List<MovimientoConNota>> getMovimientosConNota() {
        return combinado;
    }

    public double getCorreccionGuardada() {
        return correccionGuardada;
    }

    // ─── Lógica de negocio centralizada aquí ────────────────────────────────

    /**
     * Calcula nota y porcentaje a partir de los ítems y la corrección.
     * Devuelve double[]{notaFinal, porcentaje}.
     * Si correccion == -1.0 (eliminado), devuelve {0, 0}.
     */
    public double[] calcular(List<MovimientoConNota> items, double correccion) {
        if (correccion < 0) return new double[]{0.0, 0.0};

        double sumaN = 0, sumaD = 0;
        for (MovimientoConNota m : items) {
            sumaN += m.nota * m.coeficiente;
            sumaD += 10.0 * m.coeficiente;
        }

        double puntosBrutos = Math.max(0, sumaN - correccion);
        double porcentaje   = (sumaD > 0) ? (puntosBrutos / sumaD) * 100.0 : 0.0;
        double notaFinal    = porcentaje / 10.0;

        return new double[]{notaFinal, porcentaje};
    }

    /**
     * Persiste la puntuación. El cálculo ya viene hecho desde la Activity.
     * @param correccion  0.0 / 2.0 / 4.0 → penalización normal
     *                   -1.0             → participante eliminado
     */
    public void guardar(int idParticipacion,
                        List<NotaMovimiento> notas,
                        double notaFinal,
                        double porcentaje,
                        double correccion,
                        boolean eliminado) {
        repo.guardarPuntuacion(
                idParticipacion, notas,
                notaFinal, porcentaje,
                correccion, eliminado);
    }
}