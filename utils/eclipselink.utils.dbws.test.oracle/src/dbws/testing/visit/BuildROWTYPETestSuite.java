package dbws.testing.visit;

//JUnit4 imports
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleArrayType;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleObjectType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherWalker;
import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLHelperObjectsBuilder;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.INTEGER_TYPE;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.NUMERIC_TYPE;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.VARCHAR_TYPE;

//domain-specific (testing) imports
import static dbws.testing.visit.DBWSTestHelper.F17_PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROCF17;
import static dbws.testing.visit.DBWSTestHelper.PROCF17_NAME;

public class BuildROWTYPETestSuite extends BuilderTestSuite {

    @Test
    public void Tesmanproc17() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setName(PROCF17);
        pModel.setReturnType("ns1:" + "TESMANPACK_ROWTYPE_SQL_TYPE");
        pModel.setCatalogPattern(F17_PACKAGE_NAME);
        pModel.setProcedurePattern(PROCF17_NAME + "%");
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(
            PROCF17_NAME.toUpperCase());
        testAssertsFor17(typesForMethod, helperObjectsBuilder);
        typesForMethod = helperObjectsBuilder.getTypesForMethod(
            PROCF17_NAME + "b".toUpperCase());
        testAssertsFor17b(typesForMethod, helperObjectsBuilder);
    }
    
    protected void testAssertsFor17(DatabaseType[] types, PLSQLHelperObjectsBuilder helperObjectsBuilder) {
        assertTrue("incorrect number of types returned for procedure " + PROCF17_NAME,
            types.length == 2);
        DatabaseType first = types[0];
        assertEquals("wrong databaseType for procedure " + PROCF17_NAME + "'s first argument",
            INTEGER_TYPE, first);
        DatabaseType second = types[1];
        assertTrue("wrong databaseType for procedure " + PROCF17_NAME + "'s second argument",
            second.isComplexDatabaseType() && ((ComplexDatabaseType)second).isRecord());
        PLSQLrecord rec = (PLSQLrecord)second;
        List<PLSQLargument> fields = rec.getFields();
        assertTrue("wrong number of fields for PLSQLrecord " + rec.getTypeName(),
            fields.size() == 2);
        PLSQLargument firstField = fields.get(0);
        assertEquals("wrong field name " + firstField.name + "for TESMAN_TABLE2%ROWTYPE's first field",
            "SRNO", firstField.name);
        assertEquals("wrong databaseType for TESMAN_TABLE2%ROWTYPE's first field",
            NUMERIC_TYPE, firstField.databaseType);
        PLSQLargument secondField = fields.get(1);
        assertEquals("wrong field name " + secondField.name + "for TESMAN_TABLE2%ROWTYPE's second field",
            "DETAIL", secondField.name);
        DatabaseType secondFieldType = secondField.databaseType;
        assertTrue("wrong databaseType for second field of TESMAN_TABLE2%ROWTYPE",
            secondFieldType.isComplexDatabaseType() && ((ComplexDatabaseType)secondFieldType).isJDBCType());
        OracleObjectType oot = (OracleObjectType)secondFieldType;
        testAssertsForTYPE2(oot, helperObjectsBuilder);
    }

    protected void testAssertsForTYPE2(OracleObjectType oot, PLSQLHelperObjectsBuilder helperObjectsBuilder) {
        assertEquals("wrong type name for oot", "TESMAN_TYPE2", 
            helperObjectsBuilder.trimOffSchemaName(oot.getTypeName()));
        Map<String, DatabaseType> ootFields = oot.getFields();
        assertTrue("wrong number of fields for Object type " + oot.getTypeName(),
            ootFields.size() == 5);
        DatabaseType pnrField = ootFields.get("PNR");
        assertEquals("wrong field type for pnrField", NUMERIC_TYPE, pnrField);
        DatabaseType companyField = ootFields.get("COMPANY");
        assertEquals("wrong field type for companyField", VARCHAR_TYPE, companyField);
        DatabaseType seField = ootFields.get("SE");
        assertEquals("wrong field type for seField", VARCHAR_TYPE, seField);
        DatabaseType scripField = ootFields.get("SCRIP");
        assertEquals("wrong field type for scripField", VARCHAR_TYPE, scripField);
        DatabaseType ttField = ootFields.get("TT");
        assertTrue("wrong databaseType for ttField",
            ttField.isComplexDatabaseType() && ((ComplexDatabaseType)ttField).isJDBCType());
        OracleObjectType oot2 = (OracleObjectType)ttField;
        assertEquals("wrong type name for oot2", "TESMAN_TYPE1",
            helperObjectsBuilder.trimOffSchemaName(oot2.getTypeName()));
        Map<String, DatabaseType> oot2Fields = oot2.getFields();
        assertTrue("wrong number of fields for Object type " + oot2.getTypeName(),
            oot2Fields.size() == 4);
        DatabaseType acctField = oot2Fields.get("ACCT");
        assertEquals("wrong field type for acctField",NUMERIC_TYPE, acctField);
        DatabaseType countryField = oot2Fields.get("COUNTRY");
        assertEquals("wrong field type for countryField", VARCHAR_TYPE, countryField);
        DatabaseType addrDivisionField = oot2Fields.get("ADDR_DIVISION");
        assertEquals("wrong field type for addrDivisionField", VARCHAR_TYPE, addrDivisionField);
        DatabaseType stateField = oot2Fields.get("STATE");
        assertEquals("wrong field type for stateField", VARCHAR_TYPE, stateField);
    }

    protected void testAssertsFor17b(DatabaseType[] types, PLSQLHelperObjectsBuilder helperObjectsBuilder) {
        assertTrue("incorrect number of types returned for procedure " + PROCF17_NAME,
            types.length == 2);
        DatabaseType first = types[0];
        assertTrue("wrong databaseType for procedure " + PROCF17_NAME + "b's first argument",
            first.isComplexDatabaseType() && ((ComplexDatabaseType)first).isRecord());
        PLSQLrecord rec = (PLSQLrecord)first;
        List<PLSQLargument> fields = rec.getFields();
        assertTrue("wrong number of fields for PLSQLrecord " + rec.getTypeName(),
            fields.size() == 2);
        PLSQLargument firstField = fields.get(0);
        assertEquals("wrong field name " + firstField.name + "for TESMAN_TABLE3%ROWTYPE's first field",
            "ID", firstField.name);
        assertEquals("wrong databaseType for TESMAN_TABLE3%ROWTYPE's first field",
            NUMERIC_TYPE, firstField.databaseType);
        PLSQLargument secondField = fields.get(1);
        assertEquals("wrong field name " + secondField.name + "for TESMAN_TABLE3%ROWTYPE's second field",
            "TT3", secondField.name);
        DatabaseType secondFieldType = secondField.databaseType;
        assertTrue("wrong databaseType for second field of TESMAN_TABLE3%ROWTYPE",
            secondFieldType.isComplexDatabaseType() && 
            ((ComplexDatabaseType)secondFieldType).isJDBCType() &&
            ((ComplexDatabaseType)secondFieldType).isCollection() );
        OracleArrayType oat = (OracleArrayType)secondFieldType;
        DatabaseType nestedType = oat.getNestedType();
        assertTrue("wrong databaseType for nested type of " + oat.getTypeName(),
            nestedType.isComplexDatabaseType() && 
            ((ComplexDatabaseType)nestedType).isJDBCType() );
        OracleObjectType oot = (OracleObjectType)nestedType;
        testAssertsForTYPE2(oot, helperObjectsBuilder);
    }
}