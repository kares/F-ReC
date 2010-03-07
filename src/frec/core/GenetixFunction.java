package frec.core;

import frec.util.Generator;
import frec.util.GenMath;

public final class GenetixFunction implements Comparable, Cloneable {
    private static final float INI = -1.0F;

    private static final float MAX = Float.MAX_VALUE;

    private static final float NAN = Float.NaN;

    private static final float LIM = 1000000F;

    private static long counter = 0;

    private static final char[] df_symbol = { '+', // x + y
            '-', // x - y
            '*', // x * y
            '/', // x / y
            '2', // x ^ 2
            '3', // x ^ 3
            '|', // abs(x)
            'e', // e ^ x
            'l', // ln(x)
            'L', // log(x)
            '>', // max(x)
            '<', // min(x)
            's', // sin(x)
            'c', // cos(x)
            't', // tan(x)
            '~', // sqrt(x)
            'S', // asin(x)
            'C', // acos(x)
            'T' // atan(x)
    };

    private static final byte[] df_arity = { 2, // x + y
            2, // x - y
            2, // x * y
            2, // x / y
            1, // x ^ 2
            1, // x ^ 3
            1, // abs(x)
            1, // e ^ x
            1, // ln(x)
            1, // log(x)
            2, // max(x)
            2, // min(x)
            1, // sin(x)
            1, // cos(x)
            1, // tan(x)
            1, // sqrt(x)
            1, // asin(x)
            1, // acos(x)
            1 // atan(x)
    };

    private static int function_min = 2;

    private static int function_max = 10;

    static {
        Function.setAllowedSymbols(df_symbol, df_arity);
        LimitedTree.setCodeLimits(0, 2);
        if (!Generator.isInitialized()) Generator.init();
    }

    private Function function;

    private float fitness = INI;

    private GenetixFunction() {
        function = new Function(function_min
                + Generator.randomInt(function_max - function_min + 1));
        counter++;
    }

    private GenetixFunction(int len) {
        function = new Function(len);
        counter++;
    }

    private GenetixFunction(Function function) {
        this.function = function;
        counter++;
    }

    /**
     * Method used to get a statistical report over the
     * <code>GenetixElement</code>. Returns the objects totaly created, this
     * is usually called and shown by a gui.
     * 
     * @return Long integer that represents how many times the constructor has
     *         been called.
     */

    public static long getFunctionsCreated() {
        return counter;
    }

    public static void setFunctionCodeLengthLimits(int min, int max) {
        if (min >= 0) function_min = min;
        if (max > 0) function_max = max;
    }

    public static int[] getFunctionCodeLengthLimits() {
        return new int[] { function_min, function_max };
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * NOTE: The comparing implemented here is compatible among all objects of
     * this class, meaning that if there are classes <tt>A</tt> and <tt>B</tt>
     * extending this class they are comparable as they both have a fitness
     * value which will be compared in this method.
     * 
     * @param o
     *            the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     * @throws ClassCastException
     *             if the specified object's type prevents it from being
     *             compared to this Object (in this case if they are not both
     *             instance of the <code>GenetixElement</code> class).
     */

    public int compareTo(Object o) throws ClassCastException {
        // if (!(o instanceof GenetixFunction))
        // throw new ClassCastException(
        // "invalid class type: "+o.getClass().getName());

        if (((GenetixFunction) o).getFitness() == NAN) return -1;
        if (this.getFitness() == NAN) return +1;
        if (this.fitness < ((GenetixFunction) o).fitness) return -1;
        if (this.fitness > ((GenetixFunction) o).fitness) return +1;
        return 0;
    }

    public boolean equals(Object o) {
        return this.function.equals(((GenetixFunction) o).function);
    }

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
            ((GenetixFunction) clone).function = new Function(this.function
                    .getCode(), this.function.getSymbols(), this.function
                    .getConstants());
        }
        catch (CloneNotSupportedException e) {}
        return clone;
    }

    public boolean isValid() {
        if ((fitness < LIM) && (fitness >= 0)) return true;
        return false;
    }

    public float getFitness() {
        if (fitness == INI) return NAN;
        return fitness;
    }

    public boolean setFitness(float value) {
        if ((value < 0) || (value >= MAX) || (value == NAN)) return false;
        this.fitness = value;
        return true;
    }

    public float getFunctionValue(float x) {
        return this.function.value(x);
    }

    public int length() {
        return this.function.getCode().length();
    }

    public String parseFunction() {
        return this.function.parse();
    }

    public boolean checkFunction() {
        this.function.check();
        if (function.getCode().length() == 1) return false;
        return true;
    }

    public void mutateFunction(boolean arbitraryMutation) {
        if (!arbitraryMutation)
            function.mutateCode(function_min, function_max);
        else
            function.mutateCode(Generator.randomInt(function_max));
    }

    public void crossFunctions(GenetixFunction funx, boolean arbitraryCrossing) {
        Function[] crossed;
        if (!arbitraryCrossing)
            crossed = this.function.crossCode(funx.function, function_min,
                    function_max);
        else
            crossed = this.function.crossCode(funx.function);
        this.function = crossed[0];
        funx.function = crossed[1];
        this.fitness = INI;
        funx.fitness = INI;
    }

    public void crossFunctions(GenetixFunction funx, int crossPosForThis) {
        this.function.setCrossPosition(crossPosForThis);
        this.crossFunctions(funx, false);
    }

    public void crossFunctions(GenetixFunction funx, int crossPosForThis,
            int crossPosForFunx) {
        this.function.setCrossPosition(crossPosForThis);
        funx.function.setCrossPosition(crossPosForFunx);
        this.crossFunctions(funx, true);
    }

    /**
     * Method used to provide a <code>String</code> representation of an
     * object.
     * 
     * @return String representing this function.
     */

    public String toString() {
        String s = "GenetixFunction:\n code = " + function.getCode()
                + " ;\n eval = ";
        char[] sym = function.getSymbols();
        float[] con = function.getConstants();
        for (int i = 0; i < sym.length; i++) {
            if (sym[i] == 'a') s += con[i] + " ";
            if (sym[i] == 'x')
                s += "x ";
            else
                s += GenMath.parseOperation(sym[i]) + " ";
        }
        s += ";\n";
        String fit = Float.toString(fitness);
        if (fitness == INI) fit = "initial";
        if (fitness == NAN) fit = "invalid";
        s += " fitness = " + fit + " ;\n";
        return s;
    }

    public static GenetixFunction[] generate(int size) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i = 0; i < size; i++)
            gp[i] = new GenetixFunction();
        return gp;
    }

    public static GenetixFunction[] generate(int size, boolean prefferShorter) {
        GenetixFunction[] gp = new GenetixFunction[size];
        if (prefferShorter) {
            for (int i = 0; i < size; i++)
                gp[i] = new GenetixFunction(function_min
                        + Generator.ascRandomInt(function_max - function_min
                                + 1));
        }
        else {
            for (int i = 0; i < size; i++)
                gp[i] = new GenetixFunction();
        }
        return gp;
    }

    public static GenetixFunction[] generate(int size, int len) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i = 0; i < size; i++)
            gp[i] = new GenetixFunction(len);
        return gp;
    }

    /*
    public static void main(String[] args) {
        Function g = new Function(5);
        System.out.println(g);
        for (int i = 2; i < 10; i++) {
            Function f = new Function(i);
            System.out.println(f);
            // System.out.println(f.value(1));
        }
        System.out.println("min=" + LimitedTree.getCodeMinLimit());
        System.out.println("max=" + LimitedTree.getCodeMaxLimit());
    }
    */

}
