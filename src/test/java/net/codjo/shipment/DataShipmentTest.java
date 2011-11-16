/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.shipment;
import fakedb.FakeDriver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Test de {@link DataShipment}.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class DataShipmentTest extends TestCase {

    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_proceed() throws Exception {
        DataField[] fields = new DataField[2];
        fields[0] = new FakeDataField("COL_A", "VAL_A");
        fields[1] = new FakeDataField("COL_B", "VAL_B");
        FakeErrorHandler errorHandler = new FakeErrorHandler();
        DataShipment ds = new DataShipment("SOURCE", fields, "DEST", errorHandler);

        Object[][] rs = {
              {"COL_A", "COL_B"},
              {"a1", "b1"},
              {"a2", "b2"}
        };
        FakeDriver.getDriver().pushResultSet(rs, "select * from SOURCE");

        FakeDriver.getDriver()
              .pushUpdateConstraint("insert into DEST (COL_A, COL_B) values (VAL_A2, VAL_B2)");
        FakeDriver.getDriver()
              .pushUpdateConstraint("insert into DEST (COL_A, COL_B) values (VAL_A1, VAL_B1)");

        ds.proceed(FakeDriver.getDriver().connect("jdbc:fakeDriver", null));

        assertEquals(((FakeDataField)fields[0]).calledNumber, 2);
        assertEquals(((FakeDataField)fields[1]).calledNumber, 2);
        assertEquals("Aucune erreur", errorHandler.calledNumber, 0);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_proceed_Error() throws Exception {
        DataField[] fields = new DataField[2];
        fields[0] = new FakeDataField("COL_A", "VAL_A");
        fields[1] = new FakeDataField("COL_B", "VAL_B");
        FakeErrorHandler errorHandler = new FakeErrorHandler();
        DataShipment ds = new DataShipment("SOURCE", fields, "DEST", errorHandler);

        Object[][] rs =
              {
                    {"COL_A", "COL_B"},
                    {"a1", "b1"},
                    {"xx", "b2"},
                    {"a3", "b3"}
              };
        FakeDriver.getDriver().pushResultSet(rs, "select * from SOURCE");

        // VAL_A3 car Field COL_A est appele 3 fois (dont une en erreur)
        // VAL_B2 car Field COL_B est appele seulement 2 fois car la deuxieme
        // ligne est en erreur
        FakeDriver.getDriver()
              .pushUpdateConstraint("insert into DEST (COL_A, COL_B) values (VAL_A3, VAL_B2)");
        FakeDriver.getDriver()
              .pushUpdateConstraint("insert into DEST (COL_A, COL_B) values (VAL_A1, VAL_B1)");

        ds.proceed(FakeDriver.getDriver().connect("jdbc:fakeDriver", null));

        assertEquals(((FakeDataField)fields[0]).calledNumber, 3);
        assertEquals(((FakeDataField)fields[1]).calledNumber, 2);
        assertEquals("Aucune erreur", errorHandler.calledNumber, 1);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_proceed_whereClause() throws Exception {
        DataField[] fields = new DataField[2];
        fields[0] = new FakeDataField("COL_A", "VAL_A");
        fields[1] = new FakeDataField("COL_B", "VAL_B");
        FakeErrorHandler errorHandler = new FakeErrorHandler();
        DataShipment ds = new DataShipment("SOURCE", fields, "DEST", errorHandler);
        ds.setSelectWhereClause("COL_A <> '5'");
        Object[][] rs = {
              {"COL_A", "COL_B"},
              {"a1", "b1"}
        };
        FakeDriver.getDriver().pushResultSet(rs,
                                             "select * from SOURCE " + "where COL_A <> '5'");

        FakeDriver.getDriver()
              .pushUpdateConstraint("insert into DEST (COL_A, COL_B) values (VAL_A1, VAL_B1)");

        ds.proceed(FakeDriver.getDriver().connect("jdbc:fakeDriver", null));

        assertEquals(((FakeDataField)fields[0]).calledNumber, 1);
        assertEquals(((FakeDataField)fields[1]).calledNumber, 1);
        assertEquals("Aucune erreur", errorHandler.calledNumber, 0);
        assertEquals("Aucune erreur", "COL_A <> '5'", ds.getSelectWhereClause());
    }


    static class FakeDataField implements DataField {
        private int calledNumber = 0;
        private String convertedField;
        private String dbDestField;


        /**
         * Constructeur de FakeDataField
         *
         * @param dbDestField    Description of the Parameter
         * @param convertedField Description of the Parameter
         */
        FakeDataField(String dbDestField, String convertedField) {
            this.dbDestField = dbDestField;
            this.convertedField = convertedField;
        }


        public Object convertField(ResultSet rs)
              throws SQLException, ConversionFailureException {
            calledNumber++;
            if (rs.getString(1).equals("xx")) {
                throw new ConversionFailureException("Erreur en xx");
            }
            return convertedField + calledNumber;
        }


        public int getConvertedSqlType() {
            return Types.VARCHAR;
        }


        public String getDBDestFieldName() {
            return dbDestField;
        }
    }

    static class FakeErrorHandler implements DataShipmentError {
        private int calledNumber = 0;


        public void manageRowError(Connection con, ResultSet row, Exception ex,
                                   String destTable) throws SQLException {
            calledNumber++;
        }
    }
}
