package uniquindio.ordenes;

public class EstadoEnDiagnostico implements EstadoOrden{
    @Override
    public String getNombre() {
        return "EN_DIAGNOSTICO";
    }

    @Override
    public void validarEdicion() {
        // La edición está permitida en esta etapa.
    }

    @Override
    public void avanzar(OrdenServicio orden) {

        if (orden.getDiagnostico().trim().isEmpty()) {
            throw new IllegalStateException(
                    "Debe registrar el diagnóstico."
            );
        }

        if (orden.getServicios().isEmpty()) {
            throw new IllegalStateException(
                    "Debe agregar al menos un servicio."
            );
        }

        if (!orden.tieneCotizacion()) {
            throw new IllegalStateException(
                    "Debe cotizar la orden antes de "
                            + "solicitar aprobación."
            );
        }

        orden.cambiarEstado(
                new EstadoPendienteAprobacion()
        );
    }
}
