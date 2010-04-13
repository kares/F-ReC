
package frec.jcm.draw;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.LinkedList;

// This class has beean added for F-ReC.

public class DrawingCanvas extends AbstractCanvas {

   //private CoordinateRect coords;
   private DrawGraph drawGraph;
           
   private transient boolean dragg = false, press = false;
   private transient int lastX, lastY;

   private List lineBuffer;

   /**
    * Create a DrawCanvas with a white background containing no CoordinateRects.
    */   
   public DrawingCanvas() {
      setBackground(Color.white);
      enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
      setHandleMouseZooms(false);
   }
   
   /**
    * Create a DrawCanvas with a white background and containing the
    * specified CoordinateRect.  The CoordinateRect fills the entire canvas.
    *
    * @param c The CoordinateRect that will fill the canvas.  If c is null, no CoordinateRect is added to the canvas.
    */   
   public DrawingCanvas(CoordinateRect c) {
      this();
      if (c != null)
         addCoordinateRect(c);
   }
   
   public void add(DrawGraph drawGraph) {
       super.add(drawGraph);
       this.drawGraph = drawGraph;
       lineBuffer = new LinkedList();
       if (drawGraph != null) {
           drawGraph.setCoords(getCoordinateRect());
       }
   }

   private void setGraphics(Graphics g) {
       g.setColor(Color.MAGENTA.darker());
       g.setFont(g.getFont().deriveFont(Font.PLAIN, 10));
   }
    
   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseEvent(MouseEvent evt) {
      if ( drawGraph == null ) {
          throw new IllegalStateException("no draw graph added !");
      }
      // If an error message is displayed, get rid of it and ignore the mouse click.
      if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            if (press) {
                int x = evt.getX();
                int y = evt.getY();
                Graphics graphics = getGraphics();
                graphics.drawLine(lastX, lastY, x, y);
                drawGraph.addLine(lastX, lastY, x, y);
                lastX = x;
                lastY = y;
                press = false;
            }
            dragg = false;
            lastX = evt.getX();
            lastY = evt.getY();
            press = true;
            if (errorMessage != null) {
                if (errorSource != null) errorSource.errorCleared();
                errorSource = null;
                errorMessage = null;
                repaint();
            }
            //evt.consume();
      }
      else if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
         if (dragg) {
             drawGraph.addLines(lineBuffer);
             lineBuffer.clear();
             press = true;
         }
         //evt.consume();
      }
      super.processMouseEvent(evt);
   }
   
   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseMotionEvent(MouseEvent evt) {
      if ( drawGraph == null ) {
          throw new IllegalStateException("no draw graph added !");
      }
      if (evt.getID() == MouseEvent.MOUSE_DRAGGED) {
          if (press) {
              dragg = true;
              press = false;
          }
          int x = evt.getX();
          int y = evt.getY();
          Graphics graphics = getGraphics();
          graphics.drawLine(lastX, lastY, x, y);
          lineBuffer.add(DrawLine.getArcInstance(lastX, lastY, x, y));
          lastX = x;
          lastY = y;
          //evt.consume();
      }
      super.processMouseMotionEvent(evt);
   }

   /**
    * This methods sets the canvas's painting state to
    * the initial state, meaning that a new painting may
    * be started.
    */
   public void resetDrawing() {
       press = false;
       dragg = false;
       if ( drawGraph != null ) drawGraph.deleteLines();
   }

} // end class DrawingCanvas

