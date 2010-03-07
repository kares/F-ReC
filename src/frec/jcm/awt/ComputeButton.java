
package frec.jcm.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * This class is from edu.hws.jcm.awt package without any modification.
 * A compute button is a button that can have an associated Controller.
 * When the user clicks the button, the compute() method of the 
 * Controller is called.  This class really just exists for convenience.
 */
public class ComputeButton extends Button {

   private Controller onUserAction;  // The Controller whose compute()
                                     // method is called when the user clicks
                                     // the button.

   /**   
    * Create a Compute button labeled "Compute!".
    */
   public ComputeButton() {
      this("Compute!");
   }
   
   /**
    * Create a Compute button displaying the given text.
    */
   public ComputeButton(String label) {
      super(label);
      setBackground(Color.lightGray);
      enableEvents(AWTEvent.ACTION_EVENT_MASK);
   }
   
   /**
    * Set the controller whose compute() method is called
    * when the user clicks this button.
    */
   public void setOnUserAction(Controller c) {
      onUserAction = c;
   }

   /**   
    * Return the controlller whose compute() method is
    * called when the user clicks this button.
    */
   public Controller getOnUserAction() {
      return onUserAction;
   }
   
   /**
    * This is called by the system when the user clicks the
    * button.  Not meant to be called directly.
    */
   public void processActionEvent(ActionEvent evt) {
      if (onUserAction != null)
         onUserAction.compute();
      super.processActionEvent(evt);
   }   

} // end class ComputeButton

