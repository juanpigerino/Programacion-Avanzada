package modelos;

public class Prenda {
    private int id;
    private String nombre;
    private String descripcion;
    private double precioBase;

    public Prenda(int id, String nombre, String descripcion, double precioBase) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
}