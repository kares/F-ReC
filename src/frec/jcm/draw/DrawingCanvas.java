
package frec.jcm.draw;

import frec.jcm.awt.*;
import frec.jcm.draw.*;
import frec.jcm.core.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class DrawingCanvas extends AbstractCanvas 
{   
   private CoordinateRect coords;
   private double xmin, xmax, ymin, ymax;
   private DrawGraph drawGraph;
           
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
   
   private transient LinkedList lineBuffer;
   
   public void add(DrawGraph drawGraph)
   {
       super.add(drawGraph);
       this.drawGraph = drawGraph;
       lineBuffer = new LinkedList();
   }
   
   private void setGraphics(Graphics g)
   {
       g.setColor(Color.MAGENTA.darker());
       g.setFont(g.getFont().deriveFont(Font.PLAIN, 10));
   }
    
   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseEvent(MouseEvent evt) {
         // If an error message is displayed, get rid of it and
         // ignore the mouse click.
      if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
          if (press)
          {
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

          if (errorMessage != null) 
          {
             if (errorSource != null)
                errorSource.errorCleared();
             errorSource = null;
             errorMessage = null;
             repaint();
             evt.consume();
             return;
         }
         evt.consume();
         return;
      }
      else 
      if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
         if (dragg)
         {
             drawGraph.addLines(lineBuffer);
             lineBuffer.clear();
             press = true;
         }
         evt.consume();
         return;
      }
      else super.processMouseEvent(evt);
   }
   
   /**
    * This methods sets the canvas's painting state to
    * the initial state, meaning that a new painting may
    * be started.
    */   
   public void setNewDrawing()
   {
       press = false;
       dragg = false;
   }
   
   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseMotionEvent(MouseEvent evt) {
      if (evt.getID() == MouseEvent.MOUSE_DRAGGED)
      {
          if (press)
          {
              dragg = true;
              press = false;
          }
          int x = evt.getX();
          int y = evt.getY();
          Graphics graphics = getGraphics();
          graphics.drawLine(lastX, lastY, x, y);
          lineBuffer.add(
            DrawLine.getArcInstance(lastX, lastY, x, y));
          lastX = x;
          lastY = y;
          evt.consume();
          return;
      }
      else super.processMouseMotionEvent(evt);
   }
   
   private transient boolean dragg = false, press = false;
   private transient int lastX, lastY;
   
} // end class DrawingCanvas

