# Taller M&A

Sistema de gestión de órdenes de servicio de un taller automotriz. Aplicación Java por consola con una demostración automática de principio a fin que integra ocho patrones de comportamiento.

## Abrir y ejecutar en IntelliJ IDEA

1. Descomprime `Taller_MA_completo.zip` y abre la carpeta `Taller_MA` con **File > Open**.
2. Configura un JDK 11 o posterior en **File > Project Structure > Project > SDK**. Debe ser un JDK, que incluye el compilador.
3. Si `src` no aparece como carpeta de código, haz clic derecho en `src` y selecciona **Mark Directory as > Sources Root**.
4. Abre `src/uniquindio/Main.java` y ejecuta su método `main` con el botón verde.
5. Para las pruebas, marca `tests` como **Test Sources Root** y ejecuta `tests/uniquindio/PruebasTaller.java`.

El paquete es `uniquindio`, exactamente como en tu ZIP. No debes cambiarlo por `tallerflow` ni `co.edu.uniquindio`. Puedes abrir esta carpeta como un proyecto separado para no mezclar las firmas anteriores con las nuevas.

## Ejecución desde terminal

Desde la carpeta `Taller_MA`:

```sh
javac -encoding UTF-8 -d out @fuentes.txt
java -Dfile.encoding=UTF-8 -cp out uniquindio.Main
```

En Windows también puedes abrir `ejecutar.bat`. Requiere que `javac` y `java` estén en PATH.

Para las pruebas:

```sh
javac -encoding UTF-8 -d out @fuentes-pruebas.txt
java -Dfile.encoding=UTF-8 -cp out uniquindio.PruebasTaller
```

También se incluyen `probar.bat`, `ejecutar.sh` y `probar.sh`.

## Qué encontrarás

- `src/uniquindio/`: los 53 archivos Java del sistema completo.
- `tests/uniquindio/PruebasTaller.java`: pruebas sin librerías externas.
- `Main.java`: configuración y demostración completa sobre una misma orden.
- `CODIGO_COMPLETO.md`: todos los archivos fuente, organizados por ruta para copiar.
- `GUIA_PARCIAL.md`: ubicación, problema y justificación de cada patrón, con distribución sugerida de 25 minutos.
- `CAMBIOS_Y_ESTRUCTURA.md`: qué se conservó y cómo se integraron los dos ZIP.
- `SALIDA_DEMO.txt`: salida real de la demostración verificada.
- `RESULTADO_PRUEBAS.txt`: resultado de las comprobaciones ejecutadas.

## Ocho patrones del parcial

| Bloque | Patrones |
|---|---|
| Base | Observer, Strategy, Command, State, Mediator |
| Investigativo, según el aporte del compañero | Chain of Responsibility, Memento, Template Method |

Esta entrega adopta los tres patrones investigativos implementados por el compañero. Interpreter e Iterator no forman parte de esta versión. La consigna permite escoger tres patrones; no se agregan carpetas de patrones vacías ni se cuentan métodos sin implementación. Builder, presente en el modelo duplicado del compañero, no se utiliza ni se cuenta: no es un patrón de comportamiento.

## Flujo integrado

Recepción registra una orden a través del mediador. El coordinador ejecuta comandos sobre el repositorio común. State exige un mecánico para iniciar diagnóstico y controla las modificaciones. El área mecánica registra diagnóstico y servicios. Strategy calcula la cotización. Memento recupera diagnóstico, servicios y presupuesto tras una edición accidental. La cadena comprueba la orden completa antes de enviar el presupuesto y aprobarlo. Después de la aprobación, el mediador coordina inventario y la plantilla de reparación. Template Method ejecuta preparación, reparación y pruebas, y registra la finalización en la orden. Solo entonces State permite dejarla lista para entrega. La entrega produce una factura simulada del presupuesto vigente. Observer comunica cada transición exitosa.

## Resultado de la demostración

- Cliente: Laura Gómez; vehículo ABC123; mecánica Ana.
- Una orden registrada y finalmente entregada.
- Valor base $200.000; tarifa normal $200.000, frecuente $180.000 y convenio $170.000.
- La tarifa final es convenio: total de factura $170.000.
- Memento recupera el diagnóstico original y el presupuesto después de una edición errónea.
- Pastillas: 10 iniciales, 8 finales. Líquido: 5 iniciales, 4 finales.
- Cinco transiciones de estado. Ambos observadores reciben las primeras cuatro; tras desuscribir al notificador, solo el panel recibe la entrega.
- 14 comandos completados; los intentos rechazados no se cuentan.

## Decisiones y límites

- Todo se imprime en consola. `Main` simula las decisiones del usuario sin pedir datos por teclado. Es la prueba automática que exige el parcial; no es un menú interactivo de operación.
- No hay base de datos, API real, facturación electrónica ni pago. Los datos se pierden al finalizar la aplicación.
- Se utilizan validaciones con `if`, ciclos y colecciones comunes. No se utiliza `Objects.requireNonNull`, `Optional`, streams, reflexión explícita ni lambdas. Las interfaces, clases abstractas y polimorfismo se conservan porque son parte de los patrones solicitados.
- Se conserva `BigDecimal` para dinero. `List`, `Map` y `Collections.unmodifiableList` conservan y protegen las colecciones; no son patrones adicionales.
- El historial de comandos registra acciones ejecutadas. El historial de Memento guarda versiones editables. Deshacer restaura datos; no borra el hecho de que se ejecutó un comando.
- Solo pueden recuperarse ediciones mientras la orden está en diagnóstico. No se revierte una etapa, una notificación enviada, una reparación física ni una entrega.
- El stock se comprueba completo antes de descontar. Una reparación repetida es rechazada. Si falla una plantilla simulada, el inventario se devuelve y se permite reintentar desde reparación. Esta compensación corresponde a la simulación; no pretende revertir trabajo físico real.
- Los precios de los servicios incluyen materiales y mano de obra. Los repuestos se descuentan del inventario sin cobrarlos por segunda vez.
- El mediador es el punto de entrada del flujo de negocio. Una llamada directa desde otro código Java puede omitir la cadena o el inventario; State mantiene sus propias validaciones, pero el ejemplo no implementa controles de acceso ni concurrencia.

## Verificación

Compilación con OpenJDK 21, nivel de lenguaje y bytecode 11, sin advertencias. Ejecución en OpenJDK 21. El código usa APIs disponibles en Java 11, pero no se ejecutó sobre una instalación Java 11. Pasaron 85 comprobaciones funcionales. No se comprobó el script Windows en Windows; las clases y las listas de compilación sí fueron verificadas.
