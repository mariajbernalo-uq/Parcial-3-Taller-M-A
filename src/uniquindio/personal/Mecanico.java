package uniquindio.personal;

public class Mecanico {
    private final int id;
    private final String nombre;
    private final String especialidad;

    public Mecanico(int id, String nombre, String especialidad) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        this.id = id;
        this.nombre = validarTexto(nombre, "El nombre");
        this.especialidad = validarTexto(
                especialidad, "La especialidad"
        );
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    campo + " no puede estar vacío."
            );
        }

        return valor.trim();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + especialidad + ")";
    }
}
