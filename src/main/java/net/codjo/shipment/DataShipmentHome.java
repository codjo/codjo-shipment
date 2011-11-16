/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import net.codjo.imports.common.translator.Translator;
import net.codjo.imports.common.translator.TranslatorFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Home des objets {@link DataShipment}
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.5 $
 */
public final class DataShipmentHome {
    protected static final Logger APP = Logger.getLogger(DataShipmentHome.class);

    private DataShipmentHome() {}

    /**
     * Construit un {@link DataField} en fonction de son type SQL.
     *
     * @param con la connection JDBC.
     * @param sourceFieldName nom du champ source.
     * @param sourceTypeSQLField type SQL du champ source.
     * @param destField nom du champ destination.
     * @param destTypeSQLField type SQL du champ destination.
     *
     * @return un nouveau {@link DataField}
     *
     * @throws SQLException si erreur BD.
     */
    public static DataField buildDataField(Connection con, String sourceFieldName,
        int sourceTypeSQLField, String destField, int destTypeSQLField)
            throws SQLException {
        if (sourceTypeSQLField == destTypeSQLField) {
            return new UnformattedDataField(sourceFieldName, destField, destTypeSQLField);
        }
        else {
            return new MultiFormattedDataField(sourceFieldName, destField,
                "SOURCE_SYSTEM", getAllSourceTranslator(con, destTypeSQLField));
        }
    }


    /**
     * Retourne un nouveau {@link Translator} construit par {@link TranslatorFactory} .
     *
     * @param destFieldType Le type SQL du champ destination
     * @param decimalSeparator Le separateur decimal utilise dans le champ source
     * @param inputDateFormat Le format date utilise dans le champ source
     *
     * @return Le nouveau Translator
     */
    static Translator getNewTranslator(int destFieldType, String decimalSeparator,
        String inputDateFormat) {
        return TranslatorFactory.newTranslator(TranslatorFactory
            .convertSqlTypeToFieldType(destFieldType), decimalSeparator, inputDateFormat);
    }


    /**
     * Construit une HashMap ayant pour <code>code</code> la liste des
     * <code>sourceSystem</code> trouve dans la table <code>PM_SOURCE_SYSTEM</code> et
     * <code>valeur</code> un liste de {@link Translator} cree par rapport au type SQL
     * <code>destTypeSQLField</code> .
     *
     * @param con
     * @param destTypeSQLField Le type SQL du champ destination
     *
     * @return La liste des {@link Translator} par <code>sourceSystem</code>
     *
     * @exception SQLException -
     */
    private static Map getAllSourceTranslator(Connection con, int destTypeSQLField)
            throws SQLException {
        HashMap sourceTranslator = new HashMap();

        Statement stmt = con.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("select * from PM_SOURCE_SYSTEM");
            while (rs.next()) {
                sourceTranslator.put(rs.getString("SOURCE_SYSTEM"),
                    getNewTranslator(destTypeSQLField, rs.getString("DECIMAL_SEPARATOR"),
                        rs.getString("DATE_FORMAT")));
            }
        }
        finally {
            stmt.close();
        }

        return sourceTranslator;
    }
}
