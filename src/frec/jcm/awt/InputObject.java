
package frec.jcm.awt;

/**
 * This interface is from edu.hws.jcm.awt package without any modification.
 * An InputObject represents some sort of value that can be changed
 * by, for example, user interaction with a GUI element.  The value can
 * actually change only when the checkInput() method is called.  Generally,
 * an InputObject is a GUI element with an associated MathObject such as
 * a Variable or Expression.  For example, a VariableInput is a text-input
 * box where the user can enter the value of a Variable.  However, the
 * input is only checked and the value of the variable can only change
 * when the VariableInput's checkInput() method is called.  The checkInput()
 * method is generally meant to be called by a Controller object.  The
 * checkInput() method should throw a JCMError if an error occurs.
 * See the Controller class for more information.
 *
 * @author David Eck
 */
public interface InputObject extends java.io.Serializable {

   /**
    * Check and possibly change the value associated with this InputObject.
    */
   public void checkInput();
   
   /**
    * This method was introduced to provide a common interface for setting
    * a Controller that is to be notified when there is a change in the
    * InputObject.  (This was introduced late in development, to be used
    * by edu.hws.jcm.awt.JCMPanel.gatherInputs().  In all the standard
    * classes that implement the InputObject interface, this method 
    * simply calls a setOnChange or setOnUserAction method.)
    */
   public void notifyControllerOnChange(Controller c);

} // end interface InputObject
