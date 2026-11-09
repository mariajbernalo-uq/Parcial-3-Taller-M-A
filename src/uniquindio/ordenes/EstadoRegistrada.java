package uniquindio.ordenes;

public class EstadoRegistrada implements EstadoOrden{
    @Override
    public String getNombre() {
        return "REGISTRADA";
    }

    @Override
    public void validarAsignacion() {
        // La asignación está permitida en esta etapa.
    }

    @Override
    public void avanzar(OrdenServicio orden) {

        if (orden.getMecanico() == null) {
            throw new IllegalStateException(
                    "Debe asignar un mecánico antes de "
                            + "iniciar el diagnóstico."
            );
        }

        orden.cambiarEstado(new EstadoEnDiagnostico());
    }
}
