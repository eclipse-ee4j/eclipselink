/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - June 10 2011, created DDL parser package
//     David McCann - July 2011, visit tests
package dbws.testing;

//javase imports
import java.sql.Connection;
import java.sql.SQLException;


//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


//testing imports
import dbws.testing.advancedjdbcpackage.AdvancedJDBCPackageTestSuite;
import dbws.testing.blobtype.BlobTypeTestSuite;
import dbws.testing.complexplsqlsf.ComplexPLSQLSFTestSuite;
import dbws.testing.complexplsqlsp.ComplexPLSQLSPTestSuite;
import dbws.testing.customsql.CustomSQLTestSuite;
import dbws.testing.invalidinput.InvalidInputTestSuite;
import dbws.testing.invalidpackage.InvalidPackageTestSuite;
import dbws.testing.iottype.IOTTypeTestSuite;
import dbws.testing.namingtransformer.NamingTransformerTestSuite;
import dbws.testing.nchartype.NcharTypeTestSuite;
import dbws.testing.nonassociativeplsql.NonAssociativePLSQLCollectionTestSuite;
import dbws.testing.objecttabletype.ObjectTableTypeTestSuite;
import dbws.testing.objecttype.ObjectTypeInLowercaseTestSuite;
import dbws.testing.objecttype.ObjectTypeTestSuite;
import dbws.testing.optionalarguments.OptionalArgumentTestSuite;
import dbws.testing.ordescriptor.ORDescriptorTestSuite;
import dbws.testing.oxdescriptor.OXDescriptorTestSuite;
import dbws.testing.plsqlcollection.PLSQLCollectionTestSuite;
import dbws.testing.plsqlrecord.PLSQLRecordTestSuite;
import dbws.testing.plsqlrecord2.PLSQLRecord2TestSuite;
import dbws.testing.prependedpackage.PrependedPackageTestSuite;
import dbws.testing.prependedschema.PrependedSchemaTestSuite;
import dbws.testing.rowtype.RowTypeTestSuite;
import dbws.testing.secondarysql.SecondarySQLTestSuite;
import dbws.testing.shadowddlgeneration.ShadowDDLGenerationTestSuite;
import dbws.testing.simplenestedtable.SimpleNestedTableTestSuite;
import dbws.testing.simpleplsqlsf.SimplePLSQLSFTestSuite;
import dbws.testing.simpleplsqlsp.SimplePLSQLSPTestSuite;
import dbws.testing.simplesf.SimpleSFTestSuite;
import dbws.testing.simplesp.SimpleSPTestSuite;
import dbws.testing.simpletablewithnestedsql.SimpleTableWithNestedSQLTestSuite;
import dbws.testing.strongrefcursor.StrongRefCursorTestSuite;
import dbws.testing.tabletype.TableTypeTestSuite;
import dbws.testing.toplevelsimpleplsqlsp.TopLevelSimplePLSQLSPTestSuite;
import dbws.testing.types.TypesTestSuite;
import dbws.testing.varray.VArrayTestSuite;
import dbws.testing.verylongidentifier.VeryLongIdentifierTestSuite;
import dbws.testing.weakrefcursor.WeakRefCursorTestSuite;
import static dbws.testing.DBWSTestSuite.buildConnection;

@RunWith(Suite.class)
@SuiteClasses({
    AdvancedJDBCPackageTestSuite.class,
    BlobTypeTestSuite.class,
    ComplexPLSQLSFTestSuite.class,
    ComplexPLSQLSPTestSuite.class,
    CustomSQLTestSuite.class,
    InvalidInputTestSuite.class,
    InvalidPackageTestSuite.class,
    IOTTypeTestSuite.class,
    NamingTransformerTestSuite.class,
    NcharTypeTestSuite.class,
    NonAssociativePLSQLCollectionTestSuite.class,
    ObjectTableTypeTestSuite.class,
    ObjectTypeInLowercaseTestSuite.class,
    ObjectTypeTestSuite.class,
    OptionalArgumentTestSuite.class,
    ORDescriptorTestSuite.class,
    OXDescriptorTestSuite.class,
    PLSQLCollectionTestSuite.class,
    PLSQLRecordTestSuite.class,
    PLSQLRecord2TestSuite.class,
    PrependedPackageTestSuite.class,
    PrependedSchemaTestSuite.class,
    RowTypeTestSuite.class,
    SecondarySQLTestSuite.class,
    ShadowDDLGenerationTestSuite.class,
    SimpleNestedTableTestSuite.class,
    SimplePLSQLSFTestSuite.class,
    SimplePLSQLSPTestSuite.class,
    SimpleSFTestSuite.class,
    SimpleSPTestSuite.class,
    SimpleTableWithNestedSQLTestSuite.class,
    StrongRefCursorTestSuite.class,
    TableTypeTestSuite.class,
    TopLevelSimplePLSQLSPTestSuite.class,
    TypesTestSuite.class,
    VArrayTestSuite.class,
    VeryLongIdentifierTestSuite.class,
    WeakRefCursorTestSuite.class
  }
)
public class AllTests {

    //shared JUnit fixtures
    public static Connection conn = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException {
        conn = buildConnection();
    }
}
