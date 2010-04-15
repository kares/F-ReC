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

/**
 * A helper class to represent Read's trees as real topological graphs 
 * (with nodes and edges) if required.
 *
 * @see #forReadsCode(String)
 * 
 * @author kares
 */
public class TreeGraph {

    /** Vertex set of a tree. */
    private final int[] V;
    /** Edge set of a tree. */
    private final int[][] E;

    /**
     * @param V
     * @param E
     */
    public TreeGraph(int[] V, int[][] E) {
        this.V = V;
        this.E = E;
    }

    /**
     * Returns a graph instance from Read's code.
     * @param code
     * @return A tree graph instance.
     */
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
     * @return Returns the vertex set of this (tree) graph. 
     */
    public int[] getVertices() {
        return this.V;
    }

    /**
     * @return Returns the edge set of this (tree) graph.
     */
    public int[][] getEdges() {
        return this.E;
    }

    /**
     * @return Returns the vertex degrees.
     */
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

    /**
     * Converts the (tree) graph to Read's code. 
     * @return Read's code for this graph.
     */
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
