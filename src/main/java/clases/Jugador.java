package clases;

public class Jugador implements Runnable{



    private String nombre;
    private Juego juego;

    public Jugador(String nombre, Juego juego) {

        this.nombre=nombre;
        this.juego = juego;
    }

    @Override
    public void run() {

        juego.jugar();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
