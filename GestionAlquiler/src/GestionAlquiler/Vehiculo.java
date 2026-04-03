package GestionAlquiler;

public class Vehiculo {
    private String patente;
    private String modelo;
    private double precioDia;
    private boolean disponible;

    public Vehiculo(String patente, String modelo, double precioDia){
        this.patente = patente;
        this.modelo = modelo;
        this.precioDia = precioDia;
        this.disponible = true;

    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecioDia() {
        return precioDia;
    }

    public void setPrecioDia(double precioDia) {
        this.precioDia = precioDia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return String.format("Auto: %s (%s) - $%.2f/día [%s]",modelo, patente, precioDia, disponible ? "Disponible" : "Alquilado");
    }
}
