package uniquindio.reparaciones;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReparacionMotor extends ProcesoReparacion {
    @Override
    public Map<String, Integer> getRepuestosNecesarios() {
        Map<String, Integer> repuestos = new LinkedHashMap<>();
        repuestos.put("Filtro de aceite", 1);
        return repuestos;
    }

    @Override
    protected void prepararHerramientas() {
        System.out.println("Preparando herramientas para el motor.");
    }

    @Override
    protected void realizarReparacion() {
        System.out.println("Sustituyendo el filtro y realizando mantenimiento del motor.");
    }

    @Override
    protected void realizarPruebas() {
        System.out.println("Verificando el funcionamiento del motor.");
    }
}
