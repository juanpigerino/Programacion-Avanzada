package Composite;

public class Main {
    public static void main(String[] args) {
        ElementoGrafico boton = new Button();
        ElementoGrafico campo = new CampoDeTexto();

        // Crear el composite (el contenedor)
        Contenedor contenedorPrincipal = new Contenedor();

        // Armar la estructura
        contenedorPrincipal.agregar(boton);
        contenedorPrincipal.agregar(campo);

        // Con una sola llamada, se dibujan todos los elementos internos
        contenedorPrincipal.dibujar();
    }
}