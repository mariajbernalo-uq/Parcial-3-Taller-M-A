package uniquindio.coordinacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialComandos {
    private final List<Comando> ejecutados =
            new ArrayList<>();

    public void ejecutar(Comando comando) {
        if (comando == null) {
            throw new IllegalArgumentException(
                    "El comando no puede ser nulo."
            );
        }

        comando.ejecutar();

        // Solo se registra si ejecutar() termina sin excepción.
        ejecutados.add(comando);
    }

    public int getCantidadEjecutados() {
        return ejecutados.size();
    }

    public List<Comando> getEjecutados() {
        return Collections.unmodifiableList(
                new ArrayList<>(ejecutados)
        );
    }
}
