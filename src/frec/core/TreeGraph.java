
package frec.core;

/**
 *
 * @author kares
 */
public class TreeGraph {

    /** Vertex set of a tree. */
    private final int[] V;
    /** Edge set of a tree. */
    private final int[][] E;

    public TreeGraph(int[] V, int[][] E) {
        this.V = V;
        this.E = E;
    }

    public static TreeGraph forReadsCode(final String code) {
        final int len = code.length();
        final int[][] E_tmp = new int[len - 1][2];
        final int[] V_tmp = new int[len];
        V_tmp[0] = 1;
        int d = 0, l = 1;
        int[] code_no = new int[len];
        for (int i = 0; i < len; i++) {
            code_no[i] = Character.digit(code.charAt(i), 10);
        }

        int[] branch = new int[len], index = new int[len];
        branch[0] = code_no[0];
        index[0] = 1;
        while ( d >= 0 ) {
            if (branch[d] > 0) {
                branch[d]--;
                d++;
                l++;
                branch[d] = code_no[l - 1];
                index[d] = l;
                V_tmp[l - 1] = index[d];
                E_tmp[l - 2][0] = index[d - 1];
                E_tmp[l - 2][1] = index[d];
            } else {
                d--;
            }
        }

        final int[] V = new int[l];
        final int[][] E = new int[l-1][2];
        for ( int i=0; i < l-1; i++ ) {
            V[i] = V_tmp[i];
            E[i][0] = E_tmp[i][0];
            E[i][1] = E_tmp[i][1];
        }
        V[l - 1] = V_tmp[l - 1];

        return new TreeGraph(V, E);
    }

    /**
     * Returns the vertex set of this tree (<code>V</code>).
     *
     * @return  Parameter <code>V</code> for this object,
     *          null if the graph has been not set.
     */
    public int[] getVertices() {
        return this.V;
    }

    /**
     * Returns the edge set of this tree (<code>E</code>).
     *
     * @return  Parameter <code>E</code> for this object,
     *          null if the graph has been not set.
     */
    public int[][] getEdges() {
        return this.E;
    }

    public int[] getDegrees() {
        final int ctr = V.length;
        final int[] degrees = new int[ctr];
        degrees[0] = 0; // V[0] = 1 = root
        for (int i = 1; i < ctr; i++) {
            degrees[i] = -1;
        }
        for (int i = 0; i < ctr - 1; i++) {
            degrees[E[i][0] - 1]++;
            degrees[E[i][1] - 1]++;
        }
        return degrees;
    }

    public String toReadsCode() {
        final StringBuffer code = new StringBuffer();
        appendCode(code, getDegrees(), 0);
        return code.toString();
    }

    private void appendCode(final StringBuffer code, final int[] degrees, int index) {
        int ctr = degrees[index];
        code.append(ctr);
        int i = 0;
        while ( ctr > 0 ) {
            if ((E[i][0] == V[index]) && (E[i][1] > E[i][0])) {
                ctr--;
                final int nextIndex = E[i][1] - 1;
                appendCode(code, degrees, nextIndex);
            }
            if ((E[i][1] == V[index]) && (E[i][0] > E[i][1])) {
                ctr--;
                final int nextIndex = E[i][0] - 1;
                appendCode(code, degrees, nextIndex);
            }
            i++;
        }
    }
    
}
