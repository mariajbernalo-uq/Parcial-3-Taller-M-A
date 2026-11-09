package uniquindio.ordenes;

public class EstadoEnReparacion implements EstadoOrden {
    @Override
    public String getNombre() {
        return "EN_REPARACION";
    }

    @Override
    public void avanzar(OrdenServicio orden) {
        if (!orden.isReparacionFinalizada()) {
            throw new IllegalStateException("Debe terminar la reparación y las pruebas.");
        }
        orden.cambiarEstado(new EstadoListaEntrega());
    }
}
