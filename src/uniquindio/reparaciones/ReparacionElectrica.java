package uniquindio.reparaciones;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReparacionElectrica extends ProcesoReparacion {
    @Override
    public Map<String, Integer> getRepuestosNecesarios() {
        Map<String, Integer> repuestos = new LinkedHashMap<>();
        repuestos.put("Fusible", 1);
        return repuestos;
    }

    @Override
    protected void prepararHerramientas() {
        System.out.println("Preparando multímetro y herramientas aisladas.");
    }

    @Override
    protected void realizarReparacion() {
        System.out.println("Reemplazando el fusible y reparando conexiones eléctricas.");
    }

    @Override
    protected void realizarPruebas() {
        System.out.println("Comprobando continuidad y funcionamiento del sistema eléctrico.");
    }
}
