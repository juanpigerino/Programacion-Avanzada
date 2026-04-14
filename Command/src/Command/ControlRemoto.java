package Command;

public class ControlRemoto {
    private Comando slot;

    public void setComando(Comando c) {
        this.slot = c;
    }

    public void presionarBoton() {
        slot.ejecutar();
    }
}
