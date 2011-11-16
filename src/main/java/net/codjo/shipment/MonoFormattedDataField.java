/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import net.codjo.imports.common.translator.Translator;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Cette classe permet d'extraire un champ depuis un {@link ResultSet} et de le restituer
 * au format approprié. Le travail de conversion est assuré par un {@link Translator}.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.2 $
 */
class MonoFormattedDataField implements DataField {
    private Translator translator;
    private String sourceField;
    private String destField;

    /**
     * Constructeur
     *
     * @param sourceField Le champ source
     * @param destField Le champ destination
     * @param translator Le {@link Translator} responsable du formatage
     *
     * @throws IllegalArgumentException si l'un des paramètres est invalide.
     */
    MonoFormattedDataField(String sourceField, String destField, Translator translator) {
        if (sourceField == null || translator == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }
        this.sourceField = sourceField;
        this.destField = destField;
        this.translator = translator;
    }

    public Object convertField(ResultSet rs) throws SQLException, BadFormatException {
        try {
            return translator.translate(rs.getString(this.sourceField));
        }
        catch (net.codjo.imports.common.translator.BadFormatException ex) {
            throw new BadFormatException(ex);
        }
    }


    public int getConvertedSqlType() {
        return translator.getSQLType();
    }


    public String getDBDestFieldName() {
        return destField;
    }
}
