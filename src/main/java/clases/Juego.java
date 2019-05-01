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
    boolean gano=Boolean.FALSE;
    private String palabraElegida;
    private Set<Character> letrasErroneas=new HashSet<>();
    private Set<Character> letrasCorrectas=new HashSet<>();
    private String nombreGanador=" ";

    public Juego(String elegirPalabra) {

        this.palabraElegida=elegirPalabra;
    }


    public synchronized boolean jugar() {

            //aca debo considerar que entran todos los hilos. Hay uno que va a lograr pasar la condicion mientras que el resto se quedara en el wait
        if(!gano){

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

            /*Analizando una vez mas este fragmento de codigo (y leyendo mi antigua explicacion) decidi modificar algunas cosas.
            * El funcionamiento sigue siendo igual: por medio de la funcion IndexOf se indicara a que lista pertenecera la
            * letra no sin antes comprobar que la misma pueda agregarse a la lista y, en caso de no poder hcerlo por la repeticion
            * de alguna letra, se elige otra al azar siempre y cuando esta no pertenezca a la palabra.
            * Ahora lo que modifique: en mi version anterior, habia considerado que la letra random no perteneciese a los erroneos
            * una vez elegida. Por eso, a la hora de mostrar mensajes que se volvia a hacer la comprobacion, agregaba en la lista
             * donde correspondia. Esto generaba un problema.
             * Supongamos que tenemos la palabra "red". H es una letra erronea que ya se encuentra cargada en el set de letras
             * erroneas. Cuando intente agregarse a la lista, no podra hacerlo. Asi que, cambia su valor a e.
             * Al final, a la hora de mostrar el mensaje de que se adivino una palabra, se agregaria al set de correctas.
             * El problema es el siguiente: Y si e ya se hallaba en la lista de correctas? Entonces uno o mas jugadores
             * presentarian la misma respuesta.
             *
             * Es por esta razon que cambie la logica en los while: dentro de la condicion de si es erronea/correcta
              * y si se puede agregar a su set especifico, tambien se comprueba si letra random
             * pertenece al otro set y, en caso de no poder agregarse, que cambie su valor una vez mas. De esa forma, me
             * aseguro que la letra siempre sera cargada en cada lista y no se repetira el valor.*/

            //Función indexOf:
            // Devuelve palabraElegida -1 si lo que busca no se encuentra, y devuelve un entero positivo si la encuentra
            if (palabraElegida.indexOf(letraRandom) < 0) {
                while (palabraElegida.indexOf(letraRandom) < 0 && !letrasErroneas.add(letraRandom)) {
                    random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                    letraRandom = (char) random;

                    while(palabraElegida.indexOf(letraRandom) >= 0 && !letrasCorrectas.add(letraRandom)) {
                        random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                        letraRandom = (char) random;
                    }
                }

                System.out.print("(" + nombreJugador + " pregunta si la letra " + letraRandom + " se encuentra en la palabra...)\n");
                System.out.print("Ups! Que maaal! La letra " + letraRandom + " no esta en la palabra...pierdes una vida. MUAJAJAJA!\n");


            } else if (palabraElegida.indexOf(letraRandom) >= 0) {

                while (palabraElegida.indexOf(letraRandom) >= 0 && !letrasCorrectas.add(letraRandom)) {
                    random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                    letraRandom = (char) random;

                    while(palabraElegida.indexOf(letraRandom) < 0 && !letrasErroneas.add(letraRandom)) {
                        random = (int) Math.floor(Math.random() * (97 - 122) + 122);
                        letraRandom = (char) random;
                    }

                }
                System.out.print("(" + nombreJugador + " pregunta si la letra " + letraRandom + " se encuentra en la palabra...)\n");
                System.out.println("(" + nombreJugador + " ha adivinado una letra :" + letraRandom + ". Muy Bien!)");

            }


            System.out.println("---------------------------------------------------------");

            System.out.println("Letras erroneas: " + letrasErroneas.toString());
            System.out.println("Palabra a adivinar: " + codificacionPalabraElegida(palabraElegida));

            System.out.println("---------------------------------------------------------");

            gano=ganoOperdio(palabraElegida,letrasCorrectas);

            if(gano) {

                System.out.println("(BIEN! "+nombreJugador+" ha adivinado la palabra!! Ha salvado el dia!!)");
                System.out.println("Me han logrado vencer...pero perder una batalla no significa que he perdido la guerra. ME VENGARE!!");
                //grabarGanador(Thread.currentThread().getName(),palabraElegida);//antes la funcion estaba aca
                nombreGanador=nombreJugador;

            }

                jugando = Boolean.FALSE;
                notifyAll();



        }

        return gano;
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

    public String getNombreGanador() {
        return nombreGanador;
    }

    public String getPalabraElegida() {
        return palabraElegida;
    }

}
