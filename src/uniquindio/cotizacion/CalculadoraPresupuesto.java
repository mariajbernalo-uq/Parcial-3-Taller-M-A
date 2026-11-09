package uniquindio.cotizacion;

import java.math.BigDecimal;

// Contexto de Strategy: delega el cálculo en la tarifa seleccionada.
public class CalculadoraPresupuesto {
    private EstrategiaTarifa estrategia;

    public CalculadoraPresupuesto(EstrategiaTarifa estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser nula.");
        }
        this.estrategia = estrategia;
    }

    public void cambiarEstrategia(EstrategiaTarifa estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser nula.");
        }
        this.estrategia = estrategia;
    }

    public BigDecimal calcular(BigDecimal valorBase) {
        return estrategia.calcularTotal(valorBase);
    }

    public String getNombreTarifa() {
        return estrategia.getNombre();
    }
}
