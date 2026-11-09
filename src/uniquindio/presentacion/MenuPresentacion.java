package uniquindio.presentacion;

import uniquindio.ordenes.ItemServicio;
import uniquindio.ordenes.OrdenServicio;

// Presentación por consola. Conserva el nombre de tu paquete original.
public class MenuPresentacion {
    public void mostrarInicio() {
        System.out.println("========================================");
        System.out.println("              Taller M&A");
        System.out.println("      Gestión de órdenes de servicio");
        System.out.println("========================================");
    }

    public void mostrarPaso(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
    }

    public void mostrarCotizacion(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("Debe indicar una orden.");
        }
        if (orden.tieneCotizacion()) {
            System.out.println(orden.getNombreTarifa() + " -> $" + orden.getPresupuesto());
        } else {
            System.out.println("La orden no tiene una cotización vigente.");
        }
    }

    public void mostrarDetalle(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("Debe indicar una orden.");
        }
        System.out.println(orden);
        System.out.println("Cliente: " + orden.getVehiculo().getPropietario().getNombre());
        System.out.println("Diagnóstico: " + orden.getDiagnostico());
        System.out.println("Servicios:");
        for (ItemServicio servicio : orden.getServicios()) {
            System.out.println("  " + servicio);
        }
        System.out.println("Valor base: $" + orden.calcularValorBase());
        mostrarCotizacion(orden);
    }
}
