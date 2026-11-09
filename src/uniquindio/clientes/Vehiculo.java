package uniquindio.clientes;

import java.util.Locale;

public class Vehiculo {
    private final String placa;
    private final String marca;
    private final String modelo;
    private final Cliente propietario;

    public Vehiculo(
            String placa,
            String marca,
            String modelo,
            Cliente propietario) {

        this.placa = validarTexto(placa, "La placa")
                .toUpperCase(Locale.ROOT);

        this.marca = validarTexto(marca, "La marca");
        this.modelo = validarTexto(modelo, "El modelo");

        if (propietario == null) {
            throw new IllegalArgumentException("El propietario no puede ser nulo.");
        }
        this.propietario = propietario;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    campo + " no puede estar vacío."
            );
        }

        return valor.trim();
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Cliente getPropietario() {
        return propietario;
    }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }
}
