package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class VistaChat extends JFrame {
    private JTextArea areaChat;
    private JTextField campoMensaje;
    private JButton botonEnviar;

    public VistaChat() {
        setTitle("Chat Cliente");
        // Aumentamos el tamaño inicial de la ventana para que la bandeja sea más grande
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Espaciado entre componentes

        // 1. BANDEJA DE MENSAJES (JTextArea)
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);

        // Aumentamos el tamaño de la letra de los mensajes
        areaChat.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        // Agregamos un margen interno para que el texto no toque los bordes
        areaChat.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollChat = new JScrollPane(areaChat);
        scrollChat.setBorder(BorderFactory.createEtchedBorder());

        // 2. BARRA DE MENSAJES (JTextField)
        campoMensaje = new JTextField();
        // Cambiamos el color de fondo para que destaque (un gris azulado claro)
        campoMensaje.setBackground(new Color(235, 245, 255));
        // Aumentamos la letra de lo que escribes
        campoMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        // Hacemos la barra un poco más alta
        campoMensaje.setPreferredSize(new Dimension(0, 50));

        // 3. BOTÓN DE ENVIAR
        botonEnviar = new JButton("Enviar");
        botonEnviar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        // Hacemos el botón más ancho y alto
        botonEnviar.setPreferredSize(new Dimension(130, 50));
        botonEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel inferior con margen extra
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        // Padding general para la ventana
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        add(scrollChat, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Centrar ventana en pantalla
        setLocationRelativeTo(null);
    }

    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaChat.append(mensaje + "\n");
            // Auto-scroll al final para ver siempre el último mensaje
            areaChat.setCaretPosition(areaChat.getDocument().getLength());
        });
    }

    public String getMensajeYLimpiar() {
        String msj = campoMensaje.getText();
        campoMensaje.setText("");
        return msj;
    }

    public void configurarAccionEnviar(ActionListener accion) {
        botonEnviar.addActionListener(accion);
        campoMensaje.addActionListener(accion);
    }
}