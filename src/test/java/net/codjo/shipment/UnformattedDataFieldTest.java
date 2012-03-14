/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import fakedb.FakeResultSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;

public class UnformattedDataFieldTest extends TestCase {
    private UnformattedDataField dataField;


    public void test_convertField() throws Exception {
        Object[][] matrix = {
              {"COL_A", "COL_B"},
              {"BOBO", "GEX"}
        };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals(dataField.convertField(rs), "GEX");
    }


    public void test_convertField_FieldNotFound()
          throws Exception {
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


    public void test_convertField_getConvertedSqlType()
          throws Exception {
        assertEquals(dataField.getConvertedSqlType(), Types.VARCHAR);
    }


    public void test_convertField_getDBDestFie() throws Exception {
        assertEquals(dataField.getDBDestFieldName(), "DEST_FIELD");
    }


    public void test_convertField_nullArguments() throws Exception {
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


    public void test_convertField_nullValue() throws Exception {
        Object[][] matrix = {
              {"COL_A", "COL_B"},
              {"BOBO", null}
        };
        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals(dataField.convertField(rs), null);
    }


    @Override
    protected void setUp() throws SQLException {
        dataField = new UnformattedDataField("COL_B", "DEST_FIELD", Types.VARCHAR);
    }
}
