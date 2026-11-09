package uniquindio.ordenes;



public abstract class ValidadorOrden {

    private ValidadorOrden siguiente;

    public void establecerSiguiente(ValidadorOrden siguiente) {
        this.siguiente = siguiente;
    }

    public String validar(OrdenServicio orden) {

        if (orden == null) {
            return "Error: la orden no existe.";
        }
        System.out.println("[CADENA] " + getClass().getSimpleName());
        String resultado = validarActual(orden);

        // Si hay un error, se detiene la cadena.
        if (resultado != null) {
            return resultado;
        }

        // Si existe otro validador, continúa con él.
        if (siguiente != null) {
            return siguiente.validar(orden);
        }

        return "La orden de reparación ha sido validada correctamente.";
    }

    protected abstract String validarActual(OrdenServicio orden);
}
