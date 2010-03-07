package frec.core;

import frec.util.Generator;
import frec.util.GenMath;
import java.util.ArrayList;

/**
 * Class <code> Function </code> brings a further extension of trees. This class
 * extends the tree by providing an evaluation (this means that for all the
 * vertices of the tree as a greph there is a symbol assigned). Thus func can be
 * represented as such a tree (called syntax tree). The variables and constant
 * used in a func expression are assigned to the lovest level vertices and the
 * other vertices correspond to the basic operations used in the expression.
 */

public class Function extends LimitedTree {
    
    private static char[] allowed_symbol;

    private static byte[] allowed_arity;
    
    protected char[] symbol;

    protected float[] constant;

    private static boolean usingConstants = false;

    /**
     * Alocates a new <code>Function</code> , this code is generated randomly
     * its length will be a (random) natural number from [2,10], the evaluation
     * of this tree will be set randomly depending on the code.
     */

    public Function() {
        super();
        symbol = new char[code.length()];
        randomSymbols(0, code.length(), getCodeNo());
        constant = null;
        if (usingConstants) {
            constant = new float[code.length()];
            randomConstants(0, code.length(), getCodeNo());
            hasConstants();
        }
    }

    /**
     * Alocates a new <code>Function</code> , this code is generated randomly.
     * The length of the code will be as provided, the evaluation of this tree
     * will be set randomly depending on the code.
     * 
     * @param code_len
     *            The new length of this code.
     */

    public Function(int code_len) {
        super(code_len);
        symbol = new char[code.length()];
        randomSymbols(0, code.length(), getCodeNo());
        constant = null;
        if (usingConstants) {
            constant = new float[code.length()];
            randomConstants(0, code.length(), getCodeNo());
            hasConstants();
        }
    }

    /**
     * Alocates a new <code>Function</code> , this code is set as provided.
     * The evaluation of this tree will be set randomly depending on the code.
     * 
     * @param code
     *            The new code of this tree.
     */

    public Function(String code) {
        super(code);
        symbol = new char[code.length()];
        randomSymbols(0, code.length(), getCodeNo());
        constant = null;
        if (usingConstants) {
            constant = new float[code.length()];
            randomConstants(0, code.length(), getCodeNo());
            hasConstants();
        }
    }

    /**
     * Alocates a new <code>Function</code> , this code is set as provided.
     * The evaluation of this tree will be set as provided. Note that this means
     * that the positions where a variable is assigned, those positions may
     * change to randomly generated constants.
     * 
     * @param code
     *            The new code of this tree.
     * @param symbol
     *            The array of symbols representing the evaluation.
     * @param setConstants
     *            Indicates whether the vatiable positions may change to
     *            constants.
     */

    public Function(String code, char[] symbol, boolean setConstants) {
        super(code);
        this.symbol = symbol;
        constant = null;
        if (setConstants && usingConstants) {
            constant = new float[code.length()];
            randomConstants(0, code.length(), getCodeNo());
            hasConstants();
        }
    }

    /**
     * Alocates a new <code>Function</code> , this code is set as provided.
     * The evaluation of this tree will be set as provided including the values
     * of the constants in the evaluation.
     * 
     * @param code
     *            The new code of this tree.
     * @param symbol
     *            The array of symbols representing the evaluation.
     * @param constant
     *            The array of constants corresponding to the positions in the
     *            evaluaion.
     */

    public Function(String code, char[] symbol, float[] constant) {
        super(code);
        this.symbol = symbol;
        if (usingConstants) {
            this.constant = constant;
            hasConstants();
        }
    }

    private void randomSymbols(int pos1, int pos2, byte[] tree_code) {

        int len = allowed_arity.length;
        for (int i = pos1; i < pos2; i++) {
            if (tree_code[i] == 0) {
                this.symbol[i] = 'x'; // bude tam konst. alebo prem.
                continue;
            }

            while (true) {
                int rnd_index = Generator.randomInt(len);
                while (allowed_arity[rnd_index] != tree_code[i])
                    rnd_index = (++rnd_index + Generator.randomInt(16)) % len;
                this.symbol[i] = allowed_symbol[rnd_index];
                break;
            }
        }
    }

    private void randomConstants(int pos1, int pos2, byte[] tree_code) {
        int j = 0;
        int[] indexes = new int[tree_code.length - 1];
        for (int i = pos1; i < pos2; i++)
            if (tree_code[i] == 0) indexes[j++] = i;
        int const_count = 0;
        int limit = j / 2; // limit pre konstanty
        for (int i = 0; i < j; i++) {
            int setConstant = Generator.randomInt(101);
            if (setConstant > 50) if (const_count < limit) {
                const_count++;
                constant[indexes[i]] = Generator.randomFloat(); // <0,1>
                symbol[indexes[i]] = 'a'; // oznac. pre konstantu !!!
            }
            else
                break;
        }
    }

    private void setSymbols(int pos1, int pos2, char[] symbol) {
        for (int i = pos1; i < pos2; i++)
            this.symbol[i] = symbol[i];
    }

    /**
     * This method is used to set the symbol set which will be used to construct
     * objects of this class. The allowed symbol set consists of mathematical
     * symbols for operation defined on functions such as f(x)+g(x), sin(x), ... .
     * Each operation has also an arity which also needsd to be set.
     * 
     * @param symbol
     *            Array of symbols - the set of allowed symbols to be set.
     * @param arity
     *            Array of arity for the symbols.
     */

    public static void setAllowedSymbols(char[] symbol, byte[] arity) {
        allowed_symbol = symbol;
        allowed_arity = arity;
    }

    /**
     * This method returns the current symbol set used for this func.
     * 
     * @return The symbol set (validation) of this func's tree.
     */

    public char[] getSymbols() {
        return this.symbol;
    }

    /**
     * This method returns the current constant set used for this func. Note
     * that this may be also <code>null</code> as it is not necessary for the
     * func to have constant symbols in its validation.
     * 
     * @return The constant set (validation) of this func's leefs if any.
     */

    public float[] getConstants() {
        return this.constant;
    }

    /**
     * Used to get the func value of this func. This method computes the value
     * at the specified value of variable.
     * <p>
     * NOTE: This works until there is no other symbol than the accepted func
     * symbols from <code>util.GenMath</code>.
     * 
     * @param x
     *            The variable value (float precision).
     * @return The f(x) value (with 32-bit precision) where f is this func
     *         object.
     */

    public float value(float x) {
        byte[] code_no = this.getCodeNo();
        int i = 0;

        if (code_no[i] == 0) {
            if (symbol[i] == 'x')
                return x;
            else
                return constant[i];
        }
        if (code_no[i] == 1) {
            if (code_no[i + 1] == 0) {
                float value = x;
                if (symbol[i + 1] == 'a') value = constant[i + 1];
                return GenMath.getValue(symbol[i], value, 0);
            }
            else {
                Function func = (Function) this.subCode(i + 1);
                float value = func.value(x);
                return GenMath.getValue(symbol[i], value, 0);
            }
        }
        if (code_no[i] == 2) {
            if (code_no[i + 1] == 0)
                if (code_no[i + 2] == 0) {
                    float value1 = x;
                    if (symbol[i + 1] == 'a') value1 = constant[i + 1];
                    float value2 = x;
                    if (symbol[i + 2] == 'a') value2 = constant[i + 2];
                    return GenMath.getValue(symbol[i], value1, value2);
                }
                else // code_no[i+1]==0
                {
                    float value1 = x;
                    if (symbol[i + 1] == 'a') value1 = constant[i + 1];
                    Function function2 = (Function) this.subCode(i + 2);
                    float value2 = function2.value(x);
                    return GenMath.getValue(symbol[i], value1, value2);
                }
            else // code_no[i+1]!=0
            {
                Function function1 = (Function) this.subCode(i + 1);
                float value1 = function1.value(x);
                int len = function1.code.length();

                if (code_no[i + 1 + len] == 0) {
                    float value2 = x;
                    if (symbol[i + 1 + len] == 'a')
                        value2 = constant[i + 1 + len];
                    return GenMath.getValue(symbol[i], value1, value2);
                }
                else {
                    Function function2 = (Function) this.subCode(i + 1 + len);
                    float value2 = function2.value(x);
                    return GenMath.getValue(symbol[i], value1, value2);
                }
            }
        }

        Function func;
        int len = 0;
        float[] values = new float[code_no[i]];
        for (int j = 0; j < code_no[i]; j++) {
            func = (Function) this.subCode(i + 1 + len);
            len = func.code.length();
            values[j] = func.value(x);
        }

        return GenMath.getValue(symbol[i], values);
    }

    /**
     * Used to get the func value of this func. This method computes the value
     * at the specified value of variable.
     * <p>
     * NOTE: This works until there is no other symbol than the accepted func
     * symbols from <code>util.GenMath</code>.
     * 
     * @param x
     *            The variable value (double precision).
     * @return The f(x) value (with 64-bit precision) where f is this func
     *         object.
     */

    public double value(double x) {
        byte[] code_no = this.getCodeNo();
        int i = 0;

        if (code_no[i] == 0) {
            if (symbol[i] == 'x')
                return x;
            else
                return constant[i];
        }
        if (code_no[i] == 1) {
            if (code_no[i + 1] == 0) {
                double value = x;
                if (symbol[i + 1] == 'a') value = constant[i + 1];
                return GenMath.getValue(symbol[i], value, 0);
            }
            else {
                Function func = (Function) this.subCode(i + 1);
                double value = func.value(x);
                return GenMath.getValue(symbol[i], value, 0);
            }
        }
        if (code_no[i] == 2) {
            if (code_no[i + 1] == 0)
                if (code_no[i + 2] == 0) {
                    double value1 = x;
                    if (symbol[i + 1] == 'a') value1 = constant[i + 1];
                    double value2 = x;
                    if (symbol[i + 2] == 'a') value2 = constant[i + 2];
                    return GenMath.getValue(symbol[i], value1, value2);
                }
                else // code_no[i+1]==0
                {
                    double value1 = x;
                    if (symbol[i + 1] == 'a') value1 = constant[i + 1];
                    Function function2 = (Function) this.subCode(i + 2);
                    double value2 = function2.value(x);
                    return GenMath.getValue(symbol[i], value1, value2);
                }
            else // code_no[i+1]!=0
            {
                Function function1 = (Function) this.subCode(i + 1);
                double value1 = function1.value(x);
                int len = function1.code.length();

                if (code_no[i + 1 + len] == 0) {
                    double value2 = x;
                    if (symbol[i + 1 + len] == 'a')
                        value2 = constant[i + 1 + len];
                    return GenMath.getValue(symbol[i], value1, value2);
                }
                else {
                    Function function2 = (Function) this.subCode(i + 1 + len);
                    double value2 = function2.value(x);
                    return GenMath.getValue(symbol[i], value1, value2);
                }
            }
        }

        Function func;
        int len = 0;
        double[] values = new double[code_no[i]];
        for (int j = 0; j < code_no[i]; j++) {
            func = (Function) this.subCode(i + 1 + len);
            len = func.code.length();
            values[j] = func.value(x);
        }

        return GenMath.getValue(symbol[i], values);
    }

    /**
     * This method is used to find a subfunction (subtree) of this func. The
     * method is overriden and requires a <code>ReadsTree</code> return type.
     * 
     * @param pos
     *            The position of the subfunction in this func's code.
     * @return Subtfunction of this func as a <code>ReadsTree</code> object.
     */

    protected ReadsTree subCode(int pos) {
        String subCode = super.subCode(pos).code;
        int len = subCode.length();
        char[] subSymbol = new char[len];
        float[] subConstant = null;
        if (constant != null) {
            subConstant = new float[len];
            for (int i = 0; i < len; i++) {
                subSymbol[i] = this.symbol[i + pos];
                subConstant[i] = this.constant[i + pos];
            }
        }
        else
            for (int i = 0; i < len; i++)
                subSymbol[i] = this.symbol[i + pos];

        return new Function(subCode, subSymbol, subConstant);
    }

    /**
     * This method is used to find out whether this object uses (has) constant
     * or not. If this <code>Function</code> (when calling this method)
     * returns <code>false</code> that means that there is no position in the
     * tree ('s leaves) where the validation is representing a constant value.
     * <p>
     * NOTE: This method also sets the internal array representing the constant
     * values of this tree to null if there is no position in the internal
     * symbol array marking a constant position
     * 
     * @return True if and only if this uses constants in its validation.
     */

    public boolean hasConstants() {
        if (constant == null)
            return false;
        else {
            boolean flag = true;
            for (int i = 0; i < symbol.length; i++)
                if (symbol[i] == 'a') {
                    flag = false;
                    break;
                }
            if (flag) {
                constant = null;
                return false;
            }
        }
        return true;
    }

    /**
     * Method used to provide a <code>String</code> representation of an
     * object.
     * 
     * @return String representing this func.
     */

    public String toString() {
        String func = "Function: code = " + code + " ; symbol = ";
        for (int i = 0; i < symbol.length; i++) {
            if (symbol[i] == 'a') func += constant[i] + " ";
            if (symbol[i] == 'x')
                func += "x ";
            else
                func += GenMath.parseOperation(symbol[i]) + " ";
        }
        return func + ";";
    }

    /**
     * Method inherited from <code>Object</code>. Provides extended equality
     * test for objects of this class.
     * <p>
     * This method is based on comparing to functions as strings returned by the
     * <code>parse()</code> method.
     * 
     * @param o
     *            Object to be compared with this object.
     */

    public boolean equals(Object o) {
        if (o instanceof Function) {
            if (super.equals(o)) return true;

            String s = ((Function) o).parse();
            if (s.equals(this.parse())) return true;
        }
        return false;
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
     * @return A clone of this instance (a same <code>Function</code> object).
     */
    /*
     * public Object clone() { Object clone = null; try { clone = super.clone(); }
     * catch(CloneNotSupportedException e) { } return clone; }
     */
    /**
     * This parses the current func object, meaning that it finds the exact
     * mathematical formula of this func. This is needed for further parsing
     * when the func is displayed.
     * <p>
     * NOTE: This works until there is no other symbol than the accepted func
     * symbols from <code>util.GenMath</code>.
     * 
     * @return String formula represented by this func.
     */

    public String parse() {
        byte[] code_no = this.getCodeNo();
        int i = 0;

        if (code_no[i] == 0) {
            if (symbol[i] == 'x')
                return "x";
            else
                return Float.toString(constant[i]);
        }
        if (code_no[i] == 1) {
            String op = GenMath.parseOperation(symbol[i]);
            if (code_no[i + 1] == 0) {
                String res = "x";
                if (symbol[i + 1] == 'a')
                    res = Float.toString(constant[i + 1]);
                if ((symbol[i] == '2') || (symbol[i] == '3')) return res + op;
                if (symbol[i] == 'e') return op + res;
                return op + "(" + res + ")";
            }
            else {
                Function func = (Function) this.subCode(i + 1);
                String res = func.parse();
                if ((symbol[i] == '2') || (symbol[i] == '3')) return res + op;
                if (symbol[i] == 'e') return op + res;
                return op + "(" + res + ")";
            }
        }
        if (code_no[i] == 2) {
            if (code_no[i + 1] == 0)
                if (code_no[i + 2] == 0) {
                    String res1 = "x";
                    if (symbol[i + 1] == 'a')
                        res1 = Float.toString(constant[i + 1]);
                    String res2 = "x";
                    if (symbol[i + 2] == 'a')
                        res2 = Float.toString(constant[i + 2]);
                    String op = GenMath.parseOperation(symbol[i]);
                    return res1 + op + res2;
                }
                else // code_no[i+1]==0
                {
                    String res1 = "x";
                    if (symbol[i + 1] == 'a')
                        res1 = Float.toString(constant[i + 1]);
                    Function function2 = (Function) this.subCode(i + 2);
                    String res2 = function2.parse();
                    String op = GenMath.parseOperation(symbol[i]);
                    return res1 + op + res2;
                }
            else // code_no[i+1]!=0
            {
                Function function1 = (Function) this.subCode(i + 1);
                String res1 = function1.parse();
                int len = function1.code.length();
                String op = GenMath.parseOperation(symbol[i]);

                if (code_no[i + 1 + len] == 0) {
                    String res2 = "x";
                    if (symbol[i + 1 + len] == 'a')
                        res2 = Float.toString(constant[i + 1 + len]);
                    return res1 + op + res2;
                }
                else {
                    Function function2 = (Function) this.subCode(i + 1 + len);
                    String res2 = function2.parse();
                    return res1 + op + res2;
                }
            }
        }

        int pos = i;
        ArrayList list = new ArrayList(5);
        while (pos < code.length()) {
            Function f = (Function) this.subCode(pos + 1);
            list.add(f);
            pos += f.getCodeNo().length;
        }

        String res = GenMath.parseOperation(symbol[i]);
        res += "(";
        int size = list.size();
        for (int j = 0; j < size; j++) {
            res += ((Function) list.get(j)).parse();
            if (j != size - 1) res += ",";
        }
        res += ")";

        return res;
    }

    /**
     * This method checks this func object's code for redundant elements e.g.
     * the func sqrt(sin(asin(x))) has two redundant elements (subfunctions)
     * sin(x) and asin(x), thus the code should be sqrt(x).
     * <p>
     * NOTE: This works until there is no other symbol than the accepted func
     * symbols from <code>util.GenMath</code>.
     */

    public void check() {
        int index = 0;
        int mark = 0;
        int len = code.length();
        int[] marked = new int[len];
        byte[] code_no = this.getCodeNo();
        while (index < len) {
            if (code_no[index] != 1 || code_no[index + 1] != 1) {
                index++;
                continue;
            }

            if ((symbol[index] == 's') && (symbol[index + 1] == 'S')
                    || (symbol[index] == 'S') && (symbol[index + 1] == 's')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbol[index] == 'c') && (symbol[index + 1] == 'C')
                    || (symbol[index] == 'C') && (symbol[index + 1] == 'c')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbol[index] == 't') && (symbol[index + 1] == 'T')
                    || (symbol[index] == 'T') && (symbol[index + 1] == 't')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbol[index] == '~') && (symbol[index + 1] == '2')
                    || (symbol[index] == '2') && (symbol[index + 1] == '~')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbol[index] == 'l') && (symbol[index + 1] == 'e')
                    || (symbol[index] == 'e') && (symbol[index + 1] == 'l')) {
                marked[index] = -1;
                marked[index + 1] = -1;
                mark++;
                index += 2;
                continue;
            }
            if ((symbol[index] == '<') && (symbol[index + 1] == 'x')
                    && (symbol[index + 2] == 'x') || (symbol[index] == '>')
                    && (symbol[index + 1] == 'x') && (symbol[index + 2] == 'x')) {
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
        String cod = new String(this.code);
        code = "";
        char[] sym = new char[len];
        System.arraycopy(symbol, 0, sym, 0, len);
        symbol = new char[len - mark];
        if (constant != null) {
            float[] con = new float[len];
            System.arraycopy(constant, 0, con, 0, len);
            constant = new float[len - mark];
            for (int i = 0; i < len; i++)
                if (marked[i] != -1) {
                    symbol[index] = sym[i];
                    constant[index] = con[i];
                    code += cod.charAt(i);
                    index++;
                }
        }
        else
            for (int i = 0; i < len; i++)
                if (marked[i] != -1) {
                    symbol[index++] = sym[i];
                    code += cod.charAt(i);
                }

        this.setCodeNo();
    }

    /**
     * This method causes that this func (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one.
     * <p>
     * The current behaviour of this method is that if the func has no constants
     * before the mutation than it also won't have after. Otherwise the mutated
     * part may contain also constants.
     * 
     * @param mut_len
     *            Length of the new subfunction of this func.
     * @return The random position where the mutation occured.
     */

    public int mutateCode(int mut_len) {
        int old_len = code.length();

        int pos = super.mutateCode(mut_len);
        int new_len = code.length();
        char[] new_symbol = new char[new_len];

        if ((usingConstants) && (constant != null)) {
            float[] new_constant = new float[new_len];
            for (int i = 0; i < pos; i++) {
                new_symbol[i] = this.symbol[i];
                new_constant[i] = this.constant[i];
            }

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++) {
                new_symbol[i] = this.symbol[index];
                new_constant[i] = this.constant[index];
                index++;
            }

            symbol = new_symbol;
            constant = new_constant;
            randomSymbols(pos, pos + mut_len, getCodeNo());
            randomConstants(pos, pos + mut_len, getCodeNo());
            hasConstants();
        }
        else {
            for (int i = 0; i < pos; i++)
                new_symbol[i] = this.symbol[i];

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++)
                new_symbol[i] = this.symbol[index++];

            symbol = new_symbol;
            randomSymbols(pos, pos + mut_len, getCodeNo());
            if (usingConstants) {
                constant = new float[new_len];
                randomConstants(pos, pos + mut_len, getCodeNo());
                hasConstants();
            }
        }

        return pos;
    }

    /**
     * This method causes that this func (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one, but the length is limited by the parameters of
     * this method.
     * <p>
     * The current behaviour of this method is that if the func has no constants
     * before the mutation than it also won't have after. Otherwise the mutated
     * part may contain also constants.
     * 
     * @param min_len
     *            Minimal accepted length of the result func.
     * @param max_len
     *            Maximal accepted length of the result func.
     * @return The random position where the mutation occured.
     */

    public int mutateCode(int min_len, int max_len) {
        int old_len = code.length();
        int pos = 1 + Generator.randomInt(old_len - 1);
        int pos_len = subcodeLength(pos);
        int mut_len = 1 + Generator.randomInt(max_len);
        int tmp = old_len + mut_len - pos_len;
        while ((tmp > max_len) || (tmp < min_len)) {
            mut_len = 1 + Generator.randomInt(max_len);
            tmp = old_len + mut_len - pos_len;
        }
        String res = code.substring(0, pos);
        res += generateRandomCode(mut_len);
        res += code.substring(pos + pos_len, old_len);
        code = res;
        setCodeNo();

        int new_len = code.length();
        char[] new_symbol = new char[new_len];

        if ((usingConstants) && (constant != null)) {
            float[] new_constant = new float[new_len];
            for (int i = 0; i < pos; i++) {
                new_symbol[i] = this.symbol[i];
                new_constant[i] = this.constant[i];
            }

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++) {
                new_symbol[i] = this.symbol[index];
                new_constant[i] = this.constant[index];
                index++;
            }

            symbol = new_symbol;
            constant = new_constant;
            randomSymbols(pos, pos + mut_len, getCodeNo());
            randomConstants(pos, pos + mut_len, getCodeNo());
            hasConstants();
        }
        else {
            for (int i = 0; i < pos; i++)
                new_symbol[i] = this.symbol[i];

            int index = pos + mut_len;
            if (old_len < new_len)
                index -= new_len - old_len;
            else
                index += old_len - new_len;

            for (int i = pos + mut_len; i < new_len; i++)
                new_symbol[i] = this.symbol[index++];

            symbol = new_symbol;
            randomSymbols(pos, pos + mut_len, getCodeNo());
            if (usingConstants) {
                constant = new float[new_len];
                randomConstants(pos, pos + mut_len, getCodeNo());
                hasConstants();
            }
        }

        return pos;
    }

    /**
     * Method that crosses two <code>Function</code> objects. This method
     * causes that a random position is selected in both functions and then the
     * subfunctions (subtrees) are changed among these functions.
     * 
     * @param func
     *            <code>Function</code> object to be crossed with this.
     * @return The new trees created.
     */

    public Function[] crossCode(Function func) {
        char[] new_symbol;
        float[] new_constant;
        int beg0, beg1, end0, end1, dif, index;

        ReadsTree[] crossed = super.crossCode(func);
        if (crossed[1] == func) return new Function[] { this, func };

        beg0 = this.crossPosBeg;
        end0 = this.crossPosEnd;
        beg1 = func.crossPosBeg;
        end1 = func.crossPosEnd;

        new_symbol = new char[crossed[0].code.length()];
        new_constant = new float[crossed[0].code.length()];

        if (this.constant != null) {
            for (index = 0; index < beg0; index++) {
                new_symbol[index] = this.symbol[index];
                new_constant[index] = this.constant[index];
            }
        }
        else {
            for (index = 0; index < beg0; index++)
                new_symbol[index] = this.symbol[index];
        }

        if (func.constant != null) {
            for (; index < end1 - beg1 + beg0; index++) {
                int i = index - beg0 + beg1;
                new_symbol[index] = func.symbol[i];
                new_constant[index] = func.constant[i];
            }
        }
        else {
            for (; index < end1 - beg1 + beg0; index++)
                new_symbol[index] = func.symbol[index - beg0 + beg1];
        }

        dif = index;

        if (this.constant != null) {
            for (; index < new_symbol.length; index++) {
                int i = index - dif + end0;
                new_symbol[index] = this.symbol[i];
                new_constant[index] = this.constant[i];
            }
        }
        else {
            for (; index < new_symbol.length; index++)
                new_symbol[index] = this.symbol[index - dif + end0];
        }

        Function[] result = new Function[2];

        boolean hasConstants = false;
        for (int i = 0; i < new_symbol.length; i++)
            if (new_symbol[i] == 'a') {
                hasConstants = true;
                break;
            }
        if (!hasConstants)
            result[0] = new Function(crossed[0].code, new_symbol, false);
        else
            result[0] = new Function(crossed[0].code, new_symbol, new_constant);

        new_symbol = new char[crossed[1].code.length()];
        new_constant = new float[crossed[1].code.length()];

        if (func.constant != null) {
            for (index = 0; index < beg1; index++) {
                new_symbol[index] = func.symbol[index];
                new_constant[index] = func.constant[index];
            }
        }
        else {
            for (index = 0; index < beg1; index++)
                new_symbol[index] = func.symbol[index];
        }

        if (this.constant != null) {
            for (; index < end0 - beg0 + beg1; index++) {
                int i = index - beg1 + beg0;
                new_symbol[index] = this.symbol[i];
                new_constant[index] = this.constant[i];
            }
        }
        else {
            for (; index < end0 - beg0 + beg1; index++)
                new_symbol[index] = this.symbol[index - beg1 + beg0];
        }

        dif = index;

        if (func.constant != null) {
            for (; index < new_symbol.length; index++) {
                int i = index - dif + end1;
                new_symbol[index] = func.symbol[i];
                new_constant[index] = func.constant[i];
            }
        }
        else {
            for (; index < new_symbol.length; index++)
                new_symbol[index] = func.symbol[index - dif + end1];
        }

        hasConstants = false;
        for (int i = 0; i < new_symbol.length; i++)
            if (new_symbol[i] == 'a') {
                hasConstants = true;
                break;
            }
        if (!hasConstants)
            result[1] = new Function(crossed[1].code, new_symbol, false);
        else
            result[1] = new Function(crossed[1].code, new_symbol, new_constant);

        return result;
    }

    /**
     * Method that crosses two <code>Function</code> objects. This method
     * causes that a random position is selected in both functions and then the
     * subfunctions (subtrees) are changed among these functions.
     * 
     * @param func
     *            <code>Function</code> object to be crossed with this.
     * @param min_len
     *            Minimal length of the newly created functions (trees).
     * @param max_len
     *            Maximal length of the newly created functions (trees).
     * @return The new trees created.
     */

    public Function[] crossCode(Function func, int min_len, int max_len) {
        char[] new_symbol;
        float[] new_constant;
        int beg0, beg1, end0, end1, dif, index = 0;

        ReadsTree[] crossed = super.crossCode(func, min_len, max_len);
        if (crossed[1] == func) return new Function[] { this, func };

        beg0 = this.crossPosBeg;
        end0 = this.crossPosEnd;
        beg1 = func.crossPosBeg;
        end1 = func.crossPosEnd;

        new_symbol = new char[crossed[0].code.length()];
        new_constant = new float[crossed[0].code.length()];

        if (this.constant != null) {
            for (index = 0; index < beg0; index++) {
                new_symbol[index] = this.symbol[index];
                new_constant[index] = this.constant[index];
            }
        }
        else {
            for (index = 0; index < beg0; index++)
                new_symbol[index] = this.symbol[index];
        }

        if (func.constant != null) {
            for (; index < end1 - beg1 + beg0; index++) {
                int i = index - beg0 + beg1;
                new_symbol[index] = func.symbol[i];
                new_constant[index] = func.constant[i];
            }
        }
        else {
            for (; index < end1 - beg1 + beg0; index++)
                new_symbol[index] = func.symbol[index - beg0 + beg1];
        }

        dif = index;

        if (this.constant != null) {
            for (; index < new_symbol.length; index++) {
                int i = index - dif + end0;
                new_symbol[index] = this.symbol[i];
                new_constant[index] = this.constant[i];
            }
        }
        else {
            for (; index < new_symbol.length; index++)
                new_symbol[index] = this.symbol[index - dif + end0];
        }

        Function[] result = new Function[2];

        boolean hasConstants = false;
        for (int i = 0; i < new_symbol.length; i++)
            if (new_symbol[i] == 'a') {
                hasConstants = true;
                break;
            }
        if (!hasConstants)
            result[0] = new Function(crossed[0].code, new_symbol, false);
        else
            result[0] = new Function(crossed[0].code, new_symbol, new_constant);

        new_symbol = new char[crossed[1].code.length()];
        new_constant = new float[crossed[1].code.length()];

        if (func.constant != null) {
            for (index = 0; index < beg1; index++) {
                new_symbol[index] = func.symbol[index];
                new_constant[index] = func.constant[index];
            }
        }
        else {
            for (index = 0; index < beg1; index++)
                new_symbol[index] = func.symbol[index];
        }

        if (this.constant != null) {
            for (; index < end0 - beg0 + beg1; index++) {
                int i = index - beg1 + beg0;
                new_symbol[index] = this.symbol[i];
                new_constant[index] = this.constant[i];
            }
        }
        else {
            for (; index < end0 - beg0 + beg1; index++)
                new_symbol[index] = this.symbol[index - beg1 + beg0];
        }

        dif = index;

        if (func.constant != null) {
            for (; index < new_symbol.length; index++) {
                int i = index - dif + end1;
                new_symbol[index] = func.symbol[i];
                new_constant[index] = func.constant[i];
            }
        }
        else {
            for (; index < new_symbol.length; index++)
                new_symbol[index] = func.symbol[index - dif + end1];
        }

        hasConstants = false;
        for (int i = 0; i < new_symbol.length; i++)
            if (new_symbol[i] == 'a') {
                hasConstants = true;
                break;
            }
        if (!hasConstants)
            result[1] = new Function(crossed[1].code, new_symbol, false);
        else
            result[1] = new Function(crossed[1].code, new_symbol, new_constant);

        return result;
    }

}
