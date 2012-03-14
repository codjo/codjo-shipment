/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import java.sql.ResultSet;
import net.codjo.imports.common.translator.Translator;
import fakedb.FakeResultSet;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Tests de la classe {@link MonoFormattedDataField}.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class MonoFormattedDataFieldTest extends TestCase {
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_FieldNotFound()
            throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        MonoFormattedDataField dataField =
            new MonoFormattedDataField("COL_B", "COL_B", translator);
        Object[][] matrix = {
                {"COL_A", "COL_C"},
                {"b", 123}
            };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        try {
            dataField.convertField(rs);
            fail("Le test doit echouer ! la colonne COL_B n'existe pas");
        }
        catch (SQLException ex) {
            ; // c'est normal !
        }
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_dateValue() throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        MonoFormattedDataField dataField =
            new MonoFormattedDataField("COL_B", "COL_B", translator);

        Object[][] matrix = {
                {"COL_A", "COL_B"},
                {"BOBO", "01/10/01"}
            };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals(dataField.convertField(rs), java.sql.Date.valueOf("2001-10-01"));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_getConvertedSqlType()
            throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        MonoFormattedDataField dataField =
            new MonoFormattedDataField("COL_B", "COL_B", translator);
        assertEquals(dataField.getConvertedSqlType(), Types.DATE);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_getDBDestField()
            throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        MonoFormattedDataField dataField =
            new MonoFormattedDataField("COL_B", "COL_DEST", translator);
        assertEquals(dataField.getDBDestFieldName(), "COL_DEST");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_nullArguments()
            throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        try {
            new MonoFormattedDataField(null, "COL_B", translator);
            fail("Le test doit echouer, le champ source est null");
        }
        catch (IllegalArgumentException sF) {
            assertEquals("Parametres invalides", sF.getMessage());
        }

        try {
            new MonoFormattedDataField("COL_B", "COL_B", null);
            fail("Le test doit echouer, le translator est null");
        }
        catch (IllegalArgumentException fI) {
            assertEquals("Parametres invalides", fI.getMessage());
        }
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_nullValue() throws Exception {
        Translator translator =
            DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");

        MonoFormattedDataField dataField =
            new MonoFormattedDataField("COL_B", "COL_B", translator);

        Object[][] matrix = {
                {"COL_A", "COL_B"},
                {"BOBO", null}
            };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals(dataField.convertField(rs), null);
    }
}
