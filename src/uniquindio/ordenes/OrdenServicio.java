package uniquindio.ordenes;

import uniquindio.clientes.Vehiculo;
import uniquindio.personal.Mecanico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdenServicio {
    private final int numero;
    private final Vehiculo vehiculo;
    private EstadoOrden estado = new EstadoRegistrada();
    private int versionEtapa;
    private boolean presupuestoAprobado;
    private boolean reparacionFinalizada;

    private Mecanico mecanico;
    private String diagnostico = "";

    private final List<ItemServicio> servicios =
            new ArrayList<>();

    // null significa que todavía no existe una cotización.
    private BigDecimal presupuesto;
    private String nombreTarifa;

    public OrdenServicio(int numero, Vehiculo vehiculo) {

        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "El número de orden debe ser mayor que cero."
            );
        }

        this.numero = numero;

        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }
        this.vehiculo = vehiculo;
    }

    public void asignarMecanico(Mecanico mecanico) {

        estado.validarAsignacion();

        if (mecanico == null) {
            throw new IllegalArgumentException("El mecánico no puede ser nulo.");
        }
        this.mecanico = mecanico;
    }

    public void actualizarDiagnostico(String diagnostico) {

        estado.validarEdicion();

        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El diagnóstico no puede estar vacío."
            );
        }

        this.diagnostico = diagnostico.trim();
        invalidarCotizacion();
    }

    public void agregarServicio(ItemServicio servicio) {

        estado.validarEdicion();

        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo.");
        }
        servicios.add(servicio);

        invalidarCotizacion();
    }

    public BigDecimal calcularValorBase() {

        BigDecimal total = new BigDecimal("0.00");

        for (ItemServicio servicio : servicios) {
            total = total.add(servicio.getValorBase());
        }

        return total;
    }

    public void registrarCotizacion(
            BigDecimal presupuesto,
            String nombreTarifa) {
        estado.validarEdicion();

        if (servicios.isEmpty()) {
            throw new IllegalStateException(
                    "Debe agregar servicios antes de cotizar."
            );
        }

        if (presupuesto == null || presupuesto.signum() < 0) {
            throw new IllegalArgumentException(
                    "El presupuesto debe ser no nulo y no negativo."
            );
        }

        if (nombreTarifa == null || nombreTarifa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe indicar la tarifa aplicada."
            );
        }

        this.presupuesto = presupuesto.setScale(
                2, RoundingMode.HALF_UP
        );

        this.nombreTarifa = nombreTarifa.trim();
    }

    private void invalidarCotizacion() {
        presupuesto = null;
        nombreTarifa = null;
    }

    public boolean tieneCotizacion() {
        return presupuesto != null;
    }

    public int getNumero() {
        return numero;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public List<ItemServicio> getServicios() {
        return Collections.unmodifiableList(
                new ArrayList<>(servicios)
        );
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public String getNombreTarifa() {
        return nombreTarifa;
    }

    public String getNombreEstado() {
        return estado.getNombre();
    }

    public void avanzarEstado() {
        estado.avanzar(this);
    }

    void cambiarEstado(EstadoOrden nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = nuevoEstado;
        versionEtapa++;
    }


    // State: la aprobación es una decisión explícita, no solo un mensaje impreso.
    public void aprobarPresupuesto() {
        if (!getNombreEstado().equals("PENDIENTE_APROBACION")) {
            throw new IllegalStateException("Solo puede aprobar una orden pendiente de aprobación.");
        }
        presupuestoAprobado = true;
    }

    public boolean isPresupuestoAprobado() {
        return presupuestoAprobado;
    }

    public void registrarReparacionFinalizada() {
        if (!getNombreEstado().equals("EN_REPARACION")) {
            throw new IllegalStateException("La orden debe estar en reparación.");
        }
        reparacionFinalizada = true;
    }

    public boolean isReparacionFinalizada() {
        return reparacionFinalizada;
    }

    // Memento: la orden crea y recupera su propia información editable.
    public MementoOrden crearMemento() {
        estado.validarEdicion();
        return new MementoOrden(this, versionEtapa, diagnostico, servicios,
                presupuesto, nombreTarifa);
    }

    public void restaurarMemento(MementoOrden recuerdo) {
        estado.validarEdicion();
        if (recuerdo == null) {
            throw new IllegalArgumentException("El respaldo no puede ser nulo.");
        }
        if (recuerdo.ordenOrigen != this) {
            throw new IllegalArgumentException("El respaldo pertenece a otra orden.");
        }
        if (recuerdo.versionEtapa != versionEtapa) {
            throw new IllegalStateException("El respaldo pertenece a una etapa anterior.");
        }
        diagnostico = recuerdo.diagnostico;
        servicios.clear();
        for (ItemServicio servicio : recuerdo.servicios) {
            servicios.add(servicio);
        }
        presupuesto = recuerdo.presupuesto;
        nombreTarifa = recuerdo.nombreTarifa;
    }

    @Override
    public String toString() {

        String responsable = "Sin asignar";
        if (mecanico != null) {
            responsable = mecanico.getNombre();
        }

        String valor = "Sin cotizar";
        if (tieneCotizacion()) {
            valor = "$" + presupuesto;
        }

        return "Orden #" + numero
                + " | Placa: " + vehiculo.getPlaca()
                + " | Estado: " + getNombreEstado()
                + " | Mecánico: " + responsable
                + " | Presupuesto: " + valor;
    }
}
