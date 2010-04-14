package frec.core;

import frec.util.RandomHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GenetixFunction implements Comparable, Cloneable {

    private static final double INI = -1.0F;

    private static final double MAX = Double.MAX_VALUE;

    private static final double NAN = Double.NaN;

    private static final double LIM = 1000000;

    static {
        Function[] defaultFunctions = new Function[] {
            Functions.Add.INSTANCE,
            Functions.Sub.INSTANCE,
            Functions.Mul.INSTANCE,
            Functions.Div.INSTANCE,
            // NOTE: % seems not supported by JCM !
            //Functions.Mod.INSTANCE,
            Functions.Pow.INSTANCE,
            Functions.Sin.INSTANCE,
            Functions.Cos.INSTANCE,
            Functions.Tan.INSTANCE,
            //Functions.Cot.INSTANCE,
            Functions.Sec.INSTANCE,
            //Functions.Csc.INSTANCE,
            Functions.ArcSin.INSTANCE,
            Functions.ArcCos.INSTANCE,
            Functions.ArcTan.INSTANCE,
            Functions.Abs.INSTANCE,
            Functions.Sqrt.INSTANCE,
            Functions.Exp.INSTANCE,
            Functions.Ln.INSTANCE,
            Functions.Log2.INSTANCE,
            Functions.Log10.INSTANCE,
            Functions.Trunc.INSTANCE,
            Functions.Round.INSTANCE,
            Functions.Floor.INSTANCE,
            Functions.Ceil.INSTANCE,
            Functions.Cbrt.INSTANCE,
            // NOTE: somehow not working in JCM !
            //Functions.Fact.INSTANCE,
            Functions.Neg.INSTANCE,
            // NOTE: <, > not supported by JCM !
            //Functions.Min.INSTANCE,
            //Functions.Max.INSTANCE,
        };
        FunctionTree.setAllowedFunctions(defaultFunctions);
    }

    private static int functionMinLength = 2;
    private static int functionMaxLength = 12;

    private FunctionTree function;
    private double fitness = INI;

    private GenetixFunction() {
        this(functionMinLength + RandomHelper.randomInt(functionMaxLength - functionMinLength + 1));
    }

    private GenetixFunction(int len) {
        this(FunctionTree.getRandomInstance(len));
    }

    private GenetixFunction(FunctionTree function) {
        this.function = function;
    }

    public static void setFunctionCodeLengthLimits(int min, int max) {
        if ( min > max ) {
            throw new IllegalArgumentException("min(" + min + ") > max(" + max + ")");
        }
        setFunctionCodeMinLength(min);
        setFunctionCodeMaxLength(max);
    }

    public static void setFunctionCodeMinLength(int min) {
        if ( min >= 0 ) functionMinLength = min;
    }

    public static int getFunctionCodeMinLength() {
        return functionMinLength;
    }

    public static void setFunctionCodeMaxLength(int max) {
        if ( max >  0 ) functionMaxLength = max;
    }

    public static int getFunctionCodeMaxLength() {
        return functionMaxLength;
    }

    public int compareTo(Object other) {
        final GenetixFunction that = (GenetixFunction) other;
        if ( this.fitness == INI ) {
            throw new IllegalStateException("fitness not set for " + this);
        }
        if ( that.fitness == INI ) {
            throw new IllegalStateException("fitness not set for " + that);
        }
        if ( this.fitness == NAN ) return +1;
        if ( that.fitness == NAN ) return -1;
        if ( this.fitness > that.fitness ) return +1;
        if ( this.fitness < that.fitness ) return -1;
        return 0;
    }

    public boolean equals(Object other) {
        if (other instanceof GenetixFunction) {
            final GenetixFunction that = (GenetixFunction) other;
            return this.function.equals(that.function);
        }
        return false;
    }

    public Object clone() {
        GenetixFunction clone;
        try {
            clone = (GenetixFunction) super.clone();
            clone.function = (FunctionTree) function.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        return clone;
    }

    public boolean isFitnessValid() {
        if ( Double.isNaN(fitness) ) return false;
        if ( Double.isInfinite(fitness) ) return false;
        if ( fitness < LIM && fitness >= 0 ) return true;
        return false;
    }

    public double getFitness() {
        return fitness == INI ? NAN : fitness;
    }

    public boolean setFitness(double value) {
        this.fitness = value;
        return (value >= 0) && (value < MAX)
            && !Double.isNaN(value) && !Double.isInfinite(value);
    }

    public void resetFitness() {
        this.fitness = INI;
    }

    public FunctionTree getFunctionTree() {
        return this.function;
    }

    public double getFunctionValue(double x) {
        return this.function.value(x);
    }

    public int length() {
        return this.function.length();
    }

    public String formatFunction() {
        return this.function.format();
    }

    public boolean checkFunction() {
        //this.function.removeInverseElements();
        return this.function.length() > 1;
    }

    public void mutateFunction(boolean arbitrary) {
        if ( arbitrary ) {
            function.mutateCode(RandomHelper.randomInt(functionMaxLength));
        }
        else {
            function.mutateCode(functionMinLength, functionMaxLength);
        }
    }

    public void crossFunctions(final GenetixFunction that, boolean arbitrary) {
        FunctionTree[] crossed;
        if ( arbitrary ) {
            crossed = function.crossCode(that.function);
        }
        else {
            crossed = function.crossCode(that.function, functionMinLength, functionMaxLength);
        }
        this.function = crossed[0];
        that.function = crossed[1];
        this.fitness = INI;
        that.fitness = INI;
    }

    public void crossFunctions(final GenetixFunction that,
            final int thisCrossPos, final int thatCrossPos) {
        final ReadsTree.CrossingContext crossContext =
                new ReadsTree.CrossingContext(
                    this.function, thisCrossPos,
                    that.function, thatCrossPos
                );
        function.crossCode(crossContext);
        this.function = (FunctionTree) crossContext.getChild1();
        that.function = (FunctionTree) crossContext.getChild2();
        this.fitness = INI;
        that.fitness = INI;
    }

    /**
     * Method used to provide a <code>String</code> representation of an
     * object.
     * 
     * @return String representing this function.
     */
    public String toString() {
        String fit = Float.toString((float) fitness);
        if (fitness == INI) fit = "initial";
        if (fitness == NAN) fit = "invalid";
        
        final StringBuffer str = new StringBuffer();
        str.append("GenetixFunction:");
        str.append(" [fitness = ").append(fit).append("] ");
        str.append(function.format());
        return str.toString();
    }

    public static GenetixFunction[] generate(int size) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i = 0; i < size; i++) gp[i] = new GenetixFunction();
        return gp;
    }

    public static GenetixFunction[] generate(int size, boolean prefferShorter) {
        if ( ! prefferShorter ) return generate(size);
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i=0; i<size; i++) {
            int ascRndInt = RandomHelper.ascRandomInt(functionMaxLength - functionMinLength + 1);
            gp[i] = new GenetixFunction(functionMinLength + ascRndInt);
        }
        return gp;
    }

    public static GenetixFunction[] generate(int size, int len) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i=0; i<size; i++) gp[i] = new GenetixFunction(len);
        return gp;
    }

    // HELPERS :

    /*
    static void addAllValidTo(Collection coll, GenetixFunction[] fxs)
    {
        for (int i=0; i<fxs.length; i++)
            if (fxs[i].isFitnessValid()) coll.add(fxs[i]);
    }
     */

    public static class Tuple implements Cloneable
    {

        private List functions;

        public Tuple()
        {
            functions = new ArrayList();
        }

        public Tuple(GenetixFunction[] elems)
        {
            this();
            functions.addAll(Arrays.asList(elems));
        }

        public Tuple(Collection elems)
        {
            this();
            functions.addAll(elems);
        }

        public GenetixFunction get(int i)
        {
            return (GenetixFunction) functions.get(i);
        }

        public void set(int i, GenetixFunction fx)
        {
            functions.set(i, fx);
        }

        public void remove(int i)
        {
            functions.remove(i);
        }

        public int size()
        {
            return functions.size();
        }

        public void clear()
        {
            functions.clear();
        }

        public void add(GenetixFunction fx)
        {
            functions.add(fx);
        }

        public void addAll(GenetixFunction[] fxs)
        {
            functions.addAll(Arrays.asList(fxs));
        }

        public void addAll(Collection fxs)
        {
            functions.addAll(fxs);
        }

        public GenetixFunction[] snapshot()
        {
            final int size = functions.size();
            return (GenetixFunction[]) functions.toArray(new GenetixFunction[size]);
        }

        public Tuple copy(boolean deep)
        {
            if ( deep ) {
                Tuple copy = new Tuple();
                for (int i=0; i<size(); i++) {
                    copy.add((GenetixFunction) get(i).clone());
                }
                return copy;
            }
            return new Tuple(functions);
        }

        public List asList()
        {
            return functions;
        }

    }

    /*
    public static void main(String[] args) {
        FunctionTree g = FunctionTree.getRandomInstance(5);
        System.out.println(g);
        for (int i = 2; i < 10; i++) {
            FunctionTree f = FunctionTree.getRandomInstance(i);
            System.out.println(f);
            // System.out.println(f.value(1));
        }
        System.out.println("min=" + LimitedTree.getCodeElementMin());
        System.out.println("max=" + LimitedTree.getCodeElementMax());
    }
    */

}
