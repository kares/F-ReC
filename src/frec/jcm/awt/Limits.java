
package frec.jcm.awt;

/**
 * This interface is from edu.hws.jcm.awt package without any modification.
 * The Limits interface is implemented by edu.hws.jcm.data.CoordinateRect
 * and by other objects that can be "Tied" to a CoordinateRect, such as
 * LimitControlPanel. This will be used to synchronize the (x,y)
 * limits on the CoordinateRect with limit values obtained elsewhere.
 * See the Tie class for more information.
 *
 * @author David Eck
 */
public interface Limits extends java.io.Serializable {

   /**
    * Return a 4-element array containing xmin, xmax, ymin, and ymax. 
    */
   public double[] getLimits();
   
   /**
    * Set the current limits.
    *
    * @param A 4-element array containing the new xmin, xmax, ymin, and ymax.
    */
   public void setLimits(double[] limits);

}
