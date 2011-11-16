/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import net.codjo.imports.common.translator.Translator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
/**
 * Cette classe permet d'extraire un champ depuis un {@link ResultSet} et de le restituer
 * au format approprié.
 * 
 * <p>
 * Le travail de conversion est assuré par un des {@link Translator} contenus dans la
 * {@link Map} : <code>sourceTranslator</code>. Le choix du bon {@link Translator} est
 * dicté par la valeur du champ <code>sourceSystemField</code>.
 * </p>
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.3 $
 */
class MultiFormattedDataField implements DataField {
    private int destFielSQLType;
    private String destField;
    private String sourceField;
    private Map sourceTranslator;
    private String sourceSystemField;

    /**
     * Constructeur
     *
     * @param sourceField Le nom physique du champ source
     * @param destField Le champ destination
     * @param sourceSystemField Le nom physique du champ sourceSystem
     * @param sourceTranslator HashMap sourceSystem-Translator
     *
     * @throws IllegalArgumentException si l'un des paramètres est invalide.
     */
    MultiFormattedDataField(String sourceField, String destField,
        String sourceSystemField, Map sourceTranslator) {
        if (sourceField == null || sourceSystemField == null || sourceTranslator == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }
        if (sourceTranslator.size() == 0) {
            throw new IllegalArgumentException(
                "La liste des 'sourceTranslator' n'a pas ete initialise");
        }

        this.sourceField = sourceField;
        this.sourceSystemField = sourceSystemField;
        this.sourceTranslator = sourceTranslator;

        this.destField = destField;
        this.destFielSQLType =
            ((Translator)this.sourceTranslator.values().iterator().next()).getSQLType();
    }

    public Object convertField(ResultSet rs)
            throws SQLException, BadFormatException, SourceNotFoundException {
        if (!sourceTranslator.containsKey(rs.getString(this.sourceSystemField))) {
            throw new SourceNotFoundException("Source inconnue : "
                + rs.getString(this.sourceSystemField));
        }
        try {
            return getSourceTranslator(rs.getString(this.sourceSystemField)).translate(rs
                .getString(this.sourceField));
        }
        catch (net.codjo.imports.common.translator.BadFormatException ex) {
            throw new BadFormatException(ex);
        }
    }


    public int getConvertedSqlType() {
        return this.destFielSQLType;
    }


    public String getDBDestFieldName() {
        return this.destField;
    }


    /**
     * Retourne le {@link Translator} du <code>sourceSystem</code> passé en paramètre.
     *
     * @param sourceSystem Le sourceSystem de la ligne a traiter
     *
     * @return L'objet Translator
     */
    private Translator getSourceTranslator(String sourceSystem) {
        return (Translator)sourceTranslator.get(sourceSystem);
    }
}
