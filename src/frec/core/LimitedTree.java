package frec.core;

import frec.util.Generator;

/**
 * Class <code> LimitedTree </code> is also an object representation of trees
 * with limits for the <code> code </code>. These limis apply during the random
 * code generation operation, they include a minimale and maximal value for the
 * code elements. Thus some methods from the base class are overriden.
 * <p>
 * NOTE: Generaly the code elements of a
 * <code>ReadsTree<code> object are not limited
 * but the computation is correct only when assuming that the code element = vertex degree
 * in a graph (tree) is limited between [0,9].
 */

class LimitedTree extends ReadsTree {
    
    private static int min = 0; // downer limit [1..max] 0=1 = no limit

    private static int max = 9; // upper limit [1..9] .. v code

    /**
     * Alocates a new <code>LimitedTree</code> , this code is generated
     * randomly (limits are considered) its length will be a (random) natural
     * number from [2,10]
     */

    public LimitedTree() {
        this(2 + Generator.randomInt(9));
    }

    /**
     * Alocates a new <code>LimitedTree</code> , this code is generated
     * randomly (limits are considered) its length will be the parameter of the
     * method.
     * 
     * @param code_len
     *            The new length of this code.
     */

    public LimitedTree(int code_len) {
        super(LimitedTree.generateRandomCode(code_len));
    }

    /**
     * Alocates a new <code>LimitedTree</code> , this code is set from the
     * value provided.
     * 
     * @param code
     *            The new code of this tree.
     */

    public LimitedTree(String code) {
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

    public static void setCodeLimits(int min_arity, int max_arity)
            throws IllegalArgumentException {
        if (max_arity < min_arity)
            throw new IllegalArgumentException("max_arity < min_arity");
        if (max_arity > 9) throw new IllegalArgumentException("max_arity > 9");
        if (min_arity < 0) throw new IllegalArgumentException("min_arity < 0");
        min = min_arity;
        max = max_arity;
        System.out.println("CODE LIMITS SET");
        System.out.println("min_arity=" + min_arity);
        System.out.println("max_arity=" + max_arity);
        System.out.println("new min=" + LimitedTree.getCodeMinLimit());
        System.out.println("new max=" + LimitedTree.getCodeMaxLimit());
        System.out.println("/CODE LIMITS SET");
    }

    public static void setCodeMinLimit(int min_arity) {
        min = min_arity;
    }

    public static int getCodeMinLimit() {
        return min;
    }

    public static void setCodeMaxLimit(int max_arity) {
        max = max_arity;
    }

    public static int getCodeMaxLimit() {
        return max;
    }

    /**
     * Method provides a random <code>LimitedTree</code> code generation of a
     * specified length.
     * <p>
     * This is mainly used by constructors.
     * 
     * @param len
     *            The length of the randomly generated tree code.
     * @return A string representing the code.
     */

    protected static String generateRandomCode(int len) {
        if (len == 1) return "0";
        if (len == 2) return "10";
        if ((max == 9) && (min == 0)) return ReadsTree.generateRandomCode(len);

        int[] d = new int[len - 1];
        d[0] = len - 1;
        if (max > d[0]) max = d[0];
        String res;
        int rnd = 1;

        if (min <= 0) {
            if (max > 1) rnd = Generator.randomInt(max) + 1;
            res = new String(String.valueOf(rnd));
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = 1 + Generator.randomInt(max);
                    else
                        rnd = 1 + Generator.randomInt(d[i]);
                else
                    if (max <= d[i])
                        rnd = Generator.randomInt(max + 1);
                    else
                        rnd = Generator.randomInt(d[i] + 1);
                res += new String(String.valueOf(rnd));
            }
        }
        else // min > 0
        {
            if (max > 1) rnd = Generator.randomInt(max) + 1;
            res = new String(String.valueOf(rnd));
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = min + Generator.randomInt(max - min + 1);
                    else
                        if (min <= d[i])
                            rnd = min + Generator.randomInt(d[i] - min + 1);
                        else
                            rnd = 0;
                else
                    if (max <= d[i]) {
                        int zero = Generator.randomInt(2);
                        if (zero == 1)
                            rnd = min + Generator.randomInt(max - min + 1);
                        else
                            rnd = 0;
                    }
                    else
                        if (min <= d[i]) {
                            int zero = Generator.randomInt(2);
                            if (zero == 1)
                                rnd = min + Generator.randomInt(d[i] - min + 1);
                            else
                                rnd = 0;
                        }
                        else
                            rnd = 0;

                res += new String(String.valueOf(rnd));
            }

        }

        res += new String("0");
        return res;
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

    protected static String generateRandomCode(int min_len, int max_len) {
        int len = min_len + Generator.randomInt(max_len - min_len + 1);
        return generateRandomCode(len);
    }

    /**
     * This method causes that this tree (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one.
     * 
     * @param mut_len
     *            Length of the new subtree of this tree.
     * @return The random position where the mutation occured.
     */

    public int mutateCode(int mut_len) {
        int len = code.length();
        int pos = 1 + Generator.randomInt(len - 1);
        int pos_len = subcodeLength(pos);
        String res = code.substring(0, pos);
        res += generateRandomCode(mut_len);
        res += code.substring(pos + pos_len, len);
        code = res;
        setCodeNo();
        return pos;
    }

    /**
     * This method causes that this tree (code) will be randomly mutated at a
     * random position which is distinct from the root position (first position
     * in the code). The whole subtree at the specified position will be
     * replaced with a new one, but the length is limited by the parameters of
     * this method.
     * 
     * @param min_len
     *            Minimal accepted length of the result tree.
     * @param max_len
     *            Maximal accepted length of the result tree.
     * @return The random position where the mutation occured.
     */

    public int mutateCode(int min_len, int max_len) {
        int len = code.length();
        int pos = 1 + Generator.randomInt(len - 1);
        int pos_len = subcodeLength(pos);
        int mut_len = Generator.randomInt(max_len);
        int tmp = len + mut_len - pos_len;
        while ((tmp > max_len) || (tmp < min_len)) {
            mut_len = Generator.randomInt(max_len);
            tmp = len + mut_len - pos_len;
        }
        String res = code.substring(0, pos);
        res += generateRandomCode(mut_len);
        res += code.substring(pos + pos_len, len);
        code = res;
        setCodeNo();
        return pos;
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
     * @param min
     *            The minimal code element limit of the randomly generated tree
     *            code.
     * @param max
     *            The maximal code element limit of the randomly generated tree
     *            code.
     * @return An array of integers representing the code.
     * @throws IllegalArgumentException
     *             if <code> min > max </code> or <code> min < 0 </code> or
     *             <code> max < 1 </code>
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
        int rnd = 1;

        if (min <= 0) {
            if (max > 1) rnd = Generator.randomInt(max) + 1;
            res[index++] = rnd;
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = 1 + Generator.randomInt(max);
                    else
                        rnd = 1 + Generator.randomInt(d[i]);
                else
                    if (max <= d[i])
                        rnd = Generator.randomInt(max + 1);
                    else
                        rnd = Generator.randomInt(d[i] + 1);
                res[index++] = rnd;
            }
        }
        else // min > 0
        {
            if (max > 1) rnd = Generator.randomInt(max) + 1;
            res[index++] = rnd;
            for (int i = 1; i < len - 1; i++) {
                d[i] = d[i - 1] - rnd;
                if (d[i] == len - i - 1)
                    if (max <= d[i])
                        rnd = min + Generator.randomInt(max - min + 1);
                    else
                        if (min <= d[i])
                            rnd = min + Generator.randomInt(d[i] - min + 1);
                        else
                            rnd = 0;
                else
                    if (max <= d[i]) {
                        int zero = Generator.randomInt(2);
                        if (zero == 1)
                            rnd = min + Generator.randomInt(max - min + 1);
                        else
                            rnd = 0;
                    }
                    else
                        if (min <= d[i]) {
                            int zero = Generator.randomInt(2);
                            if (zero == 1)
                                rnd = min + Generator.randomInt(d[i] - min + 1);
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

    public static void main(String[] args) {
        LimitedTree.setCodeLimits(0, 2);
        Generator.init();
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
}
