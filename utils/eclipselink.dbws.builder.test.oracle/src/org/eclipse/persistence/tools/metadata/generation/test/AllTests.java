/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - July 2013 - Initial Implementation
package org.eclipse.persistence.tools.metadata.generation.test;

import static org.eclipse.persistence.internal.oxm.Constants.EMPTY_STRING;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.tools.metadata.generation.JPAMetadataGenerator;
import org.eclipse.persistence.tools.metadata.generation.test.cursortype.CursorTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.functiontype.FunctionTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.objecttabletype.ObjectTableTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.objecttype.ObjectTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.plsqlcollectiontype.PLSQLCollectionTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.plsqlrecordtype.PLSQLRecordTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.procedureoverload.ProcedureOverloadTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.proceduretype.ProcedureTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.tabletype.TableTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.typerowtype.TypeRowTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.varraytype.VArrayTypeTestSuite;
import org.eclipse.persistence.tools.metadata.generation.test.xmltype.XMLTypeTestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


@RunWith(Suite.class)
@SuiteClasses({
    CursorTypeTestSuite.class,
    FunctionTypeTestSuite.class,
    ObjectTypeTestSuite.class,
    ObjectTableTypeTestSuite.class,
    PLSQLCollectionTypeTestSuite.class,
    PLSQLRecordTypeTestSuite.class,
    ProcedureOverloadTestSuite.class,
    ProcedureTypeTestSuite.class,
    TableTypeTestSuite.class,
    TypeRowTypeTestSuite.class,
    VArrayTypeTestSuite.class,
    XMLTypeTestSuite.class
})

public class AllTests {
    // Database connect info
    public static final String DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
    public static final String DATABASE_USERNAME_KEY = "db.user";
    public static final String DATABASE_PASSWORD_KEY = "db.pwd";
    public static final String DATABASE_URL_KEY = "db.url";
    public static final String DEFAULT_DATABASE_USERNAME = "user";
    public static final String DEFAULT_DATABASE_PASSWORD = "password";
    public static final String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    public static final String DATABASE_PLATFORM = "org.eclipse.persistence.platform.database.oracle.Oracle12Platform";
    // DDL creation
    public static final String DATABASE_DDL_CREATE_KEY = "db.ddl.create";
    public static final String DEFAULT_DATABASE_DDL_CREATE = "false";
    public static final String DATABASE_DDL_DROP_KEY = "db.ddl.drop";
    public static final String DEFAULT_DATABASE_DDL_DROP = "false";
    public static final String DATABASE_DDL_DEBUG_KEY = "db.ddl.debug";
    public static final String DEFAULT_DATABASE_DDL_DEBUG = "false";

    public static final String DEFAULT_PACKAGE_NAME = "metadatagen";

    //shared JUnit fixtures
    public static Connection conn = null;
    public static DatabasePlatform databasePlatform = null;

    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException {
        conn = buildConnection();
        databasePlatform = JPAMetadataGenerator.loadDatabasePlatform(DATABASE_PLATFORM);
        comparer.setIgnoreOrder(true);
    }

    /**
     * Helper method that removes empty text nodes from a Document.
     * This is typically called prior to comparing two documents
     * for equality.
     *
     */
    public static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals(EMPTY_STRING)) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

    /**
     * Returns the given org.w3c.dom.Document as a String.
     *
     */
    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }

    /**
     * Returns a connection to the Database using System properties, or defaults
     * if a given System property is not set.
     */
    public static Connection buildConnection() throws ClassNotFoundException, SQLException {
        String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        Class.forName(DATABASE_DRIVER);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Executes the provided DDL statement on the database connection.
     */
    public static void runDdl(Connection conn, String ddl, boolean printStackTrace) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(ddl);
            pStmt.execute();
        } catch (SQLException e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }
}
