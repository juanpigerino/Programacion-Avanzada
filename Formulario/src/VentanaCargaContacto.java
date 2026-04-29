import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument;

// =========================================================
//  VentanaCargaContacto: formulario principal.
//  Validaciones EN campo  → DocumentFilter (tiempo real).
//  Validaciones FUERA     → FocusListener (al salir del campo).
// =========================================================
public class VentanaCargaContacto extends JFrame implements ActionListener {

    // --- Campos del formulario ---
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoDNI;
    private JTextField campoPasaporte;
    private JTextField campoTelefono;
    private JTextField campoCodPostal;
    private JTextField campoDomicilio;

    // --- Etiquetas de error (una por campo) ---
    private JLabel errorNombre;
    private JLabel errorApellido;
    private JLabel errorDNI;
    private JLabel errorPasaporte;
    private JLabel errorTelefono;
    private JLabel errorCodPostal;
    private JLabel errorDomicilio;

    // --- Botones ---
    private JButton btnAceptar;
    private JButton btnLimpiar;
    private JButton btnCancelar;

    // --- Colores de borde según estado ---
    private static final Border BORDE_NORMAL  = BorderFactory.createLineBorder(new Color(100, 120, 130), 1);
    private static final Border BORDE_ERROR   = BorderFactory.createLineBorder(new Color(231, 76,  60),  2);
    private static final Border BORDE_OK      = BorderFactory.createLineBorder(new Color(46,  204, 113), 2);

    // --- Colores generales ---
    private static final Color BG_VENTANA  = new Color(33,  47,  61);
    private static final Color BG_PANEL    = new Color(44,  62,  80);
    private static final Color BG_CAMPO    = new Color(52,  73,  94);
    private static final Color COLOR_LABEL = new Color(189, 195, 199);
    private static final Color COLOR_HINT  = new Color(127, 140, 141);
    private static final Color COLOR_ERROR = new Color(231, 76,  60);
    private static final Color COLOR_TEXTO = Color.WHITE;

    public VentanaCargaContacto() {
        setTitle("Carga de Contacto");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_VENTANA);

        construirUI();
        pack();
        setLocationRelativeTo(null); // Centrar en pantalla
        setVisible(true);
    }

    // =========================================================
    //  construirUI: arma toda la interfaz
    // =========================================================
    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout(0, 15));
        raiz.setBackground(BG_VENTANA);
        raiz.setBorder(new EmptyBorder(20, 30, 20, 30));

        raiz.add(construirTitulo(),   BorderLayout.NORTH);
        raiz.add(construirFormulario(), BorderLayout.CENTER);
        raiz.add(construirBotones(),  BorderLayout.SOUTH);

        add(raiz);
    }

    // --- Título ---
    private JLabel construirTitulo() {
        JLabel titulo = new JLabel("Carga de Contacto", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(0, 0, 5, 0));
        return titulo;
    }

    // --- Panel del formulario con GridBagLayout ---
    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 85, 100), 1),
            new EmptyBorder(18, 22, 18, 22)
        ));

        // Crear campos
        campoNombre    = crearCampo(22);
        campoApellido  = crearCampo(22);
        campoDNI       = crearCampo(10);
        campoPasaporte = crearCampo(10);
        campoTelefono  = crearCampo(22);
        campoCodPostal = crearCampo(6);
        campoDomicilio = crearCampo(36);

        // Crear etiquetas de error
        errorNombre    = crearLabelError();
        errorApellido  = crearLabelError();
        errorDNI       = crearLabelError();
        errorPasaporte = crearLabelError();
        errorTelefono  = crearLabelError();
        errorCodPostal = crearLabelError();
        errorDomicilio = crearLabelError();

        // Aplicar filtros DocumentFilter (validación EN el campo)
        setFiltro(campoNombre,    new ValidadorCampos.FiltroAlfabetico(20));
        setFiltro(campoApellido,  new ValidadorCampos.FiltroAlfabetico(20));
        setFiltro(campoDNI,       new ValidadorCampos.FiltroNumerico(8));
        setFiltro(campoPasaporte, new ValidadorCampos.FiltroPasaporte());
        setFiltro(campoTelefono,  new ValidadorCampos.FiltroTelefono());
        setFiltro(campoCodPostal, new ValidadorCampos.FiltroNumerico(4));
        setFiltro(campoDomicilio, new ValidadorCampos.FiltroLongitud(50));

        // Configurar tooltips con el formato esperado
        campoNombre.setToolTipText("Solo letras, máximo 20 caracteres");
        campoApellido.setToolTipText("Solo letras, máximo 20 caracteres");
        campoDNI.setToolTipText("8 dígitos numéricos, entre 10.000.000 y 60.000.000");
        campoPasaporte.setToolTipText("1 letra A-Z + 8 dígitos. Ej: N39392288");
        campoTelefono.setToolTipText("Más de 6 dígitos. Permite + ( ) -  Ej: +54 9 (261)-5-012345");
        campoCodPostal.setToolTipText("Exactamente 4 dígitos numéricos");
        campoDomicilio.setToolTipText("Máximo 50 caracteres");

        // Aplicar FocusListeners (validación FUERA del campo)
        setFoco(campoNombre,    errorNombre,    "nombre");
        setFoco(campoApellido,  errorApellido,  "apellido");
        setFoco(campoDNI,       errorDNI,       "dni");
        setFoco(campoPasaporte, errorPasaporte, "pasaporte");
        setFoco(campoTelefono,  errorTelefono,  "telefono");
        setFoco(campoCodPostal, errorCodPostal, "codpostal");
        setFoco(campoDomicilio, errorDomicilio, "domicilio");

        // Agregar filas al panel
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(3, 5, 0, 5);
        g.anchor  = GridBagConstraints.WEST;

        int fila = 0;
        agregarFila(panel, g, fila++, "Nombre:",       campoNombre,    errorNombre,
                    "máx. 20 caracteres alfabéticos");
        agregarFila(panel, g, fila++, "Apellido:",     campoApellido,  errorApellido,
                    "máx. 20 caracteres alfabéticos");
        agregarFila(panel, g, fila++, "DNI:",          campoDNI,       errorDNI,
                    "8 dígitos · 10.000.000–60.000.000");
        agregarFila(panel, g, fila++, "Pasaporte:",    campoPasaporte, errorPasaporte,
                    "letra A-Z + 8 dígitos  ej: N39392288");
        agregarFila(panel, g, fila++, "Teléfono:",     campoTelefono,  errorTelefono,
                    "> 6 dígitos, permite + ( ) -");
        agregarFila(panel, g, fila++, "Cód. Postal:",  campoCodPostal, errorCodPostal,
                    "4 dígitos numéricos");
        agregarFila(panel, g, fila++, "Domicilio:",    campoDomicilio, errorDomicilio,
                    "máx. 50 caracteres");

        return panel;
    }

    // --- Agrega una fila: label | campo + hint | error ---
    private void agregarFila(JPanel panel, GridBagConstraints g,
                              int fila, String textoLabel,
                              JTextField campo, JLabel errorLabel, String hint) {
        // Columna 0: etiqueta
        g.gridx = 0; g.gridy = fila * 2;
        g.fill  = GridBagConstraints.NONE;
        g.weightx = 0;
        JLabel lbl = new JLabel(textoLabel);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(COLOR_LABEL);
        panel.add(lbl, g);

        // Columna 1: campo + hint (en un sub-panel)
        g.gridx = 1; g.gridy = fila * 2;
        g.fill  = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        JPanel wrapper = new JPanel(new BorderLayout(8, 0));
        wrapper.setBackground(BG_PANEL);

        JLabel hintLbl = new JLabel(hint);
        hintLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        hintLbl.setForeground(COLOR_HINT);

        wrapper.add(campo,   BorderLayout.WEST);
        wrapper.add(hintLbl, BorderLayout.CENTER);
        panel.add(wrapper, g);

        // Fila de error (debajo del campo)
        g.gridx = 1; g.gridy = fila * 2 + 1;
        g.insets = new Insets(0, 5, 6, 5);
        panel.add(errorLabel, g);
        g.insets = new Insets(3, 5, 0, 5);
    }

    // --- Panel de botones ---
    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        panel.setBackground(BG_VENTANA);

        btnAceptar  = crearBoton("✔  Aceptar",   new Color(46,  204, 113));
        btnLimpiar  = crearBoton("🧹 Limpiar",    new Color(230, 126, 34));
        btnCancelar = crearBoton("✖  Cancelar",   new Color(231, 76,  60));

        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnCancelar.addActionListener(this);

        panel.add(btnAceptar);
        panel.add(btnLimpiar);
        panel.add(btnCancelar);
        return panel;
    }

    // =========================================================
    //  actionPerformed: maneja los tres botones
    // =========================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if      (e.getSource() == btnAceptar)  accionAceptar();
        else if (e.getSource() == btnLimpiar)  accionLimpiar();
        else if (e.getSource() == btnCancelar) System.exit(0);
    }

    // --- Aceptar: valida todos los campos y muestra resultado ---
    private void accionAceptar() {
        boolean ok = true;
        ok &= validar(campoNombre,    errorNombre,    "nombre");
        ok &= validar(campoApellido,  errorApellido,  "apellido");
        ok &= validar(campoDNI,       errorDNI,       "dni");
        ok &= validar(campoPasaporte, errorPasaporte, "pasaporte");
        ok &= validar(campoTelefono,  errorTelefono,  "telefono");
        ok &= validar(campoCodPostal, errorCodPostal, "codpostal");
        ok &= validar(campoDomicilio, errorDomicilio, "domicilio");

        // Validación de exclusividad DNI / Pasaporte
        ok &= validarExclusividadDNIPasaporte();

        if (ok) {
            mostrarResumen();
        } else {
            JOptionPane.showMessageDialog(this,
                "Hay campos con errores. Por favor corregílos antes de continuar.",
                "Formulario inválido",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Limpiar: borra todo y resetea bordes/errores ---
    private void accionLimpiar() {
        JTextField[] campos  = { campoNombre, campoApellido, campoDNI,
                                 campoPasaporte, campoTelefono, campoCodPostal, campoDomicilio };
        JLabel[]     errores = { errorNombre, errorApellido, errorDNI,
                                 errorPasaporte, errorTelefono, errorCodPostal, errorDomicilio };
        for (int i = 0; i < campos.length; i++) {
            campos[i].setText("");
            setBorde(campos[i], BORDE_NORMAL);
            errores[i].setText(" ");
        }
        campoNombre.requestFocus();
    }

    // --- Muestra resumen con los datos ingresados ---
    private void mostrarResumen() {
        StringBuilder sb = new StringBuilder("✔  Contacto guardado correctamente\n\n");
        sb.append("Nombre:       ").append(campoNombre.getText()).append("\n");
        sb.append("Apellido:     ").append(campoApellido.getText()).append("\n");
        if (!campoDNI.getText().isEmpty())
            sb.append("DNI:          ").append(campoDNI.getText()).append("\n");
        if (!campoPasaporte.getText().isEmpty())
            sb.append("Pasaporte:    ").append(campoPasaporte.getText()).append("\n");
        sb.append("Teléfono:     ").append(campoTelefono.getText()).append("\n");
        sb.append("Cód. Postal:  ").append(campoCodPostal.getText()).append("\n");
        sb.append("Domicilio:    ").append(campoDomicilio.getText()).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Contacto guardado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    //  Validación FUERA del campo (FocusListener)
    // =========================================================
    private void setFoco(JTextField campo, JLabel errorLabel, String tipo) {
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validar(campo, errorLabel, tipo);
                // Al salir de DNI o Pasaporte re-evaluar exclusividad
                if (tipo.equals("dni") || tipo.equals("pasaporte"))
                    validarExclusividadDNIPasaporte();
            }
        });
    }

    // --- Valida un campo e indica el resultado visualmente ---
    private boolean validar(JTextField campo, JLabel errorLabel, String tipo) {
        String valor = campo.getText().trim();
        String error = ValidadorCampos.validar(valor, tipo);
        if (error != null) {
            marcarError(campo, errorLabel, error);
            return false;
        }
        marcarOk(campo, errorLabel);
        return true;
    }

    // --- Exclusividad DNI / Pasaporte ---
    private boolean validarExclusividadDNIPasaporte() {
        String dni = campoDNI.getText().trim();
        String pas = campoPasaporte.getText().trim();
        if (!dni.isEmpty() && !pas.isEmpty()) {
            marcarError(campoDNI,       errorDNI,
                "DNI y Pasaporte no pueden tener valor al mismo tiempo.");
            marcarError(campoPasaporte, errorPasaporte,
                "DNI y Pasaporte no pueden tener valor al mismo tiempo.");
            return false;
        }
        // Si uno se vació, limpiar el error de exclusividad del otro
        if (dni.isEmpty() && errorDNI.getText().contains("al mismo tiempo"))
            marcarOk(campoDNI, errorDNI);
        if (pas.isEmpty() && errorPasaporte.getText().contains("al mismo tiempo"))
            marcarOk(campoPasaporte, errorPasaporte);
        return true;
    }

    // =========================================================
    //  Helpers visuales
    // =========================================================
    private void marcarError(JTextField campo, JLabel lbl, String msg) {
        setBorde(campo, BORDE_ERROR);
        lbl.setText("✖  " + msg);
        lbl.setForeground(COLOR_ERROR);
    }

    private void marcarOk(JTextField campo, JLabel lbl) {
        setBorde(campo, campo.getText().trim().isEmpty() ? BORDE_NORMAL : BORDE_OK);
        lbl.setText(" ");
    }

    private void setBorde(JTextField campo, Border borde) {
        campo.setBorder(new CompoundBorder(borde, new EmptyBorder(4, 7, 4, 7)));
    }

    // =========================================================
    //  Fábricas de componentes con estilo uniforme
    // =========================================================
    private JTextField crearCampo(int columnas) {
        JTextField f = new JTextField(columnas);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBackground(BG_CAMPO);
        f.setForeground(COLOR_TEXTO);
        f.setCaretColor(COLOR_TEXTO);
        setBorde(f, BORDE_NORMAL);
        return f;
    }

    private JLabel crearLabelError() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Arial", Font.ITALIC, 11));
        l.setForeground(COLOR_ERROR);
        return l;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(135, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setFiltro(JTextField campo, javax.swing.text.DocumentFilter filtro) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(filtro);
    }

    // =========================================================
    //  Main
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCargaContacto());
    }
}
