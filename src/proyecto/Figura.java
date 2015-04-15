package proyecto;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.picking.PickTool;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;
import net.sf.nwn.loader.AnimationBehavior;

/**
 *
 * @author Adrian Portillo
 */
public class Figura {

    float dt, radio;
    float[] velocidades = new float[3];
    float[] posiciones = new float[3];
    public boolean adelante, atras, izquierda, derecha, golpeando, ocupado;
    public TransformGroup desplazamientoFigura = new TransformGroup();
    int identificador;
    ArrayList<Figura> listaObjetos;
    Scene escena;
    Navegador_Tema_3 x;
    Appearance apariencia;
    int vida;
    AnimationBehavior ab;
    Boolean muerto;
    BranchGroup conjunto;
    String nombre;

    Figura(Float radio, BranchGroup mdl, BranchGroup conjunto, ArrayList<Figura> _listaObjetos, Scene scene, Navegador_Tema_3 x, Appearance apariencia, String nombre) {
        this.conjunto = conjunto;
        this.vida = 100;
        this.radio = radio;
        this.apariencia = apariencia;
        this.escena = scene;
        this.x = x;
        this.nombre = nombre;
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        this.conjunto.setCapability(BranchGroup.ALLOW_DETACH);
        if (identificador == 0 || identificador == 9 || identificador == 10 || identificador == 11 || identificador == 12) {  //PJ + enemigos
            ab = new AnimationBehavior();
            ab = (AnimationBehavior) listaObjetos.get(identificador).escena.getNamedObjects().get("AnimationBehavior");
            //Enderezar personaje
            Transform3D rotacionCombinada = new Transform3D();
            rotacionCombinada.rotX(-Math.PI / 2d);
            Transform3D correcionTemp = new Transform3D();
            if (identificador == 9 || identificador == 10 || identificador == 11) {
                //Vida de knights
                this.vida = 60;
            }
            if (identificador == 12) {
                //Vida de bichos
                this.vida = 100;
            }
            if (identificador == 9 || identificador == 10) {
                correcionTemp.rotZ(-Math.PI);
            }
            if (identificador == 11) {
                correcionTemp.rotZ(Math.PI / 2);
            } else if (identificador == 0) {
                correcionTemp.rotZ(Math.PI);
            }
            rotacionCombinada.mul(correcionTemp);
            correcionTemp.rotY(0);
            rotacionCombinada.mul(correcionTemp);
            correcionTemp.setScale(radio);
            rotacionCombinada.mul(correcionTemp);
            TransformGroup rotadorDeFiguraMDL = new TransformGroup(rotacionCombinada);
            rotadorDeFiguraMDL.addChild(mdl);
            desplazamientoFigura.addChild(rotadorDeFiguraMDL);
            this.muerto = false;
            this.golpeando = false;
            this.ocupado = false;
        } else {
            //Figuras secundarias, como botones y cubos
            System.out.println("Identificador del cubo: " + identificador + " " + nombre);
            Box b = new Box(radio, radio, radio, Box.GENERATE_TEXTURE_COORDS, apariencia);
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
            listaObjetos = _listaObjetos;
            listaObjetos.add(this);
            desplazamientoFigura.addChild(b);
            for (int i = 0; i < 6; i++) {
                PickTool.setCapabilities(b.getShape(i), PickTool.INTERSECT_FULL);
                b.getShape(i).setPickable(true);
            }
        }

        conjunto.addChild(desplazamientoFigura);
    }

    void inicializar(float x, float y, float z) {
        posiciones[0] = x;
        posiciones[1] = y;
        posiciones[2] = z;
    }

    public void actualizar(float _dt) throws InterruptedException {
        dt = _dt;
        Transform3D datosDesplazamientoActual = new Transform3D();
        desplazamientoFigura.getTransform(datosDesplazamientoActual);
        Vector3f posicionActual = new Vector3f(0, 0, 0);
        datosDesplazamientoActual.get(posicionActual);
        posiciones[0] = posicionActual.x;
        posiciones[1] = posicionActual.y;
        posiciones[2] = posicionActual.z;
        if (listaObjetos.get(0).vida == 0 && !listaObjetos.get(0).muerto) {
            if (listaObjetos.get(0).colision(listaObjetos.get(12))) {

                Espera espera_muerte = new Espera(listaObjetos.get(0), 1000, true);
                espera_muerte.start();
            } else {
                Espera espera_muerte = new Espera(listaObjetos.get(0), 1000, true);
                espera_muerte.start();
            }
            listaObjetos.get(0).muerto = true;
        }
        //actualizando el jaguar
        if (this.identificador < 1) {
            for (int p = 0; p < 3; p++) {
                posiciones[p] = posiciones[p] + dt * velocidades[p];
            }
            if (listaObjetos.get(0).colision(listaObjetos.get(1))) {
                listaObjetos.get(0).adelante = false;

            }
            if (listaObjetos.get(0).colision(listaObjetos.get(3))) {
                listaObjetos.get(0).adelante = false;
            }
            if (listaObjetos.get(9).colision(listaObjetos.get(0))) {//Caballero 1 me ha encontrado
                ab = new AnimationBehavior();
                ab = (AnimationBehavior) listaObjetos.get(9).escena.getNamedObjects().get("AnimationBehavior");
                if (listaObjetos.get(9).vida == 0 && !listaObjetos.get(9).muerto) {
                    listaObjetos.get(9).muerto = true;
                    ab.playAnimation("a_ba_med_weap:kdfntdie", false);
                    Espera espera = new Espera(listaObjetos.get(9), 1000, listaObjetos.get(9).muerto);
                    espera.start();
                }
                if (!listaObjetos.get(9).golpeando && !listaObjetos.get(9).muerto) {
                    listaObjetos.get(9).golpeando = true;
                    ab.playAnimation("a_ba:2hslashl", false);
                    Espera espera = new Espera(listaObjetos.get(9), 1000, listaObjetos.get(9).muerto);
                    espera.start();
                    listaObjetos.get(0).vida -= 10;
                }
            }
            if (listaObjetos.get(10).colision(listaObjetos.get(0))) {  //Caballero 2 me ha encontrado
                ab = new AnimationBehavior();
                ab = (AnimationBehavior) listaObjetos.get(10).escena.getNamedObjects().get("AnimationBehavior");
                if (listaObjetos.get(10).vida == 0 && !listaObjetos.get(10).muerto) {
                    listaObjetos.get(10).muerto = true;
                    ab.playAnimation("a_ba_med_weap:kdfntdie", false);
                    Espera espera = new Espera(listaObjetos.get(10), 1000, listaObjetos.get(10).muerto);
                    espera.start();
                }
                if (!listaObjetos.get(10).golpeando && !listaObjetos.get(10).muerto) {
                    listaObjetos.get(10).golpeando = true;
                    ab.playAnimation("a_ba:2hslashl", false);
                    Espera espera = new Espera(listaObjetos.get(10), 1000, listaObjetos.get(10).muerto);
                    espera.start();
                    listaObjetos.get(0).vida -= 10;
                }
            }
            if (listaObjetos.get(11).colision(listaObjetos.get(0))) {  //Caballero 3 me ha encontrado
                ab = new AnimationBehavior();
                ab = (AnimationBehavior) listaObjetos.get(11).escena.getNamedObjects().get("AnimationBehavior");
                if (listaObjetos.get(11).vida == 0 && !listaObjetos.get(11).muerto) {
                    listaObjetos.get(11).muerto = true;
                    ab.playAnimation("a_ba_med_weap:kdfntdie", false);
                    Espera espera = new Espera(listaObjetos.get(11), 1000, listaObjetos.get(11).muerto);
                    espera.start();
                }
                if (!listaObjetos.get(11).golpeando && !listaObjetos.get(11).muerto) {
                    listaObjetos.get(11).golpeando = true;
                    ab.playAnimation("a_ba:2hslashl", false);
                    Espera espera = new Espera(listaObjetos.get(11), 1000, listaObjetos.get(11).muerto);
                    espera.start();
                    listaObjetos.get(0).vida -= 10;
                }
            }
            if (listaObjetos.get(12).colision(listaObjetos.get(0))) {  //Final boss
                ab = new AnimationBehavior();
                ab = (AnimationBehavior) listaObjetos.get(12).escena.getNamedObjects().get("AnimationBehavior");
                if (listaObjetos.get(12).vida < 0 && !listaObjetos.get(12).muerto) {
                    x.boss_is_dead = true;
                    System.out.println("Has derrotado al jefe final");
                    listaObjetos.get(12).muerto = true;
                    ab.playAnimation("umberhulk:ctaunt", false);
                    Espera espera = new Espera(listaObjetos.get(12), 1000, listaObjetos.get(12).muerto);
                    espera.start();
                }
                if (!listaObjetos.get(12).golpeando && !listaObjetos.get(12).muerto) {
                    listaObjetos.get(12).golpeando = true;
                    ab.playAnimation("umberhulk:ca1slashr", false);
                    Espera espera = new Espera(listaObjetos.get(12), 1000, listaObjetos.get(12).muerto);
                    espera.start();
                    listaObjetos.get(0).vida -= 10;
                }
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean colision(Figura F2) {
        Vector3f x = new Vector3f(posiciones[0] - F2.posiciones[0], posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
        double distanciaActual = x.length();
        return distanciaActual < (this.radio + F2.radio + 2);
    }

    void mostrar() {
        Transform3D inip = new Transform3D();
        inip.set(new Vector3f(posiciones[0], posiciones[1], posiciones[2]));
        desplazamientoFigura.setTransform(inip);
    }
}
