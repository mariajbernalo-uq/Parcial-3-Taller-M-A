package uniquindio.ordenes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Recuerdo inmutable. No expone sus datos fuera del paquete de órdenes.
public final class MementoOrden {
    final OrdenServicio ordenOrigen;
    final int versionEtapa;
    final String diagnostico;
    final List<ItemServicio> servicios;
    final BigDecimal presupuesto;
    final String nombreTarifa;

    MementoOrden(OrdenServicio ordenOrigen, int versionEtapa, String diagnostico,
            List<ItemServicio> servicios, BigDecimal presupuesto, String nombreTarifa) {
        this.ordenOrigen = ordenOrigen;
        this.versionEtapa = versionEtapa;
        this.diagnostico = diagnostico;

        List<ItemServicio> copia = new ArrayList<>();
        for (ItemServicio servicio : servicios) {
            copia.add(new ItemServicio(servicio.getDescripcion(), servicio.getValorBase()));
        }
        this.servicios = Collections.unmodifiableList(copia);
        this.presupuesto = presupuesto;
        this.nombreTarifa = nombreTarifa;
    }
}
