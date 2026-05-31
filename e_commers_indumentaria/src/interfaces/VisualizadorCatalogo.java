package interfaces;

import modelos.VariantePrenda;
import java.util.List;

public interface VisualizadorCatalogo {
    void mostrarCatalogo();
    List<VariantePrenda> filtrarPorTalleYColor(String talle, String color);
}
