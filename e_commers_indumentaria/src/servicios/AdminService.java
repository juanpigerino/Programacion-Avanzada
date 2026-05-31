package servicios;

import conexion.ConexionDB;
import interfaces.VisualizadorCatalogo;
import interfaces.GestorInventario;
import modelos.Prenda;
import modelos.VariantePrenda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService implements VisualizadorCatalogo, GestorInventario {

    @Override
    public void mostrarCatalogo() {
        System.out.println("\n--- VISTA PANEL ADMIN: INVENTARIO COMPLETO (SINCRO MYSQL) ---");
        String query = "SELECT v.id, p.nombre, v.talle, v.color, v.stock " +
                "FROM variantes_prenda v " +
                "INNER JOIN prendas p ON v.prenda_id = p.id";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID Variante [" + rs.getInt("id") + "] -> " +
                        rs.getString("nombre") + " (" + rs.getString("talle") + "/" + rs.getString("color") + ") " +
                        "- Stock Actual: " + rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al cargar panel de administración: " + e.getMessage());
        }
    }

    @Override
    public void reponerStock(int varianteId, int cantidad) {
        String query = "UPDATE variantes_prenda SET stock = stock + ? WHERE id = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, varianteId);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ DB Actualizada: Se agregaron " + cantidad + " unidades a la variante ID " + varianteId);
            } else {
                System.out.println("❌ Error: El ID de variante " + varianteId + " no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al reponer stock en MySQL: " + e.getMessage());
        }
    }

    @Override
    public void agregarNuevaPrenda(Prenda prenda) {}

    @Override
    public List<VariantePrenda> filtrarPorTalleYColor(String talle, String color) { return new ArrayList<>(); }
}