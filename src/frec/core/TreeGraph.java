/*
 * TreeGraph.java
 *
 * Created on November 25, 2004, 9:13 PM
 */

package frec.core;

/**
 *
 * @author  kares
 */
public class TreeGraph {
    
    /** Vertex set of a tree. (if we thing about tree as a graph). */
	private byte[] V;

    /** Edge set of a tree. (if we thing about tree as a graph). */
	private byte[][] E;
        
        private TreeGraph() { }
        
        public TreeGraph(byte[] V, byte[][] E) {
            this.V = V;
            this.E = E;
        }
        
	public static final TreeGraph getTreeGraph(String code) {
            int len = code.length();
            int[][] E_tmp = new int[len-1][2];
            int[] V_tmp = new int[len];
            V_tmp[0] = 1;
            int d = 0, l = 1;
            int branch[] = new int[len];
            int index[] = new int[len];
            int[] code_no = new int[len];
            for (int i=0; i<len; i++)
		//code_no[i] = Byte.parseByte(code.substring(i, i+1));
                code_no[i] = (int) code.charAt(i);
            branch[0] = code_no[0];
            index[0] = 1;

            while (d>=0)
		if (branch[d]>0) {
                	branch[d]--;
			d++;
			l++;
			branch[d] = code_no[l-1];
			index[d] = l;
			V_tmp[l-1] = index[d];
			E_tmp[l-2][0] = index[d-1];
			E_tmp[l-2][1] = index[d];
		}
		else d--;

            TreeGraph res = new TreeGraph();
            res.V = new byte[l];
            res.E = new byte[l-1][2];

            for (int i=0; i<l-1; i++) {
            	res.V[i] = (byte)V_tmp[i];
            	res.E[i][0] = (byte)E_tmp[i][0];
		res.E[i][1] = (byte)E_tmp[i][1];
            }

            res.V[l-1] = (byte) V_tmp[l-1];
            return res;
	}
        
	public final String generateReadsCode() {
            int ctr = V.length;
            int[] deg = new int[ctr];
            deg[0] = 0; //V[0] = 1 = root
            for (int i=1; i<ctr; i++) deg[i] = -1;
            for (int i=0; i<ctr-1; i++) {
            	deg[E[i][0] - 1]++;
		deg[E[i][1] - 1]++;
            }

            return setString(0, deg);
	}
        
    /**
     * Returns the vertex set of this tree (<code>V</code>).
     * <p> Before calling this method the method <code>setGraph()</code>
     * should be called to make sure the graph representation is set.
     *
     * @return  Parameter <code>V</code> for this object,
     *          null if the graph has been not set.
     */

	public byte[] getVertices() {
            return this.V;
	}

    /**
     * Returns the edge set of this tree (<code>E</code>).
     * <p> Before calling this method the method <code>setGraph()</code>
     * should be called to make sure the graph representation is set.
     *
     * @return  Parameter <code>E</code> for this object,
     *          null if the graph has been not set.
     */

	public byte[][] getEdges() {
            return this.E;
	}

	private String setString(int v_index, int[] deg) {
            int ctr = deg[v_index];
            String res = String.valueOf(ctr);
            if (ctr > 0) {
            	int i = 0;
            	while (ctr > 0) {
                    if ((E[i][0] == V[v_index]) && (E[i][1] > E[i][0])) {
			ctr--;
			int index = E[i][1] - 1;
			res += setString(index, deg);
                    }
                    if ((E[i][1] == V[v_index]) && (E[i][0] > E[i][1])) {
                       	ctr--;
			int index = E[i][0] - 1;
			res += setString(index, deg);
                    }
                    i++;
		}
            }

            return res;
	}        
    
}
