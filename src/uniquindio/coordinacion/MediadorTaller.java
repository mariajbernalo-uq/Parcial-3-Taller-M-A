package uniquindio.coordinacion;

import java.util.List;
import uniquindio.clientes.Vehiculo;
import uniquindio.personal.Mecanico;
import uniquindio.ordenes.ItemServicio;
import uniquindio.ordenes.OrdenServicio;
import uniquindio.cotizacion.EstrategiaTarifa;
import uniquindio.reparaciones.ProcesoReparacion;

public interface MediadorTaller {
    void crearOrden(int numero, Vehiculo vehiculo);
    void asignarMecanico(int numero, Mecanico mecanico);
    void iniciarDiagnostico(int numero);
    void actualizarDiagnostico(int numero, String diagnostico);
    void agregarServicio(int numero, ItemServicio servicio);
    void cotizar(int numero, EstrategiaTarifa tarifa);
    boolean deshacerUltimaEdicion(int numero);
    void solicitarAprobacion(int numero);
    void aprobarPresupuesto(int numero);
    void reparar(int numero, ProcesoReparacion proceso);
    String entregar(int numero);
    OrdenServicio consultar(int numero);
    List<OrdenServicio> listar();
}
