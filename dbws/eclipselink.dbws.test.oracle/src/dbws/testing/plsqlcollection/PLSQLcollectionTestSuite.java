/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Dave McCann - May 2008, created DBWS test package

package dbws.testing.plsqlcollection;

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Array;
import org.w3c.dom.Document;

//Java extension libraries

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.ProjectHelper;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.Oracle10Platform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

import dbws.testing.AllTests;

import static dbws.testing.DBWSTestHelper.DATABASE_DDL_CREATE_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_DDL_DEBUG_KEY;
import static dbws.testing.DBWSTestHelper.DATABASE_DDL_DROP_KEY;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_DDL_CREATE;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_DDL_DEBUG;
import static dbws.testing.DBWSTestHelper.DEFAULT_DATABASE_DDL_DROP;

public class PLSQLcollectionTestSuite {

    static final String CREATE_DDL =
        "CREATE OR REPLACE PACKAGE SOMEPACKAGE AS" +
        "    TYPE TBL1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
        "    PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);" +
        "END SOMEPACKAGE;" +
        "|" +
        "CREATE OR REPLACE PACKAGE BODY SOMEPACKAGE AS" +
        "    PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS" +
        "    BEGIN" +
        "        NULL;" +
        "    END P1;" +
        "END SOMEPACKAGE;" +
        "|" +
        "CREATE OR REPLACE TYPE SOMEPACKAGE_TBL1 AS TABLE OF VARCHAR2(111)|" ;

    static final String DROP_DDL =
        "DROP PACKAGE BODY SOMEPACKAGE|" +
        "DROP PACKAGE SOMEPACKAGE|" +
        "DROP TYPE SOMEPACKAGE_TBL1|";

    static final String CONSTANT_PROJECT_BUILD_VERSION =
        "Eclipse Persistence Services - some version (some build date)";
    static final String QUERY_NAME = "PLSQLcollectionQuery";

    static final String DATABASE_USERNAME_KEY = "db.user";
    static final String DATABASE_PASSWORD_KEY = "db.pwd";
    static final String DATABASE_URL_KEY = "db.url";

    static XMLComparer comparer = new XMLComparer();
    static XMLParser xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();

    // testsuite fixture(s)
    static Project project = null;
    static Session session = null;
    static String username = null;
    static String password = null;
    static String url = null;
    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUpProject() {
        final String ddlCreateProp = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreateProp)) {
            ddlCreate = true;
        }
        final String ddlDropProp = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDropProp)) {
            ddlDrop = true;
        }
        final String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }

        username = System.getProperty(DATABASE_USERNAME_KEY);
        if (username == null) {
            fail("error retrieving database username");
        }
        password = System.getProperty(DATABASE_PASSWORD_KEY);
        if (password == null) {
            fail("error retrieving database password");
        }
        url = System.getProperty(DATABASE_URL_KEY);
        if (url == null) {
            fail("error retrieving database url");
        }
        project = new Project();
        project.setName("PLSQLcollectionTestSuite");
        ObjectRelationalDataTypeDescriptor t1Descriptor = new ObjectRelationalDataTypeDescriptor();
        t1Descriptor.setAlias("T1");
        t1Descriptor.setJavaClassName("org.eclipse.persistence.testing.tests.plsqlcollection.T1");
        t1Descriptor.descriptorIsAggregate();
        t1Descriptor.getQueryManager();
        project.addDescriptor(t1Descriptor);
        PLSQLCollection simplArray = new PLSQLCollection();
        simplArray.setTypeName("SOMEPACKAGE.TBL1");
        simplArray.setCompatibleType("SOMEPACKAGE_TBL1");
        simplArray.setNestedType(JDBCTypes.VARCHAR_TYPE);
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("SOMEPACKAGE.P1");
        call.addNamedArgument("SIMPLARRAY", simplArray);
        call.addNamedArgument("FOO", JDBCTypes.VARCHAR_TYPE, 10);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("SIMPLARRAY", Array.class);
        query.addArgument("FOO", String.class);
        query.setCall(call);
        t1Descriptor.getQueryManager().addQuery(QUERY_NAME, query);

        if (ddlCreate) {
            try {
                AllTests.runDdl(CREATE_DDL, ddlDebug);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            try {
                AllTests.runDdl(DROP_DDL, ddlDebug);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    static final String TEST_PROJECT_CONTROL_DOC =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\" " +
           "xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" " +
           "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
           "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
           "xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\">" +
           "<name>PLSQLcollectionTestSuite</name>" +
           "<class-mapping-descriptors>" +
                 "<class-mapping-descriptor xsi:type=\"object-relational-class-mapping-descriptor\">" +
                    "<class>org.eclipse.persistence.testing.tests.plsqlcollection.T1</class>" +
                    "<alias>T1</alias>" +
                    "<events/>" +
                    "<querying>" +
                       "<queries>" +
                          "<query name=\"PLSQLcollectionQuery\" xsi:type=\"data-modify-query\">" +
                             "<arguments>" +
                                "<argument name=\"SIMPLARRAY\">" +
                                   "<type>java.sql.Array</type>" +
                                "</argument>" +
                                "<argument name=\"FOO\">" +
                                   "<type>java.lang.String</type>" +
                                "</argument>" +
                             "</arguments>" +
                             "<call xsi:type=\"plsql-stored-procedure-call\">" +
                                "<procedure-name>SOMEPACKAGE.P1</procedure-name>" +
                                "<arguments>" +
                                    "<argument xsi:type=\"plsql-collection\">" +
                                       "<name>SIMPLARRAY</name>" +
                                       "<index>0</index>" +
                                       "<type-name>SOMEPACKAGE.TBL1</type-name>" +
                                       "<compatible-type>SOMEPACKAGE_TBL1</compatible-type>" +
                                       "<java-type>java.util.ArrayList</java-type>" +
                                       "<nested-type type-name=\"VARCHAR_TYPE\" xsi:type=\"jdbc-type\"/>" +
                                    "</argument>" +
                                    "<argument type-name=\"VARCHAR_TYPE\" xsi:type=\"jdbc-type\">" +
                                      "<name>FOO</name>" +
                                      "<index>1</index>" +
                                      "<length>10</length>" +
                                    "</argument>" +
                                "</arguments>" +
                             "</call>" +
                         "</query>" +
                    "</queries>" +
              "</querying>" +
              "<descriptor-type>aggregate</descriptor-type>" +
              "<caching>" +
                "<cache-size>-1</cache-size>" +
              "</caching>" +
              "<remote-caching>" +
                "<cache-size>-1</cache-size>" +
              "</remote-caching>" +
              "<instantiation/>" +
              "<copying xsi:type=\"instantiation-copy-policy\"/>" +
              "</class-mapping-descriptor>" +
           "</class-mapping-descriptors>" +
        "</object-persistence>";
    @Test
    public void toProjectXML() throws IllegalArgumentException, IllegalAccessException,
        SecurityException, NoSuchFieldException {
        ObjectPersistenceWorkbenchXMLProject runtimeProject =
            new ObjectPersistenceWorkbenchXMLProject();
        XMLTransformationMapping versionMapping =
            (XMLTransformationMapping)runtimeProject.getDescriptor(Project.class).
                getMappings().get(0);
        TransformerBasedFieldTransformation  versionTransformer =
            (TransformerBasedFieldTransformation)versionMapping.getFieldTransformations().get(0);
        Field transformerField =
            TransformerBasedFieldTransformation.class.getDeclaredField("transformer");
        transformerField.setAccessible(true);
        ConstantTransformer constantTransformer =
            (ConstantTransformer)transformerField.get(versionTransformer);
        constantTransformer.setValue(CONSTANT_PROJECT_BUILD_VERSION);
        XMLContext context = new XMLContext(runtimeProject);
        XMLMarshaller marshaller = context.createMarshaller();
        Document testDoc = marshaller.objectToXML(project);
        Document controlDoc = xmlParser.parse(new StringReader(TEST_PROJECT_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, testDoc));
    }

    @Test
    public void fromProjectXML() {
        XRDynamicClassLoader xrdecl =
            new XRDynamicClassLoader(PLSQLcollectionTestSuite.class.getClassLoader());
        Project projectFromXML = XMLProjectReader.read(new StringReader(TEST_PROJECT_CONTROL_DOC),
            xrdecl);
        DatasourceLogin login = new DatabaseLogin();
        login.setUserName(username);
        login.setPassword(password);
        ((DatabaseLogin)login).setConnectionString(url);
        ((DatabaseLogin)login).setDriverClassName("oracle.jdbc.OracleDriver");
        Platform platform = new Oracle10Platform();
        ConversionManager cm = platform.getConversionManager();
        cm.setLoader(xrdecl);
        login.setDatasourcePlatform(platform);
        ((DatabaseLogin)login).bindAllParameters();
        projectFromXML.setDatasourceLogin(login);
        ProjectHelper.fixOROXAccessors(projectFromXML, null);
        xrdecl.dontGenerateSubclasses();
        ClassDescriptor t1Descriptor = projectFromXML.getDescriptorForAlias("T1");
        DatabaseQuery query = t1Descriptor.getQueryManager().getQuery(QUERY_NAME);
        assertTrue(QUERY_NAME + " is wrong type of query: " + query.getClass().getSimpleName(),
            query.isDataModifyQuery());

        session = projectFromXML.createDatabaseSession();
        session.dontLogMessages();
        t1Descriptor = session.getDescriptorForAlias("T1");
        Class<?> t1Clz = t1Descriptor.getJavaClass();
        ((DatabaseSession)session).login();
        String[] elements = {"first string", "second string", "third string"};
        NonSynchronizedVector queryArgs = new NonSynchronizedVector();
        queryArgs.add(elements);
        queryArgs.add("barf");
        boolean worked = false;
        String msg = null;
        try {
            session.executeQuery(QUERY_NAME, t1Clz, queryArgs);
            worked = true;
        }
        catch (Exception e) {
            msg = e.getMessage();
        }
        assertTrue("invocation somePackage.p1 failed: " + msg, worked);

    }
}
