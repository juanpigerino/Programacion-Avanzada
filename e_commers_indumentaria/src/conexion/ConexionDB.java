package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // URL de la base de datos que creaste en Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_indumentaria?serverTimezone=UTC";

    // Configura el usuario y contraseña de tu MySQL local
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // Registramos el Driver que acabas de agregar en la Opción B
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentamos conectar
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ Error: No se encontró el Driver de MySQL.");
        } catch (SQLException e) {
            System.out.println("⚠️ Error de conexión: " + e.getMessage());
        }
        return conexion;
    }
}