package Command;

public class ComandoEncender implements Comando{
    private Televisor tv;

    public ComandoEncender(Televisor tv){
        this.tv = tv;
    }

    @Override
    public void ejecutar(){
        tv.encender();
    }
}
