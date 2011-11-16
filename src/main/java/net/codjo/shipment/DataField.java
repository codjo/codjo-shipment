/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Cette interface permet d'extraire un champ source et de le convertir au format de la
 * colonne destination.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.2 $
 */
public interface DataField {
    /**
     * Extrait et convertit le champ.
     *
     * @param rs Le {@link ResultSet} pointant sur une ligne de la table source
     *
     * @return Le champ converti
     *
     * @exception SQLException Erreur DB ou champ absent
     * @exception ConversionFailureException Echec de la conversion
     */
    public Object convertField(ResultSet rs)
            throws SQLException, ConversionFailureException;


    /**
     * Retourne le type sql du champ converti.
     *
     * @return Le type sql
     *
     * @see java.sql.Types
     */
    public int getConvertedSqlType();


    /**
     * Retourne le nom physique du champ destination.
     *
     * @return Le champ destination
     */
    public String getDBDestFieldName();
}
