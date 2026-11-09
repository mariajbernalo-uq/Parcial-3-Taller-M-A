# Guía para sustentar Taller M&A

## Preparación de pantalla

Abre el proyecto en IntelliJ, deja visible `Main.java` y el panel de paquetes. Ejecuta el Main una vez antes de grabar y verifica el resultado final: una orden entregada, total $170.000, 8 pastillas, 4 líquidos y 14 comandos exitosos. No se requieren servicios externos.

La explicación debe mostrar clases y comportamiento, no limitarse a leer nombres de patrones. Todos operan sobre la orden #1 del mismo repositorio.

## Distribución sugerida: 25 minutos como máximo

| Segmento | Tiempo |
|---|---|
| Contexto y flujo del taller | 1 minuto |
| Ocho patrones, mostrando las tres preguntas | 2 minutos por patrón: 16 minutos |
| Ejecutar Main y señalar integración y rechazos | 5 minutos |
| Decisiones, límites y cierre | 2 minutos |
| Margen para cambios de archivo | 1 minuto |

Total: 25 minutos. Practiquen la narración y reduzcan la lectura de código si se acercan al límite.

## 1. Observer

**¿Qué patrón es?**

Permite suscribir varios objetos para reaccionar a un mismo evento sin que el publicador dependa de sus implementaciones concretas.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `notificaciones/ObservadorOrden.java`
- `notificaciones/PublicadorEventos.java`
- `notificaciones/PanelTallerObserver.java`
- `notificaciones/NotificadorClienteObserver.java`
- `notificaciones/EventoOrden.java`
- `ordenes/AvanzarOrdenCommand.java`

**¿Qué problema del negocio resuelve?**

Cuando una orden cambia de etapa se debe actualizar el panel e informar al cliente. El publicador conserva una lista de la interfaz y llama actualizar para cada suscriptor. El comando publica después de avanzar y guardar. Desuscribir al notificador antes de entregar muestra que la lista puede cambiar.

**¿Por qué este patrón?**

El problema es distribuir novedades a varios interesados. Command representa solicitudes y State controla permisos; ninguno de ellos sustituye el mecanismo de suscripción y distribución. ServicioNotificaciones y ApiNotificaciones son apoyo a la integración simulada, no la interfaz Observer.

**Qué mostrar:** Mostrar suscribir/publicar/desuscribir y después la entrega sin notificación de API.

## 2. Strategy

**¿Qué patrón es?**

Encapsula formas intercambiables de resolver una misma operación; el contexto delega en la estrategia seleccionada.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `cotizacion/EstrategiaTarifa.java`
- `cotizacion/TarifaNormal.java`
- `cotizacion/TarifaClienteFrecuente.java`
- `cotizacion/TarifaConvenio.java`
- `cotizacion/CalculadoraPresupuesto.java`
- `cotizacion/CotizarOrdenCommand.java`

**¿Qué problema del negocio resuelve?**

La cotización cambia según la política comercial. Normal cobra 100 %, cliente frecuente 90 % y convenio 85 %. La calculadora recibe una estrategia y delega calcularTotal. El comando aplica el resultado a los servicios reales de la orden.

**¿Por qué este patrón?**

Varía una regla de cálculo, no la etapa de una orden. State decide permisos por ciclo de vida; Strategy selecciona el algoritmo de precio. Añadir otra tarifa no obliga a modificar la calculadora.

**Qué mostrar:** Mostrar la interfaz, las fórmulas y los resultados 200000, 180000 y 170000 del Main.

## 3. Command

**¿Qué patrón es?**

Representa una solicitud como un objeto que conserva sus parámetros y expone una operación de ejecución común.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `coordinacion/Comando.java`
- `coordinacion/HistorialComandos.java`
- `ordenes/CrearOrdenCommand.java`
- `ordenes/AsignarMecanicoCommand.java`
- `ordenes/ActualizarDiagnosticoCommand.java`
- `ordenes/AgregarServicioCommand.java`
- `ordenes/AvanzarOrdenCommand.java`
- `cotizacion/CotizarOrdenCommand.java`

**¿Qué problema del negocio resuelve?**

El taller necesita ejecutar acciones diferentes mediante un mecanismo uniforme. HistorialComandos es el invocador y registra después de ejecutar correctamente. Cada comando busca la orden y delega el trabajo en los receptores: modelo, repositorio y calculadora. El mediador construye los comandos en el flujo integrado.

**¿Por qué este patrón?**

Las acciones tienen objetivos distintos; no son solo variantes de un cálculo como en Strategy. Memento conserva los datos para recuperar una edición, mientras Command representa la solicitud. El proyecto no afirma que todo comando tenga una operación inversa: solo las ediciones autorizadas por el mediador pueden recuperarse con Memento.

**Qué mostrar:** Mostrar un comando concreto, el método ejecutar del historial y el rechazo de orden duplicada sin incrementar el historial.

## 4. State

**¿Qué patrón es?**

Delegación del comportamiento de un objeto en una clase que representa su etapa actual.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `ordenes/OrdenServicio.java`
- `ordenes/EstadoOrden.java`
- `ordenes/EstadoRegistrada.java`
- `ordenes/EstadoEnDiagnostico.java`
- `ordenes/EstadoPendienteAprobacion.java`
- `ordenes/EstadoEnReparacion.java`
- `ordenes/EstadoListaEntrega.java`
- `ordenes/EstadoEntregada.java`

**¿Qué problema del negocio resuelve?**

Una orden registrada exige mecánico para iniciar diagnóstico. Solo en diagnóstico se editan los datos. Se exige aprobación antes de iniciar reparación y finalización de reparación/pruebas antes de marcar lista. Entregada no admite más avances. La orden delega estado.avanzar(this) y las validaciones de edición/asignación.

**¿Por qué este patrón?**

El comportamiento depende de la etapa interna y cambia durante el ciclo de vida. Strategy cambia el cálculo por una selección comercial, mientras State cambia permisos y transiciones. Los if sirven para las condiciones dentro de cada estado, sin reemplazar el polimorfismo por un gran switch central.

**Qué mostrar:** Mostrar EstadoRegistrada, EstadoEnReparacion y la delegación en OrdenServicio.

## 5. Mediator

**¿Qué patrón es?**

Centraliza la coordinación de componentes para que no tengan que conocerse ni llamarse directamente entre todos.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `coordinacion/MediadorTaller.java`
- `coordinacion/CoordinadorTaller.java`
- `recepcion/Recepcion.java`
- `personal/AreaMecanica.java`
- `inventario/Inventario.java`
- `facturacion/Facturacion.java`

**¿Qué problema del negocio resuelve?**

Recepción y mecánica solicitan operaciones a la misma interfaz. El coordinador organiza comandos, respaldos, cadena, stock, proceso y facturación. El área mecánica no llama directamente a recepción ni a facturación, y recepción no manipula inventario. El resultado del proceso de reparación alimenta la transición y luego la entrega.

**¿Por qué este patrón?**

El problema es coordinar áreas y asignar quién organiza las interacciones. Observer avisa a suscriptores después de un evento, pero no organiza las etapas y dependencias necesarias para completar la reparación. Mediator usa los otros patrones sin reemplazarlos.

**Qué mostrar:** Mostrar que Recepcion y AreaMecanica solo guardan MediadorTaller, y después reparar y entregar en el coordinador.

## 6. Memento

**¿Qué patrón es?**

Guarda una versión del estado interno recuperable de un objeto, para restaurarla sin que el cuidador tenga que interpretar sus datos.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `ordenes/OrdenServicio.java`
- `ordenes/MementoOrden.java`
- `ordenes/HistorialOrden.java`
- `coordinacion/CoordinadorTaller.java`

**¿Qué problema del negocio resuelve?**

Una edición accidental del diagnóstico invalida el presupuesto. Antes de la edición, la orden crea un recuerdo de diagnóstico, servicios, presupuesto y tarifa. HistorialOrden conserva versiones por orden. Al restaurar, el modelo verifica origen y etapa y recupera la información, incluida la cotización.

**¿Por qué este patrón?**

El problema es recuperar datos anteriores de forma consistente. Volver a calcular con Strategy no recupera un diagnóstico ni una lista de servicios. Command tampoco contiene por sí solo una copia anterior. No se usa Memento para revertir entregas, notificaciones ni inventario; solo información editable en diagnóstico.

**Qué mostrar:** Mostrar crearMemento/restaurarMemento y el paso 7 del Main, antes y después de la restauración.

## 7. Chain of Responsibility

**¿Qué patrón es?**

Conecta manejadores que atienden una solicitud por turnos y deciden si continúan al siguiente o detienen el recorrido.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `ordenes/ValidadorOrden.java`
- `ordenes/ValidadorCliente.java`
- `ordenes/ValidadorVehiculo.java`
- `ordenes/ValidadorDiagnostico.java`
- `ordenes/ValidadorPresupuesto.java`
- `coordinacion/CoordinadorTaller.java`
- `Main.java`

**¿Qué problema del negocio resuelve?**

Antes de enviar o aprobar el presupuesto y ejecutar la reparación se verifica que la orden esté preparada. Cada validador devuelve un error o delega. El coordinador no avanza si recibe un resultado que comienza con Error:. El Main muestra el rechazo de una orden sin cotización y luego el recorrido completo.

**¿Por qué este patrón?**

La cadena permite componer y ordenar responsabilidades de validación. State se ocupa de permisos por etapa; la cadena comprueba preparación completa y la política de presupuesto positivo. Las comprobaciones de cliente/vehículo son defensivas porque los constructores también garantizan esos datos.

**Qué mostrar:** Mostrar establecerSiguiente, el corte ante error y la composición de la cadena en Main.

## 8. Template Method

**¿Qué patrón es?**

Una clase base fija el orden de un proceso y deja que las subclases implementen pasos variables.

**¿En qué clases/archivos está implementado?** Todas las rutas parten de `src/uniquindio/`.

- `reparaciones/ProcesoReparacion.java`
- `reparaciones/ReparacionFrenos.java`
- `reparaciones/ReparacionMotor.java`
- `reparaciones/ReparacionElectrica.java`
- `coordinacion/CoordinadorTaller.java`

**¿Qué problema del negocio resuelve?**

Frenos, motor y electricidad requieren herramientas, trabajos y pruebas diferentes, pero mantienen la secuencia preparar, reparar y probar. ejecutarProceso es final. Al terminar registra reparacionFinalizada en la misma orden; State utiliza ese resultado para permitir el avance. Si falla un paso, no marca finalizada ni ejecuta los posteriores.

**¿Por qué este patrón?**

Aquí se conserva la estructura del proceso y se especializan pasos por herencia. Strategy cambia un algoritmo completo por composición. La plantilla no vuelve a diagnosticar, cotizar o entregar: esos pasos ya están integrados con los otros patrones y no deben duplicarse.

**Qué mostrar:** Mostrar ejecutarProceso final y compararlo con los tres métodos sobrescritos de ReparacionFrenos.

## Recorrido para la demostración final

1. Identificar que el Main configura un solo repositorio y un solo coordinador.
2. Registrar la orden desde recepción, rechazar duplicado y rechazar diagnóstico sin mecánico.
3. Asignar mecánico, iniciar diagnóstico y observar panel/API.
4. Registrar diagnóstico/servicios e intentar solicitar aprobación sin presupuesto: la cadena detiene el flujo.
5. Cotizar tres veces para mostrar Strategy; queda convenio como cotización vigente.
6. Modificar accidentalmente el diagnóstico: la cotización se invalida. Recuperar con Memento.
7. Validar la orden completa y solicitar aprobación. State bloquea ediciones desde esa etapa.
8. Registrar aprobación explícita. Ejecutar reparación por mediador: stock, plantilla, pruebas, finalización, avance y notificaciones.
9. Intentar repetir la reparación: se rechaza y no vuelve a descontar materiales.
10. Desuscribir al notificador y entregar: solo el panel recibe esa transición; se imprime factura simulada.
11. Intentar editar la orden entregada: se rechaza. Mostrar los totales finales.

## Preguntas frecuentes

**¿Hay ocho ejemplos separados?** No. Se comparten cliente, vehículo, mecánico, orden, repositorio y mediador; el resultado de cotizar habilita validar, el resultado de validar permite avanzar y el de la plantilla permite entregar.

**¿Por qué se guardan dos historiales?** Uno registra comandos ejecutados y otro guarda versiones editables de las órdenes. Restaurar un dato no elimina el registro de la acción realizada.

**¿Dónde se simulan APIs y base de datos?** En `ApiNotificaciones`, `RepositorioOrdenesMemoria` e `Inventario`. `Facturacion` genera texto de demostración.

**¿Por qué no están Interpreter e Iterator?** Se adoptaron los tres patrones investigativos del aporte del compañero: Chain of Responsibility, Memento y Template Method. La consigna permite elegir tres.

**¿Por qué no usar solo if en todo?** Se usan if para validar datos y condiciones. Las interfaces y el polimorfismo distribuyen las responsabilidades de los patrones, como exige el parcial.

**¿Un fallo al enviar una notificación revierte el cambio de estado?** No. El estado ya se guardó. Se informa el fallo de comunicación y se continúa con los demás observadores; no se promete reintento automático.

**¿Deshacer devuelve repuestos o revierte una entrega?** No. Memento solo recupera información editable durante diagnóstico. La compensación de inventario ante un fallo simulado de reparación pertenece al coordinador.
