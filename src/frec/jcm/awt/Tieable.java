
package frec.jcm.awt;

/**
 * This interface is from edu.hws.jcm.awt package without any modification.
 * A Tieable object has an associated serial number.  The value of the serial
 * should increase when the value of the object changes.  A Tieable can "sync" with another
 * Tieable, presumably by copying its serial number and other information.  
 * A given Tieable might only be able to synchronize with other Tiebles of
 * certain types.  If its sync() method is called with an object of the wrong 
 * type, it should probably thrown an IllegalArguemntException. 
 *
 * See the "Tie" and "Controller" classes for information about how Tieable 
 * are used. 
 *
 */
public interface Tieable extends java.io.Serializable {

   /**
    * Get the serial number associated with this Tieable.  If the
    * value of this Tieable changes, then the serial number should
    * increase.
    */
   public long getSerialNumber();

   /**      
    * This routine is called to tell this Tieable that the serial
    * numbers of the Tieables that have been added to the Tie do not
    * match.  newest has a serial number that is at least as
    * large as the serial number of any other Tieable in the Tie.
    * This Tieable should synchronize its value and serial number
    * with the "newest" Tieables.
    *    (Note:  So far, I haven't found any reason to use
    * the Tie parameter in this method!  Maybe it should be removed.)
    */
   public void sync(Tie tie, Tieable newest);

}
