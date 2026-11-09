package uniquindio.ordenes;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemServicio {
    private final String descripcion;
    private final BigDecimal valorBase;

    public ItemServicio(String descripcion, BigDecimal valorBase) {

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La descripción no puede estar vacía."
            );
        }

        if (valorBase == null || valorBase.signum() < 0) {
            throw new IllegalArgumentException(
                    "El valor debe ser no nulo y no negativo."
            );
        }

        this.descripcion = descripcion.trim();
        this.valorBase = valorBase.setScale(
                2, RoundingMode.HALF_UP
        );
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    @Override
    public String toString() {
        return descripcion + ": $" + valorBase;
    }
}
