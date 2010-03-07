
package frec.jcm.draw;

import frec.jcm.core.*;

/**
 * This class is from edu.hws.jcm.draw package without any modification.
 * A Crosshair is a small cross, 15 pixels wide and high, that is drawn in
 * a CoordinateRect at a specified point.
 *     A Crosshair is a Computable object, so should be added to a Controller to be 
 * recomputed when the coordinates of the point change. 
 */

public class Crosshair extends DrawGeometric {

   /**
    * Create a cross that appears at the point with coordinates (x,y).
    */
   public Crosshair(Value x, Value y) {
      super(CROSS, x, y, 7, 7);
   }
   
   /**
    * Create a cross that appears on the graph of the function y=f(x)
    * at the point with coordinates (x,f(x)).  f should be a function
    * of one variable.
    */
   public Crosshair(Value x, Function f) {
      super(CROSS, x, new ValueMath(f,x), 7, 7);
   }
   
}

