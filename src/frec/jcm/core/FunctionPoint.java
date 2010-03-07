
package frec.jcm.core;

/**
 * This class has beean added for F-ReC.
 * FunctionPoint is an abstraction of a point of a
 * function, it is used to represent data from an user
 * input. Those data are further used by the kernel module.
 */

public class FunctionPoint 
{
    private double x, y;
    
   /**
    * Create a new FunctionPoint. 
    * Simply using the parameters of the constructor.
    */    
    public FunctionPoint(double x, double y) 
    {
        this.x = x;
        this.y = y;
    }
    
    public double getArgumentValue()
    {
        return x;
    }
    
    public double getFunctionValue()
    {
        return y;
    }
}
