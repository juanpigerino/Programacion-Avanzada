package Composite;
import java.util.function.Consumer;

public interface ElementoBoton extends ElementoGrafico {
    <T> void click(Consumer<T> consumer);

        int getColor();
}
