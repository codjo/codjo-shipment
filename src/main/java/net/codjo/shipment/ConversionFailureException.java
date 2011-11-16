/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Echec de la conversion d'une colonne source.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.4 $
 */
public class ConversionFailureException extends Exception {
    private Exception causedByException;

    /**
     * Constructeur
     *
     * @param msg message d'erreur.
     */
    public ConversionFailureException(String msg) {
        super(msg);
    }


    public ConversionFailureException(Exception cause) {
        super(cause.getLocalizedMessage());
        causedByException = cause;
    }

    public Exception getCausedByException() {
        return causedByException;
    }


    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getCausedByException() != null) {
            writer.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(writer);
        }
    }


    public void printStackTrace() {
        super.printStackTrace();
        if (getCausedByException() != null) {
            System.err.println(" ---- cause ---- ");
            getCausedByException().printStackTrace();
        }
    }


    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (getCausedByException() != null) {
            stream.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(stream);
        }
    }
}
