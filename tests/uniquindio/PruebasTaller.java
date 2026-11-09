package uniquindio;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import uniquindio.clientes.*;
import uniquindio.personal.Mecanico;
import uniquindio.coordinacion.*;
import uniquindio.cotizacion.*;
import uniquindio.ordenes.*;
import uniquindio.notificaciones.*;
import uniquindio.inventario.Inventario;
import uniquindio.facturacion.Facturacion;
import uniquindio.reparaciones.*;

// Pruebas ejecutables sin JUnit. No se necesitan para iniciar el Main.
public class PruebasTaller {
    private static int comprobaciones;

    private static class ContadorEventos implements ObservadorOrden {
        int cantidad;
        @Override
        public void actualizar(EventoOrden evento) {
            cantidad++;
        }
    }

    private static class Escenario {
        final RepositorioOrdenes repositorio = new RepositorioOrdenesMemoria();
        final HistorialComandos comandos = new HistorialComandos();
        final HistorialOrden respaldos = new HistorialOrden();
        final Inventario inventario = new Inventario();
        final PublicadorEventos publicador = new PublicadorEventos();
        final ContadorEventos eventos = new ContadorEventos();
        final MediadorTaller mediador;

        Escenario() {
            ValidadorOrden cliente = new ValidadorCliente();
            ValidadorOrden vehiculo = new ValidadorVehiculo();
            ValidadorOrden diagnostico = new ValidadorDiagnostico();
            ValidadorOrden presupuesto = new ValidadorPresupuesto();
            cliente.establecerSiguiente(vehiculo);
            vehiculo.establecerSiguiente(diagnostico);
            diagnostico.establecerSiguiente(presupuesto);
            publicador.suscribir(eventos);
            mediador = new CoordinadorTaller(repositorio, comandos, respaldos,
                    publicador, inventario, new Facturacion(), cliente);
        }

        OrdenServicio preparar(int numero) {
            Cliente cliente = new Cliente("123", "Cliente", "3000000000");
            mediador.crearOrden(numero, new Vehiculo("ABC" + numero, "Marca", "2026", cliente));
            mediador.asignarMecanico(numero, new Mecanico(1, "Ana", "General"));
            mediador.iniciarDiagnostico(numero);
            mediador.actualizarDiagnostico(numero, "Diagnóstico original");
            mediador.agregarServicio(numero, new ItemServicio("Servicio", new BigDecimal("200000")));
            mediador.cotizar(numero, new TarifaClienteFrecuente());
            return mediador.consultar(numero);
        }

        void cargarRepuestos() {
            inventario.registrarRepuesto("Pastillas de freno", 10);
            inventario.registrarRepuesto("Liquido de frenos", 5);
        }
    }

    private static class ProcesoFallido extends ProcesoReparacion {
        boolean llegoAPruebas;
        @Override
        public Map<String, Integer> getRepuestosNecesarios() {
            Map<String, Integer> repuestos = new LinkedHashMap<>();
            repuestos.put("Pastillas de freno", 2);
            repuestos.put("Liquido de frenos", 1);
            return repuestos;
        }
        @Override
        protected void prepararHerramientas() { }
        @Override
        protected void realizarReparacion() {
            throw new IllegalStateException("Fallo simulado de reparación.");
        }
        @Override
        protected void realizarPruebas() { llegoAPruebas = true; }
    }

    private static class ProcesoConRegistro extends ProcesoReparacion {
        final List<String> pasos = new ArrayList<>();
        @Override
        public Map<String, Integer> getRepuestosNecesarios() {
            return new LinkedHashMap<>();
        }
        @Override
        protected void prepararHerramientas() { pasos.add("preparar"); }
        @Override
        protected void realizarReparacion() { pasos.add("reparar"); }
        @Override
        protected void realizarPruebas() { pasos.add("probar"); }
    }

    private static class ValidadorPrueba extends ValidadorOrden {
        int llamadas;
        private final boolean rechaza;
        ValidadorPrueba(boolean rechaza) { this.rechaza = rechaza; }
        @Override
        protected String validarActual(OrdenServicio orden) {
            llamadas++;
            if (rechaza) {
                return "Error: rechazo de prueba.";
            }
            return null;
        }
    }

    private static class ObservadorFallido implements ObservadorOrden {
        @Override
        public void actualizar(EventoOrden evento) {
            throw new IllegalStateException("Fallo simulado de notificación.");
        }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        comprobaciones++;
    }

    public static void main(String[] args) {
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream registro = new ByteArrayOutputStream();
        PrintStream salidaPruebas = new PrintStream(registro);
        System.setOut(salidaPruebas);
        try {
            probarStrategy();
            probarCommand();
            probarMemento();
            probarChain();
            probarStateYFlujo();
            probarInventario();
            probarFalloYReintento();
            probarTemplateMethod();
            probarObserver();
        } finally {
            System.setOut(salidaOriginal);
            salidaPruebas.close();
        }
        System.out.println("Taller M&A: " + comprobaciones + " comprobaciones correctas.");
    }

    private static void probarStrategy() {
        BigDecimal base = new BigDecimal("200000");
        CalculadoraPresupuesto calculadora = new CalculadoraPresupuesto(new TarifaNormal());
        comprobar(calculadora.calcular(base).compareTo(new BigDecimal("200000")) == 0, "Tarifa normal");
        calculadora.cambiarEstrategia(new TarifaClienteFrecuente());
        comprobar(calculadora.calcular(base).compareTo(new BigDecimal("180000")) == 0, "Tarifa frecuente");
        calculadora.cambiarEstrategia(new TarifaConvenio());
        comprobar(calculadora.calcular(base).compareTo(new BigDecimal("170000")) == 0, "Tarifa convenio");
        comprobar(calculadora.calcular(new BigDecimal("0.05")).compareTo(new BigDecimal("0.04")) == 0, "Redondeo");
        boolean rechazo = false;
        try { calculadora.calcular(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Valor base nulo");
        rechazo = false;
        try { calculadora.calcular(new BigDecimal("-1")); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Valor base negativo");
        rechazo = false;
        try { calculadora.cambiarEstrategia(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Estrategia nula");
    }

    private static void probarCommand() {
        Escenario e = new Escenario();
        OrdenServicio orden = e.preparar(1);
        comprobar(e.comandos.getCantidadEjecutados() == 6, "Seis comandos de preparación");
        boolean rechazo = false;
        try { e.mediador.crearOrden(1, orden.getVehiculo()); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Número duplicado");
        comprobar(e.comandos.getCantidadEjecutados() == 6, "No registrar creación fallida");
        int respaldos = e.respaldos.cantidadEstadosGuardados(orden);
        rechazo = false;
        try { e.mediador.actualizarDiagnostico(1, " "); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Diagnóstico inválido");
        comprobar(e.comandos.getCantidadEjecutados() == 6, "No registrar edición fallida");
        comprobar(e.respaldos.cantidadEstadosGuardados(orden) == respaldos, "No conservar respaldo fallido");
        comprobar(orden.tieneCotizacion(), "Edición fallida conserva cotización");
        rechazo = false;
        try { e.mediador.asignarMecanico(99, new Mecanico(1, "Ana", "General")); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Orden inexistente");
        comprobar(e.repositorio.listar().size() == 1, "Una sola orden");
    }

    private static void probarMemento() {
        Escenario e = new Escenario();
        OrdenServicio primera = e.preparar(1);
        OrdenServicio segunda = e.preparar(2);
        MementoOrden recuerdo = primera.crearMemento();
        boolean rechazo = false;
        try { segunda.restaurarMemento(recuerdo); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "No restaurar en otra orden");
        e.mediador.actualizarDiagnostico(1, "Error de digitación");
        comprobar(!primera.tieneCotizacion(), "Editar invalida cotización");
        comprobar(e.mediador.deshacerUltimaEdicion(1), "Existe respaldo de edición");
        comprobar(primera.getDiagnostico().equals("Diagnóstico original"), "Recuperar diagnóstico");
        comprobar(primera.getPresupuesto().compareTo(new BigDecimal("180000")) == 0, "Recuperar presupuesto");
        comprobar(primera.getNombreTarifa().contains("10 %"), "Recuperar tarifa");
        comprobar(segunda.getDiagnostico().equals("Diagnóstico original"), "Segunda orden intacta");
        e.mediador.agregarServicio(1, new ItemServicio("Adicional", BigDecimal.TEN));
        comprobar(primera.getServicios().size() == 2, "Servicio adicional");
        e.mediador.deshacerUltimaEdicion(1);
        comprobar(primera.getServicios().size() == 1, "Recuperar colección de servicios");
        comprobar(primera.tieneCotizacion(), "Recuperar cotización invalidada por servicio");
        rechazo = false;
        try { primera.getServicios().clear(); }
        catch (UnsupportedOperationException error) { rechazo = true; }
        comprobar(rechazo, "No modificar servicios por el getter");
        e.mediador.solicitarAprobacion(1);
        comprobar(e.respaldos.cantidadEstadosGuardados(primera) == 0, "Limpiar historial al enviar presupuesto");
        rechazo = false;
        try { primera.restaurarMemento(recuerdo); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No restaurar después de cambiar de etapa");
        rechazo = false;
        try { e.mediador.deshacerUltimaEdicion(1); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No deshacer presupuesto enviado");
    }

    private static void probarChain() {
        Escenario e = new Escenario();
        OrdenServicio orden = e.preparar(1);
        orden.registrarCotizacion(BigDecimal.ZERO, "Preliminar");
        int eventos = e.eventos.cantidad;
        boolean rechazo = false;
        try { e.mediador.solicitarAprobacion(1); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "La cadena exige presupuesto positivo");
        comprobar(orden.getNombreEstado().equals("EN_DIAGNOSTICO"), "Cadena rechazada no avanza");
        comprobar(e.eventos.cantidad == eventos, "Cadena rechazada no notifica");
        ValidadorPrueba primero = new ValidadorPrueba(true);
        ValidadorPrueba segundo = new ValidadorPrueba(false);
        primero.establecerSiguiente(segundo);
        comprobar(primero.validar(orden).startsWith("Error:"), "Propagar error de cadena");
        comprobar(primero.llamadas == 1 && segundo.llamadas == 0, "Detener cadena en primer fallo");
        ValidadorPrueba tercero = new ValidadorPrueba(false);
        tercero.establecerSiguiente(segundo);
        comprobar(!tercero.validar(orden).startsWith("Error:"), "Cadena completa correcta");
        comprobar(segundo.llamadas == 1, "Delegar al siguiente");
        comprobar(new ValidadorCliente().validar(null).startsWith("Error:"), "Orden nula en cadena");
    }

    private static void probarStateYFlujo() {
        Escenario e = new Escenario();
        OrdenServicio orden = e.preparar(1);
        e.mediador.solicitarAprobacion(1);
        boolean rechazo = false;
        try { orden.avanzarEstado(); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "State exige aprobación explícita");
        e.mediador.aprobarPresupuesto(1);
        comprobar(orden.isPresupuestoAprobado(), "Aprobación registrada");
        rechazo = false;
        try { orden.avanzarEstado(); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "State exige reparación y pruebas");
        int eventos = e.eventos.cantidad;
        rechazo = false;
        try { e.mediador.reparar(1, new ReparacionFrenos()); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "Stock insuficiente");
        comprobar(orden.getNombreEstado().equals("EN_REPARACION"), "Sin stock sigue en reparación");
        comprobar(e.eventos.cantidad == eventos, "Sin stock no anunciar trabajo terminado");
        rechazo = false;
        try { e.mediador.entregar(1); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No entregar antes de terminar");
        e.cargarRepuestos();
        e.mediador.reparar(1, new ReparacionFrenos());
        comprobar(orden.isReparacionFinalizada(), "Plantilla registra finalización");
        comprobar(orden.getNombreEstado().equals("LISTA_ENTREGA"), "State avanza después de plantilla");
        comprobar(e.inventario.consultarExistencias("Pastillas de freno") == 8, "Consumir dos pastillas");
        comprobar(e.inventario.consultarExistencias("Liquido de frenos") == 4, "Consumir un líquido");
        rechazo = false;
        try { e.mediador.reparar(1, new ReparacionFrenos()); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No repetir reparación");
        comprobar(e.inventario.consultarExistencias("Pastillas de freno") == 8, "No descontar dos veces");
        String factura = e.mediador.entregar(1);
        comprobar(factura.contains("Taller M&A") && factura.contains("180000.00"), "Factura usa total vigente");
        comprobar(orden.getNombreEstado().equals("ENTREGADA"), "Orden entregada");
        comprobar(e.eventos.cantidad == 5, "Observer recibe cinco transiciones");
        rechazo = false;
        try { e.mediador.entregar(1); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No entregar dos veces");
        rechazo = false;
        try { orden.actualizarDiagnostico("Cambio"); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No editar orden cerrada");
    }

    private static void probarInventario() {
        Inventario inventario = new Inventario();
        inventario.registrarRepuesto("Pastillas", 10);
        Map<String, Integer> solicitud = new LinkedHashMap<>();
        solicitud.put("Pastillas", 2);
        solicitud.put("Faltante", 1);
        boolean rechazo = false;
        try { inventario.descontarRepuestos(solicitud); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "Comprobar todos los materiales primero");
        comprobar(inventario.consultarExistencias("PASTILLAS") == 10, "Descuento no parcial");
        solicitud.clear();
        solicitud.put("Pastillas", 6);
        solicitud.put(" PASTILLAS ", 5);
        comprobar(!inventario.hayDisponibilidad(solicitud), "Agrupar nombres equivalentes");
        solicitud.clear();
        solicitud.put("Pastillas", -1);
        rechazo = false;
        try { inventario.descontarRepuestos(solicitud); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "No consumir cantidades negativas");
        rechazo = false;
        try { inventario.hayDisponibilidad(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Solicitud nula");
        comprobar(inventario.consultarExistencias("Desconocido") == 0, "Material no registrado");
    }

    private static void probarFalloYReintento() {
        Escenario e = new Escenario();
        OrdenServicio orden = e.preparar(1);
        e.mediador.solicitarAprobacion(1);
        e.mediador.aprobarPresupuesto(1);
        e.cargarRepuestos();
        ProcesoFallido proceso = new ProcesoFallido();
        boolean rechazo = false;
        try { e.mediador.reparar(1, proceso); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "Propagar fallo de reparación");
        comprobar(!proceso.llegoAPruebas, "No continuar la plantilla tras fallo");
        comprobar(!orden.isReparacionFinalizada(), "No marcar como finalizada una reparación fallida");
        comprobar(orden.getNombreEstado().equals("EN_REPARACION"), "No avanzar tras fallo");
        comprobar(e.inventario.consultarExistencias("Pastillas de freno") == 10, "Devolver pastillas en simulación");
        comprobar(e.inventario.consultarExistencias("Liquido de frenos") == 5, "Devolver líquido en simulación");
        e.mediador.reparar(1, new ReparacionFrenos());
        comprobar(orden.getNombreEstado().equals("LISTA_ENTREGA"), "Reintento correcto");
        comprobar(e.inventario.consultarExistencias("Pastillas de freno") == 8, "Reintento consume una vez");
    }

    private static void probarTemplateMethod() {
        ProcesoReparacion[] procesos = {new ReparacionMotor(), new ReparacionElectrica()};
        String[] materiales = {"Filtro de aceite", "Fusible"};
        for (int i = 0; i < procesos.length; i++) {
            Escenario e = new Escenario();
            OrdenServicio orden = e.preparar(1);
            e.mediador.solicitarAprobacion(1);
            e.mediador.aprobarPresupuesto(1);
            e.inventario.registrarRepuesto(materiales[i], 5);
            e.mediador.reparar(1, procesos[i]);
            comprobar(orden.getNombreEstado().equals("LISTA_ENTREGA"), "Variante de reparación completada");
            comprobar(e.inventario.consultarExistencias(materiales[i]) == 4, "Material de variante consumido");
        }
        Escenario e = new Escenario();
        OrdenServicio orden = e.preparar(1);
        boolean rechazo = false;
        try { new ReparacionFrenos().ejecutarProceso(orden); }
        catch (IllegalStateException error) { rechazo = true; }
        comprobar(rechazo, "No ejecutar plantilla antes de aprobación");
        e.mediador.solicitarAprobacion(1);
        e.mediador.aprobarPresupuesto(1);
        ProcesoConRegistro registro = new ProcesoConRegistro();
        e.mediador.reparar(1, registro);
        comprobar(registro.pasos.size() == 3, "Tres pasos de plantilla");
        comprobar(registro.pasos.get(0).equals("preparar"), "Primer paso preparar");
        comprobar(registro.pasos.get(1).equals("reparar"), "Segundo paso reparar");
        comprobar(registro.pasos.get(2).equals("probar"), "Tercer paso probar");
    }

    private static void probarObserver() {
        PublicadorEventos publicador = new PublicadorEventos();
        ContadorEventos contador = new ContadorEventos();
        EventoOrden evento = new EventoOrden(1, "ABC123", "300", "A", "B");
        publicador.suscribir(contador);
        publicador.suscribir(contador);
        publicador.publicar(evento);
        comprobar(contador.cantidad == 1, "No duplicar suscripción");
        publicador.desuscribir(contador);
        publicador.publicar(evento);
        comprobar(contador.cantidad == 1, "Desuscripción efectiva");
        boolean rechazo = false;
        try { publicador.suscribir(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Rechazar suscripción nula");
        rechazo = false;
        try { publicador.publicar(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Rechazar evento nulo");
        rechazo = false;
        try { new NotificadorClienteObserver(null); }
        catch (IllegalArgumentException error) { rechazo = true; }
        comprobar(rechazo, "Rechazar servicio nulo");
        publicador.suscribir(new ObservadorFallido());
        publicador.suscribir(contador);
        publicador.publicar(evento);
        comprobar(contador.cantidad == 2, "Un observador fallido no bloquea a los demás");
    }
}
