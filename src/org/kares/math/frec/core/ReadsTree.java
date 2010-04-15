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
import java.util.Random;

import org.kares.math.frec.util.RandomHelper;

/**
 * Instances of <code>ReadsTree</code> are an object representation of single 
 * rooted trees. This class provides the basic operations necessary for 
 * "genetic" operations on such trees.
 * The primary thing that characterizes instances of this class is Read's
 * linear code that is a code used for effectively code (tree) graphs as 
 * a sequence of numbers.
 * <p>
 * As an implementation detail the maximum degree of a node in a graph is 9.
 *
 * NOTE: Instances of this class are mutable and are not thread-safe !
 *
 * @see TreeGraph#forReadsCode(java.lang.String) 
 * @author kares
 */
public class ReadsTree implements java.io.Serializable, Cloneable {

    private static int maxRandomCodeLength = 10;

    /** Read's linear code as a string - sequence of 0-9 values. */
    private String code;

    /**
     * Creates a new tree with the given code.
     *
     * NOTE: The passed code is not validated !
     * 
     * @param code
     */
    public ReadsTree(final CharSequence code) {
        this.code = code.toString();
    }
    
    public static int getMaxRandomCodeLength() {
        return maxRandomCodeLength;
    }

    public static void setMaxRandomCodeLength(int length) {
        if (length < 2) {
            throw new IllegalArgumentException("length should be >= 2 got: " + length);
        }
        maxRandomCodeLength = length;
    }

    /**
     * Generates a random tree instance.
     * @return random tree
     */
    public static ReadsTree getRandomInstance() {
        return getRandomInstance(randomCodeLength());
    }

    /**
     * Generates a random tree instance.
     * @param length
     * @return random tree
     */
    public static ReadsTree getRandomInstance(int length) {
        return new ReadsTree(generateRandomCode(length));
    }

    /**
     * Return a random int - a valid Read's code length.
     * @see ReadsTree#getMaxRandomCodeLength()
     * @return random code length
     */
    protected static int randomCodeLength() {
        return 2 + RandomHelper.randomInt(maxRandomCodeLength - 2 + 1);
    }

    /**
     * Generates a random Read's code of the given length.
     *
     * @param len The length of the randomly generated tree code.
     * @return String representing a random tree code.
     */
    public static String generateRandomCode(int len) {
        if (len == 1) return "0";
        if (len == 2) return "10";
        int[] d = new int[len - 1];
        d[0] = len - 1;

        Random random = RandomHelper.newRandom();
        StringBuffer res = new StringBuffer(len);
        
        int rnd = 1 + random.nextInt(d[0]);
        if (rnd > 9) rnd = 9;
        res.append(rnd);

        for (int i = 1; i < len - 1; i++) {
            d[i] = d[i - 1] - rnd;
            if (d[i] == len - i - 1)
                rnd = 1 + random.nextInt(d[i]);
            else
                rnd = random.nextInt(d[i] + 1);
            if (rnd > 9) rnd = 5 + random.nextInt(5);
            res.append(rnd);
        }

        return res.append("0").toString();
    }

    /**
     * Method generates a random code of the given length.
     * @param min_len The minimal length of the randomly generated code.
     * @param max_len The maximal length of the randomly generated code.
     * @return String representing a random tree code.
     */
    public static String generateRandomCode(int min_len, int max_len) {
        int len = min_len + RandomHelper.randomInt(max_len - min_len + 1);
        return ReadsTree.generateRandomCode(len);
    }

    private transient byte[] codeDigits;

    /**
     * Returns the code representation of this tree as an array of numbers.
     * NOTE: The array should be treated as read-only !
     * @return code as decimal digits.
     */
    public byte[] getCodeDigits() {
        if (codeDigits == null) {
            final byte[] digits = new byte[code.length()];
            for (int i = 0; i < code.length(); i++) {
                digits[i] = (byte) Character.digit(code.charAt(i), 10);
            }
            this.codeDigits = digits;
        }
        return codeDigits;
    }

    /**
     * Returns the Read's code of this tree.
     * @return String Read's code of this tree.
     */
    public String getCode() {
        return this.code;
    }

    public String format() {
        return this.code;
    }

    /**
     * Set the code for this tree.
     * @param code the new code value
     */
    protected void setCode(final CharSequence code) {
        this.code = code.toString();
        this.codeDigits = null;
        //this.subcodeLength = null;
    }

    /**
     * @return The length of this tree.
     */
    public int length() {
        return this.code.length();
    }

    //private transient int[] subcodeLength;

    /**
     * Returns the length of a subtree in this tree.
     * @param pos The code position of the subtree.
     * @return The length of a subtree at the given position.
     */
    public int subcodeLength(int pos) {
        if (pos < 0) {
            throw new IndexOutOfBoundsException("pos < 0 : " + pos);
        }
        if (pos >= length()) {
            throw new IndexOutOfBoundsException("pos >= length : " + pos);
        }
        //if (subcodeLength == null) {
        //    subcodeLength = new int[code.length()];
        //}
        //int len = subcodeLength[pos];
        //if (len > 0) return len;
        int len = 1;
        final byte[] codeDigits = getCodeDigits();
        if (codeDigits[pos] == 0) return 1;
        while (true) {
            len++;
            int sum = 0;
            for (int i = pos; i < pos + len; i++) {
                sum += codeDigits[i];
                if (sum < i - pos) {
                    sum = 0;
                    break;
                }
            }
            if (sum == len - 1) return len;
        }
    }

    /**
     * Finds and returns a sub-code of this tree. 
     * @param pos The code position of the subtree.
     * @return Read's code of a subtree of this tree.
     */
    public String subcode(int pos) {
        final int len = subcodeLength(pos);
        return getCode().substring(pos, pos + len);
    }

    /**
     * Returns a subtree of this tree.
     * @param pos The code position of the subtree.
     * @return Sub-tree of this tree.
     * @see #subcode(int)
     */
    public ReadsTree subTree(int pos) {
        return new ReadsTree(subcode(pos));
    }

    /**
     * This method causes that this tree (code) will be randomly mutated at a
     * random position, the whole subtree at the specified position will be
     * replaced with a new (randomly generated) one.
     *
     * NOTE: The mutation modifies this tree object !
     *
     * @param mut_len Length of the added (mutation) subtree.
     * @return The random position where the mutation ocured.
     */
    public int mutateCode(int mut_len) {
        final int pos = 1 + RandomHelper.randomInt(length() - 1);
        mutateCode(new MutationContext(this, pos, mut_len));
        return pos;
    }

    /**
     * This method causes that this tree (code) will be randomly mutated at a
     * random position, the whole subtree at the specified position will be
     * replaced with a new (randomly generated) one.
     *
     * NOTE: The mutation modifies this tree object !
     *
     * @param min_len The minimal allowed length of the resulting tree.
     * @param max_len The maximal allowed length of the resulting tree.
     * @return The random position where the mutation ocured.
     */
    public int mutateCode(int min_len, int max_len) {
        final int pos = 1 + RandomHelper.randomInt(length() - 1);
        int mut_len = randomMutationLength(pos, min_len, max_len);
        mutateCode(new MutationContext(this, pos, mut_len));
        return pos;
    }

    /**
     * @param context
     * @see #mutateCode(int)
     * @see #mutateCode(int, int)
     * @see MutationContext
     */
    public void mutateCode(final MutationContext context) {
        final int pos = context.getIndex();
        final int len = context.getLength();
        //System.out.println("mutateCode pos = "+ pos +" len = " + len);
        setCode(generateMutatedCode(pos, len));
    }

    /**
     * Generated a mutated code for this tree.
     * @param pos
     * @param mut_len
     * @return Mutated code based on this tree's code.
     */
    protected String generateMutatedCode(int pos, int mut_len) {
        final int pos_len = subcodeLength(pos);
        return code.substring(0, pos) +
               ReadsTree.generateRandomCode(mut_len) +
               code.substring(pos + pos_len, code.length());
    }

    protected int randomMutationLength(int pos, int min_len, int max_len) {
        final int len = length();
        final int pos_len = subcodeLength(pos);
        //System.out.println("randomMutationLength pos = "+ pos +" len = "+ len +" pos_len = " + pos_len +
        //                   " min_len = "+ min_len +" max_len = " + max_len);
        int mut_min_len = min_len - (len - pos_len); // <= mut_len
        if ( mut_min_len < 1 ) mut_min_len = 1;
        int mut_max_len = max_len - (len - pos_len); // >= mut_len
        if ( mut_max_len <= 0 ) { //return -1;
            throw new IllegalStateException("could not decide mutation length");
        }
        return mut_min_len + RandomHelper.randomInt(mut_max_len);
    }

    /**
     * Crosses two trees objects by selecting a random position in 
     * both trees and then exchanges the subtrees among each other.
     *
     * NOTE: the original trees are not affected by the crossing !
     *
     * @param that The tree to be crossed with this tree.
     * @return The new child trees created as a result of crossing.
     */
    public ReadsTree[] crossCode(final ReadsTree that) {
        return crossCode(that, 0, Integer.MAX_VALUE);
    }

    /**
     * Crosses two trees objects by selecting a random position in 
     * both trees and then exchanges the subtrees among each other. 
     * Limits for the new that codes are provided as parameters.
     * 
     * @param that The tree to be crossed with this tree.
     * @param min_len The minimal required length of the newly created trees.
     * @param max_len The maximal required length of the newly created trees.
     * @return The new child trees created as a result of crossing.
     */
    public ReadsTree[] crossCode(ReadsTree that, int min_len, int max_len) {
        CrossingContext context = randomCrossingContext(that, min_len, max_len);
        crossCode( context );
        return new ReadsTree[] { context.getChild1(), context.getChild2() };
    }

    /**
     * @param context
     * @see #crossCode(ReadsTree)
     * @see #crossCode(ReadsTree, int, int)
     * @See CrossingContext
     */
    public void crossCode(final CrossingContext context) {
        final ReadsTree tree1 = context.parent1; // this
        final ReadsTree tree2 = context.parent2; // that
        int beg1 = context.startIndex1, end1 = context.endIndex1;
        int beg2 = context.startIndex2, end2 = context.endIndex2;

        /*
        final int len1 = this.length();
        final int len2 = that.length();
        if (len1 == 1 && len2 == 1) {
            this.setCrossPosition(0, 1);
            that.setCrossPosition(0, 1);
            return new ReadsTree[] { this, that };
        }
        */
        // code exchange:
        String res1 = tree1.code.substring(0, beg1) +
                      tree2.code.substring(beg2, end2) +
                      tree1.code.substring(end1, tree1.length());
        String res2 = tree2.code.substring(0, beg2) +
                      tree1.code.substring(beg1, end1) +
                      tree2.code.substring(end2, tree2.length());

        context.setChild1(new ReadsTree(res1));
        context.setChild2(new ReadsTree(res2));
    }

    /**
     * Selects random cross positions for this and the passed tree.
     * The selected position satisfies the given constraints meaning the crossing
     * applied at those positions will produce codes that are between the given 
     * length constraints.
     * 
     * @param that The tree to be crossed with this tree.
     * @param min_len The minimum code length constraint.
     * @param max_len The maximum code length constraint.
     * @throws IllegalStateException if such positions could not be selected
     */
    protected CrossingContext randomCrossingContext(final ReadsTree that, int min_len, int max_len) {
        final int len1 = this.length();
        final int len2 = that.length();
        int pos1 = 0, pos2 = 0, pos1_len = 1, pos2_len = 1;

        // try the "fast" way at first :
        if (len1 != 1) pos1 = 1 + RandomHelper.randomInt(len1 - 1);
        if (len2 != 1) pos2 = 1 + RandomHelper.randomInt(len2 - 1);
        pos1_len = this.subcodeLength(pos1);
        pos2_len = that.subcodeLength(pos2);

        if ( (len1 - pos1_len + pos2_len < min_len)
          || (len1 - pos1_len + pos2_len > max_len)
          || (len2 - pos2_len + pos1_len < min_len)
          || (len2 - pos2_len + pos1_len > max_len) ) {
            // no luck thus collect all valid posibilities :
            ArrayList validContexts = new ArrayList();
            for (int i1=1; i1<len1; i1++) {
                for (int i2=1; i1<len2; i2++) {
                    pos1 = i1; pos1_len = this.subcodeLength(pos1);
                    pos2 = i2; pos2_len = that.subcodeLength(pos2);
                    if ( (len1 - pos1_len + pos2_len >= min_len)
                      && (len1 - pos1_len + pos2_len <= max_len)
                      && (len2 - pos2_len + pos1_len >= min_len)
                      && (len2 - pos2_len + pos1_len <= max_len) ) {
                        validContexts.add(new CrossingContext(
                              this, pos1, pos1 + pos1_len,
                              that, pos2, pos2 + pos2_len)
                        );
                    }
                }
            }
            if (validContexts.isEmpty()) {
                throw new IllegalStateException("could not satisfy min-max length " +
                        "requirements for crossing context:" +
                        " this.length = " + len1 + " that.length = " + len2 +
                        " min length = " + min_len + " max length = " + max_len);
            }

            int index = RandomHelper.randomInt(validContexts.size());
            return (CrossingContext) validContexts.get(index);
        }
        else {
            return new CrossingContext(
                this, pos1, pos1 + pos1_len,
                that, pos2, pos2 + pos2_len
            );
        }

        //this.setCrossPosition(pos1, pos1 + pos1_len);
        //that.setCrossPosition(pos2, pos2 + pos2_len);
    }

    /**
     * A mutation context. Holds all information required for mutating a tree.
     * 
     * @author kares
     */
    protected static class MutationContext {

        final ReadsTree target;

        int index, length;

        MutationContext(ReadsTree target) {
            this.target = target;
        }

        public MutationContext(ReadsTree target, int index, int length) {
            this.target = target;
            this.index = index;
            this.length = length;
        }

        public ReadsTree getTarget() {
            return target;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

    }

    /**
     * A crossing context. Holds all information required for crossing trees.
     * 
     * @author kares
     */
    protected static class CrossingContext {

        final ReadsTree parent1;
        final ReadsTree parent2;

        int startIndex1, endIndex1;
        int startIndex2, endIndex2;

        // crossing outcome :
        private ReadsTree child1;
        private ReadsTree child2;

        CrossingContext(ReadsTree parent1, ReadsTree parent2) {
            this.parent1 = parent1;
            this.parent2 = parent2;
        }

        public CrossingContext(
                ReadsTree parent1, int startIndex1,
                ReadsTree parent2, int startIndex2) {
            this.parent1 = parent1;
            this.startIndex1 = startIndex1;
            this.endIndex1 = startIndex1 + parent1.subcodeLength(startIndex1);
            this.parent2 = parent2;
            this.startIndex2 = startIndex2;
            this.endIndex2 = startIndex2 + parent2.subcodeLength(startIndex2);
        }

        CrossingContext(
                ReadsTree parent1, int startIndex1, int endIndex1,
                ReadsTree parent2, int startIndex2, int endIndex2) {
            this.parent1 = parent1;
            this.startIndex1 = startIndex1;
            this.endIndex1 = endIndex1;
            this.parent2 = parent2;
            this.startIndex2 = startIndex2;
            this.endIndex2 = endIndex2;
        }

        public ReadsTree getParent1() {
            return parent1;
        }

        public ReadsTree getParent2() {
            return parent2;
        }

        public int getStartIndex1() {
            return startIndex1;
        }

        public void setStartIndex1(int startIndex1) {
            this.startIndex1 = startIndex1;
        }

        public int getEndIndex1() {
            return endIndex1;
        }

        public void setEndIndex1(int endIndex1) {
            this.endIndex1 = endIndex1;
        }

        public int getStartIndex2() {
            return startIndex2;
        }

        public void setStartIndex2(int startIndex2) {
            this.startIndex2 = startIndex2;
        }

        public int getEndIndex2() {
            return endIndex2;
        }

        public void setEndIndex2(int endIndex2) {
            this.endIndex2 = endIndex2;
        }

        public ReadsTree getChild1() {
            return child1;
        }

        public void setChild1(ReadsTree child1) {
            this.child1 = child1;
        }

        public ReadsTree getChild2() {
            return child2;
        }

        public void setChild2(ReadsTree child2) {
            this.child2 = child2;
        }

    }

    // Object :

    /**
     * @see Object#equals(java.lang.Object) 
     */
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() == this.getClass()) {
            ReadsTree that = (ReadsTree) other;
            return this.code.equals(that.code);
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return 17 * this.code.hashCode();
    }

    /**
     * @see Object#clone() 
     * @return clone of this tree
     */
    public Object clone() {
        ReadsTree clone;
        try {
            clone = (ReadsTree) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        return clone;
    }

    /**
     * For debugging purposes. 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ReadsTree: " + code + "";
    }

}
