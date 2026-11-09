package uniquindio.coordinacion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import uniquindio.clientes.Vehiculo;
import uniquindio.personal.Mecanico;
import uniquindio.ordenes.*;
import uniquindio.cotizacion.CotizarOrdenCommand;
import uniquindio.cotizacion.EstrategiaTarifa;
import uniquindio.notificaciones.PublicadorEventos;
import uniquindio.inventario.Inventario;
import uniquindio.facturacion.Facturacion;
import uniquindio.reparaciones.ProcesoReparacion;

// Mediador concreto: recepción y mecánica no coordinan directamente entre sí.
public class CoordinadorTaller implements MediadorTaller {
    private final RepositorioOrdenes repositorio;
    private final HistorialComandos comandos;
    private final HistorialOrden respaldos;
    private final PublicadorEventos publicador;
    private final Inventario inventario;
    private final Facturacion facturacion;
    private final ValidadorOrden validadores;

    public CoordinadorTaller(RepositorioOrdenes repositorio, HistorialComandos comandos,
            HistorialOrden respaldos, PublicadorEventos publicador,
            Inventario inventario, Facturacion facturacion, ValidadorOrden validadores) {
        if (repositorio == null || comandos == null || respaldos == null
                || publicador == null || inventario == null
                || facturacion == null || validadores == null) {
            throw new IllegalArgumentException("Debe configurar todos los componentes del taller.");
        }
        this.repositorio = repositorio;
        this.comandos = comandos;
        this.respaldos = respaldos;
        this.publicador = publicador;
        this.inventario = inventario;
        this.facturacion = facturacion;
        this.validadores = validadores;
    }

    @Override
    public OrdenServicio consultar(int numero) {
        OrdenServicio orden = repositorio.buscarPorNumero(numero);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numero);
        }
        return orden;
    }

    @Override
    public List<OrdenServicio> listar() {
        return repositorio.listar();
    }

    private void exigirEstado(OrdenServicio orden, String estadoEsperado) {
        if (!orden.getNombreEstado().equals(estadoEsperado)) {
            throw new IllegalStateException("Se requiere " + estadoEsperado
                    + "; el estado actual es " + orden.getNombreEstado());
        }
    }

    private void avanzar(int numero) {
        comandos.ejecutar(new AvanzarOrdenCommand(repositorio, numero, publicador));
    }

    private void validarOrdenCompleta(OrdenServicio orden) {
        String resultado = validadores.validar(orden);
        if (resultado.startsWith("Error:")) {
            throw new IllegalStateException(resultado);
        }
        System.out.println("[CADENA] " + resultado);
    }

    private void ejecutarConRespaldo(OrdenServicio orden, Comando comando) {
        respaldos.guardarEstado(orden);
        try {
            comandos.ejecutar(comando);
        } catch (RuntimeException error) {
            // Estos comandos validan antes de modificar. No guardamos una edición fallida.
            respaldos.descartarUltimoEstado(orden);
            throw error;
        }
    }

    @Override
    public void crearOrden(int numero, Vehiculo vehiculo) {
        comandos.ejecutar(new CrearOrdenCommand(repositorio, numero, vehiculo));
    }

    @Override
    public void asignarMecanico(int numero, Mecanico mecanico) {
        comandos.ejecutar(new AsignarMecanicoCommand(repositorio, numero, mecanico));
    }

    @Override
    public void iniciarDiagnostico(int numero) {
        exigirEstado(consultar(numero), "REGISTRADA");
        avanzar(numero);
    }

    @Override
    public void actualizarDiagnostico(int numero, String diagnostico) {
        OrdenServicio orden = consultar(numero);
        ejecutarConRespaldo(orden,
                new ActualizarDiagnosticoCommand(repositorio, numero, diagnostico));
    }

    @Override
    public void agregarServicio(int numero, ItemServicio servicio) {
        OrdenServicio orden = consultar(numero);
        ejecutarConRespaldo(orden, new AgregarServicioCommand(repositorio, numero, servicio));
    }

    @Override
    public void cotizar(int numero, EstrategiaTarifa tarifa) {
        OrdenServicio orden = consultar(numero);
        ejecutarConRespaldo(orden, new CotizarOrdenCommand(repositorio, numero, tarifa));
    }

    @Override
    public boolean deshacerUltimaEdicion(int numero) {
        OrdenServicio orden = consultar(numero);
        exigirEstado(orden, "EN_DIAGNOSTICO");
        boolean restaurado = respaldos.restaurarUltimoEstado(orden);
        if (restaurado) {
            repositorio.guardar(orden);
        }
        return restaurado;
    }

    @Override
    public void solicitarAprobacion(int numero) {
        OrdenServicio orden = consultar(numero);
        exigirEstado(orden, "EN_DIAGNOSTICO");
        validarOrdenCompleta(orden);
        avanzar(numero);
        respaldos.limpiar(orden);
    }

    @Override
    public void aprobarPresupuesto(int numero) {
        OrdenServicio orden = consultar(numero);
        exigirEstado(orden, "PENDIENTE_APROBACION");
        validarOrdenCompleta(orden);
        orden.aprobarPresupuesto();
        avanzar(numero);
    }

    @Override
    public void reparar(int numero, ProcesoReparacion proceso) {
        if (proceso == null) {
            throw new IllegalArgumentException("Debe indicar el proceso de reparación.");
        }
        OrdenServicio orden = consultar(numero);
        exigirEstado(orden, "EN_REPARACION");
        if (orden.isReparacionFinalizada()) {
            throw new IllegalStateException("La reparación ya se ejecutó.");
        }
        validarOrdenCompleta(orden);
        Map<String, Integer> requeridos = proceso.getRepuestosNecesarios();
        if (requeridos == null) {
            throw new IllegalArgumentException("El proceso debe indicar sus repuestos.");
        }
        requeridos = new LinkedHashMap<>(requeridos);
        if (!inventario.hayDisponibilidad(requeridos)) {
            throw new IllegalStateException("Faltan repuestos. La reparación no comenzó.");
        }

        inventario.descontarRepuestos(requeridos);
        try {
            proceso.ejecutarProceso(orden);
        } catch (RuntimeException error) {
            // Compensación de esta simulación; la orden sigue en reparación.
            inventario.devolverRepuestos(requeridos);
            throw error;
        }
        // La plantilla ya confirmó reparación y pruebas; State permite avanzar.
        avanzar(numero);
    }

    @Override
    public String entregar(int numero) {
        OrdenServicio orden = consultar(numero);
        exigirEstado(orden, "LISTA_ENTREGA");
        String factura = facturacion.generarFactura(orden);
        avanzar(numero);
        return factura;
    }
}
