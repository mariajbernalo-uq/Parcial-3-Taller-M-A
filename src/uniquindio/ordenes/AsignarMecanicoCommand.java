package uniquindio.ordenes;

import uniquindio.coordinacion.Comando;
import uniquindio.personal.Mecanico;

public class AsignarMecanicoCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final Mecanico mecanico;

    public AsignarMecanicoCommand(RepositorioOrdenes repositorio, int numeroOrden, Mecanico mecanico) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.mecanico = mecanico;
    }

    @Override
    public void ejecutar() {
        OrdenServicio orden = repositorio.buscarPorNumero(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numeroOrden);
        }
        orden.asignarMecanico(mecanico);
        repositorio.guardar(orden);
    }
}
