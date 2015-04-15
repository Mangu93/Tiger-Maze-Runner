package proyecto;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.sf.nwn.loader.NWNLoader;

/**
 *
 * @author Adrian Portillo
 * This is the main class. The teacher gave us some inspiration, methods and classes. If you don't understand anything, go and ask me at Github.
 */
public class Navegador_Tema_3 extends JFrame implements Runnable {

    SimpleUniverse universo;
    Figura personaje;
    ArrayList<Figura> listaObjetos = new ArrayList<Figura>();
    ArrayList<Muro> listaMuros = new ArrayList<Muro>();
    float dt;
    TransformGroup TGcamara;
    public PickTool explorador;
    BranchGroup objMuros, escena;
    Float distancia;
    Muro jaula1Ladocorto1;
    Muro jaula2Ladocorto2;
    BranchGroup objRoot;
    Appearance aparienciaM;
    BranchGroup totem1;
    BranchGroup totem2;
    boolean totem1_eliminado;
    boolean totem2_eliminado;
    JuegoCanvas zonaDibujo;
    public boolean boss_is_dead = false;
    public ArrayList<String> creatures_dead = new ArrayList<String>();

    public Navegador_Tema_3() {

        /*Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());*/
        zonaDibujo = new JuegoCanvas(SimpleUniverse.getPreferredConfiguration(), this);
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        universo = new SimpleUniverse(zonaDibujo);
        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.getViewer().getView().setBackClipDistance(30);
        getContentPane().add(zonaDibujo);
        escena = crearEscena();
        escena.compile();
        universo.addBranchGraph(escena);

        this.distancia = 5f;
    }

    BranchGroup crearEscena() {

        objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        BranchGroup objsuelo = new BranchGroup();

        objMuros = new BranchGroup();
        objMuros.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objMuros.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objMuros.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        totem1 = new BranchGroup();
        totem1.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        totem1.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        totem1.setCapability(BranchGroup.ALLOW_DETACH);

        totem2 = new BranchGroup();
        totem2.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        totem2.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        totem2.setCapability(BranchGroup.ALLOW_DETACH);

        //Iluminacion
        AmbientLight LuzAmbiente = new AmbientLight(new Color3f(0.2f, 0.5f, 0.8f));
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(0.8f, 0.8f, 0.8f), new Vector3f(1f, -1f, -0f));
        BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        LuzAmbiente.setInfluencingBounds(limites);
        LuzDireccional.setInfluencingBounds(limites);
        LuzAmbiente.setInfluencingBounds(limites);
        objRoot.addChild(LuzAmbiente);
        objRoot.addChild(LuzDireccional);

        //Cielo
        //Como decía en Firefly, "You can't take the sky from me"
        TextureLoader bgTexture = new TextureLoader("resources/cielo.jpg", this);
        Background bg = new Background(bgTexture.getImage());
        bg.setImageScaleMode(Background.SCALE_FIT_ALL);
        bg.setApplicationBounds(limites);
        BranchGroup backGeoBranch = new BranchGroup();
        bg.setGeometry(backGeoBranch);
        objRoot.addChild(bg);

        //Apariencia del suelo
        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader("resources/cesped.jpg", this).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Apariencia muro
        aparienciaM = new Appearance();
        Texture texM = new TextureLoader("resources/metal.jpg", this).getTexture();
        aparienciaM.setTexture(texM);
        TextureAttributes textAttrM = new TextureAttributes();
        textAttrM.setTextureMode(TextureAttributes.MODULATE);
        aparienciaM.setTextureAttributes(textAttrM);

        //Muros del escenario
        Muro detrasPersonaje = new Muro(76.8f, 0, 0.5f, 76.8f, 0.5f, 4.5f, aparienciaM);
        Muro EnfrentePersonaje = new Muro(-76.8f, 0, 0.5f, 76.8f, 0.5f, 4.5f, aparienciaM);
        Muro lateral1 = new Muro(0.5f, 0, 42f, 0.5f, 76.8f, 4.5f, aparienciaM);
        Muro lateral2 = new Muro(0.5f, 0, -42f, 0.5f, 76.8f, 4.5f, aparienciaM);
        listaMuros.add(detrasPersonaje);
        listaMuros.add(EnfrentePersonaje);
        listaMuros.add(lateral1);
        listaMuros.add(lateral2);
        //Jaula 1
        Muro jaula1Ladolargo1 = new Muro(-40.549606f, 0, -22.881102f, 12f, 0.5f, 4.5f, aparienciaM);
        Muro jaula1Ladolargo2 = new Muro(-21f, 0, -22.881102f, 12f, 0.5f, 4.5f, aparienciaM);
        jaula1Ladocorto1 = new Muro(-31f, 0, -11f, 0.5f, 10, 4.5f, aparienciaM);
        Muro jaula1Ladocorto2 = new Muro(-31f, 0, -35f, 0.5f, 10, 4.5f, aparienciaM);
        listaMuros.add(jaula1Ladolargo1);
        listaMuros.add(jaula1Ladolargo2);
        listaMuros.add(jaula1Ladocorto1);
        listaMuros.add(jaula1Ladocorto2);

        //Jaula 2
        Muro jaula2Ladolargo1 = new Muro(-40.549606f, 0, 22.881102f, 12, 0.5f, 10.5f, aparienciaM);
        Muro jaula2Ladolargo2 = new Muro(-21f, 0, 22.881102f, 12f, 0.5f, 10.5f, aparienciaM);
        Muro jaula2Ladocorto1 = new Muro(-31f, 0, 11f, 0.5f, 10, 10.5f, aparienciaM);
        jaula2Ladocorto2 = new Muro(-31f, 0, 35f, 0.5f, 10, 10.5f, aparienciaM);
        listaMuros.add(jaula2Ladolargo1);
        listaMuros.add(jaula2Ladolargo2);
        listaMuros.add(jaula2Ladocorto1);
        listaMuros.add(jaula2Ladocorto2);

        /**
         * Experimental: Jaula 3 dummy
         */
        Muro jaula3Ladolargo1 = new Muro(14.6f, 0, 8.8f, 12, 0.5f, 4.5f, aparienciaM);
        Muro jaula3Ladolargo2 = new Muro(13.25f, 0, 27.6f, 12, 0.5f, 4.5f, aparienciaM);
        Muro jaula3Ladocorto1 = new Muro(27f, 0, 27f, 0.5f, 30, 4.5f, aparienciaM);
        Muro jaula3Ladocorto2 = new Muro(27f, 0, 11f, 0.5f, 30, 4.5f, aparienciaM);
        listaMuros.add(jaula3Ladolargo1);
        listaMuros.add(jaula3Ladolargo2);
        listaMuros.add(jaula3Ladocorto1);
        listaMuros.add(jaula3Ladocorto2);

        Muro jaula4Ladolargo1 = new Muro(-5.4f, 0, -19.8f, 22, 0.5f, 4.5f, aparienciaM);
        Muro jaula4Ladolargo2 = new Muro(-6.5f, 0, -27.6f, 22, 0.5f, 4.5f, aparienciaM);
        Muro jaula4Ladocorto1 = new Muro(12f, 0, -23f, 0.5f, 20, 4.5f, aparienciaM);
        Muro jaula4Ladocorto2 = new Muro(12f, 0, -11f, 0.5f, 20, 4.5f, aparienciaM);
        listaMuros.add(jaula4Ladolargo1);
        listaMuros.add(jaula4Ladolargo2);
        listaMuros.add(jaula4Ladocorto1);
        listaMuros.add(jaula4Ladocorto2);

        //Creacion y modificacion del suelo
        TransformGroup transPrincipal = new TransformGroup();
        Transform3D desplazamiento = new Transform3D();
        desplazamiento.set(new Vector3f(0f, 0f, 0f));
        transPrincipal.setTransform(desplazamiento);
        Box suelo = new Box(76.8f, 0f, 42f, Box.GENERATE_TEXTURE_COORDS, apariencia);
        transPrincipal.addChild(suelo);
        objsuelo.addChild(transPrincipal);

        //Personaje principal
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            //Scene escena = nwn2.load("objetosMDL/Dire_Cat.mdl");
            Scene escena = nwn2.load("objetosMDL/Jaguar.mdl");
            BranchGroup RamaMDL = escena.getSceneGroup();
            Capabilities.setCapabilities(RamaMDL);
            personaje = new Figura(1.2f, RamaMDL, objRoot, listaObjetos, escena, this, null, "Jaguar");
            personaje.inicializar(-65.56333f, 0.0f, -1.4365028f);
            personaje.mostrar();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (IncorrectFormatException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (ParsingErrorException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        }

        //Figuras secundarias
        //Textura Cubos
        Appearance aparienciaC = new Appearance();
        Texture texC = new TextureLoader("resources/piedra.jpg", this).getTexture();
        aparienciaC.setTexture(texC);
        TextureAttributes textAttrC = new TextureAttributes();
        textAttrC.setTextureMode(TextureAttributes.MODULATE);
        aparienciaC.setTextureAttributes(textAttrC);

        //Cubos
        Figura cubo1 = new Figura(2f, null, objRoot, listaObjetos, null, this, aparienciaC, "cubo1");
        Figura cubo2 = new Figura(2f, null, objRoot, listaObjetos, null, this, aparienciaC, "cubo2");
        Figura cubo3 = new Figura(2f, null, objRoot, listaObjetos, null, this, aparienciaC, "cubo3");
        Figura cubo4 = new Figura(2f, null, objRoot, listaObjetos, null, this, aparienciaC, "cubo4");

        //Puntos
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;
        try {
            scene = file.load("resources/venus.obj");
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        } catch (IncorrectFormatException ex) {
            System.err.println(ex);
            System.exit(1);
        } catch (ParsingErrorException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        TransformGroup posicionaT1 = new TransformGroup();
        Transform3D despTotem1 = new Transform3D();
        despTotem1.set(new Vector3f(56.00107f, 1.5f, -32.195637f));
        posicionaT1.setTransform(despTotem1);
        posicionaT1.addChild(scene.getSceneGroup());
        totem1.addChild(posicionaT1);
        objRoot.addChild(totem1);

        ObjectFile file2 = new ObjectFile(ObjectFile.RESIZE);
        Scene scene2 = null;
        try {
            scene2 = file2.load("resources/venus2.obj");
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (IncorrectFormatException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (ParsingErrorException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        TransformGroup posicionaT2 = new TransformGroup();
        Transform3D despTotem2 = new Transform3D();
        despTotem2.set(new Vector3f(-32, 2f, -25));
        posicionaT2.setTransform(despTotem2);
        posicionaT2.addChild(scene2.getSceneGroup());
        totem2.addChild(posicionaT2);
        objRoot.addChild(totem2);

        //Enemigos
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            Scene escena = nwn2.load("objetosMDL/DoomKnight.mdl");
            BranchGroup RamaMDL = escena.getSceneGroup();
            Capabilities.setCapabilities(RamaMDL);
            Figura knight = new Figura(1.5f, RamaMDL, objRoot, listaObjetos, escena, this, null, "Caballero1");
            knight.inicializar(-33, 0.0f, -22);
            knight.mostrar();
            System.out.println("ID caballero : " + knight.identificador);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (IncorrectFormatException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (ParsingErrorException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");
        }

        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            Scene escena = nwn2.load("objetosMDL/DoomKnight.mdl");
            BranchGroup RamaMDL = escena.getSceneGroup();
            Capabilities.setCapabilities(RamaMDL);
            Figura knight = new Figura(1.5f, RamaMDL, objRoot, listaObjetos, escena, this, null, "Caballero 2");
            knight.inicializar(-29, 0.0f, -22);
            knight.mostrar();
            System.out.println("ID caballero : " + knight.identificador);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (IncorrectFormatException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (ParsingErrorException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        }

        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            Scene escena = nwn2.load("objetosMDL/DoomKnight.mdl");
            BranchGroup RamaMDL = escena.getSceneGroup();
            Capabilities.setCapabilities(RamaMDL);
            Figura knight = new Figura(1.5f, RamaMDL, objRoot, listaObjetos, escena, this, null, "Caballero 3");
            knight.inicializar(48.700733f, 0.0f, -30.111284f);
            knight.mostrar();
            System.out.println("ID caballero : " + knight.identificador);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (IncorrectFormatException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (ParsingErrorException ex) {
            ex.printStackTrace();
            System.out.println("Error fichero MDL");
        }

        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            Scene escena = nwn2.load("objetosMDL/Umberhulk.mdl");
            BranchGroup RamaMDL = escena.getSceneGroup();
            Capabilities.setCapabilities(RamaMDL);
            Figura umber = new Figura(3f, RamaMDL, objRoot, listaObjetos, escena, this, null, "Jefe final");
            umber.inicializar(-30, 0.0f, 24);
            umber.mostrar();
            System.out.println("ID jefe final " + umber.identificador);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");

        } catch (IncorrectFormatException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");
        } catch (ParsingErrorException exc) {
            exc.printStackTrace();
            System.out.println("Error fichero MDL");
        }
        cubo1.inicializar(8.984214f, 2, -28.5689f);
        cubo2.inicializar(24.277466f, 2f, 36.625626f);
        cubo3.inicializar(0, -4f, 0f);
        cubo4.inicializar(0, -4f, 0f);
        cubo1.mostrar();
        cubo2.mostrar();
        cubo3.mostrar();
        cubo4.mostrar();

        //Eventos del pj
        DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(listaObjetos.get(0));
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        mostrar.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        objRoot.addChild(mueve);
        objRoot.addChild(mostrar);
        objRoot.addChild(objsuelo);

        //Añadimos los muros
        objMuros.addChild(lateral1.getMuro());
        objMuros.addChild(lateral2.getMuro());
        objMuros.addChild(EnfrentePersonaje.getMuro());
        objMuros.addChild(detrasPersonaje.getMuro());

        //Añadimos las jaulas
        objMuros.addChild(jaula1Ladolargo1.getMuro());
        objMuros.addChild(jaula1Ladolargo2.getMuro());
        objMuros.addChild(jaula2Ladolargo1.getMuro());
        objMuros.addChild(jaula2Ladolargo2.getMuro());
        objMuros.addChild(jaula1Ladocorto1.getMuro());
        objMuros.addChild(jaula1Ladocorto2.getMuro());
        objMuros.addChild(jaula2Ladocorto1.getMuro());
        objMuros.addChild(jaula2Ladocorto2.getMuro());
        //Experimental
        objMuros.addChild(jaula3Ladolargo1.getMuro());
        objMuros.addChild(jaula3Ladolargo2.getMuro());
        objMuros.addChild(jaula3Ladocorto1.getMuro());
        objMuros.addChild(jaula3Ladocorto2.getMuro());

        objMuros.addChild(jaula4Ladolargo1.getMuro());
        objMuros.addChild(jaula4Ladolargo2.getMuro());
        objMuros.addChild(jaula4Ladocorto1.getMuro());
        objMuros.addChild(jaula4Ladocorto2.getMuro());

        objRoot.addChild(objMuros);
        return objRoot;
    }

    void actualizar(float dt) throws InterruptedException {
        for (int i = 0; i < this.listaObjetos.size(); i++) {
            listaObjetos.get(i).actualizar(dt);
        }

    }

    void mostrar() {
        for (int i = 1; i < this.listaObjetos.size(); i++) {
            listaObjetos.get(i).mostrar();
        }
    }

    public void run() {
        float distanciaBotonx;
        Math.abs(56.222168f - personaje.posiciones[0]);
        float distanciaBotonz;
        Math.abs(-32.086052f - personaje.posiciones[2]);
        float distanciaBotonx2;
        float distanciaBotonz2;
        boolean puertaAbierta1 = false;
        boolean puertaAbierta2 = false;
        dt = 0.1f;
        while (true) {
            try {
                actualizar(dt);
            } catch (InterruptedException ex) {
                Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
            }
            mostrar();
            //se asume que PuntoPosicionActual es el Punto3d donde está el Personaje (o su cabeza)
            PickTool localizador = new PickTool(objMuros);        
            localizador.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
            Vector3d direccionAexplorar = new Vector3d(personaje.posiciones[0], 0, personaje.posiciones[2]);
            Point3d PuntoPosicionActual = new Point3d(personaje.posiciones[0] +2, personaje.posiciones[1], personaje.posiciones[2]);
            localizador.setShapeRay(PuntoPosicionActual, direccionAexplorar);
            PickResult objetoMasCercano = localizador.pickClosest();
            //PickResult[] listaObjetos = localizador.pickAllSorted();
            if (objetoMasCercano != null) {

                distancia = (float) objetoMasCercano.getClosestIntersection(PuntoPosicionActual).getDistance();
                if (distancia < 4) {
                    personaje.adelante = false;
                }
                distanciaBotonx = Math.abs(56.00107f - personaje.posiciones[0]);
                distanciaBotonz = Math.abs(-32.195637f - personaje.posiciones[2]);
                distanciaBotonx2 = Math.abs(-32f - personaje.posiciones[0]);
                distanciaBotonz2 = Math.abs(-25f - personaje.posiciones[2]);
                if (distanciaBotonx < 2 && distanciaBotonz < 2 && !puertaAbierta1) {
                    objMuros.removeChild(8);
                    puertaAbierta1 = true;
                    objRoot.removeChild(totem1);
                    totem1_eliminado = true;
                }
                if (distanciaBotonx2 < 2 && distanciaBotonz2 < 2 && !puertaAbierta2) {
                    objMuros.removeChild(9);
                    puertaAbierta2 = true;
                    objRoot.removeChild(totem2);
                    totem2_eliminado = true;
                }
            }
            try {
                Thread.sleep((int) dt * 1000);
            } catch (Exception e) {
            }
        }
    }

    void colocarCamara(SimpleUniverse universo, Point3d posiciónCamara, Point3d objetivoCamara) {
        Point3d posicionCamara = new Point3d(posiciónCamara.x + 0.001, posiciónCamara.y + 0.001d, posiciónCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Figura getPersonaje() {
        return personaje;
    }

    public void setPersonaje(Figura personaje) {
        this.personaje = personaje;
    }

    public static void main(String args[]) {
        Navegador_Tema_3 x = new Navegador_Tema_3();
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setSize(1024, 768);
        x.setTitle("Tiger Maze Runner");
        x.setVisible(true);
        x.colocarCamara(x.universo, new Point3d(x.personaje.posiciones[0], 5, x.personaje.posiciones[2]), new Point3d(0, 0, 0));
        x.run();
    }
}
