package uniquindio.cotizacion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TarifaClienteFrecuente implements EstrategiaTarifa {
    private static final BigDecimal PORCENTAJE =
            new BigDecimal("0.90");

    @Override
    public BigDecimal calcularTotal(BigDecimal valorBase) {

        if (valorBase == null) {
            throw new IllegalArgumentException(
                    "El valor base no puede ser nulo."
            );
        }

        if (valorBase.signum() < 0) {
            throw new IllegalArgumentException(
                    "El valor base no puede ser negativo."
            );
        }

        return valorBase.multiply(PORCENTAJE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNombre() {
        return "Cliente frecuente: descuento del 10 %";
    }
}
