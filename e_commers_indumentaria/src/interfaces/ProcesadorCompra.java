package interfaces;

import modelos.Pedido;
import modelos.ItemPedido;
import java.util.List;

public interface ProcesadorCompra {
    Pedido registrarPedido(int usuarioId, List<ItemPedido> items);
    boolean procesarPago(Pedido pedido, String metodoPago);
}
