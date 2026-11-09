package uniquindio.inventario;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

// Base de datos de repuestos simulada en memoria.
public class Inventario {
    private final Map<String, Integer> existencias = new LinkedHashMap<>();

    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del repuesto es obligatorio.");
        }
        return nombre.trim().toLowerCase(Locale.ROOT);
    }

    public void registrarRepuesto(String nombre, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        // Registra la existencia total; no suma una entrada adicional.
        existencias.put(normalizarNombre(nombre), cantidad);
    }

    private Map<String, Integer> validarSolicitud(Map<String, Integer> solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("Debe indicar los repuestos requeridos.");
        }
        Map<String, Integer> copia = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : solicitud.entrySet()) {
            String nombre = normalizarNombre(entrada.getKey());
            Integer cantidad = entrada.getValue();
            if (cantidad == null || cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad solicitada debe ser positiva.");
            }
            Integer anterior = copia.get(nombre);
            if (anterior == null) {
                anterior = 0;
            }
            if (cantidad > Integer.MAX_VALUE - anterior) {
                throw new IllegalArgumentException("La cantidad solicitada es demasiado grande.");
            }
            copia.put(nombre, anterior + cantidad);
        }
        return copia;
    }

    public boolean hayDisponibilidad(Map<String, Integer> solicitud) {
        Map<String, Integer> cantidades = validarSolicitud(solicitud);
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            int disponible = consultarExistencias(entrada.getKey());
            if (disponible < entrada.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void descontarRepuestos(Map<String, Integer> solicitud) {
        Map<String, Integer> cantidades = validarSolicitud(solicitud);
        // Primero comprueba todos; solo después modifica el inventario.
        if (!hayDisponibilidad(cantidades)) {
            throw new IllegalStateException("Faltan repuestos. No se descontó ninguna existencia.");
        }
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            int actual = consultarExistencias(entrada.getKey());
            existencias.put(entrada.getKey(), actual - entrada.getValue());
        }
        System.out.println("[INVENTARIO] Repuestos utilizados: " + cantidades);
    }

    public void devolverRepuestos(Map<String, Integer> solicitud) {
        Map<String, Integer> cantidades = validarSolicitud(solicitud);
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            int actual = consultarExistencias(entrada.getKey());
            if (entrada.getValue() > Integer.MAX_VALUE - actual) {
                throw new IllegalArgumentException("La existencia supera el máximo permitido.");
            }
        }
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            int actual = consultarExistencias(entrada.getKey());
            existencias.put(entrada.getKey(), actual + entrada.getValue());
        }
    }

    public int consultarExistencias(String nombre) {
        Integer cantidad = existencias.get(normalizarNombre(nombre));
        if (cantidad == null) {
            return 0;
        }
        return cantidad;
    }
}
