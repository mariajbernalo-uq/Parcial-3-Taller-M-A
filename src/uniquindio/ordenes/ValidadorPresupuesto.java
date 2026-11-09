package uniquindio.ordenes;



public class ValidadorPresupuesto extends ValidadorOrden {

    @Override
    protected String validarActual(OrdenServicio orden) {

        if (!orden.tieneCotizacion() ||
                orden.getPresupuesto().signum() <= 0) {

            return "Error: el presupuesto debe ser mayor que cero.";
        }

        return null;
    }
}
