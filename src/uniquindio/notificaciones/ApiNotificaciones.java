package uniquindio.notificaciones;

public class ApiNotificaciones implements ServicioNotificaciones {
    @Override
    public void enviar(String destinatario, String mensaje) {

        System.out.println("\n[API DE NOTIFICACIONES SIMULADA]");
        System.out.println("POST /notificaciones");
        System.out.println("Destinatario: " + destinatario);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("Respuesta simulada: 200 OK");
    }
}
