package frec.core;

import frec.util.Generator;

/**
 * Class <code> ReadsTree </code> is an object representation of trees (with a
 * root). This class provides all the operations (methods) necessary for the
 * basics of genetic programming. The primary thing that characterizes this
 * object is Read's linear code, this code is used for effectively coding graphs
 * (trees) to a sequence of numbers (or strings).
 */

public class ReadsTree {

    /** Read's linear code as a string for this tree. */
    protected String code;

    /** Read's linear code as an array of bytes for this tree. */
    private byte[] code_no;

    /** Vertex set of a tree. (if we thing about tree as a graph). */
    private byte[] V;

    /** Edge set of a tree. (if we thing about tree as a graph). */
    private byte[][] E;

    /**
     * Parameter, that marks the (begining) position were the tree was last time
     * crossed.
     */
    protected byte crossPosBeg;

    /**
     * Parameter, that marks the (end) position were the tree was last time
     * crossed.
     */
    protected byte crossPosEnd;

    private boolean useCrossPos = false;

    private static int dfMaxCodeLength = 10;

    /**
     * Alocates a new <code>ReadsTree</code> , this code is generated randomly
     * its length will be a (random) natural number from [2,10]
     */

    private ReadsTree() {
        // this(2 + Generator.randomInt(9));
    }

    /**
     * Alocates a new <code>ReadsTree</code> , this code is set from the value
     * provided.
     * 
     * @param code
     *            The new code of this tree.
     */

    public ReadsTree(String code) {
        this.code = code;
        setCodeNo();
    }

    /**
     * Alocates a new <code>ReadsTree</code> , the code will be set from the
     * graph representation: <code>(V,E)</code> , it is assumed that the
     * vertices are numbered starting 1, where 1 is the root vertex an the whole
     * set is arranged.
     * 
     * @param V
     *            The vertex set of the tree.
     * @param E
     *            The edge set of the tree.
     */

    // public ReadsTree(byte[] V, byte[][] E) {
    // this.V = V; this.E = E;
    // setCode();
    // setCodeNo();
    // }
    
    /**
     * Alocates a new <code>ReadsTree</code> , this code is generated randomly
     * its length will be the parameter of the method.
     * 
     * @param code_len
     *            The new length of this code.
     */

    // public ReadsTree(int code_len) {
    // this.code = generateRandomCode(code_len);
    // setCodeNo();
    // }
    
    public static void setDefaultMaxCodeLength(int length) {
        if (length < 2) return; // / ???
        dfMaxCodeLength = length;
    }

    public static ReadsTree getRandomInstance() {
        return getRandomInstance(dfMaxCodeLength);
    }

    public static ReadsTree getRandomInstance(int code_len) {
        ReadsTree instance = new ReadsTree();
        instance.code = generateRandomCode(code_len);
        instance.setCodeNo();
        return instance;
    }

    /**
     * Returns the code representation of this tree as an array (<code>code_no</code>).
     * 
     * @return Parameter <code>code_no</code> fot this object.
     */

    protected byte[] getCodeNo() {
        return code_no;
    }

    /**
     * Returns the vertex set of this tree (<code>V</code>).
     * <p>
     * Before calling this method the method <code>setGraph()</code> should be
     * called to make sure the graph representation is set.
     * 
     * @return Parameter <code>V</code> for this object, null if the graph has
     *         been not set.
     */

    // public byte[] getGraphVertices() {
    // return V;
    // }
    /**
     * Returns the edge set of this tree (<code>E</code>).
     * <p>
     * Before calling this method the method <code>setGraph()</code> should be
     * called to make sure the graph representation is set.
     * 
     * @return Parameter <code>E</code> for this object, null if the graph has
     *         been not set.
     */

    // public byte[][] getGraphEdges() {
    // return E;
    // }
    /**
     * Sets the graph representation of this tree (<code>V</code> and
     * <code>E</code>) using the code representation (<code>code</code>).
     */
    /*
     * public void setGraph() { int len = code.length(); int[][] E_tmp = new
     * int[len-1][2]; int[] V_tmp = new int[len]; V_tmp[0] = 1; int d = 0, l =
     * 1; int branch[] = new int[len]; int index[] = new int[len]; branch[0] =
     * code_no[0]; index[0] = 1; while (d>=0) if (branch[d]>0) { branch[d]--;
     * d++; l++; branch[d] = code_no[l-1]; index[d] = l; V_tmp[l-1] = index[d];
     * E_tmp[l-2][0] = index[d-1]; E_tmp[l-2][1] = index[d]; } else d--; V = new
     * byte[l]; E = new byte[l-1][2]; for (int i=0; i<l-1; i++) { V[i] =
     * (byte)V_tmp[i]; E[i][0] = (byte)E_tmp[i][0]; E[i][1] = (byte)E_tmp[i][1]; }
     * V[l-1] = (byte)V_tmp[l-1]; }
     */
    /*
     * private void setCode() { int poc = V.length; int[] deg = new int[poc];
     * deg[0] = 0; //V[0] = 1 = root for (int i=1; i<poc; i++) deg[i] = -1; for
     * (int i=0; i<poc-1; i++) { deg[E[i][0] - 1]++; deg[E[i][1] - 1]++; } code =
     * setString(0, deg); } private String setString(int v_index, int[] deg) {
     * int counter = deg[v_index]; String res = String.valueOf(counter); if
     * (counter > 0) { int i = 0; while (counter > 0) { if ((E[i][0] ==
     * V[v_index]) && (E[i][1] > E[i][0])) { counter--; int index = E[i][1] - 1;
     * res += setString(index, deg); } if ((E[i][1] == V[v_index]) && (E[i][0] >
     * E[i][1])) { counter--; int index = E[i][0] - 1; res += setString(index,
     * deg); } i++; } } return res; }
     */

    /**
     * This method is used to set the parameter <code>code_no</code> using the
     * <code>code<code> (so it won't be necessary to parse the string
     * all the times when it is used).
     */

    protected void setCodeNo() {
        code_no = new byte[code.length()];
        for (int i = 0; i < code.length(); i++)
            code_no[i] = Byte.parseByte(code.substring(i, i + 1));
    }

    /**
     * Method used to provide a <code>String</code> representation of an
     * object.
     * 
     * @return String representing this tree.
     */

    public String toString() {
        String s = "Tree: code = " + code + " ;";
        return s;
    }

    /**
     * Method provides a random <code>ReadsTree</code> generation of the
     * specified length.
     * <p>
     * This is mainly used by constructors.
     * 
     * @param len
     *            The length of the randomly generated tree code.
     * @return String representing the code.
     */

    protected static String generateRandomCode(int len) {
        if (len == 1) return "0";
        if (len == 2) return "10";
        int[] d = new int[len - 1];
        d[0] = len - 1;
        int rnd = 1 + Generator.randomInt(d[0]);
        if (rnd > 9) rnd = 9;
        String res = new String(String.valueOf(rnd));

        for (int i = 1; i < len - 1; i++) {
            d[i] = d[i - 1] - rnd;
            if (d[i] == len - i - 1)
                rnd = 1 + Generator.randomInt(d[i]);
            else
                rnd = Generator.randomInt(d[i] + 1);
            if (rnd > 9) rnd = 5 + Generator.randomInt(5);
            res += new String(String.valueOf(rnd));
        }

        res += new String("0");
        return res;
    }

    /**
     * Method provides a random <code>ReadsTree</code> generation of the
     * specified length.
     * <p>
     * This is mainly used by constructors.
     * 
     * @param min_len
     *            The minimal length of the randomly generated tree code.
     * @param max_len
     *            The maximal length of the randomly generated tree code.
     * @return String representing the code.
     */

    protected static String generateRandomCode(int min_len, int max_len) {
        int len = min_len + Generator.randomInt(max_len - min_len + 1);
        return generateRandomCode(len);
    }

    /**
     * This method is used to find a length of a subtree (subcode) of this tree.
     * 
     * @param pos
     *            The (code) position of the subtree.
     * @return Length of a subtree of this tree.
     */

    protected int subcodeLength(int pos) {
        if (code_no[pos] == 0)
            return 1;
        else {
            int len = 1;
            while (true) {
                len++;
                int sum = 0;
                for (int i = pos; i < pos + len; i++) {
                    sum += code_no[i];
                    if (sum < i - pos) {
                        sum = 0;
                        break;
                    }
                }
                if (sum != len - 1)
                    continue;
                else
                    return len;
            }
        }
    }

    /**
     * This method is used to find a subtree (subcode) of this tree.
     * 
     * @param pos
     *            The (code) position of the subtree.
     * @return Subtree of this tree.
     */

    protected ReadsTree subCode(int pos) {
        int len = subcodeLength(pos);
        String subCode = code.substring(pos, pos + len);
        return new ReadsTree(subCode);
    }

    /**
     * This method simply returns the <code>code</code> parameter of this
     * tree.
     * 
     * @return String - the code of the tree.
     */

    public String getCode() {
        return this.code;
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
     * @param
     */

    public void setCrossPosition(int pos) {
        int len = this.code.length();
        if (pos < 0 && pos >= len)
            throw new IllegalArgumentException(
                    "position must satisfy: pos >= 0 & pos < code_length");
        else {
            crossPosBeg = (byte) pos;
            crossPosEnd = (byte) subcodeLength(pos);
            crossPosEnd += crossPosBeg;
            useCrossPos = true;
        }
    }

    /**
     * Method that crosses two <code>ReadsTree</code> objects. This method
     * causes that a random position is selected in both trees and then the
     * subtrees are changed among these trees.
     * 
     * @param tree
     *            <code>ReadsTree</code> object to be crossed with this tree.
     * @return The new (2) trees created.
     */
    /*
     * public ReadsTree[] crossCode(ReadsTree tree) { int len1 =
     * this.code.length(); int len2 = tree.code.length(); int pos1, pos2,
     * pos1_len, pos2_len; if (len1==1) { if (len2==1) { this.crossPosBeg =
     * (byte)0; this.crossPosEnd = (byte)1; tree.crossPosBeg = (byte)0;
     * tree.crossPosEnd = (byte)1; return new ReadsTree[] {this, tree}; } pos1 =
     * 0; pos1_len = 1; } else pos1 = 1 + Generator.randomInt(len1 - 1); if
     * (len2==1) { pos2 = 0; pos2_len = 1; } else pos2 = 1 +
     * Generator.randomInt(len2 - 1); pos1_len = this.subcodeLength(pos1);
     * pos2_len = tree.subcodeLength(pos2); this.crossPosBeg = (byte)pos1;
     * this.crossPosEnd = (byte)(pos1_len + pos1); tree.crossPosBeg =
     * (byte)pos2; tree.crossPosEnd = (byte)(pos2_len + pos2); String res1 =
     * this.code.substring(0, pos1); res1 += tree.code.substring(pos2, pos2 +
     * pos2_len); res1 += this.code.substring(pos1 + pos1_len, len1); String
     * res2 = tree.code.substring(0, pos2); res2 += this.code.substring(pos1,
     * pos1 + pos1_len); res2 += tree.code.substring(pos2 + pos2_len, len2);
     * ReadsTree[] result = new ReadsTree[2]; result[0] = new ReadsTree(res1);
     * result[1] = new ReadsTree(res2); return result; }
     */

    public ReadsTree[] crossCode(ReadsTree tree) {
        int len1 = this.code.length();
        int len2 = tree.code.length();
        int pos1 = 0, pos2 = 0, pos1_len = 1, pos2_len = 1;

        if (this.useCrossPos && tree.useCrossPos) {
            pos1 = this.crossPosBeg;
            pos1_len = this.crossPosEnd - pos1;
            pos2 = tree.crossPosBeg;
            pos2_len = tree.crossPosEnd - pos2;
        }
        else // !useCrossPos for both
        {
            if (len1 == 1 && len2 == 1) {
                this.crossPosBeg = (byte) 0;
                this.crossPosEnd = (byte) 1;
                tree.crossPosBeg = (byte) 0;
                tree.crossPosEnd = (byte) 1;
                return new ReadsTree[] { this, tree };
            }
            else
                if (!this.useCrossPos && !tree.useCrossPos) {
                    if (len1 != 1) pos1 = 1 + Generator.randomInt(len1 - 1);
                    if (len2 != 1) pos2 = 1 + Generator.randomInt(len2 - 1);
                    pos1_len = this.subcodeLength(pos1);
                    pos2_len = tree.subcodeLength(pos2);
                    this.crossPosBeg = (byte) pos1;
                    this.crossPosEnd = (byte) (pos1_len + pos1);
                    tree.crossPosBeg = (byte) pos2;
                    tree.crossPosEnd = (byte) (pos2_len + pos2);
                }
                else
                    if (this.useCrossPos) {
                        pos1 = this.crossPosBeg;
                        pos1_len = this.crossPosEnd - pos1;
                        if (len2 != 1)
                            pos2 = 1 + Generator.randomInt(len2 - 1);
                        pos2_len = tree.subcodeLength(pos2);
                        tree.crossPosBeg = (byte) pos2;
                        tree.crossPosEnd = (byte) (pos2_len + pos2);
                    }
                    else
                        if (tree.useCrossPos) {
                            pos2 = tree.crossPosBeg;
                            pos2_len = tree.crossPosEnd - pos2;
                            if (len1 != 1)
                                pos1 = 1 + Generator.randomInt(len1 - 1);
                            pos1_len = this.subcodeLength(pos1);
                            this.crossPosBeg = (byte) pos1;
                            this.crossPosEnd = (byte) (pos1_len + pos1);
                        }

        }// !useCrossPos for both

        String res1 = this.code.substring(0, pos1);
        res1 += tree.code.substring(pos2, pos2 + pos2_len);
        res1 += this.code.substring(pos1 + pos1_len, len1);
        String res2 = tree.code.substring(0, pos2);
        res2 += this.code.substring(pos1, pos1 + pos1_len);
        res2 += tree.code.substring(pos2 + pos2_len, len2);

        ReadsTree[] result = new ReadsTree[2];
        result[0] = new ReadsTree(res1);
        result[1] = new ReadsTree(res2);

        return result;
    }

    /**
     * Method that crosses two <code>ReadsTree</code> objects. This method
     * causes that a random position is selected in both trees and then the
     * subtrees are changed among these trees. Limits for the new tree codes are
     * provided by the parameters of the method.
     * 
     * @param tree
     *            <code>ReadsTree</code> object to be crossed with this tree.
     * @param min_len
     *            Minimal length of the newly created trees.
     * @param max_len
     *            Maximal length of the newly created trees.
     * @return The new (2) trees created.
     */

    public ReadsTree[] crossCode(ReadsTree tree, int min_len, int max_len) {
        int len1 = this.code.length();
        int len2 = tree.code.length();
        int pos1 = 0, pos2 = 0, pos1_len = 1, pos2_len = 1;

        if (this.useCrossPos && tree.useCrossPos)
        // either we are using cross positons that
        // has been set before, they may change if they
        // do not satisfy the conditions about min_len &
        // max_len, they won't change (even if they do not
        // satisfy these conditions) if we can not find
        // positions that will satisfy them (100 triings)
        {
            pos1 = this.crossPosBeg;
            pos1_len = this.crossPosEnd - pos1;
            pos2 = tree.crossPosBeg;
            pos2_len = tree.crossPosEnd - pos2;
        }
        else // !useCrossPos for both
        {
            if (len1 == 1 && len2 == 1) {
                this.crossPosBeg = (byte) 0;
                this.crossPosEnd = (byte) 1;
                tree.crossPosBeg = (byte) 0;
                tree.crossPosEnd = (byte) 1;
                return new ReadsTree[] { this, tree };
            }
            else
                if (!this.useCrossPos && !tree.useCrossPos) {
                    if (len1 != 1) pos1 = 1 + Generator.randomInt(len1 - 1);
                    if (len2 != 1) pos2 = 1 + Generator.randomInt(len2 - 1);
                    pos1_len = this.subcodeLength(pos1);
                    pos2_len = tree.subcodeLength(pos2);

                }
                else
                    if (this.useCrossPos) {
                        pos1 = this.crossPosBeg;
                        pos1_len = this.crossPosEnd - pos1;
                        if (len2 != 1)
                            pos2 = 1 + Generator.randomInt(len2 - 1);
                        pos2_len = tree.subcodeLength(pos2);
                    }
                    else
                        if (tree.useCrossPos) {
                            pos2 = tree.crossPosBeg;
                            pos2_len = tree.crossPosEnd - pos2;
                            if (len1 != 1)
                                pos1 = 1 + Generator.randomInt(len1 - 1);
                            pos1_len = this.subcodeLength(pos1);
                        }

        }// !useCrossPos for both

        int counter = 0;

        while ((len1 - pos1_len + pos2_len < min_len)
                || (len1 - pos1_len + pos2_len > max_len)
                || (len2 - pos2_len + pos1_len < min_len)
                || (len2 - pos2_len + pos1_len > max_len)) {
            // trying to find positions that match the
            // limits (min_len & max_len):
            if (++counter == 100) break;
            if (!this.useCrossPos) {
                pos1 = 1 + Generator.randomInt(len1 - 1);
                pos1_len = this.subcodeLength(pos1);
            }
            else
                if (counter > 50) {
                    pos1 = 1 + Generator.randomInt(len1 - 1);
                    pos1_len = this.subcodeLength(pos1);
                }
            if (!tree.useCrossPos) {
                pos2 = 1 + Generator.randomInt(len2 - 1);
                pos2_len = tree.subcodeLength(pos2);
            }
            else
                if (counter > 50) {
                    pos2 = 1 + Generator.randomInt(len2 - 1);
                    pos2_len = tree.subcodeLength(pos2);
                }
        }

        if (counter == 100 && this.useCrossPos && tree.useCrossPos)
        // in case we are using a cross position which is bad
        // (not satisfiing the conditions about min_len & max_len)
        // but we can not satisfy the conditions after 100 iterations
        // we will still use the "bad" cross positions set before
        {
            pos1 = this.crossPosBeg;
            pos1_len = this.crossPosEnd - pos1;
            pos2 = tree.crossPosBeg;
            pos2_len = tree.crossPosEnd - pos2;
        }
        else {
            this.crossPosBeg = (byte) pos1;
            this.crossPosEnd = (byte) (pos1_len + pos1);
            tree.crossPosBeg = (byte) pos2;
            tree.crossPosEnd = (byte) (pos2_len + pos2);
        }

        // code exchange:
        String res1 = this.code.substring(0, pos1);
        res1 += tree.code.substring(pos2, pos2 + pos2_len);
        res1 += this.code.substring(pos1 + pos1_len, len1);
        String res2 = tree.code.substring(0, pos2);
        res2 += this.code.substring(pos1, pos1 + pos1_len);
        res2 += tree.code.substring(pos2 + pos2_len, len2);

        ReadsTree[] result = new ReadsTree[2];
        result[0] = new ReadsTree(res1);
        result[1] = new ReadsTree(res2);

        return result;
    }

}
