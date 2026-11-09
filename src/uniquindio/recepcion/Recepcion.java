package uniquindio.recepcion;

import uniquindio.coordinacion.MediadorTaller;
import uniquindio.clientes.Vehiculo;
import uniquindio.personal.Mecanico;
import uniquindio.cotizacion.EstrategiaTarifa;

// Colega del mediador: no conoce las clases de inventario ni facturación.
public class Recepcion {
    private final MediadorTaller mediador;

    public Recepcion(MediadorTaller mediador) {
        if (mediador == null) {
            throw new IllegalArgumentException("Debe indicar el mediador.");
        }
        this.mediador = mediador;
    }

    public void registrarOrden(int numero, Vehiculo vehiculo) {
        mediador.crearOrden(numero, vehiculo);
    }

    public void asignarMecanico(int numero, Mecanico mecanico) {
        mediador.asignarMecanico(numero, mecanico);
    }

    public void cotizar(int numero, EstrategiaTarifa tarifa) {
        mediador.cotizar(numero, tarifa);
    }

    public void solicitarAprobacion(int numero) {
        mediador.solicitarAprobacion(numero);
    }

    public void registrarAprobacionCliente(int numero) {
        mediador.aprobarPresupuesto(numero);
    }

    public String entregarVehiculo(int numero) {
        return mediador.entregar(numero);
    }
}
