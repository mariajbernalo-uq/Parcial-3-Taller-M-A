package uniquindio.ordenes;



public class ValidadorDiagnostico extends ValidadorOrden {

    @Override
    protected String validarActual(OrdenServicio orden) {

        if (orden.getMecanico() == null) {
            return "Error: debe asignar un mecánico.";
        }
        if (orden.getServicios().isEmpty()) {
            return "Error: debe registrar los servicios.";
        }
        if (orden.getDiagnostico() == null) {
            return "Error: la orden no tiene un diagnóstico registrado.";
        }

        if (orden.getDiagnostico() == null ||
                orden.getDiagnostico().trim().isEmpty()) {

            return "Error: la descripción del diagnóstico está vacía.";
        }

        return null;
    }
}
