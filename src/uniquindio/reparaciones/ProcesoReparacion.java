package uniquindio.reparaciones;

import java.util.Map;
import uniquindio.ordenes.OrdenServicio;

public abstract class ProcesoReparacion {

    // TEMPLATE METHOD: ninguna subclase puede cambiar el orden de los pasos.
    public final void ejecutarProceso(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula.");
        }
        if (!orden.getNombreEstado().equals("EN_REPARACION")) {
            throw new IllegalStateException("La orden debe estar en reparación.");
        }
        if (orden.isReparacionFinalizada()) {
            throw new IllegalStateException("Esta reparación ya fue terminada.");
        }

        System.out.println("[PLANTILLA] Reparación de la orden #" + orden.getNumero());
        prepararHerramientas();
        realizarReparacion();
        realizarPruebas();
        orden.registrarReparacionFinalizada();
        System.out.println("[PLANTILLA] Reparación y pruebas completadas.");
    }

    public abstract Map<String, Integer> getRepuestosNecesarios();
    protected abstract void prepararHerramientas();
    protected abstract void realizarReparacion();
    protected abstract void realizarPruebas();
}
