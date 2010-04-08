/*************************************************************************
*                                                                        *
*   1) This source code file, in unmodified form, and compiled classes   *
*      derived from it can be used and distributed without restriction,  *
*      including for commercial use.  (Attribution is not required       *
*      but is appreciated.)                                              *
*                                                                        *
*    2) Modified versions of this file can be made and distributed       *
*       provided:  the modified versions are put into a Java package     *
*       different from the original package, edu.hws;  modified          *
*       versions are distributed under the same terms as the original;   *
*       and the modifications are documented in comments.  (Modification *
*       here does not include simply making subclasses that belong to    *
*       a package other than edu.hws, which can be done without any      *
*       restriction.)                                                    *
*                                                                        *
*   David J. Eck                                                         *
*   Department of Mathematics and Computer Science                       *
*   Hobart and William Smith Colleges                                    *
*   Geneva, New York 14456,   USA                                        *
*   Email: eck@hws.edu          WWW: http://math.hws.edu/eck/            *
*                                                                        *
*************************************************************************/

package frec.jcm.draw;

import java.awt.*;
import java.awt.event.*;

// This class is from edu.hws.jcm.draw package with modifications for F-ReC.

/**
 * A DisplayCanvas is a drawing area that can contain one or more CoordinateRects
 * as the AbstractCanvas class. The class is used to display data (usualy functions).
 * Mouse zooming (dragging) and mouse event handling support is added to extend the 
 * functionality of this class.
 */
public class DisplayCanvas extends AbstractCanvas {

   private Draggable dragged;  // The draggable object, if any, that is being dragged.
    
   /**
    * Create a DisplayCanvas with a white background containing no CoordinateRects.
    */   
   public DisplayCanvas() {
      setBackground(Color.white);
      enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
      setHandleMouseZooms(true);
   }
   
   /**
    * Create a DisplayCanvas with a white background and containing the
    * specified CoordinateRect.  The CoordinateRect fills the entire canvas.
    *
    * @param c The CoordinateRect that will fill the canvas.  If c is null, no CoordinateRect is added to the canvas.
    */   
   public DisplayCanvas(CoordinateRect c) {
      this();
      if (c != null)
         addCoordinateRect(c);
   }

   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseEvent(MouseEvent evt) {
         // If an error message is displayed, get rid of it and
         // ignore the mouse click.
      if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
         dragging = false;  // Shouldn't be possible, but some old, buggy versions of Java made it so.
         dragged = null;
         if (errorMessage != null) {
            if (errorSource != null)
               errorSource.errorCleared();
            errorSource = null;
            errorMessage = null;
            repaint();
            evt.consume();
            return;
         }
         CoordinateRect c = findCoords(evt);
         if (c != null)
            dragged = c.checkDraggables(evt);
         if (dragged != null)
            return;
         if (getHandleMouseZooms() && !(evt.getClickCount() > 1 || evt.isAltDown() || evt.isMetaDown() || evt.isControlDown())) {
            super.processMouseEvent(evt);
            if (!evt.isConsumed())
               doMouseZoom_pressed(evt);
            return;
         }
      }
      else if (evt.getID() == MouseEvent.MOUSE_RELEASED && getHandleMouseZooms() && dragged != null) {
         dragged.finishDrag(evt);
         dragged = null;
         return;
      }
      else if (evt.getID() == MouseEvent.MOUSE_RELEASED && getHandleMouseZooms() && dragging) {
         doMouseZoom_released(evt);
         return;
      }
      super.processMouseEvent(evt);
   }
   
   /**
    * This has been overridden to handle the mouse zoom feature.
    * Not meant to be called directly.
    */
   public void processMouseMotionEvent(MouseEvent evt) {
      if (dragged != null && evt.getID() == MouseEvent.MOUSE_DRAGGED)
         dragged.continueDrag(evt);
      else if (dragging && evt.getID() == MouseEvent.MOUSE_DRAGGED)
         doMouseZoom_moved(evt);
      else
         super.processMouseMotionEvent(evt);
   }
   
   // ------------ Handle Mouse Zooming -----------------------------------------
   
   private transient boolean dragging, draggingZoomWindow;
   private transient CRData draggingInRect;
   private transient int dragXmax, dragXmin, dragYmax, dragYmin;
   private transient int lastX, lastY, startX, startY;
   
   private CoordinateRect findCoords(MouseEvent evt) {
          // Find coord rect containing the mouse.
      int xMouse = evt.getX();
      int yMouse = evt.getY();
      int size = (coordinateRects == null)? -1 : coordinateRects.size();
      int width = getSize().width;
      int height = getSize().height;
      for (int i = size-1; i >= 0; i--) {
         CRData c = (CRData)coordinateRects.elementAt(i);
         double xmin = (int)(c.xmin*width);
         double ymin = (int)(c.ymin*height);
         double xmax = (int)(c.xmax*width) - 1;
         double ymax = (int)(c.ymax*height) - 1;
         if (xMouse >= xmin && xMouse <= xmax && yMouse >= ymin && yMouse <= ymax)
            return c.coords;
      }
      return null;
   }
   
   private synchronized void doMouseZoom_pressed(MouseEvent evt) {
         // Called from processMouseEvent, above.
         // Ignore multiple clicks and clicks with other than button 1
         // and clicks modified with any key except shift.
      if (evt.getClickCount() > 1 || evt.isAltDown() || evt.isMetaDown() || evt.isControlDown())
         return;
      int xMouse = evt.getX();
      int yMouse = evt.getY();
      int size = (coordinateRects == null)? -1 : coordinateRects.size();
      int width = getSize().width;
      int height = getSize().height;
      for (int i = size-1; i >= 0; i--) {
         CRData c = (CRData)coordinateRects.elementAt(i);
         dragXmin = (int)(c.xmin*width);
         dragYmin = (int)(c.ymin*height);
         dragXmax = (int)(c.xmax*width) - 1;
         dragYmax = (int)(c.ymax*height) - 1;
         if (xMouse >= dragXmin && xMouse <= dragXmax && yMouse >= dragYmin && yMouse <= dragYmax) {
            dragging = true;
            draggingZoomWindow = false;
            draggingInRect = c;
            startX = xMouse;
            startY = yMouse;
            lastX = xMouse;
            lastY = yMouse;
            break;
         } 
      }
   }
   
   private synchronized void doMouseZoom_released(MouseEvent evt) {
      Graphics g = getGraphics();
      putDragRect(g);
      g.dispose();
      CoordinateRect c = draggingInRect.coords;
      if ( (Math.abs(lastX - startX) < 4 && Math.abs(lastY - startY) < 4)
                || Math.abs(startX - lastX) < 2 || Math.abs(startY - lastY) < 2 ) {
         if (draggingZoomWindow)
             return;
         if (evt.isShiftDown())
             c.zoomOutFromPixel(startX,startY);
         else
             c.zoomInOnPixel(startX,startY);
      }
      else {
         c.setLimits( c.pixelToX(startX), c.pixelToX(lastX), c.pixelToY(startY), c.pixelToY(lastY) );
      }
      dragging = false;
   }
   
   private synchronized void doMouseZoom_moved(MouseEvent evt) {
      Graphics g = getGraphics();
      putDragRect(g);
      lastX = evt.getX();
      lastY = evt.getY();
      putDragRect(g);
      g.dispose();
   }
   
   private void putDragRect(Graphics g) {  // (Assume dragging = true)
      if (lastX < dragXmin)
         lastX = dragXmin;
      if (lastX > dragXmax)
         lastX = dragXmax;
      if (lastY < dragYmin)
         lastY = dragYmin;
      if (lastY > dragYmax)
         lastY = dragYmax;
      if ( (Math.abs(startX - lastX) < 4 && Math.abs(startY - lastY) < 4)
                || Math.abs(startX - lastX) < 2 || Math.abs(startY - lastY) < 2 )
         return;  
      draggingZoomWindow = true;
       Color bc = draggingInRect.background;
       if (bc == null)
          bc = getBackground();
       g.setXORMode(bc);
       if (bc.getRed() <= 100 && bc.getGreen() <= 100 && bc.getBlue() <= 150)
          g.setColor(Color.white);
       else
          g.setColor(Color.black);
       int x, y, w, h;
       if (startX < lastX) {
          x = startX;
          w = lastX - startX;
       }
       else {
          x = lastX;
          w = startX - lastX;
       }
       if (startY < lastY) {
          y = startY;
          h = lastY - startY;
       }
       else {
          y = lastY;
          h = startY - lastY;
       }
       g.drawRect(x,y,w,h);
       g.setPaintMode();
   }
   
   /**
    * Draw the contents of the DisplayCanvas.
    * Not usually called directly.
    */
   public void paint(Graphics g) {
       super.paint(g);
       if (dragging)
           putDragRect(g);
   }   

} // end class DisplayCanvas

