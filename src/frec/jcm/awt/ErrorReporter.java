
package frec.jcm.awt;

/**
 * This interface is from edu.hws.jcm.awt package without any modification.
 * To allow different styles of reporting errors, a
 * Controller uses an ErrorReporter to report any
 * errors that are thrown during its checkInput/compute
 * cycle.  The DisplayCanvas and MessagePopup classes
 * implement this interface.
 *
 * @author David Eck
 */
public interface ErrorReporter {

   /**
    * Report the specifed message as an error.  If source is non-null,
    * then it is the Controller that called this routine.  In that case,
    * if the error reporter is capable of clearing its own error
    * condition, it should call source.errorCleared() when it does so.
    *
    * @param source Controller that called this method (if non-null).
    * @param message error message to report.
    */
   public void setErrorMessage(Controller source, String message);

   /**
    * Clear the error reprort, if there is one.
    */
   public void clearErrorMessage();

   /**
    * Get the error message that is currently being displayed, or
    * return null if there is no error message.
    */
   public String getErrorMessage();
   
}

