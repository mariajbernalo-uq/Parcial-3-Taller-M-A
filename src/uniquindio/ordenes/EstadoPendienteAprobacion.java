package uniquindio.ordenes;

public class EstadoPendienteAprobacion implements EstadoOrden {
    @Override
    public String getNombre() {
        return "PENDIENTE_APROBACION";
    }

    @Override
    public void avanzar(OrdenServicio orden) {
        if (!orden.isPresupuestoAprobado()) {
            throw new IllegalStateException("El cliente debe aprobar el presupuesto.");
        }
        orden.cambiarEstado(new EstadoEnReparacion());
    }
}
