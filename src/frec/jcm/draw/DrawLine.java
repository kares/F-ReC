
package frec.jcm.draw;

import java.awt.Graphics;

// This class has beean added for F-ReC.

/**
 * DrawLine represents a line segment of a graph.
 * These lines are internally used by the DrawedGraph class.
 */
public class DrawLine extends Drawable implements Comparable {
        
   /**
    * Coordinates of the first (start) point of
    * this line. These coordinates
    * correspond to a Canvas (usually PaintCanvas).
    */    
    double x1, y1;
    
   /**
    * Coordinates of the second (end) point of
    * this line. These coordinates
    * correspond to a Canvas (usually PaintCanvas).
    */        
    double x2, y2;
    
    private int drawx1 = 1, drawy1;
    private int drawx2 = 0, drawy2;
    private boolean isArc = false;
    
    private static CoordinateRect defaultCoords;
    
    public static void setDefaultCoords(CoordinateRect coords) {
        defaultCoords = coords;
    }
    
   /**
    * Constructs a new GraphLine object using the
    * included coordinates.
    */        
    public DrawLine(int x1, int y1, int x2, int y2) {
        if ( x1 <= x2 ) {
            if ( coords == null ) {
                if ( defaultCoords != null ) coords = defaultCoords;
                else throw new NullPointerException("no valid coords");
            }
            this.x1 = coords.pixelToX(x1);
            this.y1 = coords.pixelToY(y1);
            this.x2 = coords.pixelToX(x2);
            this.y2 = coords.pixelToY(y2);
            drawx1 = x1;
            drawx2 = x2;
            drawy1 = y1;
            drawy2 = y2;
        }
        else {
            if ( coords == null ) {
                if ( defaultCoords != null ) coords = defaultCoords;
                else throw new NullPointerException("no valid coords");
            }
            this.x1 = coords.pixelToX(x2);
            this.y1 = coords.pixelToY(y2);
            this.x2 = coords.pixelToX(x1);
            this.y2 = coords.pixelToY(y1);
            drawx1 = x2;
            drawx2 = x1;
            drawy1 = y2;
            drawy2 = y1;            
        } 
    }
    
   /**
    * Constructs a new GraphLine object using the
    * included coordinates.
    */        
    public DrawLine(double x1, double y1, double x2, double y2) {
        if (x1 <= x2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        else {
            this.x1 = x2;
            this.y1 = y2;
            this.x2 = x1;
            this.y2 = y1;
        } 
    }    
    
   /**
    * Constructs a new GraphLine object using the
    * included coordinates.
    */        
    public DrawLine(double x1, double y1, double x2, double y2, CoordinateRect coords) {
        this.coords = coords;
        if (x1 <= x2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        else {
            this.x1 = x2;
            this.y1 = y2;
            this.x2 = x1;
            this.y2 = y1;
        } 
    }        
    
    public static DrawLine getArcInstance(int x1, int y1, int x2, int y2) {
        DrawLine arcLine = new DrawLine(x1, y1, x2, y2);
        arcLine.isArc = true;
        return arcLine;
    }
    
    public static DrawLine getArcInstance(double x1, double y1, double x2, double y2) {
        DrawLine arcLine = new DrawLine(x1, y1, x2, y2);
        arcLine.isArc = true;
        return arcLine;
    }    
    
    public static DrawLine getArcInstance(double x1, double y1, double x2, double y2, CoordinateRect coords) {
        DrawLine arcLine = new DrawLine(x1, y1, x2, y2, coords);
        arcLine.isArc = true;
        return arcLine;
    }    
    
   /**
    * This method is implemented according to the Drawable
    * interface. The method causes to draw this object.
    * This is meant to be called by the DrawedGraph 
    * (not CoordinateRect as fot other Drawables).
    *
    * @param g The graphics context in which the Drawable is to be drawn.
    * @param coordsChanged Indicates whether the CoordinateRect has changed
    *                      (has no effect here).
    */    
    public void draw(Graphics g, boolean coordsChanged) {
        if ( coordsChanged | (drawx1 == 1 && drawx2 ==0) ) {
            if ( coords == null ) coords = defaultCoords;
            drawx1 = coords.xToPixel(x1);
            drawx2 = coords.xToPixel(x2);
            drawy1 = coords.yToPixel(y1);
            drawy2 = coords.yToPixel(y2);        
        }
        g.drawLine(drawx1, drawy1, drawx2, drawy2);
    }
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * 
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object (in this case if they are not
     *         both instance of the same class).
     */    
    public int compareTo(Object o) {
        if (this.x1 < ((DrawLine) o).x1) return -1;
        if (this.x1 > ((DrawLine) o).x1) return +1;
        return 0;
    }
    
   /**
    * Set the CoordinateRect for this object.
    *
    * @ param coords The CoordinateRect to be set.
    */    
    public void setCoords(CoordinateRect coords) {
        this.coords = coords;
    }    
    
}
