import javax.swing.text.*;

// =========================================================
//  ValidadorCampos: contiene la lógica de validación de cada
//  campo y los filtros DocumentFilter para control en tiempo real.
// =========================================================
public class ValidadorCampos {

    // --- Punto de entrada unificado para validar por tipo ---
    public static String validar(String valor, String tipo) {
        switch (tipo) {
            case "nombre":    return validarNombre(valor);
            case "apellido":  return validarApellido(valor);
            case "dni":       return validarDNI(valor);
            case "pasaporte": return validarPasaporte(valor);
            case "telefono":  return validarTelefono(valor);
            case "codpostal": return validarCodigoPostal(valor);
            case "domicilio": return validarDomicilio(valor);
            default:          return null;
        }
    }

    // ---------------------------------------------------------
    //  NOMBRE: solo letras (incluyendo acentos y ñ), máx 20
    // ---------------------------------------------------------
    private static String validarNombre(String v) {
        if (v.isEmpty())                              return "El nombre es obligatorio.";
        if (!v.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))   return "Solo se permiten letras.";
        if (v.length() > 20)                          return "Máximo 20 caracteres.";
        return null;
    }

    // ---------------------------------------------------------
    //  APELLIDO: ídem nombre
    // ---------------------------------------------------------
    private static String validarApellido(String v) {
        if (v.isEmpty())                              return "El apellido es obligatorio.";
        if (!v.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))   return "Solo se permiten letras.";
        if (v.length() > 20)                          return "Máximo 20 caracteres.";
        return null;
    }

    // ---------------------------------------------------------
    //  DNI: 8 dígitos, entre 10.000.000 y 60.000.000
    //  (campo opcional siempre que Pasaporte tenga valor)
    // ---------------------------------------------------------
    public static String validarDNI(String v) {
        if (v.isEmpty()) return null;                   // Se valida exclusividad afuera
        if (!v.matches("\\d{8}"))                       return "Debe tener exactamente 8 dígitos.";
        long num = Long.parseLong(v);
        if (num < 10_000_000 || num > 60_000_000)       return "Debe estar entre 10.000.000 y 60.000.000.";
        return null;
    }

    // ---------------------------------------------------------
    //  PASAPORTE: 1 letra A-Z + 8 dígitos, parte numérica
    //             entre 10.000.000 y 60.000.000
    // ---------------------------------------------------------
    public static String validarPasaporte(String v) {
        if (v.isEmpty()) return null;
        if (!v.matches("[A-Z]\\d{8}"))                  return "Formato inválido. Ej: N39392288";
        long num = Long.parseLong(v.substring(1));
        if (num < 10_000_000 || num > 60_000_000)       return "Parte numérica entre 10.000.000 y 60.000.000.";
        return null;
    }

    // ---------------------------------------------------------
    //  TELÉFONO: al menos 6 dígitos, permite +, -, (, ), espacio
    // ---------------------------------------------------------
    private static String validarTelefono(String v) {
        if (v.isEmpty())                                return "El teléfono es obligatorio.";
        if (!v.matches("[0-9+()\\- ]+"))                return "Solo dígitos y los símbolos + ( ) -";
        long digitos = v.chars().filter(Character::isDigit).count();
        if (digitos < 6)                                return "Debe contener al menos 6 dígitos.";
        return null;
    }

    // ---------------------------------------------------------
    //  CÓDIGO POSTAL: exactamente 4 dígitos
    // ---------------------------------------------------------
    private static String validarCodigoPostal(String v) {
        if (v.isEmpty())                                return "El código postal es obligatorio.";
        if (!v.matches("\\d{4}"))                       return "Debe tener exactamente 4 dígitos.";
        return null;
    }

    // ---------------------------------------------------------
    //  DOMICILIO: máximo 50 caracteres
    // ---------------------------------------------------------
    private static String validarDomicilio(String v) {
        if (v.isEmpty())                                return "El domicilio es obligatorio.";
        if (v.length() > 50)                            return "Máximo 50 caracteres.";
        return null;
    }

    // =========================================================
    //  FILTROS DocumentFilter  (control en tiempo real)
    // =========================================================

    // --- Solo letras (y acentos/ñ) con longitud máxima ---
    public static class FiltroAlfabetico extends DocumentFilter {
        private final int maxLen;
        public FiltroAlfabetico(int maxLen) { this.maxLen = maxLen; }

        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
            String f = str.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (fb.getDocument().getLength() + f.length() <= maxLen)
                super.insertString(fb, off, f, a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
            String f = str.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (fb.getDocument().getLength() - len + f.length() <= maxLen)
                super.replace(fb, off, len, f, a);
        }
    }

    // --- Solo dígitos con longitud máxima ---
    public static class FiltroNumerico extends DocumentFilter {
        private final int maxLen;
        public FiltroNumerico(int maxLen) { this.maxLen = maxLen; }

        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
            String f = str.replaceAll("[^0-9]", "");
            if (fb.getDocument().getLength() + f.length() <= maxLen)
                super.insertString(fb, off, f, a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
            String f = str.replaceAll("[^0-9]", "");
            if (fb.getDocument().getLength() - len + f.length() <= maxLen)
                super.replace(fb, off, len, f, a);
        }
    }

    // --- Pasaporte: 1ª posición A-Z (forzada a mayúscula), resto dígitos, máx 9 ---
    public static class FiltroPasaporte extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
            aplicar(fb, off, 0, str, a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
            aplicar(fb, off, len, str, a);
        }

        private void aplicar(FilterBypass fb, int off, int len, String entrada, AttributeSet a)
                throws BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo  = actual.substring(0, off) + entrada + actual.substring(off + len);
            if (nuevo.length() > 9) return;
            if (nuevo.isEmpty())    { super.replace(fb, 0, actual.length(), "", a); return; }

            // Primera posición: solo letra, convertida a mayúscula
            char primero = nuevo.charAt(0);
            if (!Character.isLetter(primero)) return;
            primero = Character.toUpperCase(primero);

            // Resto: solo dígitos
            String resto = nuevo.substring(1);
            if (!resto.matches("\\d*")) return;

            super.replace(fb, 0, actual.length(), primero + resto, a);
        }
    }

    // --- Teléfono: dígitos + + ( ) - espacio ---
    public static class FiltroTelefono extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
            super.insertString(fb, off, str.replaceAll("[^0-9+()\\- ]", ""), a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
            super.replace(fb, off, len, str.replaceAll("[^0-9+()\\- ]", ""), a);
        }
    }

    // --- Solo longitud máxima, cualquier carácter ---
    public static class FiltroLongitud extends DocumentFilter {
        private final int maxLen;
        public FiltroLongitud(int maxLen) { this.maxLen = maxLen; }

        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
            if (fb.getDocument().getLength() + str.length() <= maxLen)
                super.insertString(fb, off, str, a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
            if (fb.getDocument().getLength() - len + str.length() <= maxLen)
                super.replace(fb, off, len, str, a);
        }
    }
}
