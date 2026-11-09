package uniquindio.notificaciones;

public class EventoOrden {
    private final int numeroOrden;
    private final String placa;
    private final String telefonoCliente;
    private final String estadoAnterior;
    private final String estadoNuevo;

    /**}
     * Constructor clase Evento Orden
     * @param numeroOrden
     * @param placa
     * @param telefonoCliente
     * @param estadoAnterior
     * @param estadoNuevo
     */
    public EventoOrden(
            int numeroOrden,
            String placa,
            String telefonoCliente,
            String estadoAnterior,
            String estadoNuevo) {

        this.numeroOrden = numeroOrden;
        this.placa = placa;
        this.telefonoCliente = telefonoCliente;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }
    //-----Getters de la clase Evento Orden
    public int getNumeroOrden() {
        return numeroOrden;
    }

    public String getPlaca() {
        return placa;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

}
