/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
/**
 * Cette exception est lancée par une opération {@link DataField} afin d'indiquer que le
 * champ n'est pas au bon format.
 * 
 * <p>
 * Exemple: Le type attendu est une date, mais le type reçu est un réel.
 * </p>
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.2 $
 */
public class BadFormatException extends ConversionFailureException {
    public BadFormatException(Exception cause) {
        super(cause);
    }
}
