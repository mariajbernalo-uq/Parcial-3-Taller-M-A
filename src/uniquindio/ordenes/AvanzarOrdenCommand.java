package uniquindio.ordenes;

import uniquindio.coordinacion.Comando;
import uniquindio.notificaciones.EventoOrden;
import uniquindio.notificaciones.PublicadorEventos;

public class AvanzarOrdenCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final PublicadorEventos publicador;

    public AvanzarOrdenCommand(RepositorioOrdenes repositorio, int numeroOrden,
            PublicadorEventos publicador) {
        if (repositorio == null || publicador == null) {
            throw new IllegalArgumentException("Debe indicar repositorio y publicador.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.publicador = publicador;
    }

    @Override
    public void ejecutar() {
        OrdenServicio orden = repositorio.buscarPorNumero(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numeroOrden);
        }
        String estadoAnterior = orden.getNombreEstado();
        orden.avanzarEstado();
        repositorio.guardar(orden);

        // La notificación solo ocurre después del avance exitoso.
        EventoOrden evento = new EventoOrden(orden.getNumero(),
                orden.getVehiculo().getPlaca(),
                orden.getVehiculo().getPropietario().getTelefono(),
                estadoAnterior, orden.getNombreEstado());
        publicador.publicar(evento);
    }
}
