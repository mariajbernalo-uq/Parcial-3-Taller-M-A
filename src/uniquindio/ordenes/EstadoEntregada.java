package uniquindio.ordenes;

public class EstadoEntregada implements EstadoOrden{
    @Override
    public String getNombre() {
        return "ENTREGADA";
    }

    @Override
    public void avanzar(OrdenServicio orden) {
        throw new IllegalStateException(
                "La orden ya fue entregada. "
                        + "No puede avanzar a otra etapa."
        );
    }
}
