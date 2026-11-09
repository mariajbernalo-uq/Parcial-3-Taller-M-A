package uniquindio.notificaciones;

public class NotificadorClienteObserver implements ObservadorOrden {
    private final ServicioNotificaciones servicio;

    public NotificadorClienteObserver(ServicioNotificaciones servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo.");
        }
        this.servicio = servicio;
    }

    @Override
    public void actualizar(EventoOrden evento) {
        String mensaje = "Taller M&A: la orden #" + evento.getNumeroOrden()
                + " del vehículo " + evento.getPlaca()
                + " cambió al estado " + evento.getEstadoNuevo() + ".";
        servicio.enviar(evento.getTelefonoCliente(), mensaje);
    }
}
