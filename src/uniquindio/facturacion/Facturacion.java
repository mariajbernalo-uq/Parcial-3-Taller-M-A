package uniquindio.facturacion;

import uniquindio.ordenes.OrdenServicio;

// Genera texto de demostración, sin cobro ni conexión con la DIAN.
public class Facturacion {
    public String generarFactura(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden a facturar.");
        }
        if (!orden.getNombreEstado().equals("LISTA_ENTREGA")) {
            throw new IllegalStateException("La orden debe estar lista para entrega.");
        }
        if (!orden.tieneCotizacion() || orden.getPresupuesto().signum() <= 0) {
            throw new IllegalStateException("La orden requiere un presupuesto válido.");
        }

        return "\nFACTURA SIMULADA - Taller M&A"
                + "\nNúmero: MA-" + orden.getNumero()
                + "\nCliente: " + orden.getVehiculo().getPropietario().getNombre()
                + "\nDocumento: " + orden.getVehiculo().getPropietario().getIdentificacion()
                + "\nVehículo: " + orden.getVehiculo().getPlaca()
                + "\nTarifa: " + orden.getNombreTarifa()
                + "\nTotal: $" + orden.getPresupuesto();
    }
}
