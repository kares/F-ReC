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

package org.kares.math.frec.jcm.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.kares.math.frec.jcm.data.*;

// This class has beean added for F-ReC.

/**
 * A DrawGraph represents the graph the user draws using the mouse.
 * The object is drawn and might be shown in an arbitrary AbstractCanvas 
 * (as it extends Drawable), the possibility of drawing is implemented 
 * on a DrawableCanvas object.
 * A DrawGraph object consists of DrawLines, those are drawn every time
 * a graph is required to be (re)drawn. Lines are always sorted from left
 * to right (from the lower x-values to the higher ones).
 *
 * @see DrawingCanvas
 * @see DrawLine
 */
public class DrawGraph extends Drawable {
    
    private final ArrayList lines; // list storing the lines (DrawLine objects)
    
    private boolean sorted = false;
    private double minDomain = 0, maxDomain = 0; // domain parameters
    private Color graphColor = Color.BLACK; // Color of the graph lines
    
   /**
    * Create a DrawedGraph with no lines.  Lines will be
    * added later when the user draws on the Canvas.
    */
   public DrawGraph() {
        lines = new ArrayList(32);
   }
   
   /**
    * Create a DrawedGraph with no lines.  Lines will be
    * added later when the user draws on the Canvas.
    */
   public DrawGraph(CoordinateRect coords) {
        this();
        setCoords(coords);
   }   
    
   /**
    * This is used to get data which represent this object in a 
    * specified accuracy. The return value is an array of FunctionPoints.
    *
    * @ param size The size (accuracy of aproximation) of data to be returned.
    */    
   
   public FunctionPoint[] getFunctionPoints(int size) {
        if ( ! sorted ) sortLines();
        if ( lines.size() == 0 ) return null;
        
        FunctionPoint[] funcPoint = new FunctionPoint[size];
        double x = ((DrawLine)lines.get(0)).x1;
        double scale = ((DrawLine)lines.get(lines.size()-1)).x2;
        scale -= x;
        scale /= size;
        int ind = 0;

        for (int i=0; i<size; i++) {
            while(true) {
                DrawLine l = (DrawLine)lines.get(ind);
                if ((l.x1 < x) && (x < l.x2)) {
                    double y = l.y2 - l.y1; y *= l.x1 - x;
                    y /= l.x1 - l.x2; y += l.y1;
                    funcPoint[i] = new FunctionPoint(x, y);
                    break;
                }                
                if (x == l.x1) {
                    funcPoint[i] = new FunctionPoint(l.x1, l.y1);
                    break;
                }
                if (x == l.x2) {
                    funcPoint[i] = new FunctionPoint(l.x2, l.y2);
                    break;
                }  
                if (ind<lines.size()-1) ind++;
                else break;
            }
            x += scale;
        }
        return funcPoint;
    }

   public boolean isEmpty() {
       return lines.isEmpty();
   }

   /**
    * Add a GraphLine to this DrawedGraph. The method parameters
    * are exactly the parameters used to construct a line. 
    */   
    public void addLine(DrawLine drawLine) {
        lines.add(drawLine);
    }   
   
   /**
    * Add a GraphLine to this DrawedGraph. The method parameters
    * are exactly the parameters used to construct a line. 
    */   
    public void addLine(int x1, int y1, int x2, int y2) {
        lines.add(new DrawLine(x1, y1, x2, y2));
    }

   /**
    * Add GraphLines to this DrawedGraph. 
    *
    * @ param c Arbitrary Collection (usualy a Vector) of GraphLines. 
    */       
    public void addLines(Collection c) {
        lines.ensureCapacity(c.size());
        lines.addAll(c);
    }      

   /**
    * Removes all the lines (GraphLines) from this object.
    */       
    public void deleteLines() {
        lines.clear();
        sorted = false;
    }  
    
    public double[] getDomain() {
        setDomain();
        return new double[] { minDomain, maxDomain };
    }
    
    private void sortLines() {
        Collections.sort(lines);
        sorted = true;       
    } 
    
    private void setDomain() {
        if ( ! sorted) sortLines();
        if ( ! lines.isEmpty() ) {
            minDomain = ((DrawLine) lines.get(0)).x1;
            maxDomain = ((DrawLine) lines.get(lines.size()-1)).x2;
        }
        else {
            minDomain = 0;
            maxDomain = 0;
        }
    }    
   
   /**
    * Computes the value of this object at the specified position
    * (argument) as if it was a regular graph linked with a real-values
    * function of one variable.
    *
    * @ param argument Argument (x position) where the value is to be computed.
    */     
    public double getValue(double arg) {
        if (lines.size() != 0) setDomain();
        else return Double.NaN; // invalid result
       
        if ((arg < minDomain)
         || (arg > maxDomain)) return Double.NaN;
        
        int ind = lines.size() / 2;
       
        DrawLine l = (DrawLine)lines.get(ind);
        while (arg < l.x1)
            l = (DrawLine)lines.get(--ind);
        while (arg > l.x2)
            l = (DrawLine)lines.get(++ind);
        
        if ((l.x1 < arg) && (arg < l.x2))
        {
            double y = l.y2 - l.y1; y *= l.x1 - arg;
            y /= l.x1 - l.x2; y += l.y1;
            return y;
        }     
        
        if (arg == l.x1)
            return l.y1;

        if (arg == l.x2)
            return l.y2;
       
        return Double.NaN;
   }    
    
   /**
    * Set the color to be used for drawing the graph.  The default color is black.
    */
   public void setColor(Color c)  {
      if ( c != null && ! c.equals(graphColor) ) {
         graphColor = c;
         needsRedraw();
      }
   }   
   
   /**
    * Get the color that is used to draw the graph.
    */
   public Color getColor() {
      return graphColor; 
   }
   
   //------------------ Implementation details -----------------------------
   
   /**
    * Draw the graph (possibly recomputing the data if the CoordinateRect has changed).
    * This is not usually called directly.
    *
    */
    public void draw(Graphics g, boolean coordsChanged) {
        g.setColor(graphColor);
        for (int i=0; i<lines.size(); i++)
            ((DrawLine)lines.get(i)).draw(g, coordsChanged);
    }   
    
   /**
    * Set the CoordinateRect for this object. The rectangle
    * should have been set by registering at a Controller.
    * This method won't be included in further versions.
    *
    * @ param coords The CoordinateRect to be set.
    */    
    public void setCoords(CoordinateRect coords) {
        this.coords = coords;
        if ( lines.size() > 1 ) {
            for (int i=0; i<lines.size(); i++) {
                ((DrawLine) lines.get(i)).setCoords(coords);
            }
        }
        
        DrawLine.setDefaultCoords(coords);
    }   
   
} // end class DrawGraph
