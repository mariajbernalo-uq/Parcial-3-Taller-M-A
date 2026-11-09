package uniquindio.notificaciones;

public class PanelTallerObserver implements ObservadorOrden {
    @Override
    public void actualizar(EventoOrden evento) {

        System.out.println("\n[PANEL DEL TALLER]");
        System.out.println(
                "Orden: #" + evento.getNumeroOrden()
        );
        System.out.println(
                "Vehículo: " + evento.getPlaca()
        );
        System.out.println(
                "Cambio: " + evento.getEstadoAnterior()
                        + " -> " + evento.getEstadoNuevo()
        );
    }
}
