import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 12345;

        try (Socket socket = new Socket(host, puerto)) {
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner sc = new Scanner(System.in);

            //hilo que escucha mensajes del servidor en todo momento
            Thread escucha = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = entrada.readLine()) != null) {
                        System.out.println(mensajeServidor);
                    }
                } catch (IOException e) {
                    System.out.println("[INFO] Conexion con el servidor cerrada.");
                }
            });
            escucha.setDaemon(true);
            escucha.start();

            //hilo principal: el usuario escribe y envía mensajes
            String mensajeUsuario;
            while (sc.hasNextLine()) {
                mensajeUsuario = sc.nextLine();
                salida.println(mensajeUsuario);
                if (mensajeUsuario.equalsIgnoreCase("salir")) break;
            }

        } catch (IOException e) {
            System.out.println("[ERROR] En el cliente: " + e.getMessage());
        }
    }
}
