
package frec.jcm.core;

/**
 * This class is from edu.hws.jcm.data package without any modification.
 * A ValueMath object is an easy way to create Value objects that are computed
 * from other Value objects.  For example, "new ValueMath(a,b,'+')" is an
 * object whose value is obtained by adding the values of a and b.
 */
public class ValueMath implements Value {

   private Function f;  // If non-null, this is a value of the form f(params);
                        // If null, it's of the form x + y, x - y, ...
   private double[] param;
   private Value x,y;
   private char op;
   
   /**
    * Create a ValueMath object whose value is computed by applying an arithmetic
    * operator the values of x and y.
    * @param op The arithmetic operator that is to be applied to x and y.  This should
    *           be one of the characters '+', '-', '*', '/', or '^'.  (No error is
    *           thrown if another character is provided.  It will be treated as a '/').
    */
   public ValueMath(Value x, Value y, char op) {
      this.x = x;
      this.y = y;
      this.op = op;
   }
   
   /**
    * Create a ValueMath object whose value is computed as f(x).
    */
   public ValueMath(Function f, Value x) {
       if (f.getArity() != 1)
          throw new IllegalArgumentException("Internal Error:  The function in a ValueMath object must have arity 1.");
       this.f = f;
       this.x = x;
       param = new double[1];
   }
   
   /**
    *  Get the value of this object.
    */
   public double getVal() {
      if (f != null) {
         param[0] = x.getVal();
         return f.getVal(param);
      }
      else {
         double a = x.getVal();
         double b = y.getVal();
         switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            case '^': return Math.pow(a, b);
            case '>': return Math.max(a, b);
            case '<': return Math.min(a, b);
            default:  throw new IllegalArgumentException("Internal Error:  Unknown math operator.");
         }
      }
   }

} // end class ValueMath
