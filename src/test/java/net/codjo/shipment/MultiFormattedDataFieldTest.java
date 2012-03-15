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
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import net.codjo.imports.common.translator.Translator;
/**
 * Tests de la classe {@link MonoFormattedDataField}.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class MultiFormattedDataFieldTest extends TestCase {
    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField() throws Exception {
        Translator datafieldSlash =
              DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        Translator datafieldHyphen =
              DataShipmentHome.getNewTranslator(Types.DATE, null, "dd-MM-yy");

        HashMap<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);
        sourceTranslator.put("FININF", datafieldHyphen);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_A", "COL_A", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        Object[][] matrix =
              {
                    {"COL_A", "COL_B", "COL_SOURCE_SYSTEM"},
                    {"10-10-66", null, "FININF"},
                    {"11/11/66", null, "GPI"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals("FINIF", multi.convertField(rs), java.sql.Date.valueOf("1966-10-10"));
        rs.next();
        assertEquals("GPI", multi.convertField(rs), java.sql.Date.valueOf("1966-11-11"));
    }


    public void test_convertField_fieldNotFound() throws Exception {
        Translator datafieldSlash =
              DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        Map<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_Z", "COL_A", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        Object[][] matrix =
              {
                    {"COL_A", "COL_B", "COL_SOURCE_SYSTEM"},
                    {null, null, "GPI"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        try {
            multi.convertField(rs);
            fail("Le test doit echouer ! la colonne COL_Z n'existe pas");
        }
        catch (SQLException ex) {
            ; // c'est normal !
        }
    }


    public void test_convertField_getConvertedSqlType() throws Exception {
        Translator datafieldSlash = DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        Map<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_SOURCE", "COL_DEST", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        assertEquals(multi.getConvertedSqlType(), Types.DATE);
    }


    public void test_convertField_getDestFieldName() throws Exception {
        Translator datafieldSlash = DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        HashMap<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_SOURCE", "COL_DEST", "COL_SOURCE_SYSTEM", sourceTranslator);

        assertEquals(multi.getDBDestFieldName(), "COL_DEST");
    }


    public void test_convertField_nullArguments() throws Exception {
        Translator datafieldSlash = DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        Map<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        try {
            new MultiFormattedDataField(null, "COL_DEST", "COL_SOURCE_SYSTEM",
                                        sourceTranslator);
            fail("Le test doit echouer, le champ source est null");
        }
        catch (IllegalArgumentException sF) {
            assertEquals("Parametres invalides", sF.getMessage());
        }

        try {
            new MultiFormattedDataField("COL_A", "COL_DEST", null, sourceTranslator);
            fail("Le test doit echouer, le champ sourceSystem est null");
        }
        catch (IllegalArgumentException sS) {
            assertEquals("Parametres invalides", sS.getMessage());
        }

        try {
            new MultiFormattedDataField("COL_A", "COL_DEST", "COL_SOURCE_SYSTEM", null);
            fail("Le test doit echouer, le sourceTranslator est null");
        }
        catch (IllegalArgumentException sFI) {
            assertEquals("Parametres invalides", sFI.getMessage());
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_nullValue() throws Exception {
        Translator datafieldSlash =
              DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        HashMap<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_A", "COL_A", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        Object[][] matrix =
              {
                    {"COL_A", "COL_B", "COL_SOURCE_SYSTEM"},
                    {null, null, "GPI"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals("GPI", multi.convertField(rs), null);
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_number() throws Exception {
        Translator datafieldSlash =
              DataShipmentHome.getNewTranslator(Types.NUMERIC, ".", null);
        HashMap<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_SOURCE", "COL_DEST", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        assertEquals(multi.getDBDestFieldName(), "COL_DEST");

        Object[][] rsVal = {
              {"COL_SOURCE", "COL_SOURCE_SYSTEM"},
              {"1,200", "GPI"}
        };
        ResultSet rs = new FakeResultSet(rsVal).getStub();
        rs.next();
        try {
            multi.convertField(rs);
            fail("Mauvais format de nombre");
        }
        catch (BadFormatException ex) {
            ; // cas normal
        }
    }


    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of the Exception
     */
    public void test_convertField_sourceNotFound()
          throws Exception {
        Translator datafieldSlash =
              DataShipmentHome.getNewTranslator(Types.DATE, null, "dd/MM/yy");
        HashMap<String, Translator> sourceTranslator = new HashMap<String, Translator>();
        sourceTranslator.put("GPI", datafieldSlash);

        MultiFormattedDataField multi =
              new MultiFormattedDataField("COL_A", "COL_A", "COL_SOURCE_SYSTEM",
                                          sourceTranslator);

        Object[][] matrix =
              {
                    {"COL_A", "COL_B", "COL_SOURCE_SYSTEM"},
                    {"10/10/66", null, "GPI"},
                    {"11/11/66", null, "FININF"}
              };

        ResultSet rs = new FakeResultSet(matrix).getStub();
        rs.next();
        assertEquals("GPI", multi.convertField(rs), java.sql.Date.valueOf("1966-10-10"));
        rs.next();
        try {
            multi.convertField(rs);
            fail("Le test doit echouer, le source 'FININF' n'a pas ete initialise");
        }
        catch (SourceNotFoundException ex) {
            assertEquals("Source inconnue : FININF", ex.getMessage());
        }
    }
}
