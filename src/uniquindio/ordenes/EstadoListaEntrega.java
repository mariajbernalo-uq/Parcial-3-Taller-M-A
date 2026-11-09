package uniquindio.ordenes;

public class EstadoListaEntrega implements EstadoOrden{
    @Override
    public String getNombre() {
        return "LISTA_ENTREGA";
    }

    @Override
    public void avanzar(OrdenServicio orden) {
        orden.cambiarEstado(new EstadoEntregada());
    }
}
