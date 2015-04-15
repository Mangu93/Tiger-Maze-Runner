package proyecto;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adrian Portillo
 */
class Espera extends Thread {

    int time;
    Figura figura;
    boolean muerto;

    public Espera(Figura f, int time, boolean muerto) {
        this.time = time;
        this.figura = f;
        this.muerto = muerto;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.time);
        } catch (InterruptedException ex) {
            Logger.getLogger(Espera.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.figura.golpeando = false;
        if (muerto) {
            System.out.println("El personaje " + figura.getNombre() + " ha muerto");
            figura.muerto = true;
            figura.ab.setEnable(false);
            if ("Jefe final".equals(figura.nombre)) {
                synchronized (this) {
                    try {
                        Thread.sleep(3000);
                        System.exit(0);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Espera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (figura.nombre.contains("Caballero")) {
                figura.inicializar(0, -100, 0);
            }
            if (figura.nombre.equals("Jaguar")) {
                synchronized (this) {
                    try {
                        Thread.sleep(3000);
                        System.exit(0);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Espera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
