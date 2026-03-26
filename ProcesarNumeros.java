import java.util.Random;

public class ProcesarNumeros {
    public static void main(String[] args) {
        int cantidad = 500;
        int min = 10;
        int max = 1000;
        
        long sumaTotal = 0;
        Random random = new Random();

        System.out.println("Generando " + cantidad + " números aleatorios...");

        for (int i = 0; i < cantidad; i++) {
            int numeroAleatorio = random.nextInt(max - min + 1) + min;
            sumaTotal += numeroAleatorio;
        }

        double promedio = (double) sumaTotal / cantidad;

        System.out.println("--- Resultados ---");
        System.out.println("Suma total: " + sumaTotal);
        System.out.println("Promedio: " + promedio);
    }
}
