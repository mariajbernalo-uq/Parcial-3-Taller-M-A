package uniquindio.ordenes;

public interface EstadoOrden {
    String getNombre();

    void avanzar(OrdenServicio orden);

    default void validarAsignacion() {
        throw new IllegalStateException(
                "No puede asignar mecánico en el estado "
                        + getNombre()
        );
    }

    default void validarEdicion() {
        throw new IllegalStateException(
                "No puede modificar diagnóstico, servicios "
                        + "o cotización en el estado "
                        + getNombre()
        );
    }
}
