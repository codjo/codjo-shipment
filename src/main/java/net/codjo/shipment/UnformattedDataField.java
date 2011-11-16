/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Cette classe permet d'extraire un champ depuis un {@link ResultSet} et de le restituer
 * sous forme de {@link Object}.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class UnformattedDataField implements DataField {
    private int convertedSqlType;
    private String destField;
    private String sourceField;

    /**
     * Initialise les paramètres <code>sourceField</code>, <code>destField</code> et
     * <code>convertedSqlType</code>.
     *
     * @param sourceField Le nom physique du champ source
     * @param destField Le nom physique du champ destination
     * @param convertedSqlType Le type SQL du champ destination
     *
     * @throws IllegalArgumentException si l'un des paramètres est invalide.
     */
    UnformattedDataField(String sourceField, String destField, int convertedSqlType) {
        if (sourceField == null || destField == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }

        this.sourceField = sourceField;
        this.destField = destField;
        this.convertedSqlType = convertedSqlType;
    }

    public Object convertField(ResultSet rs) throws SQLException {
        return rs.getObject(sourceField);
    }


    public int getConvertedSqlType() {
        return this.convertedSqlType;
    }


    public String getDBDestFieldName() {
        return this.destField;
    }
}
