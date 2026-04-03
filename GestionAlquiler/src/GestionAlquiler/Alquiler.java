package GestionAlquiler;

public class Alquiler {
    private Vehiculo vehiculo;
    private Cliente cliente;
    private int dias;

    public Alquiler(Vehiculo vehiculo, Cliente cliente, int dias) {
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.dias = dias;
        this.vehiculo.setDisponible(false);
    }

    public double calcularCostoTotal() {
        return vehiculo.getPrecioDia() * dias;
    }

    public void mostrarTicket() {
        System.out.println("--- TICKET DE ALQUILER ---");
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Vehículo: " + vehiculo.getModelo());
        System.out.println("Días: " + dias);
        System.out.println("Total a pagar: $" + calcularCostoTotal());
        System.out.println("--------------------------\n");
    }
}
