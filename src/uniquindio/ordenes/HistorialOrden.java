package uniquindio.ordenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Caretaker: conserva respaldos por orden sin leer diagnóstico ni presupuesto.
public class HistorialOrden {
    private final Map<OrdenServicio, List<MementoOrden>> respaldos = new HashMap<>();

    public void guardarEstado(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula.");
        }
        MementoOrden recuerdo = orden.crearMemento();
        List<MementoOrden> historial = respaldos.get(orden);
        if (historial == null) {
            historial = new ArrayList<>();
            respaldos.put(orden, historial);
        }
        historial.add(recuerdo);
    }

    public boolean restaurarUltimoEstado(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula.");
        }
        List<MementoOrden> historial = respaldos.get(orden);
        if (historial == null || historial.isEmpty()) {
            return false;
        }
        int ultimaPosicion = historial.size() - 1;
        MementoOrden recuerdo = historial.get(ultimaPosicion);
        orden.restaurarMemento(recuerdo);
        historial.remove(ultimaPosicion);
        if (historial.isEmpty()) {
            respaldos.remove(orden);
        }
        return true;
    }

    public void descartarUltimoEstado(OrdenServicio orden) {
        List<MementoOrden> historial = respaldos.get(orden);
        if (historial != null && !historial.isEmpty()) {
            historial.remove(historial.size() - 1);
            if (historial.isEmpty()) {
                respaldos.remove(orden);
            }
        }
    }

    public void limpiar(OrdenServicio orden) {
        respaldos.remove(orden);
    }

    public int cantidadEstadosGuardados(OrdenServicio orden) {
        List<MementoOrden> historial = respaldos.get(orden);
        if (historial == null) {
            return 0;
        }
        return historial.size();
    }
}
