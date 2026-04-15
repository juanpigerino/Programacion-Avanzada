import java.io.*;
import java.net.*;

public class Servidor {
    public static void  main(String[] args){
        int puerto = 12345;

        try(ServerSocket serverSocket = new ServerSocket(puerto)){
            System.out.println("Servidor iniciado. Esperando al cliente...");

            //Esto es para aceptar la conexion del cliente
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

            BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(),true);

            String mensajeRecibido;
            while((mensajeRecibido = entrada.readLine()) != null){
                //log en la consola del servidor
                System.out.println("[LOG] Mensaje recibido: " + mensajeRecibido);

                if(mensajeRecibido.equalsIgnoreCase("salir")){
                    salida.println("conexion cerrada");
                }

                //logica para el ejercicio
                if(mensajeRecibido.startsWith("RESOLVE")){
                    String ecuacion = mensajeRecibido.replace("RESOLVE","").trim();
                    salida.println("Servidor: procesando la ecuacion " + ecuacion + " Resultado:");
                }else{
                    salida.println("Servidor recibio: " + mensajeRecibido);
                }

            }

            clientSocket.close();
            System.out.println("Servidor finalizado");

        }catch (IOException e){
            System.out.println("Error en el servidor: " + e.getMessage());
        }

    }
}
