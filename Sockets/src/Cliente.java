import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args){
        String host = "localhost";
        int puerto = 12345;

        try(Socket socket = new Socket(host, puerto)){
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true );
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner sc  = new Scanner(System.in);

            System.out.println("Conectado al servidor. Escribe tus mensajes (o 'salir' para terminar):");
            System.out.println("Ingresa la cuenta que desees ");

            String mensajeUsuario;
            while(true){
                System.out.println("> ");
                mensajeUsuario = sc.nextLine();

                //para enviar al servidor
                salida.println(mensajeUsuario);

                //para leer la respuesta
                String respuesta = entrada.readLine();
                System.out.println(respuesta);

                if(mensajeUsuario.equalsIgnoreCase("salir")){
                    break;
                }
            }
        }catch (IOException e){
            System.out.printf("ERROR en el cliente: " + e.getMessage());
        }
    }
}
