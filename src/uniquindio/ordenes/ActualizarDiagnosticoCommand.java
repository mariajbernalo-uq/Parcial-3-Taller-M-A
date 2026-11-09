package uniquindio.ordenes;

import uniquindio.coordinacion.Comando;


public class ActualizarDiagnosticoCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final String diagnostico;

    public ActualizarDiagnosticoCommand(RepositorioOrdenes repositorio, int numeroOrden, String diagnostico) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.diagnostico = diagnostico;
    }

    @Override
    public void ejecutar() {
        OrdenServicio orden = repositorio.buscarPorNumero(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numeroOrden);
        }
        orden.actualizarDiagnostico(diagnostico);
        repositorio.guardar(orden);
    }
}
