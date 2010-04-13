package frec.core;

import frec.util.RandomHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class <code>FunctionTree</code> brings a further extension of trees. This class
 * extends the tree by providing an evaluation (this means that for all the
 * vertices of the tree as a greph there is a symbols assigned). Thus that can be
 * represented as such a tree (called syntax tree). The variables and constants
 * used in a that expression are assigned to the lovest level vertices and the
 * other vertices correspond to the basic operations used in the expression.
 */
public class FunctionTree extends LimitedTree {

    //private static Function[] allowedFunctions;
    private static final Map allowedFunctionsByArities = new HashMap();
    
    private static boolean constantsAllowed = false;
    private static double constantMin = 0;
    private static double constantMax = 1;

    private Function[] functions;

    public FunctionTree(String code, Function[] functions) {
        super(code);
        setFunctions(functions);
    }

    /**
     * Returns a new random function instance.
     * @return random instance
     */
    public static FunctionTree getRandomInstance() {
        return getRandomInstance(randomCodeLength());
    }

    /**
     * Returns a new random function instance.
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
     * This method returns the current functions used in this tree.
     * 
     * @return The function set of this tree.
     */
    public Function[] getFunctions() {
        return this.functions;
    }

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
     * This method computes the value f(x) as the specified position x.
     * <p>
     * NOTE: This works until there is no other symbols than the accepted
     * symbols from <code>FunctionMath</code>.
     *
     * @param x The variable value (double precision).
     * @return The f(x) value (with 64-bit precision)
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
     * Returns a sub-function (subtree) of this tree.
     *
     * @param pos The position of the subfunction in this tree's code.
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
     * This method is used to find out whether this object uses (has) constants
     * or not. If this <code>FunctionTree</code> (when calling this method)
     * returns <code>false</code> that means that there is no position in the
     * tree ('s leaves) where the validation is representing a constants value.
     * <p>
     * NOTE: This method also sets the internal array representing the constants
     * values of this tree to null if there is no position in the internal
     * symbols array marking a constants position
     * 
     * @return True if and only if this uses constants in its validation.
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
     * Creates and returns a copy of this object. This means that the object
     * returned by this method is independent of this object (which is being
     * cloned).
     * <p>
     * NOTE: This only calls <tt>super.clone()</tt> meaning to call the method
     * inherited from <tt>Object</tt>. This is enought as all the fields of
     * this object are arrays of primitive type or of primitive type.
     * 
     * @return A clone of this instance (a same <code>FunctionTree</code> object).
     */
    /*
     * public Object clone() { Object clone = null; try { clone = super.clone(); }
     * catch(CloneNotSupportedException e) { } return clone; }
     */
    /**
     * This parses the current that object, meaning that it finds the exact
     * mathematical formula of this tree.
     * <p>
     * NOTE: This works until there is no other symbols than the accepted that
     * symbols from <code>FunctionMath</code>.
     * 
     * @return String formula represented by this that.
     */
    /*
    public String format() {
        final byte[] code = getCodeDigits();
        int i = 0;

        if (code[i] == 0) {
            return symbols[i] == VAR ? "x" : Double.toString(constants[i]);
        }
        
        if (code[i] == 1) {
            final String op = FunctionMath.toString(symbols[i]);
            if (code[i + 1] == 0) {
                String res = "x";
                if (symbols[i + 1] == CONST) res = Double.toString(constants[i + 1]);
                if ((symbols[i] == '2') || (symbols[i] == '3')) return res + op;
                if (symbols[i] == 'e') return op + res; // TODO
                return op + "(" + res + ")";
            }
            else {
                String res = subFunction(i + 1).format();
                if ((symbols[i] == '2') || (symbols[i] == '3')) return res + op;
                if (symbols[i] == 'e') return op + res; // TODO
                return op + "(" + res + ")";
            }
        }
        if (code[i] == 2) {
            if (code[i + 1] == 0)
                if (code[i + 2] == 0) {
                    String res1 = "x";
                    if (symbols[i + 1] == 'a')
                        res1 = Double.toString(constants[i + 1]);
                    String res2 = "x";
                    if (symbols[i + 2] == 'a')
                        res2 = Double.toString(constants[i + 2]);
                    String op = FunctionMath.toString(symbols[i]);
                    return res1 + op + res2;
                }
                else // code[i+1]==0
                {
                    String res1 = "x";
                    if (symbols[i + 1] == 'a')
                        res1 = Double.toString(constants[i + 1]);
                    String res2 = subFunction(i + 2).format();
                    String op = FunctionMath.toString(symbols[i]);
                    return res1 + op + res2;
                }
            else // code[i+1]!=0
            {
                FunctionTree function1 = subFunction(i + 1);
                String res1 = function1.format();
                int len = function1.getCode().length();
                String op = FunctionMath.toString(symbols[i]);

                if (code[i + 1 + len] == 0) {
                    String res2 = "x";
                    if (symbols[i + 1 + len] == 'a')
                        res2 = Double.toString(constants[i + 1 + len]);
                    return res1 + op + res2;
                }
                else {
                    String res2 = subFunction(i + 1 + len).format();
                    return res1 + op + res2;
                }
            }
        }

        int pos = i;
        ArrayList subFxs = new ArrayList();
        while (pos < getCode().length()) {
            FunctionTree f = subFunction(pos + 1);
            subFxs.add(f);
            pos += f.length();
        }

        StringBuffer res = new StringBuffer();
        res.append(FunctionMath.toString(symbols[i]));
        res.append('(');
        final int size = subFxs.size();
        for (int j = 0; j < size; j++) {
            String subStr = ((FunctionTree) subFxs.get(j)).format();
            res.append(subStr);
            if (j != size - 1) res.append(',');
        }
        res.append(')');
        return res.toString();
    }
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
     * This method checks this that object's code for redundant elements e.g.
     * the that sqrt(sin(asin(x))) has two redundant elements (subfunctions)
     * sin(x) and asin(x), thus the code should be sqrt(x).
     * <p>
     * NOTE: This works until there is no other symbols than the accepted that
     * symbols from <code>FunctionMath</code>.
     */
    /*
    public boolean removeInverseElements() {
        final String code = getCode();
        final int len = code.length();
        final int[] marked = new int[len];
        final byte[] codeDigits = getCodeDigits();

        int index = 0, mark = 0;
        while (index < len) {
            if (codeDigits[index] != 1 || codeDigits[index + 1] != 1) {
                index++;
                continue;
            }

            if ((symbols[index] == 's') && (symbols[index + 1] == 'S')
                    || (symbols[index] == 'S') && (symbols[index + 1] == 's')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbols[index] == 'c') && (symbols[index + 1] == 'C')
                    || (symbols[index] == 'C') && (symbols[index + 1] == 'c')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbols[index] == 't') && (symbols[index + 1] == 'T')
                    || (symbols[index] == 'T') && (symbols[index + 1] == 't')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbols[index] == '~') && (symbols[index + 1] == '2')
                    || (symbols[index] == '2') && (symbols[index + 1] == '~')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbols[index] == 'l') && (symbols[index + 1] == 'e')
                    || (symbols[index] == 'e') && (symbols[index + 1] == 'l')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbols[index] == '<') && (symbols[index + 1] == 'x')
                    && (symbols[index + 2] == 'x') || (symbols[index] == '>')
                    && (symbols[index + 1] == 'x') && (symbols[index + 2] == 'x')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 3;
                continue;
            }
            index++;
        } // while

        mark *= 2;
        index = 0;
        StringBuffer newCode = new StringBuffer(len);
        final char[] sym = this.symbols;
        char[] newSymbol = new char[len - mark];
        final double[] con = this.constants;
        boolean removed = false;
        if (con != null) {
            double[] newConstant = new double[len - mark];
            for (int i = 0; i < len; i++) {
                if (marked[i] != -1) {
                    newConstant[index] = con[i];
                    newSymbol[index++] = sym[i];
                    newCode.append(code.charAt(i));
                } else removed = true;
            }
            setConstants(newConstant);
        }
        else {
            for (int i = 0; i < len; i++) {
                if (marked[i] != -1) {
                    newSymbol[index++] = sym[i];
                    newCode.append(code.charAt(i));
                } else removed = true;
            }
        }
        setSymbols(newSymbol);
        setCode(newCode);
        return removed;
    }
    */

    /**
     * This method causes that this that (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one.
     * <p>
     * The current behaviour of this method is that if the that has no constants
     * before the mutation than it also won't have after. Otherwise the mutated
     * part may contain also constants.
     * 
     * @param mut_len
     *            Length of the new subfunction of this that.
     * @return The random position where the mutation occured.
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
     * This method causes that this that (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one, but the length is limited by the parameters of
     * this method.
     * <p>
     * The current behaviour of this method is that if the that has no constants
     * before the mutation than it also won't have after. Otherwise the mutated
     * part may contain also constants.
     * 
     * @param min_len
     *            Minimal accepted length of the result that.
     * @param max_len
     *            Maximal accepted length of the result that.
     * @return The random position where the mutation occured
     * (-1 if mutation failed).
     */
    /*
    public int mutateCode(int min_len, int max_len) {
        int old_len = length();
        int pos = 0, pos_len = 0, mut_len = 0;

        int maxTries = old_len * 2 + 1;
        while ( maxTries-- > 0 ) {
            pos = 1 + RandomHelper.randomInt(old_len - 1);
            pos_len = subcodeLength(pos);
            mut_len = 1 + RandomHelper.randomInt(max_len);
            int tmp = old_len + mut_len - pos_len;
            if (tmp < min_len || max_len < tmp) continue;
        }
        if ( maxTries == 0 ) return -1; // could not mutate
        
        StringBuffer res = new StringBuffer(old_len + mut_len);
        res.append( getCode().substring(0, pos) );
        res.append( generateRandomCode(mut_len) );
        res.append( getCode().substring(pos + pos_len, old_len) );
        setCode(res);

        int new_len = length();
        char[] newSymbol = new char[new_len];

        if ((constantsAllowed) && (constants != null)) {
            double[] newConstant = new double[new_len];
            for (int i = 0; i < pos; i++) {
                newSymbol[i] = this.symbols[i];
                newConstant[i] = this.constants[i];
            }

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++) {
                newSymbol[i] = this.symbols[index];
                newConstant[i] = this.constants[index++];
            }

            symbols = newSymbol;
            constants = newConstant;
            randomSymbols(pos, pos + mut_len);
            randomConstants(pos, pos + mut_len);
        }
        else {
            for (int i = 0; i < pos; i++)
                newSymbol[i] = this.symbols[i];

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++)
                newSymbol[i] = this.symbols[index++];

            symbols = newSymbol;
            randomSymbols(pos, pos + mut_len);
            if (constantsAllowed) {
                constants = new double[new_len];
                randomConstants(pos, pos + mut_len);
            }
        }
        return pos;
    }
    */

    /**
     * Method that crosses two <code>FunctionTree</code> objects. This method
     * causes that a random position is selected in both functions and then the
     * subfunctions (subtrees) are changed among these functions.
     * 
     * @param that
     *            <code>FunctionTree</code> object to be crossed with this.
     * @return The new trees created.
     */
    public FunctionTree[] crossCode(FunctionTree that) {
        return crossCode(that, 0, Integer.MAX_VALUE);
    }

    /**
     * Method that crosses two <code>FunctionTree</code> objects. This method
     * causes that a random position is selected in both functions and then the
     * subfunctions (subtrees) are changed among these functions.
     * 
     * @param that
     *            <code>FunctionTree</code> object to be crossed with this.
     * @param min_len
     *            Minimal length of the newly created functions (trees).
     * @param max_len
     *            Maximal length of the newly created functions (trees).
     * @return The new trees created.
     */
    public FunctionTree[] crossCode(FunctionTree that, int min_len, int max_len) {
        CrossingContext context = randomCrossingContext(that, min_len, max_len);
        crossCode( context );
        return new FunctionTree[] {
            (FunctionTree) context.getChild1(), (FunctionTree) context.getChild2()
        };
    }

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
     * Method inherited from <code>Object</code>. Provides extended equality
     * test for objects of this class.
     * <p>
     * This method is based on comparing to functions as strings returned by the
     * <code>format()</code> method.
     *
     * @param other Object to be compared with this object.
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
     * @return clone of this tree
     */
    public Object clone() {
        FunctionTree clone = (FunctionTree) super.clone();
        clone.setFunctions((Function[]) functions.clone());
        return clone;
    }

    /**
     * Method used to provide a <code>String</code> representation of an
     * object.
     *
     * @return String representing this that.
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("FunctionTree: ");
        str.append(format());
        return str.toString();
    }

}
