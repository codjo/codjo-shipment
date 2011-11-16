/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Interface decrivant le gestionnaire d'erreur de {@link DataShipment}.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
interface DataShipmentError {
    /**
     * Gère l'erreur <code>error</code> arrivé lors du traitement de la ligne
     * <code>row</code> .
     *
     * @param con Connection
     * @param row La ligne en erreur
     * @param error L'erreur
     * @param destTable La table destination
     *
     * @exception SQLException Erreur acces BD
     */
    public void manageRowError(Connection con, ResultSet row, Exception error,
        String destTable) throws SQLException;
}
