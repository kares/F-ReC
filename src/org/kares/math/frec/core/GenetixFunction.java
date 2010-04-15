/*
 * Copyright 2004 Karol Bucek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kares.math.frec.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kares.math.frec.core.ReadsTree.CrossingContext;
import org.kares.math.frec.core.ReadsTree.MutationContext;
import org.kares.math.frec.util.RandomHelper;

/**
 * Each instance of <code>GenetixFunction</code> represents a 
 * subject that forms a generation. Usually {@link Genetix}
 * generates, clones, mutates or crosses instances of this class.
 * 
 * A "genetix" function consist of a {@link FunctionTree} and has a
 * fitness value representing how well the instance maps the target
 * approximated data (the fitness is computed by {@link Genetix}).
 * 
 * <p>
 * NOTE: Due to the JCM parser limits only those elementary functions
 * are used that the parser understands (to be able to show the results).
 * If the GUI part is not important use {@link FunctionTree#setAllowedFunctions(Function[])}
 * to change the allowed functions.
 * 
 * @author kares
 *
 */
public class GenetixFunction implements Comparable, Cloneable {

    private static final double INI = -1.0;
    //private static final double MAX = Double.MAX_VALUE;

    private static double validFitnessLimit = 1000000;

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
            // NOTE: ceil not supported by JCM !
            //Functions.Ceil.INSTANCE,
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

    /**
     * Constructor.
     */
    protected GenetixFunction() {
        this(functionMinLength + RandomHelper.randomInt(functionMaxLength - functionMinLength + 1));
    }

    /**
     * Constructor.
     * @param len
     */
    protected GenetixFunction(int len) {
        this(FunctionTree.getRandomInstance(len));
    }

    /**
     * Constructor.
     * @param function
     */
    private GenetixFunction(FunctionTree function) {
        this.function = function;
    }

    /**
     * Factory for generating random instances.
     * @param size
     * @return Generated function instances.
     */
    public static GenetixFunction[] generate(int size) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i = 0; i < size; i++) gp[i] = new GenetixFunction();
        return gp;
    }

    /**
     * Factory for generating random instances (of a given length).
     * @param size
     * @param length
     * @return Generated function instances.
     */
    public static GenetixFunction[] generate(int size, int length) {
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i=0; i<size; i++) gp[i] = new GenetixFunction(length);
        return gp;
    }
    
    /**
     * Factory for generating random instances.
     * @param size
     * @param shorter If true shorter length functions will be preffered.
     * @return Generated function instances.
     */
    public static GenetixFunction[] generate(int size, boolean shorter) {
        if ( ! shorter ) return generate(size);
        GenetixFunction[] gp = new GenetixFunction[size];
        for (int i=0; i<size; i++) {
            int ascRndInt = RandomHelper.ascRandomInt(functionMaxLength - functionMinLength + 1);
            gp[i] = new GenetixFunction(functionMinLength + ascRndInt);
        }
        return gp;
    }
    
    /**
     * Set the function length limits. These limits should be kept
     * while generating random functions as well as during mutation
     * and crossings.
     * @param min
     * @param max
     */
    public static void setFunctionCodeLengthLimits(int min, int max) {
        if ( min > max ) {
            throw new IllegalArgumentException("min(" + min + ") > max(" + max + ")");
        }
        setFunctionCodeMinLength(min);
        setFunctionCodeMaxLength(max);
    }

    /**
     * @see #setFunctionCodeLengthLimits(int, int)
     */
    public static void setFunctionCodeMinLength(int min) {
        if ( min >= 0 ) functionMinLength = min;
    }

    /**
     * @see #setFunctionCodeLengthLimits(int, int)
     */
    public static int getFunctionCodeMinLength() {
        return functionMinLength;
    }

    /**
     * @see #setFunctionCodeLengthLimits(int, int)
     */
    public static void setFunctionCodeMaxLength(int max) {
        if ( max >  0 ) functionMaxLength = max;
    }

    /**
     * @see #setFunctionCodeLengthLimits(int, int)
     */
    public static int getFunctionCodeMaxLength() {
        return functionMaxLength;
    }

    /**
     * @see #setValidFitnessLimit(double)
     * @return The current fitness limit.
     */
    public static double getValidFitnessLimit() {
    	return validFitnessLimit;
    }

    /**
     * Set the valid fitness threshold fitness values greater
     * than this value are considered invalid and will be discarded.
     * @param limit
     */
    public static void setValidFitnessLimit(final double limit) {
    	if ( limit <= 0 ) {
    		throw new IllegalArgumentException(limit + " <= 0");
    	}
    	if ( Double.isInfinite(limit) ) {
    		throw new IllegalArgumentException("limit is infinite");
    	}
    	if ( Double.isNaN(limit) ) {
    		throw new IllegalArgumentException("limit is NaN");
    	}
    	GenetixFunction.validFitnessLimit = limit;
    }
    
    /**
     * Check the fitness for errors.
     * @return Returns true if the fitness is a valid value.
     */
    public boolean isFitnessValid() {
        if ( Double.isNaN(fitness) ) return false;
        if ( Double.isInfinite(fitness) ) return false;
        return fitness >= 0 && fitness <= validFitnessLimit;
    }

    /**
     * @return The computed fitness
     * @see Genetix#computeFitness(GenetixFunction)
     */
    public double getFitness() {
        return fitness == INI ? Double.NaN : fitness;
    }

    /**
     * Set the fitness value.
     * @param value
     * @return {@link #isFitnessValid()} 
     * @see Genetix#computeFitness(GenetixFunction)
     */
    public boolean setFitness(double value) {
        this.fitness = value;
        return isFitnessValid();
    }

    /**
     * Resets the fitness to an initial state.
     */
    public void resetFitness() {
        this.fitness = INI;
    }

    /**
     * @return The function tree of this instance.
     */
    public FunctionTree getFunctionTree() {
        return this.function;
    }

    /**
     * Get the function value f(x).
     * @param x
     * @return The function value at x.
     * @see FunctionTree#value(double)
     */
    public double getFunctionValue(double x) {
        return this.function.value(x);
    }

    /**
     * @see FunctionTree#length()
     */
    public int length() {
        return this.function.length();
    }

    /**
     * @see FunctionTree#format()
     */
    public String formatFunction() {
        return this.function.format();
    }

    public boolean checkFunction() {
        //this.function.removeInverseElements();
        return this.function.length() > 1;
    }

    /**
     * Mutates this function.
     * @param arbitrary Whether to use arbitrary crossing or respect the
     * minimum - maximum function length limits.
     * @see FunctionTree#mutateCode(MutationContext)
     */
    public void mutateFunction(boolean arbitrary) {
        if ( arbitrary ) {
            function.mutateCode(RandomHelper.randomInt(functionMaxLength));
        }
        else {
            function.mutateCode(functionMinLength, functionMaxLength);
        }
    }

    /**
     * Crosses two functions.
     * @param that The other function to be crossed with this.
     * @param arbitrary Whether to use arbitrary crossing or respect the
     * minimum - maximum function length limits.
     * @see FunctionTree#crossCode(CrossingContext)
     */
    public void crossFunctions(final GenetixFunction that, boolean arbitrary) {
        FunctionTree[] crossed;
        if ( arbitrary ) {
            crossed = this.function.crossCode(that.function);
        }
        else {
            crossed = this.function.crossCode(that.function, functionMinLength, functionMaxLength);
        }
        this.function = crossed[0];
        that.function = crossed[1];
        this.fitness = INI;
        that.fitness = INI;
    }

    /**
     * Crosses two functions at the given positions.
     * @param that he other function to be crossed with this.
     * @param thisCrossPos
     * @param thatCrossPos
     * @see FunctionTree#crossCode(CrossingContext)
     */
    public void crossFunctions(
    		final GenetixFunction that,
            final int thisCrossPos, 
            final int thatCrossPos) {
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
     * Comparison based on the {@link #getFitness()} value.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object other) {
        final GenetixFunction that = (GenetixFunction) other;
        final double fitness1 = this.getFitness();
        final double fitness2 = that.getFitness();
        if ( fitness1 == INI ) {
            throw new IllegalStateException("fitness not set for " + this);
        }
        if ( fitness2 == INI ) {
            throw new IllegalStateException("fitness not set for " + that);
        }
        if ( Double.isNaN(fitness1) ) return +1;
        if ( Double.isNaN(fitness2) ) return -1;
        if ( fitness1 > fitness2 ) return +1;
        if ( fitness1 < fitness2 ) return -1;
        return 0;
    }

    /**
     * Equality is based on the {@link #getFunctionTree()}.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other instanceof GenetixFunction) {
            final GenetixFunction that = (GenetixFunction) other;
            return this.function.equals(that.function);
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
    	return 7 * this.function.hashCode();
    }
    
    /**
     * @see java.lang.Object#clone()
     */
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
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String fit = Float.toString((float) fitness);
        if (fitness == INI) fit = "initial";
        if (Double.isNaN(fitness)) fit = "invalid";
        
        final StringBuffer str = new StringBuffer();
        str.append("GenetixFunction:");
        str.append(" [fitness = ").append(fit).append("] ");
        str.append(function.format());
        return str.toString();
    }

    public static class Tuple implements Cloneable {

        private final List functions;

        public Tuple() {
            functions = new ArrayList();
        }

        public Tuple(GenetixFunction[] elems) {
            this();
            functions.addAll(Arrays.asList(elems));
        }

        public Tuple(Collection elems) {
            this();
            functions.addAll(elems);
        }

        public GenetixFunction get(int i) {
            return (GenetixFunction) functions.get(i);
        }

        public void set(int i, GenetixFunction fx) {
            functions.set(i, fx);
        }

        public void remove(int i) {
            functions.remove(i);
        }

        public int size() {
            return functions.size();
        }

        public void clear() {
            functions.clear();
        }

        public void add(GenetixFunction fx) {
            functions.add(fx);
        }

        public void addAll(GenetixFunction[] fxs) {
            functions.addAll(Arrays.asList(fxs));
        }

        public void addAll(Collection fxs) {
            functions.addAll(fxs);
        }

        public GenetixFunction[] snapshot() {
            final int size = functions.size();
            return (GenetixFunction[]) functions.toArray(new GenetixFunction[size]);
        }

        public Tuple copy(boolean deep) {
            if ( deep ) {
                Tuple copy = new Tuple();
                for (int i=0; i<size(); i++) {
                    copy.add((GenetixFunction) get(i).clone());
                }
                return copy;
            }
            return new Tuple(functions);
        }

        public List asList() {
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
