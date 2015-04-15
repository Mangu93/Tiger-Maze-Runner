package proyecto;

import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 *
 * @author Adrian Portillo
 */
public class Muro {

    BranchGroup muro;
    TransformGroup transPrincipal;
    Transform3D desplazamiento;
    Box pared;
    Appearance apariencia;

    /**
     *
     * @param desp1
     * @param desp2
     * @param desp3
     * @param ancho (Z)
     * @param largo (X)
     * @param alto (Y)
     * @param apariencia
     */
    public Muro(float desp1, float desp2, float desp3, float ancho, float largo, float alto, Appearance apariencia) {
        this.muro = new BranchGroup();
        muro.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        muro.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        this.apariencia = apariencia;
        this.transPrincipal = new TransformGroup();
        transPrincipal.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        this.desplazamiento = new Transform3D();
        this.desplazamiento.set(new Vector3f(desp1, desp2, desp3));
        this.transPrincipal.setTransform(desplazamiento);
        this.pared = new Box(largo, alto, ancho, Box.GENERATE_TEXTURE_COORDS, this.apariencia);
        pared.setPickable(true);
        transPrincipal.addChild(pared);

        this.muro.addChild(transPrincipal);
    }

    public BranchGroup getMuro() {
        return muro;
    }

}
