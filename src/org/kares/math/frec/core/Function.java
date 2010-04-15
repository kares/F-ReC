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
 * A function abstraction. Function in math terms is a mapping
 * from a value x to a function value f(x). In general functions
 * might have different arities depending on how many input 
 * variables they accept (and thus depend on).
 * 
 *  Example: sin(x).
 *
 * @see Base
 * @see Functions
 * 
 * @author kares
 */
public interface Function {

	/** 
	 * @return The arity of the function.
	 */
    public int arity() ;

    /**
     * @param x the variable 
     * @see #value(double[])
     */
    public double value(double x) ;

    /**
     * @param x1
     * @param x2
     * @see #value(double[])
     */
    public double value(double x1, double x2) ;

    /**
     * @param x the variables (x.length should be the same as the function's arity) 
     * @return The function value f(x) for the given x
     */
    public double value(double[] x) ;

    /**
     * Functions should implement the equality operation.
     * @param other
     * @return true if the functions are equal
     */
    public boolean equals(Function other) ;

    //public boolean isInverse(Function other) ;

    //public String format() ;

    public String format(String[] varNames) ;

    /**
     * A base function implementation.
     * 
     * @see Functions
     * 
     * @author kares
     */
    public static abstract class Base implements Function {

        protected final Object id;
        protected final int arity;

        protected Base(Object id, int arity) {
            this.id = id;
            this.arity = arity;
        }

        /**
         * @see org.kares.math.frec.core.Function#arity()
         */
        public int arity() {
            return arity;
        }

        /**
         * @see org.kares.math.frec.core.Function#value(double)
         */
        public double value(double x) {
            return value(new double[] { x });
        }

        /**
         * @see org.kares.math.frec.core.Function#value(double, double)
         */
        public double value(double x1, double x2) {
            return value(new double[] { x1, x2 });
        }

        public boolean isInverse(Function other) {
            return false;
        }
        
        /**
         * @see org.kares.math.frec.core.Function#equals(org.kares.math.frec.core.Function)
         */
        public boolean equals(Function that) {
            if (this.getClass() != that.getClass()) return false;
            return this.id.equals( ((Base) that).id ) &&
                   this.arity == ((Base) that).arity;
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object other) {
            if (other instanceof Function) {
                return equals((Function) other);
            }
            return false;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return this.id.hashCode();
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return id.toString();
        }

    }

}
