package uniquindio.cotizacion;

import java.math.BigDecimal;
import uniquindio.coordinacion.Comando;
import uniquindio.ordenes.OrdenServicio;
import uniquindio.ordenes.RepositorioOrdenes;

public class CotizarOrdenCommand implements Comando {
    private final RepositorioOrdenes repositorio;
    private final int numeroOrden;
    private final EstrategiaTarifa estrategia;

    public CotizarOrdenCommand(RepositorioOrdenes repositorio, int numeroOrden,
            EstrategiaTarifa estrategia) {
        if (repositorio == null || estrategia == null) {
            throw new IllegalArgumentException("Debe indicar repositorio y estrategia.");
        }
        this.repositorio = repositorio;
        this.numeroOrden = numeroOrden;
        this.estrategia = estrategia;
    }

    @Override
    public void ejecutar() {
        OrdenServicio orden = repositorio.buscarPorNumero(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden #" + numeroOrden);
        }
        if (orden.getServicios().isEmpty()) {
            throw new IllegalStateException("Debe agregar servicios antes de cotizar.");
        }
        CalculadoraPresupuesto calculadora = new CalculadoraPresupuesto(estrategia);
        BigDecimal total = calculadora.calcular(orden.calcularValorBase());
        orden.registrarCotizacion(total, calculadora.getNombreTarifa());
        repositorio.guardar(orden);
    }
}
