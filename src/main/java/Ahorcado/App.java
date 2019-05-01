package Ahorcado;

import clases.Juego;
import clases.Jugador;

import java.sql.*;
import java.util.ArrayList;
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

        String palabraElegida= elegirPalabra();

        //se comprueba que no hubo errores en la funcion(si se devolvio algun valor)
        if(!palabraElegida.equals("")) {

            Juego juego = new Juego(palabraElegida);

            System.out.println("BIENVENIDOS AL JUEGO DEL AHORCADO!! Conducido por su humilde,carismatico y muy guapo servidor, el Acertijo... enemigo renombrado de Ciudad Gotica");
            System.out.println("Parece que el duo maravilla ha venido aqui a vencerme...y vinieron con una amiguita veo.No creo que salgan de aqui CON VIDA!! ");
            System.out.println("*risas malevolas de fondo*");
            System.out.println("Les dare cinco intentos a cada uno para adivinar en que palabra estoy pensando... ya la he escogido. \n COMIENCEN!");

            Jugador h1 = new Jugador("Batman", juego);
            Jugador h2 = new Jugador("Robin", juego);
            Jugador h3 = new Jugador("Batichica", juego);
            Thread t1 = new Thread(h1);
            t1.setName(h1.getNombre());
            Thread t2 = new Thread(h2);
            t2.setName(h2.getNombre());
            Thread t3 = new Thread(h3);
            t3.setName(h3.getNombre());

            t1.start();
            t2.start();
            t3.start();
        }


    }

    /**
     *Metodo que elige desde la base de datos una palabra que los jugadores deberan adivinar
     */

    private static String elegirPalabra()
    {
        String palabra= "";

        try {
            Class.forName("com.mysql.jdbc.Driver");

        }catch (ClassNotFoundException e)
        {
            System.out.println("Falta la libreria de mysql!!");
        }


        try {
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost/tp2ahorcado", "root", "");

            Statement st= connection.createStatement();
            ResultSet rs=st.executeQuery("SELECT PALABRA_RANDOM FROM PALABRAS");
            ArrayList<String> palabrasDAO=new ArrayList<>();

            while(rs.next())
            {
                palabrasDAO.add(rs.getString("palabra_random"));
            }

            palabra=palabrasDAO.get((int)Math.floor(Math.random()*(palabrasDAO.size())));


        }catch (SQLException e){

            System.out.println("No se pudo conectar a la base de datos");
        }
        catch (Exception e){
            System.out.println("es otra cosa");
        }


        return palabra;
    }



}
