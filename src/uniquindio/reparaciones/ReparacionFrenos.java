package uniquindio.reparaciones;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReparacionFrenos extends ProcesoReparacion {
    @Override
    public Map<String, Integer> getRepuestosNecesarios() {
        Map<String, Integer> repuestos = new LinkedHashMap<>();
        repuestos.put("Pastillas de freno", 2);
        repuestos.put("Liquido de frenos", 1);
        return repuestos;
    }

    @Override
    protected void prepararHerramientas() {
        System.out.println("Preparando herramientas para discos y pastillas.");
    }

    @Override
    protected void realizarReparacion() {
        System.out.println("Reemplazando pastillas y ajustando el sistema de frenos.");
    }

    @Override
    protected void realizarPruebas() {
        System.out.println("Comprobando el funcionamiento del sistema de frenado.");
    }
}
