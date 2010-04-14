/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kares.math.frec.core;

/**
 *
 * @author kares
 */
public class ConstantFunction implements Function {

    private final double value;

    public ConstantFunction(final double value) {
        this.value = value;
    }

    public int arity() {
        return 0; // special case
    }

    public double value() {
        return value;
    }

    public double value(double x) {
        return value;
    }

    public double value(double x1, double x2) {
        return value;
    }

    public double value(double[] x) {
        return value;
    }

    public String format(String[] varNames) {
        return Double.toString(value);
    }

    public boolean equals(Function other) {
        if (other instanceof ConstantFunction) {
            ConstantFunction that = (ConstantFunction) other;
            return this.value == that.value;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            return equals((Function) obj);
        }
        return false;
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }

    public String toString() {
        return format(null);
    }

}
