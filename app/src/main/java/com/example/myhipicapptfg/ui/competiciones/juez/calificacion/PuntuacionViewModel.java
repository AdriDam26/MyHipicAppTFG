package com.example.myhipicapptfg.ui.competiciones.juez.calificacion;

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

/**
 * ViewModel encargado de gestionar la puntuación de una participación.
 *
 * Su función principal es:
 * - Obtener los movimientos de una prueba.
 * - Obtener las notas registradas para una participación.
 * - Combinar ambas fuentes de datos en una única estructura.
 * - Calcular la nota final y el porcentaje obtenido.
 * - Persistir las puntuaciones introducidas por el juez.
 *
 */
public class PuntuacionViewModel extends AndroidViewModel {

    // Repositorio encargado del acceso a los datos de puntuaciones y movimientos
    private final JuezRepository repo;

    // Lista observable de movimientos pertenecientes a una prueba
    private LiveData<List<Movimiento>>     movimientosLive;

    // Lista observable de notas asociadas a una participación
    private LiveData<List<NotaMovimiento>> notasLive;

    /**
     * LiveData que combina movimientos y notas en una única colección
     * de objetos MovimientoConNota para facilitar su visualización.
     */
    private final MediatorLiveData<List<MovimientoConNota>> combinado =
            new MediatorLiveData<>();

    // Caché local de movimientos obtenidos desde la base de datos
    private List<Movimiento>     movimientosCached = new ArrayList<>();

    // Caché local de notas obtenidas desde la base de datos
    private List<NotaMovimiento> notasCached       = new ArrayList<>();

    /**
     * Corrección previamente almacenada en la base de datos.
     *
     * Se utiliza para restaurar el estado de los controles de la interfaz
     * cuando se vuelve a abrir una puntuación ya registrada.
     */
    private double correccionGuardada = 0.0;

    /**
     * Constructor del ViewModel.
     *
     * @param application Contexto de la aplicación.
     */
    public PuntuacionViewModel(@NonNull Application application) {
        super(application);
        repo = new JuezRepository(application);
    }

    /**
     * Inicializa los datos necesarios para la pantalla de puntuación.
     *
     * Obtiene:
     * - Los movimientos de la prueba.
     * - Las notas ya registradas para la participación.
     *
     * Además, configura un MediatorLiveData que combinará ambas fuentes
     * para generar una lista de MovimientoConNota.
     *
     * @param idPrueba Identificador de la prueba.
     * @param idParticipacion Identificador de la participación.
     * @param correccionPrevia Corrección previamente almacenada.
     */
    public void init(int idPrueba, int idParticipacion, double correccionPrevia) {
        this.correccionGuardada = correccionPrevia;
        // Obtiene los movimientos de la prueba
        movimientosLive = repo.getMovimientosByPrueba(idPrueba);
        // Obtiene las notas registradas para la participación
        notasLive       = repo.getNotasByParticipacion(idParticipacion);
        // Observa cambios en los movimientos
        combinado.addSource(movimientosLive, movs -> {
            movimientosCached = (movs != null) ? movs : new ArrayList<>();
            combinado.setValue(combinar());
        });
        // Observa cambios en las notas
        combinado.addSource(notasLive, notas -> {
            notasCached = (notas != null) ? notas : new ArrayList<>();
            combinado.setValue(combinar());
        });
    }

    /**
     * Combina los movimientos de la prueba con las notas registradas.
     *
     * Para cada movimiento se busca su nota correspondiente y se genera
     * un objeto MovimientoConNota que contiene toda la información necesaria
     * para mostrarla en pantalla.
     *
     * @return Lista de movimientos junto con sus notas.
     */
    private List<MovimientoConNota> combinar() {
        // Mapa para acceder rápidamente a las notas por idMovimiento
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

            // Obtiene la nota asociada al movimiento
            NotaMovimiento n = mapaNotas.get(m.idMovimiento);
            // Si no existe nota previa se asignan valores por defecto
            mcn.nota        = (n != null) ? n.nota        : 0.0;
            mcn.observacion = (n != null) ? n.observacion : "";
            resultado.add(mcn);
        }
        return resultado;
    }

    /**
     * Devuelve la lista observable de movimientos junto con sus notas.
     *
     * @return LiveData con la información combinada.
     */
    public LiveData<List<MovimientoConNota>> getMovimientosConNota() {
        return combinado;
    }

    /**
     * Devuelve la corrección almacenada previamente.
     *
     * @return Corrección guardada.
     */
    public double getCorreccionGuardada() {
        return correccionGuardada;
    }

    // ─── Lógica de negocio centralizada aquí ────────────────────────────────

    /**
     * Calcula la nota final y el porcentaje obtenido por un participante.
     *
     * El cálculo se realiza ponderando cada movimiento mediante su
     * coeficiente correspondiente.
     *
     * Si el participante ha sido eliminado (corrección negativa),
     * la nota y el porcentaje resultan cero.
     *
     * @param items Lista de movimientos con sus notas.
     * @param correccion Penalización aplicada.
     *
     * @return Array con:
     *         [0] Nota final (0-10)
     *         [1] Porcentaje obtenido (0-100)
     */
    public double[] calcular(List<MovimientoConNota> items, double correccion) {
        if (correccion < 0) {
            return new double[]{0.0, 0.0};
        }

        double sumaN = 0,
                sumaD = 0;

        // Cálculo de puntos obtenidos y puntos máximos posibles
        for (MovimientoConNota m : items) {
            sumaN += m.nota * m.coeficiente;
            sumaD += 10.0 * m.coeficiente;
        }
        // Aplicación de la penalización
        double puntosBrutos = Math.max(0, sumaN - correccion);
        // Cálculo del porcentaje final
        double porcentaje   = (sumaD > 0) ? (puntosBrutos / sumaD) * 100.0 : 0.0;
        // Conversión a escala sobre 10
        double notaFinal    = porcentaje / 10.0;

        return new double[]{notaFinal, porcentaje};
    }

    /**
     * Guarda la puntuación definitiva de una participación.
     *
     * Delega la persistencia de datos al repositorio.
     *
     * @param idParticipacion Identificador de la participación.
     * @param notas Lista de notas de los movimientos.
     * @param notaFinal Nota final calculada.
     * @param porcentaje Porcentaje obtenido.
     * @param correccion Penalización aplicada.
     * @param eliminado Indica si el participante fue eliminado.
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