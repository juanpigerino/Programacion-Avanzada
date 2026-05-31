package servicios;

import conexion.ConexionDB;
import interfaces.VisualizadorCatalogo;
import interfaces.ProcesadorCompra;
import modelos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteService implements VisualizadorCatalogo, ProcesadorCompra {

    @Override
    public void mostrarCatalogo() {
        System.out.println("\n--- CATÁLOGO DISPONIBLE (CLIENTE) ---");
        String query = "SELECT v.id, p.nombre, v.talle, v.color, p.precio_base, v.stock " +
                "FROM variantes_prenda v " +
                "INNER JOIN prendas p ON v.prenda_id = p.id " +
                "WHERE v.stock > 0";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID [" + rs.getInt("id") + "] -> " + rs.getString("nombre") +
                        " | Talle: " + rs.getString("talle") + " | Color: " + rs.getString("color") +
                        " | Precio: $" + rs.getDouble("precio_base") + " | Disponibles: " + rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al leer el catálogo: " + e.getMessage());
        }
    }

    public VariantePrenda obtenerVariantePorId(int idBusqueda) {
        String query = "SELECT v.id AS vid, v.talle, v.color, v.stock, p.id AS pid, p.nombre, p.descripcion, p.precio_base " +
                "FROM variantes_prenda v " +
                "INNER JOIN prendas p ON v.prenda_id = p.id " +
                "WHERE v.id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idBusqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Prenda prenda = new Prenda(rs.getInt("pid"), rs.getString("nombre"), rs.getString("descripcion"), rs.getDouble("precio_base"));
                    return new VariantePrenda(rs.getInt("vid"), prenda, rs.getString("talle"), rs.getString("color"), rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al buscar prenda en DB: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Pedido registrarPedido(int usuarioId, List<ItemPedido> items) {
        Connection conn = null;
        try {
            conn = ConexionDB.obtenerConexion();
            conn.setAutoCommit(false); // Transacción segura

            // 1. Validar Stock real
            for (ItemPedido item : items) {
                String sqlStock = "SELECT stock FROM variantes_prenda WHERE id = ?";
                try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
                    stmtStock.setInt(1, item.getVariante().getId());
                    try (ResultSet rs = stmtStock.executeQuery()) {
                        if (!rs.next() || rs.getInt("stock") < item.getCantidad()) {
                            System.out.println("❌ Stock insuficiente en Base de Datos para el ID: " + item.getVariante().getId());
                            conn.rollback();
                            return null;
                        }
                    }
                }
            }

            double total = items.stream().mapToDouble(ItemPedido::getSubtotal).sum();

            // 2. Insertar cabecera del pedido
            String sqlPedido = "INSERT INTO pedidos (usuario_id, total, estado) VALUES (?, ?, 'PENDIENTE')";
            int pedidoId = -1;
            try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setInt(1, usuarioId);
                stmtPedido.setDouble(2, total);
                stmtPedido.executeUpdate();

                try (ResultSet keys = stmtPedido.getGeneratedKeys()) {
                    if (keys.next()) pedidoId = keys.getInt(1);
                }
            }

            // 3. Insertar los detalles del pedido
            String sqlDetalle = "INSERT INTO detalles_pedido (pedido_id, variante_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                for (ItemPedido item : items) {
                    stmtDetalle.setInt(1, pedidoId);
                    stmtDetalle.setInt(2, item.getVariante().getId());
                    stmtDetalle.setInt(3, item.getCantidad());
                    stmtDetalle.setDouble(4, item.getVariante().getPrenda().getPrecioBase());
                    stmtDetalle.addBatch();
                }
                stmtDetalle.executeBatch();
            }

            conn.commit();
            return new Pedido(pedidoId, usuarioId, items);

        } catch (SQLException e) {
            System.out.println("⚠️ Error en la base de datos al registrar pedido: " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) {} }
            return null;
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) {} }
        }
    }

    @Override
    public boolean procesarPago(Pedido pedido, String metodoPago) {
        if (pedido == null) return false;

        Connection conn = null;
        try {
            conn = ConexionDB.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Actualizar estado
            String sqlUpdatePedido = "UPDATE pedidos SET estado = 'PAGADO' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdatePedido)) {
                stmt.setInt(1, pedido.getId());
                stmt.executeUpdate();
            }

            // 2. Descontar stock real
            String sqlUpdateStock = "UPDATE variantes_prenda SET stock = stock - ? WHERE id = ?";
            try (PreparedStatement stmtStock = conn.prepareStatement(sqlUpdateStock)) {
                for (ItemPedido item : pedido.getLineas()) {
                    stmtStock.setInt(1, item.getCantidad());
                    stmtStock.setInt(2, item.getVariante().getId());
                    stmtStock.addBatch();
                }
                stmtStock.executeBatch();
            }

            conn.commit();
            pedido.setEstado("PAGADO");
            System.out.println("🎉 ¡Compra guardada y stock descontado con éxito en MySQL!");
            return true;

        } catch (SQLException e) {
            System.out.println("⚠️ Error al procesar el pago en DB: " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) {} }
            return false;
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) {} }
        }
    }

    @Override
    public List<VariantePrenda> filtrarPorTalleYColor(String talle, String color) { return new ArrayList<>(); }
}