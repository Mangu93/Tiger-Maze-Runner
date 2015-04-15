package proyecto;

import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 *
 * @author Adrian Portillo
 */
public class ComportamientoMostrar extends Behavior {

    Figura personaje;
    TransformGroup cam;
    TransformGroup TG_personaje;
    WakeupOnElapsedFrames framewake = new WakeupOnElapsedFrames(0);
    Navegador_Tema_3 juego;

    public ComportamientoMostrar(Navegador_Tema_3 juego) {
        personaje = juego.personaje;
        TG_personaje = personaje.desplazamientoFigura;
        cam = juego.universo.getViewingPlatform().getViewPlatformTransform();
        this.juego = juego;
    }

    /**
     * Con este metodo, que se llama en el proccessStimulus mueves la camara del
     * personaje una velocidad, en un angulo, con un T3D nuevo y el del PJ
     *
     * @param deltaVel
     * @param deltaAngulo
     * @param t3dNueva
     * @param t3dPersonaje
     */
    public void moverCamara(float deltaVel, float deltaAngulo, Transform3D t3dNueva, Transform3D t3dPersonaje) {
        t3dNueva.set(new Vector3d(0f, 3.5f, 8f));
        t3dPersonaje.mul(t3dNueva);
        this.juego.TGcamara.setTransform(t3dPersonaje);
        t3dNueva.rotY(Math.PI);
        t3dPersonaje.mul(t3dNueva);
        this.juego.TGcamara.setTransform(t3dPersonaje);
        t3dNueva.set(new Vector3d(0.0f, 0.8f, 25f));
        t3dPersonaje.mul(t3dNueva);
        this.juego.TGcamara.setTransform(t3dPersonaje);
    }

    @Override
    public void initialize() {
        wakeupOn(framewake);
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        if (personaje.derecha || personaje.izquierda || personaje.adelante || personaje.atras) {
            float deltaVel = 0;
            float deltaAngulo = 0;
            if (personaje.derecha) {
                deltaAngulo = -0.004f;
            }
            if (personaje.izquierda) {
                deltaAngulo = 0.004f;
            }
            if (personaje.adelante) {
                deltaVel = 0.1f;
            }
            if (personaje.atras) {
                deltaVel = -0.09f;
            }
            Transform3D t3dNueva = new Transform3D();
            t3dNueva.set(new Vector3d(0.0d, 0.0d, deltaVel));
            t3dNueva.setRotation(new AxisAngle4f(0, 1f, 0, deltaAngulo));
            Transform3D t3dPersonaje = new Transform3D();
            TG_personaje.getTransform(t3dPersonaje);
            t3dPersonaje.mul(t3dNueva);
            TG_personaje.setTransform(t3dPersonaje);
            moverCamara(deltaVel, deltaAngulo, t3dNueva, t3dPersonaje);
        }
        wakeupOn(framewake);
    }
}
