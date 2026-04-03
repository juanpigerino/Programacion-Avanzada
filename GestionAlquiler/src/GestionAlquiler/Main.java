package GestionAlquiler;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Vehiculo> flota = new ArrayList<>();
        flota.add(new Vehiculo("ABC-123", "Toyota Corolla", 50.0));
        flota.add(new Vehiculo("XYZ-789", "Ford Fiesta", 35.0));

        Cliente cliente1 = new Cliente("12345678","Joaquin Perez");

        System.out.println("Autos disponibles");
        for(Vehiculo v: flota){
            if(v.isDisponible()){
                System.out.println(v);
            }
        }

        Vehiculo autoElegido = flota.get(0);
        Alquiler miAlquiler = new Alquiler(autoElegido,cliente1,5);

        miAlquiler.mostrarTicket();

        System.out.println("Estado actualizado de la flota:");
        flota.forEach(System.out::println);
    }
}