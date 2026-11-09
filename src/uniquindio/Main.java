package uniquindio;

import java.math.BigDecimal;
import uniquindio.clientes.Cliente;
import uniquindio.clientes.Vehiculo;
import uniquindio.coordinacion.CoordinadorTaller;
import uniquindio.coordinacion.HistorialComandos;
import uniquindio.coordinacion.MediadorTaller;
import uniquindio.cotizacion.TarifaNormal;
import uniquindio.cotizacion.TarifaClienteFrecuente;
import uniquindio.cotizacion.TarifaConvenio;
import uniquindio.facturacion.Facturacion;
import uniquindio.inventario.Inventario;
import uniquindio.notificaciones.ApiNotificaciones;
import uniquindio.notificaciones.NotificadorClienteObserver;
import uniquindio.notificaciones.PanelTallerObserver;
import uniquindio.notificaciones.PublicadorEventos;
import uniquindio.ordenes.*;
import uniquindio.personal.AreaMecanica;
import uniquindio.personal.Mecanico;
import uniquindio.presentacion.MenuPresentacion;
import uniquindio.recepcion.Recepcion;
import uniquindio.reparaciones.ReparacionFrenos;

public class Main {
    public static void main(String[] args) {
        MenuPresentacion pantalla = new MenuPresentacion();
        pantalla.mostrarInicio();

        // CONFIGURACIÓN. Todos los patrones trabajan con este mismo repositorio.
        RepositorioOrdenes repositorio = new RepositorioOrdenesMemoria();
        HistorialComandos comandos = new HistorialComandos();
        HistorialOrden respaldos = new HistorialOrden();
        PublicadorEventos publicador = new PublicadorEventos();
        PanelTallerObserver panel = new PanelTallerObserver();
        NotificadorClienteObserver notificador = new NotificadorClienteObserver(
                new ApiNotificaciones());
        publicador.suscribir(panel);
        publicador.suscribir(notificador);

        Inventario inventario = new Inventario();
        inventario.registrarRepuesto("Pastillas de freno", 10);
        inventario.registrarRepuesto("Liquido de frenos", 5);
        inventario.registrarRepuesto("Filtro de aceite", 4);
        inventario.registrarRepuesto("Fusible", 6);

        ValidadorOrden validadorCliente = new ValidadorCliente();
        ValidadorOrden validadorVehiculo = new ValidadorVehiculo();
        ValidadorOrden validadorDiagnostico = new ValidadorDiagnostico();
        ValidadorOrden validadorPresupuesto = new ValidadorPresupuesto();
        validadorCliente.establecerSiguiente(validadorVehiculo);
        validadorVehiculo.establecerSiguiente(validadorDiagnostico);
        validadorDiagnostico.establecerSiguiente(validadorPresupuesto);

        MediadorTaller mediador = new CoordinadorTaller(repositorio, comandos,
                respaldos, publicador, inventario, new Facturacion(), validadorCliente);
        Recepcion recepcion = new Recepcion(mediador);
        AreaMecanica mecanica = new AreaMecanica(mediador);

        Cliente cliente = new Cliente("1001234567", "Laura Gómez", "3000000000");
        Vehiculo vehiculo = new Vehiculo("ABC123", "Renault", "Sandero", cliente);
        Mecanico mecanico = new Mecanico(1, "Ana", "Frenos");

        pantalla.mostrarPaso("1. MEDIATOR + COMMAND: recepción registra la orden");
        recepcion.registrarOrden(1, vehiculo);
        OrdenServicio orden = mediador.consultar(1);
        System.out.println(orden);

        pantalla.mostrarPaso("2. COMMAND: comprobar número duplicado");
        boolean duplicadaRechazada = false;
        try {
            recepcion.registrarOrden(1, vehiculo);
        } catch (IllegalArgumentException error) {
            duplicadaRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!duplicadaRechazada) {
            throw new IllegalStateException("La demostración debía rechazar la orden duplicada.");
        }

        pantalla.mostrarPaso("3. STATE: no puede iniciar sin mecánico");
        boolean sinMecanicoRechazada = false;
        try {
            mecanica.iniciarDiagnostico(1);
        } catch (IllegalStateException error) {
            sinMecanicoRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!sinMecanicoRechazada) {
            throw new IllegalStateException("Debía exigir un mecánico.");
        }

        pantalla.mostrarPaso("4. COMMAND + STATE + OBSERVER: iniciar diagnóstico");
        recepcion.asignarMecanico(1, mecanico);
        mecanica.iniciarDiagnostico(1);
        mecanica.registrarDiagnostico(1, "Desgaste de pastillas y líquido de frenos.");

        // Los precios incluyen material y mano de obra; no se cobra el inventario dos veces.
        mecanica.agregarServicio(1, new ItemServicio("Cambio de pastillas",
                new BigDecimal("120000")));
        mecanica.agregarServicio(1, new ItemServicio("Cambio de líquido y revisión",
                new BigDecimal("80000")));

        pantalla.mostrarPaso("5. CHAIN: detener una orden todavía sin presupuesto");
        boolean sinPresupuestoRechazada = false;
        try {
            recepcion.solicitarAprobacion(1);
        } catch (IllegalStateException error) {
            sinPresupuestoRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!sinPresupuestoRechazada) {
            throw new IllegalStateException("La cadena debía exigir presupuesto.");
        }

        pantalla.mostrarPaso("6. STRATEGY + COMMAND: comparar tres tarifas");
        recepcion.cotizar(1, new TarifaNormal());
        pantalla.mostrarCotizacion(orden);
        recepcion.cotizar(1, new TarifaClienteFrecuente());
        pantalla.mostrarCotizacion(orden);
        recepcion.cotizar(1, new TarifaConvenio());
        pantalla.mostrarCotizacion(orden);

        pantalla.mostrarPaso("7. MEMENTO: recuperar una edición accidental");
        mecanica.registrarDiagnostico(1, "Diagnóstico ingresado por error.");
        System.out.println("Diagnóstico modificado: " + orden.getDiagnostico());
        pantalla.mostrarCotizacion(orden);
        boolean restaurado = mecanica.deshacerUltimaEdicion(1);
        if (!restaurado) {
            throw new IllegalStateException("Falta el respaldo de la edición.");
        }
        System.out.println("Diagnóstico recuperado: " + orden.getDiagnostico());
        pantalla.mostrarCotizacion(orden);

        pantalla.mostrarPaso("8. CHAIN + STATE + OBSERVER: solicitar aprobación");
        recepcion.solicitarAprobacion(1);

        pantalla.mostrarPaso("9. STATE: bloquear edición del presupuesto enviado");
        boolean edicionRechazada = false;
        try {
            recepcion.cotizar(1, new TarifaNormal());
        } catch (IllegalStateException error) {
            edicionRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!edicionRechazada) {
            throw new IllegalStateException("Debía impedir la modificación.");
        }

        pantalla.mostrarPaso("10. MEDIATOR + STATE: registrar aprobación del cliente");
        recepcion.registrarAprobacionCliente(1);

        pantalla.mostrarPaso("11. INVENTARIO + TEMPLATE METHOD: ejecutar reparación");
        mecanica.reparar(1, new ReparacionFrenos());
        System.out.println("Pastillas restantes: "
                + inventario.consultarExistencias("Pastillas de freno"));
        System.out.println("Líquido restante: "
                + inventario.consultarExistencias("Liquido de frenos"));

        pantalla.mostrarPaso("12. STATE: impedir repetir la reparación");
        boolean repeticionRechazada = false;
        try {
            mecanica.reparar(1, new ReparacionFrenos());
        } catch (IllegalStateException error) {
            repeticionRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!repeticionRechazada) {
            throw new IllegalStateException("Debía impedir reparar dos veces.");
        }

        pantalla.mostrarPaso("13. OBSERVER: retirar una suscripción");
        publicador.desuscribir(notificador);
        System.out.println("En la entrega solo recibirá el evento el panel del taller.");

        pantalla.mostrarPaso("14. MEDIATOR + FACTURACIÓN + STATE: entregar vehículo");
        String factura = recepcion.entregarVehiculo(1);
        System.out.println(factura);

        pantalla.mostrarPaso("15. STATE: impedir modificar una orden entregada");
        boolean ordenCerradaRechazada = false;
        try {
            mecanica.registrarDiagnostico(1, "Intento de modificar una orden cerrada.");
        } catch (IllegalStateException error) {
            ordenCerradaRechazada = true;
            System.out.println("[RECHAZADO] " + error.getMessage());
        }
        if (!ordenCerradaRechazada) {
            throw new IllegalStateException("Debía impedir editar una orden entregada.");
        }

        pantalla.mostrarPaso("RESULTADO FINAL - Taller M&A");
        pantalla.mostrarDetalle(orden);
        System.out.println("Órdenes registradas: " + mediador.listar().size());
        System.out.println("Comandos exitosos: " + comandos.getCantidadEjecutados());
        System.out.println("Los ocho patrones participaron sobre la misma orden #1.");
    }
}
