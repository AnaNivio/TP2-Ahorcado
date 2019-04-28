package clases;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;


public class Juego {
    private boolean jugando= Boolean.FALSE;
    private static final int intentos=50;
    private int vidasJugador=50;
    private String palabraElegida=elegirPalabra();
    private Set<Character> letrasErroneas=new HashSet<>();
    private Set<Character> letrasCorrectas=new HashSet<>();



    public synchronized void jugar() {

        boolean gano=Boolean.FALSE;


        while(vidasJugador != 0 && gano == Boolean.FALSE && !palabraElegida.equals(" ")){

            //aca debo considerar que entran todos los hilos. Hay uno que va a lograr pasar la condicion mientras que el resto se quedara en el wait
            while (jugando) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            jugando = Boolean.TRUE;

            String nombreJugador = Thread.currentThread().getName();

            System.out.println("\n(Va a adivinar " + nombreJugador + ")");
            //Las palabras cargadas en la BD estan en minuscula
            //random para tirar un caracter aleatorio
            int random = (int) Math.floor(Math.random() * (97 - 122) + 122);
            char letraRandom = (char) random;

            //Función indexOf:
            // Devuelve palabraElegida -1 si lo que busca no se encuentra, y devuelve un entero positivo si la encuentra
            if (palabraElegida.indexOf(letraRandom) < 0) {
                while (palabraElegida.indexOf(letraRandom) < 0 && !letrasErroneas.add(letraRandom)) {
                    random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                    letraRandom = (char) random;
                }

            } else if (palabraElegida.indexOf(letraRandom) >= 0) {

                while (palabraElegida.indexOf(letraRandom) >= 0 && !letrasCorrectas.add(letraRandom)) {
                    random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                    letraRandom = (char) random;
                }

            }


            /*La comprobacion de los caracteres repetidos fue lo mas dificil. Funciona asi:
             * En el caso que alguna de las letras (ya sea correcta o no) ya se encuentre en su respectiva lista
             * se eligira otro numero caracter para reemplazarla. El problema es que si hablamos en los erroneos, este
             * caracter puede pertenecer a los correctos y lo mismo sucede en el caso opuesto.
             * Por eso, junto a la condicion de si se puede o no agregar a la lista, se pregunta si este nuevo caracter
             * sigue siendo erroneo/correcto.
             * Lo que definira si pertenece a un grupo u otro es la siguiente condicion :
             * Se pregunta una vez mas si la letra pertenece o no a la palabra y, en cada respectivo caso, se lanza el mensaje
             * correspondiente y se agrega la letra a la lista*/
            System.out.print("(" + nombreJugador + " pregunta si la letra " + letraRandom + " se encuentra en la palabra...)\n");

            if (palabraElegida.indexOf(letraRandom) < 0) {
                letrasErroneas.add(letraRandom);
                System.out.print("Ups! Que maaal! La letra " + letraRandom + " no esta en la palabra...pierdes una vida. MUAJAJAJA!\n");
                vidasJugador = vidasJugador - 1;

            } else {
                letrasCorrectas.add(letraRandom);
                System.out.println("(" + nombreJugador + " ha adivinado una letra :" + letraRandom + ". Muy Bien!)");
            }


            System.out.println("---------------------------------------------------------");

            System.out.println("Letras erroneas: " + letrasErroneas.toString());
            System.out.println("Palabra a adivinar: " + codificacionPalabraElegida(palabraElegida));


            gano=ganoOperdio(palabraElegida,letrasCorrectas);

            if(gano == Boolean.FALSE) {

                if ((palabraElegida.length() - letrasCorrectas.size() > 1)) {
                    System.out.println("JAJAJA! Jamas adivinaran la palabra!!!");
                } else {
                    System.out.println("E-EM... Puede ser que esten cerca...PERO NO LOGRARAN VENCERME, YA VERAN!!");
                }

                System.out.println("---------------------------------------------------------");

            } else {

                System.out.println("Me han logrado vencer...pero perder una batalla no significa que he perdido la guerra. ME VENGARE!!");
                grabarGanador(Thread.currentThread().getName(),palabraElegida);
                palabraElegida=" ";

            }

            if(vidasJugador == 0) {
            System.out.println("MUAJAJAJA! HAN PERDIDO. SON TAAAN PATETICOS...VEAMOS QUIEN LOS SALVARA AHORA MUAJAJAJAJA");

            } else if (vidasJugador < intentos && !palabraElegida.equals(" ")) {
                System.out.println("(Tu compañero te da animos; aun sigues teniendo vidas, no te des por vencido!)");
                jugando = Boolean.FALSE;
                notifyAll();

            }

        }


    }

    /**
    *Metodo que elige desde la base de datos una palabra que los jugadores deberan adivinar
    */

    private String elegirPalabra()
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

    /**
     * Esconde las letras que no se han averiguado aun para que el jugador sepa que palabras ha adivinado
     * @param palabraElegida
     * @return String con la palabra codificada; solo mostrara las letras adivinadas
     */
    private String codificacionPalabraElegida(String palabraElegida){

            String palabraCodificada=palabraElegida;
            int largo=palabraCodificada.length();
            char secreto=(char)63;

            if(letrasCorrectas.isEmpty())
            {
                for(int i=0;i <largo;i++)
                {
                    palabraCodificada=palabraCodificada.replace(palabraCodificada.charAt(i),'?');

                }
            }else
            {
                for(Character c : letrasCorrectas){

                    for(int i=0;i <largo;i++)
                    {
                        if(!letrasCorrectas.contains(palabraCodificada.charAt(i))){
                            palabraCodificada=palabraCodificada.replace(palabraCodificada.charAt(i),'?');
                        }
                    }
                }
            }


            return palabraCodificada;
        }

    /**
     *Indica si el jugador ha ganado o no
     * @param palabraElegida
     * @param set
     * @return booleano que devuelve true si el jugador gano o false si perdio
     *
     * */
    private boolean ganoOperdio(String palabraElegida,Set<Character> set){
        boolean gano=Boolean.FALSE;
        int largoPalabra=palabraElegida.length();
        int largoSet=set.size();

        //voy a considerar junto con las palabras dentro del set, las repetidas. Las contare desde el string

        for(Character c: set){

            int posicion, contador = 0;
            //se busca la primera vez que aparece
            posicion = palabraElegida.indexOf(c);
            while (posicion != -1) { //mientras se encuentre el caracter
                contador++;           //se cuenta
                //se sigue buscando a partir de la posición siguiente a la encontrada
                posicion = palabraElegida.indexOf(c, posicion + 1);
            }

            largoSet=largoSet+contador-1; // resto uno porque el contador cuenta la cant de veces que aparece en la palabra. En el set, se presenta solo una vez, asi que resto eso

        }

        if(largoSet == largoPalabra)
        {
            gano=Boolean.TRUE;
        }

        return gano;
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
