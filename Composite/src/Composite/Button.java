package Composite;
import java.util.function.Consumer;


public class Button implements ElementoBoton{

    @Override
    public void dibujar(){
        System.out.println("dibujando boton");
    }
    @Override
    public <T> void click(Consumer<T> consumer){
        consumer.accept(null);
    }

    @Override
    public int getColor(){
        return 0;
    }
}
