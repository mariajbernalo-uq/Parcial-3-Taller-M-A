package uniquindio.ordenes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioOrdenesMemoria implements RepositorioOrdenes {
    private final Map<Integer, OrdenServicio> ordenes = new LinkedHashMap<>();

    @Override
    public void guardar(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula.");
        }
        ordenes.put(orden.getNumero(), orden);
    }

    @Override
    public OrdenServicio buscarPorNumero(int numero) {
        return ordenes.get(numero);
    }

    @Override
    public List<OrdenServicio> listar() {
        return new ArrayList<>(ordenes.values());
    }
}
