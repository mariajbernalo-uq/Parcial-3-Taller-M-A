package uniquindio.ordenes;

import uniquindio.coordinacion.Comando;


public class AgregarServicioCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final ItemServicio servicio;

    public AgregarServicioCommand(RepositorioOrdenes repositorio, int numeroOrden, ItemServicio servicio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.servicio = servicio;
    }

    @Override
    public void ejecutar() {
        OrdenServicio orden = repositorio.buscarPorNumero(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numeroOrden);
        }
        orden.agregarServicio(servicio);
        repositorio.guardar(orden);
    }
}
