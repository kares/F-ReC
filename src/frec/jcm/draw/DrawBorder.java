
package frec.jcm.draw;

import java.awt.Graphics;
import java.awt.Color;

/**
 * This class is from edu.hws.jcm.draw package without any modification.
 * A DrawBorder object is just a simple border around the edges of its CoordinateRect, with 
 * a specified width, in pixels, and a specified color.
 */
public class DrawBorder extends Drawable {

   /** 
    * A non-null Color, giving the color of the bortder.
    */
   protected Color color;
   
   /**
    * A non-negative integer giving the width of the border in pixels.
    */
   protected int width;
   
   
   /**
    * Create a black border that is one pixel thick.
    */
   public DrawBorder() {
      this(Color.black,1);
   }
   
   /**
    * Create a border with the spcified color and width.  If the color is null,
    * black is used.  If the width is less than zero, a width of 1 is used.
    * A border of width zero is invisible.
    * 
    */
   public DrawBorder(Color color, int width) {
      this.color = ((color == null)? Color.black : color);
      this.width = ((width >= 0)? width : 1);
   }
   
   /**
    * Get the color of the border.
    *
    */
   public Color getColor() {
      return color;
   }
   
   /**
    * Set the color of the border to the specified color.  If the color is null, nothing is done.
    *
    */
   public void setColor(Color c) {
      if (c != null && !c.equals(color)) {
         color = c;
         needsRedraw();
      }
   }
   
   /**
    * Get the width of the border, in pixels.
    *
    */
   public int getWidth() {
      return width;
   }
   
   /**
    * Set the width of the border to be w pixels.  If w is negative,
    * this is ignored.  A border of witdth 0 is invisible.
    *
    * @param w the desired width for the border.
    */
   public void setWidth(int w) {
      if (w >= 0 && width != width) {
         width = w;
      }
   }   

   /**
    * Draw the border in the given graphics context.  This is not ordinarily called directly.
    *
    */
   public void draw(Graphics g, boolean changed) {
      if (coords == null || width == 0) 
         return;
      g.setColor(color);
      for (int i = 0; i < width; i++)
         g.drawRect(coords.getLeft() + i, coords.getTop() + i,
                       coords.getWidth() - 2*i - 1, coords.getHeight() - 2*i - 1);
   }
   
} // end class DrawBorder

