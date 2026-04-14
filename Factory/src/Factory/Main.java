package Factory;

public class Main {
    public static void main(String[] args) {

        EnvioCreator EnvioCorreo = new EnvioCorreoCreator();
        EnvioCorreo.procesarEnvio();

        EnvioCreator EnvioCamion = new EnvioCamionCreator();
        EnvioCamion.procesarEnvio();

        EnvioCreator EnvioMoto = new EnvioMotoCreator();
        EnvioMoto.procesarEnvio();

    }
}