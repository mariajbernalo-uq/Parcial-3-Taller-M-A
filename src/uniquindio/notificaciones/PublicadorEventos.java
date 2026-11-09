package uniquindio.notificaciones;

import java.util.ArrayList;
import java.util.List;

public class PublicadorEventos {
    private final List<ObservadorOrden> observadores = new ArrayList<>();

    public void suscribir(ObservadorOrden observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo.");
        }
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorOrden observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoOrden evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo.");
        }
        List<ObservadorOrden> copia = new ArrayList<>(observadores);
        for (ObservadorOrden observador : copia) {
            try {
                observador.actualizar(evento);
            } catch (RuntimeException error) {
                // El cambio ya ocurrió; fallar al comunicarlo no lo revierte.
                System.out.println("[AVISO] No se pudo notificar: " + error.getMessage());
            }
        }
    }
}
