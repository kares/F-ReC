
package frec.jcm.awt;

/**
 * This interface is from edu.hws.jcm.awt package without any modification.
 * A Computable is an object that performs some sort of computation or
 * action when its compute() method is called.   The compute() method is 
 * meant to be called (usually) by a Controller.  See the Controller class for more 
 * information.
 */
public interface Computable {
   /**
    * Perform the computation or action associated with this
    * Computable object.
    */
   public void compute();
}
