import vista.VistaChat;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 12345;

        // 1. Iniciamos y mostramos la ventana (la Vista)
        VistaChat vista = new VistaChat();
        vista.setVisible(true);

        try {
            // 2. Nos conectamos al servidor (sin try-with-resources para no cerrar el socket prematuramente)
            Socket socket = new Socket(host, puerto);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Buffe   redReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 3. Enlazamos la acción de escribir un mensaje en la ventana con el envío de datos
            vista.configurarAccionEnviar(e -> {
                String mensajeUsuario = vista.getMensajeYLimpiar(); // Obtenemos el texto y limpiamos la caja
                if (!mensajeUsuario.trim().isEmpty()) {
                    salida.println(mensajeUsuario); // Enviamos al servidor
                    if (mensajeUsuario.equalsIgnoreCase("salir")) {
                        System.exit(0); // Cierra todo si el usuario escribe "salir"
                    }
                }
            });

            // 4. Hilo que escucha mensajes del servidor en todo momento (Tu código original adaptado)
            Thread escucha = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = entrada.readLine()) != null) {
                        vista.mostrarMensaje(mensajeServidor); // Muestra en Swing en vez de consola
                    }
                } catch (IOException e) {
                    vista.mostrarMensaje("[INFO] Conexion con el servidor cerrada.");
                }
            });
            escucha.setDaemon(true);
            escucha.start();

        } catch (IOException e) {
            vista.mostrarMensaje("[ERROR] En el cliente: " + e.getMessage());
        }
    }
}
