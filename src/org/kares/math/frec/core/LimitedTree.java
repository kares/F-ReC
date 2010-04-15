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

import java.util.Random;

import org.kares.math.frec.util.RandomHelper;

/**
 * An extension of Read's trees with limits for the tree's code. 
 * These limits apply during the random code generation, they include 
 * a minimal and maximal value for the code elements (that is for the
 * degree of the nodes in the graph).
 * <p>
 * Generally the degree of a Read's tree nodes is not limited, but 
 * as an implementation detail the computation is currently only correct
 * when assuming that the code element = vertex degree in a (tree) graph 
 * is between [0,9]. This is sufficient for our purposes as the degree
 * will represent the arity of a function - and thus we will further 
 * probably further limit it when using {@link FunctionTree}s.
 * 
 * @author kares
 */
public class LimitedTree extends ReadsTree {
    
    private static int codeElementMin = 0; // downer limit [1..codeElementMax]
    private static int codeElementMax = 9; // upper limit [codeElementMin..9]

    /**
     * Creates a new limited tree with the given code.
     * 
     * NOTE: The passed code is not validated !
     * 
     * @param code Read's code of this tree.
     */
    public LimitedTree(final String code) {
        super(code);
    }

    /**
     * Generates a random tree instance.
     * @return random tree
     */
    public static LimitedTree getRandomInstance() {
        return getRandomInstance(randomCodeLength());
    }

    /**
     * Generates a random tree instance.
     * @param length
     * @return random tree
     */
    public static LimitedTree getRandomInstance(final int length) {
        return new LimitedTree(generateRandomCode(length));
    }

    /**
     * @see ReadsTree#generateRandomCode(int)
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
                    if (codeElementMax <= d[i]) {
                        rnd = 1 + random.nextInt(codeElementMax);
                    } else {
                        rnd = 1 + random.nextInt(d[i]);
                    }
                } else {
                    if (codeElementMax <= d[i]) {
                        rnd = random.nextInt(codeElementMax + 1);
                    } else {
                        rnd = random.nextInt(d[i] + 1);
                    }
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
                    if (codeElementMax <= d[i]) {
                        rnd = codeElementMin + random.nextInt(codeElementMax - codeElementMin + 1);
                    } else {
                        if (codeElementMin <= d[i]) {
                            rnd = codeElementMin + random.nextInt(d[i] - codeElementMin + 1);
                        }
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
                            if (random.nextInt(2) == 1) {
                                rnd = codeElementMin + random.nextInt(d[i] - codeElementMin + 1);
                            } 
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
     * @see ReadsTree#generateRandomCode(int, int)
     */
    public static String generateRandomCode(int min_len, int max_len) {
        int len = min_len + RandomHelper.randomInt(max_len - min_len + 1);
        return LimitedTree.generateRandomCode(len);
    }

    /**
     * A shorthand to set the limits at once.
     * 
     * Code elements are generally numbers from [0,9].
     * After setting the limits newly generated trees will have code
     * elements within [min_arity, max_arity].
     * 
     * @param min_arity Downer limit for the code elements of this tree.
     * @param max_arity Upper limit for the code elements of this tree.
     * @throws IllegalArgumentException
     * 
     * @see #setCodeElementMin(int)
     * @see #setCodeElementMax(int)
     */
    public static void setCodeElementLimits(int min_arity, int max_arity) {
        if (max_arity < min_arity)
            throw new IllegalArgumentException("max_arity < min_arity");
        setCodeElementMin(min_arity);
        setCodeElementMax(max_arity);
    }

    /**
     * Sets the minimum allowed degree of elements in generated codes.
     * @param min_arity
     */
    public static void setCodeElementMin(int min_arity) {
        if (min_arity < 0)
            throw new IllegalArgumentException("min_arity < 0");
        codeElementMin = min_arity;
    }

    /**
     * @return The code element minimum.
     * @see #setCodeElementMin(int)
     */
    public static int getCodeElementMin() {
        return codeElementMin;
    }

    /**
     * Sets the maximum allowed degree of elements in generated codes.
     * @param max_arity
     */
    public static void setCodeElementMax(int max_arity) {
        if (max_arity > 9)
            throw new IllegalArgumentException("max_arity > 9");
        codeElementMax = max_arity;
    }

    /**
     * @return The code element maximum.
     * @see #setCodeElementMax(int)
     */
    public static int getCodeElementMax() {
        return codeElementMax;
    }
    
    /**
     * Same as the inherited method but with the limits applied during generation.
     * @see org.kares.math.frec.core.ReadsTree#generateMutatedCode(int, int)
     */
    protected String generateMutatedCode(int pos, int mut_len) {
        final String code = getCode();
        final int pos_len = subcodeLength(pos);
        return code.substring(0, pos) +
               LimitedTree.generateRandomCode(mut_len) +
               code.substring(pos + pos_len, code.length());
    }

    /**
     * For debugging purposes. 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LimitedTree: " + getCode() + "";
    }
    
}
