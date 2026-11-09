package uniquindio.clientes;

public class Cliente {
    private final String identificacion;
    private final String nombre;
    private final String telefono;

    public Cliente(
            String identificacion,
            String nombre,
            String telefono) {

        this.identificacion = validarTexto(
                identificacion, "La identificación"
        );
        this.nombre = validarTexto(nombre, "El nombre");
        this.telefono = validarTexto(telefono, "El teléfono");
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    campo + " no puede estar vacío."
            );
        }

        return valor.trim();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public String toString() {
        return nombre + " - Documento: " + identificacion;
    }
}
