package interfaces;

import modelos.Prenda;

public interface GestorInventario {
    void agregarNuevaPrenda(Prenda prenda);
    void reponerStock(int varianteId, int cantidad);
}
