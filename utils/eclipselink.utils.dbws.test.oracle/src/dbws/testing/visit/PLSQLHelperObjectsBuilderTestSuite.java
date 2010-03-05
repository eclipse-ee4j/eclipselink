/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS Oracle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.util.List;

//JUnit4 imports
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherWalker;
import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLHelperObjectsBuilder;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.NUMERIC_TYPE;
import static org.eclipse.persistence.platform.database.jdbc.JDBCTypes.VARCHAR_TYPE;
import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.PLSQLBoolean;

//testing imports
import static dbws.testing.visit.DBWSTestHelper.ARECORD_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.ARECORD_DATABASETYPE;
import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC1;
import static dbws.testing.visit.DBWSTestHelper.PROC2;
import static dbws.testing.visit.DBWSTestHelper.PROC3;
import static dbws.testing.visit.DBWSTestHelper.PROC4;
import static dbws.testing.visit.DBWSTestHelper.PROC5;
import static dbws.testing.visit.DBWSTestHelper.PROC6;
import static dbws.testing.visit.DBWSTestHelper.TBL1_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL1_DATABASETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL2_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL2_DATABASETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL3_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL3_DATABASETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL4_DATABASETYPE;

public class PLSQLHelperObjectsBuilderTestSuite extends BuilderTestSuite {
    
    @Test
    public void p1Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC1);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC1.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC1,
            typesForMethod.length == 2);
        DatabaseType first = typesForMethod[0];
        tbl1Asserts(first);
        DatabaseType second = typesForMethod[1];
        assertEquals("wrong databaseType for procedure " + PROC1 + "'s second argument",
            VARCHAR_TYPE, second);
    }
    protected void tbl1Asserts(DatabaseType tbl1DatabaseType) {
        assertTrue("tbl1DatabaseType incorrect databaseType",
            tbl1DatabaseType.getTypeName().equals(TBL1_DATABASETYPE));
        assertTrue("tbl1DatabaseType is not complex", tbl1DatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt = (ComplexDatabaseType)tbl1DatabaseType;
        assertTrue("tbl1DatabaseType is not a collection", cdt.isCollection());
        PLSQLCollection coll = (PLSQLCollection)cdt;
        assertTrue("wrong compatibleType for tbl1DatabaseType",
            coll.getCompatibleType().equals(TBL1_COMPATIBLETYPE));
        assertEquals("wrong nestedType for compatibleType", VARCHAR_TYPE, coll.getNestedType());
    }

    @Test
    public void p2Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC2);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC2.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC2,
            typesForMethod.length == 2);
        DatabaseType first = typesForMethod[0];
        tbl2Asserts(first);
        DatabaseType second = typesForMethod[1];
        tbl2Asserts(second);
        assertEquals(PROC2 + "'s second argument is not equal to the first", first, second);
    }
    protected void tbl2Asserts(DatabaseType tbl2DatabaseType) {
        assertTrue("tbl2DatabaseType incorrect databaseType",
            tbl2DatabaseType.getTypeName().equals(TBL2_DATABASETYPE));
        assertTrue("tbl2DatabaseType is not complex", tbl2DatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt = (ComplexDatabaseType)tbl2DatabaseType;
        assertTrue("tbl2DatabaseType is not a collection", cdt.isCollection());
        PLSQLCollection coll = (PLSQLCollection)cdt;
        assertTrue("wrong compatibleType for tbl2DatabaseType", 
            coll.getCompatibleType().equals(TBL2_COMPATIBLETYPE));
        assertEquals("wrong nestedType for databaseType", NUMERIC_TYPE, coll.getNestedType());
    }

    @Test
    public void p3Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC3);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC3.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC3,
            typesForMethod.length == 1);
        tbl3Asserts(typesForMethod[0]);
    }
    protected void tbl3Asserts(DatabaseType tbl3DatabaseType) {
        assertTrue("tbl3DatabaseType incorrect databaseType",
            tbl3DatabaseType.getTypeName().equals(TBL3_DATABASETYPE));
        assertTrue("tbl3DatabaseType is not complex", tbl3DatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt = (ComplexDatabaseType)tbl3DatabaseType;
        assertTrue("tbl3DatabaseType is not a collection", cdt.isCollection());
        PLSQLCollection coll = (PLSQLCollection)cdt;
        assertTrue("wrong compatibleType for tbl3DatabaseType", 
            coll.getCompatibleType().equals(TBL3_COMPATIBLETYPE));
        DatabaseType nestedType = coll.getNestedType();
        aRecordAsserts(nestedType);
    }
    
    protected void aRecordAsserts(DatabaseType aRecordDatabaseType) {
        assertEquals("aRecordDatabaseType incorrect databaseType",
            ARECORD_DATABASETYPE, aRecordDatabaseType.getTypeName());
        assertTrue("aRecordDatabaseType is not Complex", aRecordDatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt2 = (ComplexDatabaseType)aRecordDatabaseType;
        assertTrue("aRecordDatabaseType is not a record", cdt2.isRecord());
        PLSQLrecord record = (PLSQLrecord)cdt2;
        assertTrue("wrong number of fields in aRecordDatabaseType", record.getFields().size() == 3);
        assertTrue("wrong compatibleType for aRecordDatabaseType",
            record.getCompatibleType().equals(ARECORD_COMPATIBLETYPE));
        List<PLSQLargument> fields = record.getFields();
        tbl1Asserts(fields.get(0).databaseType);
        tbl2Asserts(fields.get(1).databaseType);
        assertEquals("wrong databaseType for aRecordDatabaseType field(2)",
            PLSQLBoolean, fields.get(2).databaseType);
    }

    @Test
    public void p4Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC4);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC4.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC4,
            typesForMethod.length == 1);
        aRecordAsserts(typesForMethod[0]);
    }

    @Test
    public void p5Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC5);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC5.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC5,
            typesForMethod.length == 2);
        DatabaseType first = typesForMethod[0];
        aRecordAsserts(first);
        DatabaseType second = typesForMethod[1];
        aRecordAsserts(second);
        assertEquals(PROC5 + "'s second argument is not equal to the first", first, second);
    }

    @Test
    public void p6Test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC6);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder(builder);
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        pModel.getJPubType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC6.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC6,
            typesForMethod.length == 1);
        tbl4Asserts(typesForMethod[0]);
    }
    
    protected void tbl4Asserts(DatabaseType tbl4DatabaseType) {
        assertEquals("tbl4DatabaseType incorrect databaseType", TBL4_DATABASETYPE, tbl4DatabaseType.getTypeName());
        assertTrue("tbl4DatabaseType is not Complex", tbl4DatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt = (ComplexDatabaseType)tbl4DatabaseType;
        assertTrue("tbl4DatabaseType is not a collection", cdt.isCollection());
        PLSQLCollection coll = (PLSQLCollection)cdt;
        DatabaseType nestedType = coll.getNestedType();
        tbl2Asserts(nestedType);
    }

/*
    @Test
    public void p7Test() {
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, PACKAGE_NAME,
            username.toUpperCase(), PROC7, builder);
        PLSQLHelperObjectsBuilder helperObjectsBuilder = new PLSQLHelperObjectsBuilder();
        PublisherWalker walker = new PublisherWalker(helperObjectsBuilder);
        builder.getSqlType().accept(walker);
        DatabaseType[] typesForMethod = helperObjectsBuilder.getTypesForMethod(PROC7.toUpperCase());
        assertTrue("incorrect number of types returned for procedure " + PROC7,
            typesForMethod.length == 1);
        DatabaseType bRecordDatabaseType = typesForMethod[0];
        assertTrue("bRecordDatabaseType is not Complex", bRecordDatabaseType.isComplexDatabaseType());
        ComplexDatabaseType cdt = (ComplexDatabaseType)bRecordDatabaseType;
        assertTrue("bRecordDatabaseType is not a record", cdt.isRecord());
        PLSQLrecord record = (PLSQLrecord)cdt;
        assertTrue("wrong number of fields in bRecordDatabaseType", record.getFields().size() == 2);
        assertTrue("wrong compatibleType for bRecordDatabaseType",
            record.getCompatibleType().equals(BRECORD_COMPATIBLETYPE));
        List<PLSQLargument> fields = record.getFields();
        aRecordAsserts(fields.get(0).databaseType);
        tbl4Asserts(fields.get(1).databaseType);
    }
*/    
}