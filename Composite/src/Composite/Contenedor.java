package Composite;
import java.util.List;
import java.util.ArrayList;

public class Contenedor implements ElementoGrafico {
    private List<ElementoGrafico> hijos = new ArrayList<>();

    public void agregar(ElementoGrafico elemento) {
        hijos.add(elemento);
    }

    public void eliminar(ElementoGrafico elemento) {
        hijos.remove(elemento);
    }

    @Override
    public void dibujar() {
        System.out.println("Dibujando contenedor y sus elementos:");
        for (ElementoGrafico hijo : hijos) {
            hijo.dibujar();
        }
    }
}