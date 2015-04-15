/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Canvas3D;

/**
 *
 * @author Adrian Portillo
 */
public class JuegoCanvas extends Canvas3D {

    private final Navegador_Tema_3 juego;

    public JuegoCanvas(GraphicsConfiguration gc, Navegador_Tema_3 juego) {
        super(gc);
        this.juego = juego;

    }

    public void postRender() {
        Figura jugador = juego.getPersonaje();
        Graphics2D gd = this.getGraphics2D();
        gd.setFont(new Font("Arial", Font.PLAIN, 40));
        gd.setColor(Color.RED);
        if (jugador.vida >= 0) {
            gd.drawString("[ Vida: " + jugador.vida + " ]", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL);
        }
        gd.drawString("X: " + jugador.posiciones[0], POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 2);
        gd.drawString("Y: " + jugador.posiciones[1], POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 3);
        gd.drawString("Z: " + jugador.posiciones[2], POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 4);
        /*
        if (juego.totem1_eliminado) {
        gd.drawString("Has cogido la primera venus", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 5);
        }
        if (juego.totem2_eliminado) {
        gd.drawString("Ya tienes las dos venus, ve a por el jefe", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 6);
        }*/
        if (!juego.totem1_eliminado) gd.drawString("Venus recogidas : 0", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL *5);
        if (juego.totem1_eliminado && (!juego.totem2_eliminado)) gd.drawString("Venus recogidas: 1", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL *5);
        if (juego.totem1_eliminado && juego.totem2_eliminado) gd.drawString("Venus recogidas: 2", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL *5);
        gd.drawString("Caballeros eliminados : " + juego.creatures_dead.size(), POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL *6);
        /*for(String a : juego.creatures_dead) {
        gd.drawString(a, POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * (juego.creatures_dead.indexOf(a)+ 7) );
        }*/
        if (juego.personaje.muerto) {
            gd.drawString("Game Over ", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 7);
        }
        if (juego.boss_is_dead) {
            gd.drawString("Victoria, has derrotado al jefe final ", POSICION_VIDA_VERTICAL, POSICION_VIDA_HORIZONTAL * 7);

        }
        this.getGraphics2D().flush(false);
    }
    public static final int POSICION_VIDA_VERTICAL = 8;
    public static final int POSICION_VIDA_HORIZONTAL = 44;

}
