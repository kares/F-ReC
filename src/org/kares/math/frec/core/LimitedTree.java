package org.kares.math.frec.core;

import java.util.Random;

import org.kares.math.frec.util.RandomHelper;

/**
 * <code>LimitedTree</code> is an extension of Read's trees with
 * limits for the tree's code. These limits apply during the random
 * code generation, they include a minimal and maximal value for the
 * code elements.
 * <p>
 * NOTE: 
 * Generaly the code elements of a <code>ReadsTree<code> are not 
 * limited, but the computation is correct only when assuming that
 * the code element = vertex degree in a (tree) graph is in [0,9].
 */
@SuppressWarnings("serial")
public class LimitedTree extends ReadsTree {
    
    private static int codeElementMin = 0; // downer limit [1..codeElementMax]
    private static int codeElementMax = 9; // upper limit [codeElementMin..9]

    /**
     * Alocates a new <code>LimitedTree</code> , this code is set from the
     * value provided.
     * 
     * @param code
     *            The new code of this tree.
     */
    public LimitedTree(final String code) {
        super(code);
    }

    /**
     * This method is used to set the limits of this <code>LimitedTree</code>.
     * The code elements are generaly numbers from [0,9]. This methos sets the
     * limits thus the elements will be from [min_arity, max_arity].
     * 
     * @param min_arity
     *            Downer limit for the code elements of this tree.
     * @param max_arity
     *            Upper limit for the code elements of this tree.
     * @throws IllegalArgumentException
     *             if <code> min_arity > max_arity </code> or
     *             <code> min_arity < 0 </code> or <code> max_arity > 9 </code>
     */

    public static void setCodeElementLimits(int min_arity, int max_arity) {
        if (max_arity < min_arity)
            throw new IllegalArgumentException("max_arity < min_arity");
        setCodeElementMin(min_arity);
        setCodeElementMax(max_arity);
    }

    public static void setCodeElementMin(int min_arity) {
        if (min_arity < 0)
            throw new IllegalArgumentException("min_arity < 0");
        codeElementMin = min_arity;
    }

    public static int getCodeElementMin() {
        return codeElementMin;
    }

    public static void setCodeElementMax(int max_arity) {
        if (max_arity > 9)
            throw new IllegalArgumentException("max_arity > 9");
        codeElementMax = max_arity;
    }

    public static int getCodeElementMax() {
        return codeElementMax;
    }

    /**
     * Returns a new instance with the code generated randomly with a
     * random length between [2, maxRandomCodeLength].
     * @return random instance
     */
    public static LimitedTree getRandomInstance() {
        return getRandomInstance(randomCodeLength());
    }

    /**
     * Returns a new instance with the code generated randomly with
     * a given length.
     * @param length
     * @return random instance
     */
    public static LimitedTree getRandomInstance(final int length) {
        return new LimitedTree(generateRandomCode(length));
    }

    /**
     * Generates a random <code>LimitedTree</code> code of a specified length.
     * 
     * @param len The length of the randomly generated tree code.
     * @return A string representing the (limited) tree's code.
     */
    public static String generateRandomCode(int len) {
        if (len == 1) return "0";
        if (len == 2) return "10";
        if (codeElementMax == 9 && codeElementMin == 0) {
            return ReadsTree.generateRandomCode(len);
        }

        int[] d = new int[len - 1];
        d[0] = len - 1;
        if (codeElementMax > d[0]) codeElementMax = d[0];
        StringBuffer res = new StringBuffer(len);
        Random random = RandomHelper.newRandom();
        int rnd = 1;

        if (codeElementMin <= 0) {
            if (codeElementMax > 1) rnd = random.nextInt(codeElementMax) + 1;
            res.append(rnd);
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1) {
                    if (codeElementMax <= d[i])
                        rnd = 1 + random.nextInt(codeElementMax);
                    else
                        rnd = 1 + random.nextInt(d[i]);
                } else {
                    if (codeElementMax <= d[i])
                        rnd = random.nextInt(codeElementMax + 1);
                    else
                        rnd = random.nextInt(d[i] + 1);
                }
                res.append(rnd);
            }
        }
        else { // codeElementMin > 0
            if (codeElementMax > 1) rnd = random.nextInt(codeElementMax) + 1;
            res.append(rnd);
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1) {
                    if (codeElementMax <= d[i])
                        rnd = codeElementMin + random.nextInt(codeElementMax - codeElementMin + 1);
                    else {
                        if (codeElementMin <= d[i])
                            rnd = codeElementMin + random.nextInt(d[i] - codeElementMin + 1);
                        else rnd = 0;
                    }
                }
                else {
                    if (codeElementMax <= d[i]) {
                        if (random.nextInt(2) == 1)
                            rnd = codeElementMin + random.nextInt(codeElementMax - codeElementMin + 1);
                        else rnd = 0;
                    } else {
                        if (codeElementMin <= d[i]) {
                            if (random.nextInt(2) == 1)
                                rnd = codeElementMin + random.nextInt(d[i] - codeElementMin + 1);
                            else rnd = 0;
                        }
                        else rnd = 0;
                    }
                }
                res.append(rnd);
            }
        }

        return res.append("0").toString();
    }

    /**
     * Method provides a random <code>LimitedTree</code> code generation of a
     * limited length.
     * <p>
     * This is mainly used by constructors.
     * 
     * @param min_len
     *            The minimal length of the randomly generated tree code.
     * @param max_len
     *            The maximal length of the randomly generated tree code.
     * @return A string representing the code.
     */
    public static String generateRandomCode(int min_len, int max_len) {
        int len = min_len + RandomHelper.randomInt(max_len - min_len + 1);
        return LimitedTree.generateRandomCode(len);
    }

    /**
     * Method provides a random <code>LimitedTree</code> code generation of
     * the specified length.
     * <p>
     * This is provided for generating trees with the maximal tree element limit
     * greater than 9.
     * <p>
     * NOTE: This package is not optimised for such a trees.
     * 
     * @param len
     *            The length of the randomly generated tree code.
     * @param codeElementMin
     *            The minimal code element limit of the randomly generated tree
     *            code.
     * @param codeElementMax
     *            The maximal code element limit of the randomly generated tree
     *            code.
     * @return An array of integers representing the code.
     * @throws IllegalArgumentException
     *             if <code> codeElementMin > codeElementMax </code> or <code> codeElementMin < 0 </code> or
     *             <code> codeElementMax < 1 </code>
     */

    public static int[] generateRandomCode(int len, int min, int max)
            throws IllegalArgumentException {
        if (max < min) throw new IllegalArgumentException("max < min");
        if (max < 1) throw new IllegalArgumentException("max < 1");
        if (min < 0) throw new IllegalArgumentException("min < 0");

        if (len == 1) return new int[] { 0 };

        int[] d = new int[len - 1];
        d[0] = len - 1;
        if (max > d[0]) max = d[0];
        int[] res = new int[len];
        int index = 0;
        Random random = RandomHelper.newRandom();
        int rnd = 1;

        if (min <= 0) {
            if (max > 1) rnd = random.nextInt(max) + 1;
            res[index++] = rnd;
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = 1 + random.nextInt(max);
                    else
                        rnd = 1 + random.nextInt(d[i]);
                else
                    if (max <= d[i])
                        rnd = random.nextInt(max + 1);
                    else
                        rnd = random.nextInt(d[i] + 1);
                res[index++] = rnd;
            }
        }
        else { // codeElementMin > 0
            if (max > 1) rnd = random.nextInt(max) + 1;
            res[index++] = rnd;
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = min + random.nextInt(max - min + 1);
                    else
                        if (min <= d[i])
                            rnd = min + random.nextInt(d[i] - min + 1);
                        else
                            rnd = 0;
                else
                    if (max <= d[i]) {
                        int zero = random.nextInt(2);
                        if (zero == 1)
                            rnd = min + random.nextInt(max - min + 1);
                        else
                            rnd = 0;
                    }
                    else
                        if (min <= d[i]) {
                            int zero = random.nextInt(2);
                            if (zero == 1)
                                rnd = min + random.nextInt(d[i] - min + 1);
                            else
                                rnd = 0;
                        }
                        else
                            rnd = 0;

                res[index++] = rnd;
            }

        }

        res[index++] = 0;

        return res;
    }

    protected String generateMutatedCode(int pos, int mut_len) {
        final String code = getCode();
        final int pos_len = subcodeLength(pos);
        return code.substring(0, pos) +
               LimitedTree.generateRandomCode(mut_len) +
               code.substring(pos + pos_len, code.length());
    }

    /**
     * @return String representation for debugging purposes
     */
    public String toString() {
        return "LimitedTree: " + getCode() + "";
    }

    /*
    public static void main(String[] args) {
        LimitedTree.setCodeElementLimits(0, 2);
        for (int i = 0; i < 10; i++)
            System.out.println(LimitedTree.generateRandomCode(3));
        for (int i = 0; i < 10; i++)
            System.out.println(LimitedTree.generateRandomCode(4));
        for (int i = 0; i < 10; i++)
            System.out.println(LimitedTree.generateRandomCode(6));
        for (int i = 0; i < 10; i++)
            System.out.println(LimitedTree.generateRandomCode(2, 8));
        for (int i = 0; i < 10; i++)
            System.out.println(LimitedTree.generateRandomCode(4, 10));
    }
    */
    
}
