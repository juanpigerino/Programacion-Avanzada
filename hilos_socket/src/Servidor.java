import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    static Map<String, PrintWriter> clientes = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int puerto = 12345;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto + ". Esperando clientes...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread hilo = new Thread(new ManejadorCliente(clientSocket));
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("[ERROR] En el servidor: " + e.getMessage());
        }
    }

    //métodos matemáticos

    static double evaluar(String expresion) {
        expresion = expresion.replaceAll("\\s+", "");
        return parseSuma(expresion, new int[]{0});
    }

    static double parseSuma(String expr, int[] pos) {
        double resultado = parseMulti(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '+' && op != '-') break;
            pos[0]++;
            double derecha = parseMulti(expr, pos);
            resultado = (op == '+') ? resultado + derecha : resultado - derecha;
        }
        return resultado;
    }

    static double parseMulti(String expr, int[] pos) {
        double resultado = parseNumero(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '*' && op != '/') break;
            pos[0]++;
            double derecha = parseNumero(expr, pos);
            resultado = (op == '*') ? resultado * derecha : resultado / derecha;
        }
        return resultado;
    }

    static double parseNumero(String expr, int[] pos) {
        int inicio = pos[0];
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '-') pos[0]++;
        while (pos[0] < expr.length() && (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.')) {
            pos[0]++;
        }
        return Double.parseDouble(expr.substring(inicio, pos[0]));
    }

    //-Clase interna- va dentro del servidor, después de los métodos matemáticos

    static class ManejadorCliente implements Runnable {

        private Socket socket;
        private String nombreUsuario;
        private PrintWriter salida;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);

                nombreUsuario = asignarNombre(entrada);
                clientes.put(nombreUsuario, salida);
                log("Cliente conectado: " + nombreUsuario);
                broadcast("[SERVIDOR] " + nombreUsuario + " se unio al chat.", null);

                salida.println("------------ Bienvenido, " + nombreUsuario + "! ------------");
                salida.println("Comandos disponibles:");
                salida.println("  RESOLVE <ecuacion>     -> resuelve una expresion matematica");
                salida.println("  HORA                   -> muestra fecha y hora actual");
                salida.println("  LISTA                  -> lista los clientes conectados");
                salida.println("  *<usuario> <mensaje>   -> envia mensaje a un cliente especifico");
                salida.println("  *ALL <mensaje>         -> envia mensaje a todos");
                salida.println("  salir                  -> desconectarse");
                salida.println("----------------------------------------------------");

                String mensajeRecibido;
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    log("[" + nombreUsuario + "] " + mensajeRecibido);

                    if (mensajeRecibido.equalsIgnoreCase("salir")) {
                        salida.println("Servidor: hasta luego, " + nombreUsuario + "!");
                        break;
                    }

                    procesarComando(mensajeRecibido);
                }

            } catch (IOException e) {
                log("[ERROR] Conexion perdida con " + nombreUsuario + ": " + e.getMessage());
            } finally {
                desconectar();
            }
        }

        private String asignarNombre(BufferedReader entrada) throws IOException {
            salida.println("Ingresa tu nombre de usuario:");
            String nombre = entrada.readLine();
            if (nombre == null || nombre.trim().isEmpty()) nombre = "Usuario";
            nombre = nombre.trim();

            String nombreFinal = nombre;
            int contador = 2;
            while (clientes.containsKey(nombreFinal)) {
                nombreFinal = nombre + contador;
                contador++;
            }

            if (!nombreFinal.equals(nombre)) {
                salida.println("[SERVIDOR] El nombre '" + nombre + "' ya estaba en uso. Se te asigno: " + nombreFinal);
            }

            return nombreFinal;
        }

        private void procesarComando(String mensaje) {
            if (mensaje.toUpperCase().startsWith("RESOLVE")) {
                String ecuacion = mensaje.substring(7).trim();
                try {
                    double resultado = evaluar(ecuacion);
                    salida.println("Servidor: " + ecuacion + " = " + resultado);
                } catch (Exception e) {
                    salida.println("Servidor: ecuacion invalida -> " + ecuacion);
                }

            } else if (mensaje.equalsIgnoreCase("HORA")) {
                String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                salida.println("Servidor: fecha y hora -> " + hora);

            } else if (mensaje.equalsIgnoreCase("LISTA")) {
                salida.println("Servidor: clientes conectados -> " + String.join(", ", clientes.keySet()));

            } else if (mensaje.toUpperCase().startsWith("*ALL ")) {
                String contenido = mensaje.substring(5).trim();
                broadcast("[" + nombreUsuario + " -> TODOS] " + contenido, nombreUsuario);
                salida.println("Servidor: mensaje enviado a todos.");

            } else if (mensaje.startsWith("*")) {
                String[] partes = mensaje.substring(1).split(" ", 2);
                if (partes.length < 2) {
                    salida.println("Servidor: formato incorrecto. Usa *<usuario> <mensaje>");
                    return;
                }
                String destinatario = partes[0];
                String contenido = partes[1];
                PrintWriter salidaDestinatario = clientes.get(destinatario);
                if (salidaDestinatario != null) {
                    salidaDestinatario.println("[" + nombreUsuario + " -> ti] " + contenido);
                    salida.println("Servidor: mensaje enviado a " + destinatario + ".");
                } else {
                    salida.println("Servidor: el usuario '" + destinatario + "' no existe o no esta conectado.");
                }

            } else {
                salida.println("Servidor recibio: " + mensaje);
            }
        }

        private void broadcast(String mensaje, String excepto) {
            for (Map.Entry<String, PrintWriter> entry : clientes.entrySet()) {
                if (!entry.getKey().equals(excepto)) {
                    entry.getValue().println(mensaje);
                }
            }
        }

        private void desconectar() {
            if (nombreUsuario != null) {
                clientes.remove(nombreUsuario);
                log("Cliente desconectado: " + nombreUsuario);
                broadcast("[SERVIDOR] " + nombreUsuario + " se desconecto.", null);
            }
            try { socket.close(); } catch (IOException e) { /* ignorar */ }
        }

        private void log(String mensaje) {
            System.out.println("[LOG] " + mensaje);
        }
    }
}
