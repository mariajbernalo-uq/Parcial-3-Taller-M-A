package uniquindio.ordenes;

import uniquindio.clientes.Vehiculo;
import uniquindio.coordinacion.Comando;

public class CrearOrdenCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final Vehiculo vehiculo;

    public CrearOrdenCommand(RepositorioOrdenes repositorio, int numeroOrden, Vehiculo vehiculo) {
        if (repositorio == null || vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar repositorio y vehículo.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.vehiculo = vehiculo;
    }

    @Override
    public void ejecutar() {
        if (repositorio.buscarPorNumero(numeroOrden) != null) {
            throw new IllegalArgumentException("Ya existe la orden #" + numeroOrden);
        }
        OrdenServicio orden = new OrdenServicio(numeroOrden, vehiculo);
        repositorio.guardar(orden);
    }
}
