/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kares.math.frec.core;

/**
 *
 * @author kares
 */
public interface Function {

    public int arity() ;

    public double value(double x) ;

    public double value(double x1, double x2) ;

    public double value(double[] x) ;

    public boolean equals(Function other) ;

    //public boolean isInverse(Function other) ;

    //public String format() ;

    public String format(String[] varNames) ;

    public static abstract class Base implements Function {

        protected final Object id;
        protected final int arity;

        protected Base(Object id, int arity) {
            this.id = id;
            this.arity = arity;
        }

        public int arity() {
            return arity;
        }

        public double value(double x) {
            return value(new double[] { x });
        }

        public double value(double x1, double x2) {
            return value(new double[] { x1, x2 });
        }

        public boolean equals(Object other) {
            if (other instanceof Function) {
                return equals((Function) other);
            }
            return false;
        }

        public boolean equals(Function that) {
            if (this.getClass() != that.getClass()) return false;
            return this.id.equals( ((Base) that).id ) &&
                   this.arity == ((Base) that).arity;
        }

        public boolean isInverse(Function other) {
            return false;
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public String toString() {
            return id.toString();
        }

    }

}
