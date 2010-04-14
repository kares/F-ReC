
package org.kares.math.frec.core;

/**
 * Function math.
 *
 * @deprecated not used anymore
 * @author kares
 */
abstract strictfp class FunctionMath {

    /**
     * This method gets the value of this elementary function
     * represented by the provided function symbol at the specified
     * value of variable(s).
     *
     * <p>
     * Accepted function symbols:
     * <ul>
     * <li> '+' - x1 + x2
     * <li> '-' - x1 - x2
     * <li> '*' - x1 * x2
     * <li> '/' - x1 / x2
     * <li> '%' - x1 % x2
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
     *
     * @param operation
     * @param x1 The variable value, considered always.
     * @param x2 The variable value, considered only if the function symbol specifies a function of 2 variables.
     * @return The value of the (elementary) function.
     */
    public static double evaluate(final char operation, final double x1, final double x2)
    {
        switch(operation)
        {
            case '+' : return x1 + x2;
            case '-' : return x1 - x2;
            case '*' : return x1 * x2;
            case '/' : return (x2 == 0) ? Double.NaN : x1 / x2;
            case '%' : return (x2 == 0) ? Double.NaN : x1 % x2;
            case '^' : return StrictMath.pow(x1, x2);

            //case '0' : return 1;
            //case '1' : return x1;
            case '2' : return x1 * x1;
            case '3' : return x1 * x1 * x1;
            case '4' : return x1 * x1 * x1 * x1;
            case '5' : return x1 * x1 * x1 * x1 * x1;
            //case '6' : return x1 * x1 * x1 * x1 * x1 * x1;
            //case '7' : return x1 * x1 * x1 * x1 * x1 * x1 * x1;
            //case '8' : return x1 * x1 * x1 * x1 * x1 * x1 * x1 * x1;
            //case '9' : return x1 * x1 * x1 * x1 * x1 * x1 * x1 * x1 * x1;

            case '|' : return StrictMath.abs(x1);
            case 'e' : return StrictMath.exp(x1);
            case 'l' : return (x1 <= 0) ? Double.NaN : StrictMath.log(x1);
            case 'L' : return (x1 <= 0) ? Double.NaN : StrictMath.log(x1) / StrictMath.log(10);

            case '>' : return StrictMath.max(x1, x2);
            case '<' : return StrictMath.min(x1, x2);
            case '~' : return (x1 < 0) ? Double.NaN : StrictMath.sqrt(x1);

            case 's' : return StrictMath.sin(x1);
            case 'c' : return StrictMath.cos(x1);
            case 't' : return StrictMath.tan(x1);

            case 'S' : return StrictMath.asin(x1);
            case 'C' : return StrictMath.acos(x1);
            case 'T' : return StrictMath.atan(x1);

            default : return Double.NaN;
        }
    }

    public static float evaluate(final char operation, final double[] xs)
    {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * This method parses the (encoded) operation represented
     * by one character symbol to a string format.
     *
     * @param operation character symbol to be parsed.
     * @return the string representation of the operation.
     */
    public static String toString(final char operation)
    {
        switch(operation)
        {
            case '+' : return "+";
            case '-' : return "-";
            case '*' : return "*";
            case '/' : return "/";
            case '%' : return "%";
            case '^' : return "^";

            case '0' : return "^0";
            case '1' : return "^1";
            case '2' : return "^2";
            case '3' : return "^3";
            case '4' : return "^4";
            case '5' : return "^5";
            case '6' : return "^6";
            case '7' : return "^7";
            case '8' : return "^8";
            case '9' : return "^9";

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

            default : return Character.toString(operation);
        }
    }

    /**
     * This method parses the (encoded) operation represented
     * by one character symbol to a string format.
     *
     * @param operation character symbol to be parsed.
     * @param operands the operands
     * @return the string representation of the operation.
     */
    public static String toString(final char operation, final String[] operands)
    {
        switch(operation)
        {
            case '+' : return operands[0] + "+" + operands[1];
            case '-' : return operands[0] + "-" + operands[1];
            case '*' : return operands[0] + "*" + operands[1];
            case '/' : return operands[0] + "/" + operands[1];
            case '^' : return operands[0] + "^" + operands[1];

            case '2' : return operands[0] + "^2";
            case '3' : return operands[0] + "^3";
            case '4' : return operands[0] + "^4";
            case '5' : return operands[0] + "^5";
            case '6' : return operands[0] + "^6";
            case '7' : return operands[0] + "^7";
            case '8' : return operands[0] + "^8";
            case '9' : return operands[0] + "^9";

            case '|' : return "abs" + operands[0];
            case 'e' : return "e^" + operands[0];
            case 'l' : return "ln" + operands[0];
            case 'L' : return "log10" + operands[0];

            case '>' : return ">"; // max
            case '<' : return "<"; // min
            case '~' : return "sqrt" + operands[0];

            case 's' : return "sin" + operands[0];
            case 'c' : return "cos" + operands[0];
            case 't' : return "tan" + operands[0];
            case 'S' : return "arcsin" + operands[0];
            case 'C' : return "arccos" + operands[0];
            case 'T' : return "arctan" + operands[0];

            default : return Character.toString(operation);
        }
    }

}
