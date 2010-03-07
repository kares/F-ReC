
package frec.jcm.draw;

import java.awt.Graphics;

/**
 * This interface is from edu.hws.jcm.draw package without any modification.
 * An object that implements this interface can draw itself, using information
 * from a CoordinateRect (in which it presumably appears).  This interface is
 * meant to be used with the method drawTemp() in AbstractCanvas.
 */
public interface DrawTemp extends java.io.Serializable {

   /**
    * Draw this item in the specified graphics context, possibly using information
    * from the specified CoordinateRect.  Note that the drawTemp() method
    * in class AbstractCanvas creates a new graphics context every time it
    * is called, just for drawing this item.
    */
   public void draw(Graphics g, CoordinateRect coords);

}


