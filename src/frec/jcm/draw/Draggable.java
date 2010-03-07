
package frec.jcm.draw;

import java.awt.event.MouseEvent;

/**
 * This interface is from edu.hws.jcm.draw package without any modification.
 * An interface that can be implemented by an object that can be dragged
 * with the mouse.
 */
public interface Draggable {

   /**
    *  Tell the object that a drag operation might be beginning.
    *  The Draggable object can decide whether it really wants
    *  to be dragged, based on the MouseEvent.  It should return
    *  true to indicate that a drag should really be started, and
    *  false if it wants to ignore the MouseEvent.
    */
   public boolean startDrag(MouseEvent evt);
   
   /**
    *  Continue a drag that was started in startDrag().  Presumably
    *  the event is a mouseDragged event.
    */
   public void continueDrag(MouseEvent evt);
   
   /**
    *  Finish a draw that was started in startDrag().  Presumably
    *  the event is a mouseReleased event.
    */
   public void finishDrag(MouseEvent evt);

}
