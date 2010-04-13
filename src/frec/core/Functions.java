/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package frec.core;

/**
 * Standart functions for computing.
 *
 * @see inner classes of this class
 * @author kares
 */
public abstract strictfp class Functions {

    public static class Add extends Function.Base {

        public static final Add INSTANCE = new Add();

        Add() { super("+", 2); }

        public double value(double[] x) {
            return x[0] + x[1];
        }

        public boolean isInverse(Function other) {
            return Sub.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Sub extends Function.Base {

        public static final Sub INSTANCE = new Sub();

        Sub() { super("-", 2); }

        public double value(double[] x) {
            return x[0] - x[1];
        }

        public boolean isInverse(Function other) {
            return Add.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Mul extends Function.Base {

        public static final Mul INSTANCE = new Mul();

        Mul() { super("*", 2); }

        public double value(double[] x) {
            return x[0] * x[1];
        }

        public boolean isInverse(Function other) {
            return Div.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Div extends Function.Base {

        public static final Div INSTANCE = new Div();

        Div() { super("/", 2); }

        public double value(double[] x) {
            return (x[1] == 0) ? Double.NaN : x[0] / x[1];
        }

        public boolean isInverse(Function other) {
            return Mul.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Mod extends Function.Base {

        public static final Mod INSTANCE = new Mod();

        Mod() { super("%", 2); }

        public double value(double[] x) {
            return (x[1] == 0) ? Double.NaN : x[0] % x[1];
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Pow extends Function.Base {

        public static final Pow INSTANCE = new Pow();

        Pow() { super("^", 2); }

        public double value(double[] x) {
            return StrictMath.pow(x[0], x[1]);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }
    
    public static class Sin extends Function.Base {

        public static final Sin INSTANCE = new Sin();

        Sin() { super("sin", 1); }

        public double value(double[] x) {
            return StrictMath.sin(x[0]);
        }

        public boolean isInverse(Function other) {
            return ArcSin.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Cos extends Function.Base {

        public static final Cos INSTANCE = new Cos();

        Cos() { super("cos", 1); }

        public double value(double[] x) {
            return StrictMath.cos(x[0]);
        }

        public boolean isInverse(Function other) {
            return ArcCos.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }
    
    public static class Tan extends Function.Base {

        public static final Tan INSTANCE = new Tan();

        Tan() { super("tan", 1); }

        public double value(double[] x) {
            return StrictMath.tan(x[0]);
        }

        public boolean isInverse(Function other) {
            return ArcTan.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Cot extends Function.Base {

        public static final Cot INSTANCE = new Cot();

        Cot() { super("cot", 1); }

        public double value(double[] x) {
            return StrictMath.cos(x[0]) / StrictMath.sin(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Sec extends Function.Base {

        public static final Sec INSTANCE = new Sec();

        Sec() { super("sec", 1); }

        public double value(double[] x) {
            return 1 / StrictMath.cos(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Csc extends Function.Base {

        public static final Csc INSTANCE = new Csc();

        Csc() { super("csc", 1); }

        public double value(double[] x) {
            return 1 / StrictMath.sin(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class ArcSin extends Function.Base {

        public static final ArcSin INSTANCE = new ArcSin();

        ArcSin() { super("arcsin", 1); }

        public double value(double[] x) {
            return StrictMath.asin(x[0]);
        }

        public boolean isInverse(Function other) {
            return Sin.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class ArcCos extends Function.Base {

        public static final ArcCos INSTANCE = new ArcCos();

        ArcCos() { super("arccos", 1); }

        public double value(double[] x) {
            return StrictMath.acos(x[0]);
        }

        public boolean isInverse(Function other) {
            return Cos.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class ArcTan extends Function.Base {

        public static final ArcTan INSTANCE = new ArcTan();

        ArcTan() { super("arctan", 1); }

        public double value(double[] x) {
            return StrictMath.atan(x[0]);
        }

        public boolean isInverse(Function other) {
            return Tan.INSTANCE.equals(other);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Abs extends Function.Base {

        public static final Abs INSTANCE = new Abs();

        Abs() { super("abs", 1); }

        public double value(double[] x) {
            return StrictMath.abs(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Sqrt extends Function.Base {

        public static final Sqrt INSTANCE = new Sqrt();

        Sqrt() { super("sqrt", 1); }

        public double value(double[] x) {
            return (x[0] < 0)? Double.NaN : StrictMath.sqrt(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Exp extends Function.Base {

        public static final Exp INSTANCE = new Exp();

        Exp() { super("exp", 1); }

        public double value(double[] x) {
            return StrictMath.exp(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Ln extends Function.Base {

        public static final Ln INSTANCE = new Ln();

        Ln() { super("ln", 1); }

        public double value(double[] x) {
            return (x[0] <= 0) ? Double.NaN : StrictMath.log(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Log2 extends Function.Base {

        public static final Log2 INSTANCE = new Log2();

        private static final double LOG2 = StrictMath.log(2);

        Log2() { super("log2", 1); }

        public double value(double[] x) {
            return (x[0] <= 0)? Double.NaN : StrictMath.log(x[0]) / LOG2;
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Log10 extends Function.Base {

        public static final Log10 INSTANCE = new Log10();

        private static final double LOG10 = StrictMath.log(10);

        Log10() { super("log10", 1); }

        public double value(double[] x) {
            return (x[0] <= 0)? Double.NaN : StrictMath.log(x[0]) / LOG10;
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Trunc extends Function.Base {

        public static final Trunc INSTANCE = new Trunc();

        Trunc() { super("trunc", 1); }

        public double value(double[] x) {
            return (long) x[0];
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Round extends Function.Base {

        public static final Round INSTANCE = new Round();

        Round() { super("round", 1); }

        public double value(double[] x) {
            return StrictMath.floor(x[0] + 0.5);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Floor extends Function.Base {

        public static final Floor INSTANCE = new Floor();

        Floor() { super("floor", 1); }

        public double value(double[] x) {
            return StrictMath.floor(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Ceil extends Function.Base {

        public static final Ceil INSTANCE = new Ceil();

        Ceil() { super("ceiling", 1); }

        public double value(double[] x) {
            return StrictMath.ceil(x[0]);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Cbrt extends Function.Base {

        public static final Cbrt INSTANCE = new Cbrt();

        Cbrt() { super("cubert", 1); }

        public double value(double[] x) {
            final double b = 1.0/3.0;
            return (x[0] >= 0) ? StrictMath.pow(x[0], b) : -StrictMath.pow(-x[0], b);
        }

        public String format(String[] varNames) {
            return id + "(" + varNames[0] + ")";
        }

    }

    public static class Fact extends Function.Base {

        public static final Fact INSTANCE = new Fact();

        Fact() { super("!", 1); }

        public double value(double[] x) {
          // Compute x!.  x is rounded to the nearest integer.  If x > 170, then the
          // answer is too big to represent in a value of type double, so the value
          // is given as Double.NaN.
          if (x[0] <= -0.5 || x[0] > 170.5) return Double.NaN;
          final int n = (int) x[0];
          double f = 1;
          for (int i = 1; i <= n; i++) f *= i;
          return f;
        }

        public String format(String[] varNames) {
            return varNames[0] + id;
        }

    }

    public static class Neg extends Function.Base {

        public static final Neg INSTANCE = new Neg();

        Neg() { super("~", 1); }

        public double value(double[] x) {
            return -x[0];
        }

        public String format(String[] varNames) {
            return "-" + varNames[0];
        }

    }

    public static class Max extends Function.Base {

        public static final Max INSTANCE = new Max();

        Max() { super(">", 2); }

        public double value(double[] x) {
            return StrictMath.max(x[0], x[1]);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

    public static class Min extends Function.Base {

        public static final Min INSTANCE = new Min();

        Min() { super("<", 2); }

        public double value(double[] x) {
            return StrictMath.min(x[0], x[1]);
        }

        public String format(String[] varNames) {
            return varNames[0] + id + varNames[1];
        }

    }

}
