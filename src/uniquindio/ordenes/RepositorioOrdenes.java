package uniquindio.ordenes;

import java.util.List;

public interface RepositorioOrdenes {
    void guardar(OrdenServicio orden);
    // Devuelve null si no encuentra la orden. El llamador debe comprobarlo.
    OrdenServicio buscarPorNumero(int numero);
    List<OrdenServicio> listar();
}
