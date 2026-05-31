package modelos;

import java.util.List;

public class Pedido {
    private int id;
    private int usuarioId;
    private List<ItemPedido> lineas;
    private double total;
    private String estado; // PENDIENTE, PAGADO, CANCELADO

    public Pedido(int id, int usuarioId, List<ItemPedido> lineas) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.lineas = lineas;
        this.estado = "PENDIENTE";
        this.total = calcularTotal();
    }

    private double calcularTotal() {
        return lineas.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    // Getters y Setters
    public int getId() { return id; }
    public double getTotal() { return total; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<ItemPedido> getLineas() { return lineas; }
}
