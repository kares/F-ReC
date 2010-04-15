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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kares.math.frec.util.RandomHelper;

/**
 * Brings function nodes to trees. The nodes of the tree represent {@link Function}s. 
 * This is called a syntax tree. The tree evaluation starts at the lowest nodes and
 * proceeds to the root element. The leaf elements represent the x variable.
 * 
 * @author kares
 */
public class FunctionTree extends LimitedTree {

    private static final Map allowedFunctionsByArities = new HashMap();
    
    private static boolean constantsAllowed = false;
    private static double constantMin = 0;
    private static double constantMax = 1;

    private Function[] functions;

    /**
     * Creates a tree with the given code and function attached to it.
     * @param code
     * @param functions
     */
    public FunctionTree(String code, Function[] functions) {
        super(code);
        setFunctions(functions);
    }

    /**
     * Generates a random instance.
     * @return random instance
     */
    public static FunctionTree getRandomInstance() {
        return getRandomInstance(randomCodeLength());
    }

    /**
     * Generates a random instance.
     * @param length
     * @return random instance
     */
    public static FunctionTree getRandomInstance(final int length) {
        String code = LimitedTree.generateRandomCode(length);
        Function[] functions = new Function[length];
        FunctionTree instance = new FunctionTree(code, functions);
        instance.randomFunctions(0, length);
        return instance;
    }

    /**
     * The constant minimum (when generating random constants).
     * @return constant min value
     */
    public static double getConstantMin() {
        return FunctionTree.constantMin;
    }

    /**
     * Set the constant minimum value.
     * @see #getConstantMin()
     * @param min
     */
    public static void setConstantMin(double min) {
        FunctionTree.constantMin = min;
    }

    /**
     * The constant maximum (when generating random constants).
     * @return constant max value
     */
    public static double getConstantMax() {
        return FunctionTree.constantMax;
    }

    /**
     * Set the constant maximum value.
     * @see #getConstantMax()
     * @param max
     */
    public static void setConstantMax(double max) {
        FunctionTree.constantMax = max;
    }
    
    public static FunctionTree parse(final String formatted) {
        throw new UnsupportedOperationException("parse() NOT IMPLEMETED !");
    }

    private static List allowedFunctions(final int arity) {
        List functions = (List) allowedFunctionsByArities.get(Integer.valueOf(arity));
        if ( functions == null ) {
            throw new IllegalArgumentException("no functions of arity " + arity);
        }
        return functions;
    }

    /**
     * Sets the allowed set of functions to be used in random generated instances.
     * @param functions the allowed set of functions
     */
    public static void setAllowedFunctions(final Function[] functions) {
        //FunctionTree.allowedFunctions = functions;
        FunctionTree.allowedFunctionsByArities.clear();
        if ( functions == null || functions.length == 0 ) return;

        int maxArity = functions[0].arity(); int minArity = maxArity;
        Map functionsByArities = FunctionTree.allowedFunctionsByArities;
        for (int i=0; i<functions.length; i++) {
            int arity = functions[i].arity();
            if (arity > maxArity) maxArity = arity;
            if (arity < minArity) minArity = arity;
            
            Integer key = Integer.valueOf(arity);
            List value = (List) functionsByArities.get(key);
            if (value == null) {
                value = new ArrayList();
                functionsByArities.put(key, value);
            }
            value.add(functions[i]);
        }
        if ( minArity == 1 ) minArity = 0;
        setCodeElementLimits(minArity, maxArity);
    }

    private void randomFunctions(final int beg, final int end) {
        final byte[] code = getCodeDigits();
        for ( int i = beg; i < end; i++ ) {
            if ( code[i] == 0 ) {
                functions[i] = null; // constant or variable
                if (constantsAllowed && RandomHelper.randomBoolean()) {
                    double constant = RandomHelper.randomDouble(); // <0,1>
                    constant = constant * (constantMax - constantMin);
                    constant += constantMin;
                    functions[i] = new ConstantFunction(constant);
                }
            }
            else {
                final List allowedFunctions = allowedFunctions(code[i]);
                int rnd = RandomHelper.randomInt(allowedFunctions.size());
                functions[i] = (Function) allowedFunctions.get(rnd);
            }
        }
    }

    /**
     * This method returns the functions used in this tree.
     * @return The function set of this tree.
     */
    public Function[] getFunctions() {
        return this.functions;
    }

    /**
     * Set the functions used in this tree.
     * @param functions
     */
    protected void setFunctions(final Function[] functions) {
        this.functions = functions;
    }

    /**
     * This method returns the current constants used in this tree. Note
     * that this may be also <code>null</code> as it is not necessary for the
     * tree to have constants.
     * 
     * @return The constants set (validation) of this that's leefs if any.
     */
    /*
    public double[] getConstants() {
        return this.constants;
    }

    protected void setConstants(double[] constants) {
        this.constants = constants;
        hasConstants();
    }
    */

    /**
     * This method computes the value f(x) at the specified position x.
     * @param x The variable value.
     * @return The f(x) value.
     */
    public double value(final double x) {
        return value(x, 0);
    }

    private double value(final double x, final int i) {
        final byte[] code = getCodeDigits();
        final int arity = code[i]; // == functions[i].arity()

        if (arity == 0) { // constant or function
            return functions[i] == null ? x : ((ConstantFunction) functions[i]).value();
        }
        if (arity == 1) { // shortcut
            return functions[i].value( value(x, i+1) );
        }
        if (arity == 2) { // shortcut
            double v1 = value(x, i+1);
            int l = subcodeLength(i+1);
            double v2 = value(x, i+1+l);
            return functions[i].value(v1, v2);
        }

        int k = i + 1;
        double[] values = new double[arity];
        for (int j = 0; j < arity; j++) {
            values[j] = value(x, k);
            k += subcodeLength(k);
        }
        return functions[i].value(values);
    }

    /**
     * Returns a sub-function (subtree) of this function tree.
     *
     * @param pos The sub-position in this tree's code.
     * @return Sub-function of this tree as a <code>FunctionTree</code> object.
     *
     * @see ReadsTree#subcode(int)
     * @see ReadsTree#subTree(int) 
     */
    public FunctionTree subFunction(final int pos) {
        final String subcode = subcode(pos);
        final int len = subcode.length();
        final Function[] subFunctions = new Function[len];
        System.arraycopy(functions, pos, subFunctions, 0, len);
        return new FunctionTree(subcode, functions);
    }

    /**
     * @return True if and only if this tree has constants in its syntax tree.
     */
    public boolean hasConstants() {
        for (int i = 0; i < functions.length; i++) {
            if (isConstantFunction(functions[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConstantFunction(Function fx) {
        return fx != null && (fx instanceof ConstantFunction);
    }

    /**
     * Formats the syntax tree to an exact mathematical formula 
     * (based on it's elementary functions).
     * @see Function#format(String[])
     * @return String A formula representing this function tree.
     */
    public String format() {
        return format(0);
    }

    private String format(final int i) {
        final byte[] code = getCodeDigits();
        final int arity = code[i]; // == functions[i].arity()

        if (arity == 0) {
            return functions[i] == null ? "x" : functions[i].format(null);
        }
        else if (arity == 1) {
            return "(" + functions[i].format(new String[]{ format(i+1) }) + ")";
        }
        else {
            int k = i + 1;
            String[] names = new String[arity];
            for (int j = 0; j < arity; j++) {
                names[j] = format(k);
                k += subcodeLength(k);
            }
            return "(" + functions[i].format(names) + ")";
        }
    }

    /**
     * @see org.kares.math.frec.core.ReadsTree#mutateCode(MutationContext)
     */
    public void mutateCode(final MutationContext context) {
        final int old_len = length();
        super.mutateCode(context);
        final int new_len = length();
        final int pos = context.getIndex();
        final int mut_len = context.getLength();
        
        final Function[] newFunctions = new Function[new_len];
        for (int i=0; i<pos; i++) newFunctions[i] = functions[i];

        int index = pos + mut_len;
        if ( old_len < new_len ) {
            index -= new_len - old_len;
        } else {
            index += old_len - new_len;
        }
        int j = index;
        for (int i=pos+mut_len; i<new_len; i++) newFunctions[i] = functions[j++];
        setFunctions(newFunctions);
        randomFunctions(pos, pos + mut_len);
    }

    /**
     * Method that crosses two function trees at a random position.
     * The functions exchange their subtrees at the selected positions.
     * 
     * @see #crossCode(ReadsTree)
     */
    public FunctionTree[] crossCode(FunctionTree that) {
        return crossCode(that, 0, Integer.MAX_VALUE);
    }

    /**
     * Method that crosses two function trees at a random position.
     * The functions exchange their subtrees at the selected positions.
     * 
     * @see #crossCode(ReadsTree, int, int)
     */
    public FunctionTree[] crossCode(FunctionTree that, int min_len, int max_len) {
        CrossingContext context = randomCrossingContext(that, min_len, max_len);
        crossCode( context );
        return new FunctionTree[] {
            (FunctionTree) context.getChild1(), (FunctionTree) context.getChild2()
        };
    }

    /**
     * @see org.kares.math.frec.core.ReadsTree#crossCode(org.kares.math.frec.core.ReadsTree.CrossingContext)
     */
    public void crossCode(final CrossingContext context) {
        super.crossCode(context);

        final FunctionTree parent1 = (FunctionTree) context.getParent1(); // this
        final FunctionTree parent2 = (FunctionTree) context.getParent2(); // that
        final ReadsTree child1 = context.getChild1();
        final ReadsTree child2 = context.getChild2();
        final int s1 = context.getStartIndex1();
        final int e1 = context.getEndIndex1();
        final int s2 = context.getStartIndex2();
        final int e2 = context.getEndIndex2();

        final Function[] newFxs1 = new Function[child1.length()];

        int i;
        for (i=0; i<s1; i++) newFxs1[i] = parent1.functions[i];

        int j = i;
        for (; i<e2-s2+s1; i++) newFxs1[i] = parent2.functions[i-s1+s2];

        int dif = i; j = i;
        for (; i<newFxs1.length; i++) newFxs1[i] = parent1.functions[i-dif+e1];

        context.setChild1( new FunctionTree(child1.getCode(), newFxs1) );

        final Function[] newFxs2 = new Function[child2.length()];

        for (i=0; i<s2; i++) newFxs2[i] = parent2.functions[i];

        j = i;
        for (; i<e1-s1+s2; i++) newFxs2[i] = parent1.functions[i-s2+s1];

        dif = i; j = i;
        for (; i<newFxs2.length; i++) newFxs2[i] = parent2.functions[i-dif+e2];

        context.setChild2( new FunctionTree(child2.getCode(), newFxs2) );
    }

    // Object :

    /**
     * Provides extended equality test for objects of this class.
     * <p>
     * This method compares functions based on {@link FunctionTree#format()}.
     *
     * @see Object#equals(Object)
     */
    public boolean equals(Object other) {
        if (!super.equals(other)) return false;
        if (this.getClass() == other.getClass()) {
            return format().equals( ((FunctionTree) other).format() );
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return 11 * super.hashCode(); //return 17 * format().hashCode();
    }

    /**
     * @see Object#clone()
     */
    public Object clone() {
        FunctionTree clone = (FunctionTree) super.clone();
        clone.setFunctions((Function[]) functions.clone());
        return clone;
    }

    /**
     * For debugging purposes. 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("FunctionTree: ");
        str.append(format());
        return str.toString();
    }

}
