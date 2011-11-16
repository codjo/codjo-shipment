/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
/**
 * Gestionnaire d'erreur pour les tables de type Quarantaine.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.4 $
 */
public class QuarantineError implements DataShipmentError {
    private static final Logger APP = Logger.getLogger(QuarantineError.class);
    private String errorMessageField = null;
    private String errorTypeField = null;
    private String quarantineIdField = null;

    /**
     * Constructeur
     *
     * @param quarantineIdField Champ identifiant de quarantine
     * @param errorMessageField Champ du message d'erreur
     * @param errorTypeField Champ du type d'erreur
     */
    public QuarantineError(String quarantineIdField, String errorMessageField,
        String errorTypeField) {
        this.quarantineIdField = quarantineIdField;
        this.errorMessageField = errorMessageField;
        this.errorTypeField = errorTypeField;
    }

    public void manageRowError(Connection con, ResultSet row, Exception error,
        String destTable) throws SQLException {
        APP.debug("Typage de ligne en erreur : " + quarantineIdField + " = "
            + row.getString(quarantineIdField), error);

        PreparedStatement stmt = con.prepareStatement(buildInsertErrorQuery(destTable));

        try {
            stmt.setBigDecimal(1, row.getBigDecimal(quarantineIdField));
            stmt.setString(2, error.getMessage());
            stmt.executeUpdate();
        }
        finally {
            stmt.close();
        }
    }


    private String buildInsertErrorQuery(String destTable) {
        return "insert into " + destTable + " (" + quarantineIdField + ", "
        + errorMessageField + ", " + errorTypeField + ") " + " values (?, ?, 1 )";
    }
}
