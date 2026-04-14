package Command;

public class Main {
    public static void main(String[] args) {
        Televisor miTv = new Televisor();
        Comando encender = new ComandoEncender(miTv);

        ControlRemoto control = new ControlRemoto();

        control.setComando(encender);
        control.presionarBoton();
    }
}