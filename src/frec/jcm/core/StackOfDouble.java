
package frec.jcm.core;

/**
 * This class is from edu.hws.jcm.data package without any modification.
 * A standard stack of values of type double, which can grow to arbitrary size.
 */
public class StackOfDouble implements java.io.Serializable {

   private double[] data;  // Contents of stack.
   private int top;        // Number of items on stack.

   /**
    * Create an initially empty stack.  It initially has space allocated for one item.
    */
   public StackOfDouble() {
    data = new double[1];
   }

   /**
    * Create an empty stack that initially has space for initialSize items pre-allocated.
    * If initialSize <= 0, an initialSize of 1 is used.
    */
   public StackOfDouble(int initialSize) {
    data = new double[initialSize > 0 ? initialSize : 1];
   }

   /**
    * Add x to top of stack.
    */
   public void push(double x) {
    if (top >= data.length) {
       double[] temp = new double[2*data.length];
       System.arraycopy(data,0,temp,0,data.length);
       data = temp;
    }
    data[top++] = x;
   }

   /**
    * Remove and return the top item on the stack.
    * Will throw an exception of type java.util.EmptyStackException
    * if the stack is empty when pop() is called.
    */
   public double pop() {
    if (top == 0)
       throw new java.util.EmptyStackException();
    return data[--top];
   }

   /**
    * Return true if and only if the stack contains no items.
    */
   public boolean isEmpty() {
    return (top == 0);
   }

   /**
    * Clear all items from the stack.
    */
   public void makeEmpty() {
    top = 0;
   }
   
   /**
    * Return the number of items on the stack.
    */
   public int size() {
    return top;
   }

} // end class StackOfDouble
