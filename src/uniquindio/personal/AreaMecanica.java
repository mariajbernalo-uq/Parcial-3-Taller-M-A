package uniquindio.personal;

import uniquindio.coordinacion.MediadorTaller;
import uniquindio.ordenes.ItemServicio;
import uniquindio.reparaciones.ProcesoReparacion;

// Otro colega del mediador: no conoce Recepcion, Inventario ni Facturacion.
public class AreaMecanica {
    private final MediadorTaller mediador;

    public AreaMecanica(MediadorTaller mediador) {
        if (mediador == null) {
            throw new IllegalArgumentException("Debe indicar el mediador.");
        }
        this.mediador = mediador;
    }

    public void iniciarDiagnostico(int numero) {
        mediador.iniciarDiagnostico(numero);
    }

    public void registrarDiagnostico(int numero, String diagnostico) {
        mediador.actualizarDiagnostico(numero, diagnostico);
    }

    public void agregarServicio(int numero, ItemServicio servicio) {
        mediador.agregarServicio(numero, servicio);
    }

    public boolean deshacerUltimaEdicion(int numero) {
        return mediador.deshacerUltimaEdicion(numero);
    }

    public void reparar(int numero, ProcesoReparacion proceso) {
        mediador.reparar(numero, proceso);
    }
}
