package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Jugador implements Runnable{

    private String nombre;
    private Juego juego;
    private int vidasJugador=5;

    public Jugador(String nombre, Juego juego) {

        this.nombre=nombre;
        this.juego = juego;
    }

    @Override
    public void run() {
        int i=vidasJugador;
        boolean gano=Boolean.FALSE;

        while(i !=0 && gano==Boolean.FALSE){
            gano=juego.jugar();
            i--;
        }

        //compruebo si ya hay un ganador. Decidi hacerlo de este modo ya que,
        // al ser "gano" parte del recurso compartido, cuando se volvia true porque habia un ganador,
        // el perdedor tambien devolvia true.
        // Asi que agregue "nombreGanador" en Juego para estar comletamente segura de quien gano

        if(juego.getNombreGanador().equals(nombre)){

                //grabarGanador(juego.getNombreGanador(),juego.getPalabraElegida());
                System.out.println("-----------------------------------------------");
                System.out.println("La victoria del superheroe ha sido publicada en los diarios, quedando para la posteridad. Felicidades!!");

        }
        
         if(i == 0)
        {
            System.out.println("MUAJAJAJA! HAS PERDIDO "+ nombre.toUpperCase() + "! VEAMOS QUIEN TE SALVARA AHORA MUAJAJAJAJA");
        }

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    /**
     * Graba en la BD el ganador de la ronda
     * @param nombreGanador
     * @param palabraGanadora
     */
    public void grabarGanador(String nombreGanador,String palabraGanadora){

        try {
            Class.forName("com.mysql.jdbc.Driver");

        }catch (ClassNotFoundException e)
        {
            System.out.println("Falta la libreria de mysql!!");
        }


        try {
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost/tp2ahorcado", "root", "");

            PreparedStatement ps= connection.prepareStatement("INSERT INTO ganadores(nombre_ganador,fecha_victoria,palabra_ganadora)"
                    + "VALUES (?,?,?);");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            ps.setString(1,nombreGanador);
            ps.setString(2,dateFormat.format(date));
            ps.setString(3,palabraGanadora);

            ps.execute();


        }catch (SQLException e){

            System.out.println("No se pudo conectar a la base de datos");
        }
        catch (Exception e){
            System.out.println("es otra cosa");
        }

        System.out.println("\n\n----------------------------------------------------------------");

    }
}
