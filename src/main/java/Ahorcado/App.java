package Ahorcado;

import clases.Juego;
import clases.Jugador;

import java.util.HashSet;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Juego juego =new Juego();

        System.out.println("BIENVENIDOS AL JUEGO DEL AHORCADO!! Conducido por su humilde,carismatico y muy guapo servidor, el Acertijo... enemigo renombrado de Ciudad Gotica");
        System.out.println("Parece que el duo maravilla ha venido aqui a vencerme...no creo que salgan de aqui CON VIDA!! ");
        System.out.println("*risas malevolas de fondo*");
        System.out.println("Les dare cinco intentos para adivinar en que palabra estoy pensando... ya la he escogido. \n COMIENCEN!");

        Jugador h1 = new Jugador("Batman",juego);
        Jugador h2 = new Jugador("Robin",juego);
        Thread t1 = new Thread(h1);
        t1.setName(h1.getNombre());
        Thread t2 = new Thread(h2);
        t2.setName(h2.getNombre());

        t1.start();
        t2.start();



    }




}
