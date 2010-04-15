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
 * A constant function (returns the same constant f(x) value for all input x).
 *
 * @author kares
 */
public class ConstantFunction implements Function {

    private final double value;

    /**
     * @param value
     */
    public ConstantFunction(final double value) {
        this.value = value;
    }

    /**
     * @see Function#arity()
     */
    public int arity() {
        return 0; // special case
    }

    /**
     * Returns the bare constant value.
     * @see Function#value(double)
     */
    public double value() {
        return value;
    }

    /**
     * @see Function#value(double)
     */
    public double value(double x) {
        return value;
    }

    /**
     * @see Function#value(double, double)
     */
    public double value(double x1, double x2) {
        return value;
    }

    /**
     * @see Function#value(double[])
     */
    public double value(double[] x) {
        return value;
    }

    /**
     * @see Function#format(String[])
     */
    public String format(String[] varNames) {
        return Double.toString(value);
    }

    /**
     * @see Function#equals(Function)
     */
    public boolean equals(Function other) {
        if (other instanceof ConstantFunction) {
            ConstantFunction that = (ConstantFunction) other;
            return this.value == that.value;
        }
        return false;
    }

    /**
     * @see Function#equals(Function)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            return equals((Function) obj);
        }
        return false;
    }

    /**
     * @see Function#equals(Function)
     */
    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }

    public String toString() {
        return format(null);
    }

}
