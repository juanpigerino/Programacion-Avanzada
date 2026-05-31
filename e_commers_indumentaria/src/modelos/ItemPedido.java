package modelos;

public class ItemPedido {
    private VariantePrenda variante;
    private int cantidad;
    private double precioUnitario;

    public ItemPedido(VariantePrenda variante, int cantidad) {
        this.variante = variante;
        this.cantidad = cantidad;
        this.precioUnitario = variante.getPrenda().getPrecioBase(); // Copia el precio actual
    }

    public VariantePrenda getVariante() { return variante; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return precioUnitario * cantidad; }
}
