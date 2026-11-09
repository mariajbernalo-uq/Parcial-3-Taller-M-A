package uniquindio.ordenes;



public class ValidadorVehiculo extends ValidadorOrden {

    @Override
    protected String validarActual(OrdenServicio orden) {

        if (orden.getVehiculo() == null) {
            return "Error: la orden no tiene un vehículo asociado.";
        }

        if (orden.getVehiculo().getPlaca() == null ||
                orden.getVehiculo().getPlaca().trim().isEmpty()) {

            return "Error: el vehículo no tiene una placa válida.";
        }

        return null;
    }
}
