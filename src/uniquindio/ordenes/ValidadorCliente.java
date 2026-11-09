package uniquindio.ordenes;



public class ValidadorCliente extends ValidadorOrden {

    @Override
    protected String validarActual(OrdenServicio orden) {

        if (orden == null) {
            return "Error: la orden de reparación no existe.";
        }

        if (orden.getVehiculo() == null) {
            return "Error: falta el vehículo de la orden.";
        }
        if (orden.getVehiculo().getPropietario() == null) {
            return "Error: la orden no tiene un cliente asociado.";
        }

        if (orden.getVehiculo().getPropietario().getIdentificacion() == null ||
                orden.getVehiculo().getPropietario().getIdentificacion().trim().isEmpty()) {

            return "Error: el cliente no tiene un documento válido.";
        }

        return null;
    }
}
