
package frec.jcm.core;

/**
 * This class is from edu.hws.jcm.data package without any modification.
 * Represents a syntax error that is found while a string is being parsed.
 */
public class ParseError extends RuntimeException {

   /**
    * The parsing context that was in effect
    * at the time the error occurred.  This includes
    * the string that was being processed and the
    * position in the string where the error occured.
    * These values are context.data and context.pos.
    */
   public ParserContext context;
      
   /**
    * Create a new ParseError with a given error message and parsing context.
    */
   public ParseError(String message, ParserContext context) { 
      super(message);
      this.context = context;
   }

}
