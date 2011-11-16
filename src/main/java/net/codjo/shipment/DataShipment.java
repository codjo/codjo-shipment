/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import net.codjo.imports.common.QueryHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 * Transfert des données d'une table source vers une table destination.
 * 
 * <p>
 * Les données sont transférees en utilisant des {@link DataField} responsables du
 * formatage et du typage.
 * </p>
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.4 $
 *
 * @see DataField
 */
public class DataShipment {
    private String destinationTable;
    private DataShipmentError errorHandler;
    private DataField[] fields;
    private String selectWhereClause = null;
    private String sourceTable;

    /**
     * Constructeur
     *
     * @param sourceTable La table origine
     * @param fields Les {@link DataField} responsables du transfert des donnees
     * @param destinationTable La table destination
     * @param errorHandler Description of the Parameter
     */
    public DataShipment(String sourceTable, DataField[] fields, String destinationTable,
        DataShipmentError errorHandler) {
        this.sourceTable = sourceTable;
        this.fields = fields;
        this.destinationTable = destinationTable;
        this.errorHandler = errorHandler;
    }

    /**
     * Retourne l'attribut selectWhereClause de DataShipment
     *
     * @return La valeur de selectWhereClause
     */
    public String getSelectWhereClause() {
        return selectWhereClause;
    }


    /**
     * Transfert des données de la table <code>sourceTable</code> vers la table
     * <code>destinationTable</code> .
     *
     * @param con La connexion
     *
     * @exception SQLException Erreur d'acces a la BD
     */
    public void proceed(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        PreparedStatement insertStmt = null;
        try {
            insertStmt = buildInsertStatement(con);
            ResultSet rs = stmt.executeQuery(buildSelectQuery());
            while (rs.next()) {
                try {
                    transferData(rs, insertStmt);
                }
                catch (Exception ex) {
                    errorHandler.manageRowError(con, rs, ex, destinationTable);
                }
            }
        }
        finally {
            stmt.close();
            if (insertStmt != null) {
                insertStmt.close();
            }
        }
    }


    /**
     * Positionne l'attribut selectWhereClause
     *
     * @param newSelectWhereClause La nouvelle valeur de selectWhereClause
     */
    public void setSelectWhereClause(String newSelectWhereClause) {
        selectWhereClause = newSelectWhereClause;
    }


    /**
     * Création du PreparedStatement de la requète d'insertion.
     *
     * @param con La connexion
     *
     * @return Le PreparedStatement
     *
     * @exception SQLException Erreur acces BD
     */
    private PreparedStatement buildInsertStatement(Connection con)
            throws SQLException {
        ArrayList columns = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            columns.add(fields[i].getDBDestFieldName());
        }

        return QueryHelper.buildInsertStatement(destinationTable, columns, con);
    }


    /**
     * Construction de la requète de sélection sur la table source.
     *
     * @return Requete de sélection.
     */
    private String buildSelectQuery() {
        String query = "select * from " + sourceTable;
        if (selectWhereClause != null && !"".equals(selectWhereClause)) {
            query += " where " + selectWhereClause;
        }
        return query;
    }


    private void throwException(PreparedStatement insertStmt)
            throws SQLException {
        if (insertStmt.getWarnings() != null) {
            throw insertStmt.getWarnings();
        }

        // Cas normalement impossible
        throw new SQLException("Ligne non insérée sans erreur BD");
    }


    /**
     * Transfert d'une ligne vers la table <code>destinationTable</code>.
     *
     * @param rs La ligne à transférer
     * @param insertStmt Le {@link PreparedStatement} à utiliser.
     *
     * @exception SQLException Erreur acces BD
     * @exception ConversionFailureException si la conversion a échoué.
     */
    private void transferData(ResultSet rs, PreparedStatement insertStmt)
            throws SQLException, ConversionFailureException {
        insertStmt.clearParameters();
        for (int i = 0; i < fields.length; i++) {
            Object val = fields[i].convertField(rs);
            insertStmt.setObject(i + 1, val, fields[i].getConvertedSqlType());
        }

        int nbOfInsertedRow = insertStmt.executeUpdate();

        if (nbOfInsertedRow == 0) {
            throwException(insertStmt);
        }
    }
}
