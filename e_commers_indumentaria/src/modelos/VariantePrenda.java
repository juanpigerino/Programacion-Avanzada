package modelos;

public class VariantePrenda {
    private int id;
    private Prenda prenda;
    private String talle; // S, M, L, XL
    private String color;
    private int stock;

    public VariantePrenda(int id, Prenda prenda, String talle, String color, int stock) {
        this.id = id;
        this.prenda = prenda;
        this.talle = talle;
        this.color = color;
        this.stock = stock;
    }

    public int getId() { return id; }
    public Prenda getPrenda() { return prenda; }
    public String getTalle() { return talle; }
    public String getColor() { return color; }
    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}