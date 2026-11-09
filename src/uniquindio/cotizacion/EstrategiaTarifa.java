package uniquindio.cotizacion;
import java.math.BigDecimal;

public interface EstrategiaTarifa {
    BigDecimal calcularTotal(BigDecimal valorBase);

    String getNombre();
}
