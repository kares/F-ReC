
package frec.util;

    /**
     * Class <code> GenMath </code> is a util class mainly needed
     * for evaluating functions.
     * Thus this class is used by the <code>Function</code> class.
     * Strict floating point operations are used.
     */

public final class GenMath
{
    /**
     * This method counts the arithmetic difference between
     * two data arrays provided as arguments.
     *
     * @return Arithmetic difference of the elements in the provided arrays.
     */    
    
    public static strictfp float E(float[] data1, float[] data2)
    {
        float dif = 0;
        for (int i=0; i<data1.length; i++)
            dif += (float)Math.abs(data1[i] - data2[i]);
        return dif;
    }           
    
    /**
     * This method gets the value of this elementary function
     * represented by the provided function symbol at the specified
     * value of variable(s).
     * <p>
     * Accepted function symbols:
     * <ul>
     * <li> '+' - x1 + x2
     * <li> '-' - x1 - x2
     * <li> '*' - x1 * x2
     * <li> '/' - x1 / x2
     * <li> '^' - x1 ^ x2
     * <li> 'e' - e ^ x1
     * <li> '2' - x1 ^ 2
     * <li> '3' - x1 ^ 3
     * <li> '4' - x1 ^ 4
     * <li> '5' - x1 ^ 5
     * <li> '|' - abs(x1)
     * <li> 'l' - ln(x1)
     * <li> 'L' - log(x1)
     * <li> '~' - sqrt(x1)
     * <li> 's' - sin(x1)
     * <li> 'c' - cos(x1)
     * <li> 't' - tan(x1)
     * <li> 'S' - asin(x1)
     * <li> 'C' - acos(x1)
     * <li> 'T' - atan(x1)
     * <li> '>' - max(x1,x2)
     * <li> '<' - min(x1,x2)
     * </ul>
     * @param x1 The variable value, considered always.
     * @param x2 The variable value, considered only if the function symbol specifies a function of 2 variables.
     * @return The value of the (elementary) function.
     */        

    public static strictfp float getValue(char symbol, float x1, float x2)
    {
        switch(symbol)
        {
            case '+' : return x1 + x2;
            case '-' : return x1 - x2;
            case '*' : return x1 * x2;
            case '/' : return (x2 == 0) ? Float.NaN : x1 / x2;
            case '^' : return (float)Math.pow(x1, x2);

            case '2' : return x1 * x1;
            case '3' : return x1 * x1 * x1;
            case '4' : return x1 * x1 * x1 * x1;
            case '5' : return x1 * x1 * x1 * x1 * x1;            

            case '|' : return Math.abs(x1);
            case 'e' : return (float)Math.exp(x1);
            case 'l' : return (x1 <= 0) ? Float.NaN : (float)Math.log(x1);
            case 'L' : return (x1 <= 0) ? Float.NaN : (float)(Math.log(x1)/Math.log(10));

            case '>' : return Math.max(x1, x2);
            case '<' : return Math.min(x1, x2);
            case '~' : return (x1 < 0) ? Float.NaN : (float)Math.sqrt(x1);

            case 's' : return (float)Math.sin(x1);
            case 'c' : return (float)Math.cos(x1);
            case 't' : return (float)Math.tan(x1);

            case 'S' : return (float)Math.asin(x1);
            case 'C' : return (float)Math.acos(x1);
            case 'T' : return (float)Math.atan(x1);

            default : return Float.NaN;
        }
    }
    
    /**
     * This method gets the value of this elementary function
     * represented by the provided function symbol at the specified
     * value of variable(s). Double precision is used.
     *
     * @param x1 The variable value, considered always.
     * @param x2 The variable value, considered only if the function symbol specifies a function of 2 variables.
     * @return The value of the (elementary) function.
     */     
    
    public static strictfp double getValue(char symbol, double x1, double x2)
    {
        switch(symbol)
        {
            case '+' : return x1 + x2;
            case '-' : return x1 - x2;
            case '*' : return x1 * x2;
            case '/' : return (x2 == 0) ? Double.NaN : x1 / x2;
            case '^' : return Math.pow(x1, x2);

            case '2' : return x1 * x1;
            case '3' : return x1 * x1 * x1;
            case '4' : return x1 * x1 * x1 * x1;
            case '5' : return x1 * x1 * x1 * x1 * x1;            

            case '|' : return Math.abs(x1);
            case 'e' : return Math.exp(x1);
            case 'l' : return (x1 <= 0) ? Double.NaN : Math.log(x1);
            case 'L' : return (x1 <= 0) ? Double.NaN : Math.log(x1)/Math.log(10);

            case '>' : return Math.max(x1, x2);
            case '<' : return Math.min(x1, x2);
            case '~' : return (x1 < 0) ? Double.NaN : Math.sqrt(x1);

            case 's' : return Math.sin(x1);
            case 'c' : return Math.cos(x1);
            case 't' : return Math.tan(x1);

            case 'S' : return Math.asin(x1);
            case 'C' : return Math.acos(x1);
            case 'T' : return Math.atan(x1);

            default : return Double.NaN;
        }
    }    
    
    /**
     * This method parses the (encoded) operation represented
     * by one character symbol to a string format.
     *
     * @param symbol Character symbol to be parsed.
     * @return The string representation of the operation.
     */     
    
    public static String parseOperation(char symbol)
    {
        switch(symbol)
        {
            case '+' : return "+";
            case '-' : return "-";
            case '*' : return "*";
            case '/' : return "/";
            case '^' : return "^";

            case '2' : return "^2";
            case '3' : return "^3";
            case '4' : return "^4";
            case '5' : return "^5";

            case '|' : return "abs";
            case 'e' : return "e^";
            case 'l' : return "ln";
            case 'L' : return "log10";

            case '>' : return ">"; // max
            case '<' : return "<"; // min
            case '~' : return "sqrt";

            case 's' : return "sin";
            case 'c' : return "cos";
            case 't' : return "tan";
            case 'S' : return "arcsin";
            case 'C' : return "arccos";
            case 'T' : return "arctan";

            default : return "";
        }
    }    

    /**
     * This method gets the value of this elementary function
     * represented by the provided function symbol at the specified
     * value of variables. This is meant to be used for elementary
     * functions which have more than 2 variables. None is yet implemented.
     */      
    
    public static strictfp float getValue(char symbol, float[] x)
    {
        return 0;
    }    

    /**
     * This method gets the value of this elementary function
     * represented by the provided function symbol at the specified
     * value of variables. This is meant to be used for elementary
     * functions which have more than 2 variables. None is yet implemented.
     */          
    
    public static strictfp double getValue(char symbol, double[] x)
    {
        return 0;
    }        
}

