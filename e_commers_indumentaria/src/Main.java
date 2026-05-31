import conexion.ConexionDB;
import modelos.*;
import servicios.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Verificando conexión con el servidor MySQL...");
        try (Connection testConn = ConexionDB.obtenerConexion()) {
            if (testConn != null) {
                System.out.println("🎉 ¡Conexión establecida exitosamente!");
            } else {
                System.out.println("❌ Error crítico: Verifica tu servidor de bases de datos.");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Error de comunicación inicial.");
            return;
        }

        // Instanciamos servicios puros vinculados a la base de datos
        ClienteService clienteService = new ClienteService();
        AdminService adminService = new AdminService();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("\n=========================================");
        System.out.println(" ¡SISTEMA E-COMMERCE DE INDUMENTARIA DB! ");
        System.out.println("=========================================");

        while (!salir) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Ver Catalogo (Cliente)");
            System.out.println("2. Comprar una Prenda (Cliente)");
            System.out.println("3. Panel de Administrador (Ver stock / Reponer)");
            System.out.println("4. Salir del Sistema");
            System.out.print("Selecciona una opcion: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    clienteService.mostrarCatalogo();
                    break;

                case 2:
                    System.out.println("\n--- PROCESO DE COMPRA ---");
                    clienteService.mostrarCatalogo();

                    System.out.print("\nIntroduce el ID de la variante que deseas comprar: ");
                    int idPrenda = scanner.nextInt();
                    System.out.print("¿Qué cantidad llevarás?: ");
                    int cantCompra = scanner.nextInt();
                    scanner.nextLine();

                    // Busqueda reactiva directo en la base de datos
                    VariantePrenda varianteDB = clienteService.obtenerVariantePorId(idPrenda);

                    if (varianteDB == null) {
                        System.out.println("⚠️ Error: Ese ID de variante no existe en el catálogo.");
                    } else {
                        List<ItemPedido> carrito = new ArrayList<>();
                        carrito.add(new ItemPedido(varianteDB, cantCompra));

                        // Procesamos el pedido asignándolo al Usuario ID 1 (Ya creado en el Paso 0)
                        Pedido miPedido = clienteService.registrarPedido(1, carrito);

                        if (miPedido != null) {
                            System.out.print("Pedido generado como PENDIENTE. ¿Confirmar pago? (SI/NO): ");
                            String confirmar = scanner.nextLine();

                            if (confirmar.equalsIgnoreCase("SI")) {
                                clienteService.procesarPago(miPedido, "Pasarela de Pago Integrada");
                            } else {
                                System.out.println("❌ Transacción cancelada de forma manual.");
                            }
                        }
                    }
                    break;

                case 3:
                    System.out.println("\n--- PANEL ADMINISTRATIVO ---");
                    System.out.println("1. Ver stock detallado de la tienda");
                    System.out.println("2. Reponer stock de prendas");
                    System.out.print("Selección: ");
                    int opAdmin = scanner.nextInt();

                    if (opAdmin == 1) {
                        adminService.mostrarCatalogo();
                    } else if (opAdmin == 2) {
                        System.out.print("ID de variante a modificar: ");
                        int idReponer = scanner.nextInt();
                        System.out.print("Cantidad a ingresar: ");
                        int cantReponer = scanner.nextInt();
                        adminService.reponerStock(idReponer, cantReponer);
                    }
                    break;

                case 4:
                    salir = true;
                    System.out.println("Cerrando el sistema de indumentaria. ¡Hasta luego!");
                    break;

                default:
                    System.out.println("⚠️ Opcion no valida.");
            }
        }
        scanner.close();
    }
}