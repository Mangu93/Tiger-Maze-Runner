package proyecto;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import net.sf.nwn.loader.AnimationBehavior;

/**
 *
 * @author Adrian Portillo
 */
public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {

    Figura personaje;
    TransformGroup TG_personaje;
    WakeupOnAWTEvent presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition keepUpCondition = null;
    WakeupCriterion[] continueArray = new WakeupCriterion[2];
    int TeclaPresionada;
    boolean andando;

    public DeteccionControlPersonaje(Figura _personaje) {
        TG_personaje = _personaje.desplazamientoFigura;
        personaje = _personaje;
        continueArray[0] = liberada;
        continueArray[1] = presionada;
        keepUpCondition = new WakeupOr(continueArray);
        this.TeclaPresionada = 0;
        this.andando = false;
    }

    public void initialize() {
        wakeupOn(keepUpCondition);
    }

    public void processStimulus(Enumeration criteria) {
        AnimationBehavior ab = (AnimationBehavior) personaje.escena.getNamedObjects().get("AnimationBehavior");
        while (criteria.hasMoreElements()) {
            WakeupCriterion ster = (WakeupCriterion) criteria.nextElement();
            if (ster instanceof WakeupOnAWTEvent) {
                AWTEvent[] events = ((WakeupOnAWTEvent) ster).getAWTEvent();
                for (int n = 0; n < events.length; n++) {
                    if (events[n] instanceof KeyEvent) {
                        KeyEvent ek = (KeyEvent) events[n];
                        if (ek.getID() == KeyEvent.KEY_PRESSED && TeclaPresionada != ek.getKeyCode() && !personaje.muerto) {
                            TeclaPresionada = ek.getKeyCode();

                            if (ek.getKeyCode() == 38) {
                                this.andando = true;
                                personaje.adelante = true;
                                ab.playAnimation("dire_cat:crun", false);
                                ab.playAnimation("dire_cat:crun", true);

                            }
                            if (ek.getKeyCode() == 37) {
                                personaje.izquierda = true;
                                if (!personaje.adelante && !personaje.atras) {
                                    ab.playAnimation("dire_cat:chturnl", true);
                                }
                            }
                            if (ek.getKeyCode() == 39) {
                                personaje.derecha = true;
                                if (!personaje.adelante && !personaje.atras) {
                                    ab.playAnimation("dire_cat:chturnr", true);
                                }
                            }
                            if (ek.getKeyCode() == 40) {
                                personaje.atras = true;
                                ab.playAnimation("dire_cat:cpause1", false);
                                ab.playAnimation("dire_cat:crun", true);

                            }
                            if (ek.getKeyChar() == 'a') {
                                if (!personaje.golpeando) {
                                    personaje.golpeando = true;
                                    ab.playAnimation("dire_cat:ca1stab", false);
                                    //Aquí le quito vida al enemigo
                                    if (personaje.colision(personaje.listaObjetos.get(9))) {
                                        if (personaje.listaObjetos.get(9).vida == 0) {
                                            personaje.listaObjetos.get(9).muerto = true;
                                        }
                                        personaje.listaObjetos.get(9).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(10))) {
                                        if (personaje.listaObjetos.get(10).vida == 0) {
                                            personaje.listaObjetos.get(10).muerto = true;
                                        }
                                        personaje.listaObjetos.get(10).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(11))) {
                                        personaje.listaObjetos.get(11).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(12))) {
                                        if (personaje.listaObjetos.get(12).vida == 0) {
                                            personaje.listaObjetos.get(12).muerto = true;
                                        }
                                        personaje.listaObjetos.get(12).vida -= 20;
                                    }
                                    Espera espera = new Espera(personaje, 500, personaje.muerto);
                                    espera.start();

                                }
                            }

                            if (ek.getKeyChar() == 's') {
                                if (!personaje.golpeando) {
                                    personaje.golpeando = true;
                                    ab.playAnimation("dire_cat:ca1slashr", false);
                                    //Aquí le quito vida al enemigo
                                    if (personaje.colision(personaje.listaObjetos.get(9))) {
                                        personaje.listaObjetos.get(9).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(10))) {
                                        personaje.listaObjetos.get(10).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(11))) {
                                        personaje.listaObjetos.get(11).vida -= 20;
                                    }
                                    if (personaje.colision(personaje.listaObjetos.get(12))) {
                                        personaje.listaObjetos.get(12).vida -= 40;
                                    }
                                    Espera espera = new Espera(personaje, 500, personaje.muerto);
                                    espera.start();
                                }
                            }
                        }
                        if (ek.getID() == KeyEvent.KEY_RELEASED) {
                            this.TeclaPresionada = 0;
                            if (ek.getKeyCode() == 38) {         //Adelante
                                personaje.adelante = false;
                                ab.playAnimation("dire_cat:crun", false);
                                ab.playAnimation("dire_cat:cpause1", true);
                                andando = false;
                            }
                            if (ek.getKeyCode() == 37) {         //Izquierda
                                personaje.izquierda = false;
                            }
                            if (ek.getKeyCode() == 39) {          //Derecha
                                personaje.derecha = false;

                            }
                            if (ek.getKeyCode() == 40) {    //Atras                           
                                personaje.atras = false;
                                ab.playAnimation("dire_cat:cpause1", true);
                                ab.playAnimation("dire_cat:crun", false);
                            }
                        }
                    }
                }
            }
        }
        wakeupOn(keepUpCondition);
    }
}
