/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import fakedb.FakeResultSet;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Tests de la classe {@link UnformattedDataField}.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class UnformattedDataFieldTest extends TestCase {
    private UnformattedDataField dataField;

    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField() throws Exception {
        Object[][] matrix = {
                {"COL_A", "COL_B"},
                {"BOBO", "GEX"}
            };
        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();
        assertEquals(dataField.convertField(rs), "GEX");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_FieldNotFound()
            throws Exception {
        Object[][] matrix = {
                {"COL_A", "COL_C"},
                {"b", new Integer(123)}
            };
        FakeResultSet rs = new FakeResultSet(matrix);
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
    public void test_convertField_getConvertedSqlType()
            throws Exception {
        assertEquals(dataField.getConvertedSqlType(), Types.VARCHAR);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_getDBDestFie() throws Exception {
        assertEquals(dataField.getDBDestFieldName(), "DEST_FIELD");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_nullArguments()
            throws Exception {
        try {
            new UnformattedDataField(null, "DEST_FIELD", Types.VARCHAR);
            fail("Le test doit echouer, le champ source est null");
        }
        catch (IllegalArgumentException sF) {
            assertTrue("Parametres invalides".equals(sF.getMessage()));
        }
        try {
            new UnformattedDataField("COL_B", null, Types.VARCHAR);
            fail("Le test doit echouer, le champ destination est null");
        }
        catch (IllegalArgumentException sF) {
            assertTrue("Parametres invalides".equals(sF.getMessage()));
        }
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of the Exception
     */
    public void test_convertField_nullValue() throws Exception {
        Object[][] matrix = {
                {"COL_A", "COL_B"},
                {"BOBO", null}
            };
        FakeResultSet rs = new FakeResultSet(matrix);
        rs.next();
        assertEquals(dataField.convertField(rs), null);
    }


    /**
     * The JUnit setup method
     *
     * @exception SQLException Description of the Exception
     */
    protected void setUp() throws SQLException {
        dataField = new UnformattedDataField("COL_B", "DEST_FIELD", Types.VARCHAR);
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {}
}
