package Factory;

public abstract class EnvioCreator {

    //Factory method
    protected abstract Envio crearEnvio();

    //Lógica en común
    public void procesarEnvio (){
        Envio envio = crearEnvio();
        envio.enviarPaquete();
    }

}
