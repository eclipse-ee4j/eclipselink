/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.customfeatures;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamResult;
import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.customfeatures.*;
import org.eclipse.persistence.tools.schemaframework.PackageDefinition;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;

public class CustomFeaturesJUnitTestSuite extends JUnitTestCase {
    private static int empId;
    protected static int NUM_INSERTS = 200;
    public static String dbVersion;

    public CustomFeaturesJUnitTestSuite() {
        super();
    }

    public CustomFeaturesJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomFeaturesJUnitTestSuite");
        suite.addTest(new CustomFeaturesJUnitTestSuite("testSetup"));
        suite.addTest(new CustomFeaturesJUnitTestSuite("testNCharXMLType"));
        suite.addTest(new CustomFeaturesJUnitTestSuite("testBatchInserts"));
        suite.addTest(new CustomFeaturesJUnitTestSuite("testBatchUpdates"));
        suite.addTest(new CustomFeaturesJUnitTestSuite("testNamedStoredProcedureInOutQuery"));
        suite.addTest(new CustomFeaturesJUnitTestSuite("testNamedStoredProcedureCursorQuery"));
        return suite;
    }

    public void testSetup() throws SQLException {
        ServerSession session = JUnitTestCase.getServerSession("customfeatures");
        new EmployeeTableCreator().replaceTables(session);
        buildOraclePackage(session);
        buildOracleStoredProcedureReadFromEmployeeInOut(session);
        buildOracleStoredProcedureReadFromEmployeeCursor(session);
        
        Accessor accessor = session.getDefaultConnectionPool().acquireConnection();
        try {
            accessor.incrementCallCount(session);
            DatabaseMetaData metaData = accessor.getConnection().getMetaData(); 
            String dbMajorMinorVersion = Integer.toString(metaData.getDatabaseMajorVersion()) + '.' + Integer.toString(metaData.getDatabaseMinorVersion()); 
            String dbProductionVersion =  metaData.getDatabaseProductVersion();
            // For Helper.compareVersions to work the first digit in the passed version String should be part of the version,
            // i.e. "10.2.0.2 ..." is ok, but "Oracle 10g ... 10.2.0.2..." is not.
            dbVersion = dbProductionVersion.substring(dbProductionVersion.indexOf(dbMajorMinorVersion));
        } finally {
            accessor.decrementCallCount();
            session.getDefaultConnectionPool().releaseConnection(accessor);
        }
    }

    /**
     * Tests a NChar and XML Type with Document.
     */
    public void testNCharXMLType() {
        EntityManager em = createEntityManager("customfeatures");
        beginTransaction(em);
        Employee emp = null;
        try {
            emp = new Employee();
            emp.setResume_xml(resume0());
            char nCh = '\u0410';
            emp.setEmpNChar(nCh);
            emp.setResume_dom(documentFromString(resume0()));
            em.persist(emp);
            empId = emp.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        try {
            em.clear();
            clearCache("customfeatures");
            if(isOnServer()) {
                beginTransaction(em);
            }
            Employee readEmp = em.find(Employee.class, empId);
            compare(readEmp, emp);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Tests a Native Batch Writing as batch inserts.
     */
    public void testBatchInserts() {
        EntityManager em = createEntityManager("customfeatures");
        beginTransaction(em);
        try {
            for (int i = 0; i < NUM_INSERTS; i++) {
                Employee emp = new Employee();
                emp.setResume_xml(resume0());
                emp.setResume_dom(documentFromString(resume0()));
                char nCh = '\u0410';
                emp.setEmpNChar(nCh);
                em.persist(emp);
            }
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Tests a Native Batch Writing as batch updates with
     * OptimisticLockingException.
     */
    public void testBatchUpdates() {
        EntityManager em = createEntityManager("customfeatures");
        beginTransaction(em);
        List emps = em.createQuery("SELECT OBJECT(e) FROM Employee e").getResultList();
        try {
            for (int i = 0; i < emps.size(); i++) {
                Employee e = (Employee) emps.get(i);
                String newName = ((Employee) emps.get(i)).getName() + i + "test";
                e.setName(newName);
                e.setVersion(e.getVersion() - 1);
            }
            em.flush();
            commitTransaction(em);
            fail("OptimisticLockingException is not thrown!");
        } catch (Exception exception) {
            if (exception.getMessage().indexOf("org.eclipse.persistence.exceptions.OptimisticLockException") == -1) {
                fail("it's not the right exception");
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Tests a @NamedStoredProcedureQuery with store procedure IN_OUT parameter,
     * and XML Type using String
     */
    public void testNamedStoredProcedureInOutQuery() {
        if(Helper.compareVersions(dbVersion, "11.2.0.2") < 0) {
            // Oracle db 11.2.0.2 or later is required for this test
            return;
        }
        EntityManager em = createEntityManager("customfeatures");
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setResume_xml(resume1());
            emp.setResume_dom(documentFromString(resume0()));
            char nCh = '\u0400';
            emp.setEmpNChar(nCh);
            em.persist(emp);
            commitTransaction(em);
            em.clear();
            clearCache("customfeatures");
            if(isOnServer()) {
                beginTransaction(em);
            }
            // note that readEmployee will have only two attributes set: id and empNChar
            Employee readEmp = (Employee) em.createNamedQuery("ReadEmployeeInOut").setParameter("ID", emp.getId()).getSingleResult();
            if (emp.getEmpNChar() != readEmp.getEmpNChar()) {
                fail("readEmp.getEmpNChar() == " + readEmp.getEmpNChar() + ", does not match empNChar of the object that was written: " + emp.getEmpNChar());
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Tests a @NamedStoredProcedureQuery with store procedure ref Cursor, and
     * XML Type using String
     */
    public void testNamedStoredProcedureCursorQuery() {
        EntityManager em = createEntityManager("customfeatures");
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setResume_xml(resume1());
            emp.setResume_dom(documentFromString(resume0()));
            char nCh = '\u0400';
            emp.setEmpNChar(nCh);
            emp.setName("Edward Xu");
            em.persist(emp);
            commitTransaction(em);
            em.clear();
            clearCache("customfeatures");
            if(isOnServer()) {
                beginTransaction(em);
            }
            Employee readEmp = (Employee) em.createNamedQuery("ReadEmployeeCursor").setParameter("ID", emp.getId()).getSingleResult();
            compare(readEmp, emp);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public static String resume0() {
        String resume = "<resume>\n";
        resume += "  <first-name>Bob</first-name>\n";
        resume += "   <last-name>Jones</last-name>\n";
        resume += "   <age>45</age>\n";
        resume += "   <education>\n";
        resume += "     <degree>BCS</degree>\n";
        resume += "     <degree>MBA</degree>\n";
        resume += "   </education>\n";
        resume += "</resume>";
        return resume;
    }

    public static String resume1() {
        String resume = "<resume>\n";
        resume += "  <first-name>Frank</first-name>\n";
        resume += "   <last-name>Cotton</last-name>\n";
        resume += "   <age>27</age>\n";
        resume += "   <education>\n";
        resume += "     <degree>BCS</degree>\n";
        resume += "   </education>\n";
        resume += "</resume>";
        return resume;
    }

    public static Document documentFromString(String xmlString) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(xmlString.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            return doc;
        } catch (Exception ex) {
            fail("Unable to create document due to: " + ex.getMessage());
        }
        return null;
    }

    public PackageDefinition buildOraclePackage(Session session) {
        if (TestCase.supportsStoredProcedures(session)) {
            PackageDefinition types = new PackageDefinition();
            types.setName("Cursor_Type");
            types.addStatement("Type Any_Cursor is REF CURSOR");

            SchemaManager schema = new SchemaManager(((DatabaseSession) session));
            schema.replaceObject(types);
            return types;
        } else {
            fail("store procedure is not supported!");
            return null;
        }
    }

    public void buildOracleStoredProcedureReadFromEmployeeInOut(Session session) {
        if (TestCase.supportsStoredProcedures(session)) {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("Read_Employee_InOut");

            proc.addInOutputArgument("employee_id_v", Integer.class);
            proc.addOutputArgument("nchar_v", "NCHAR");

            String statement = "SELECT NCHARTYPE INTO nchar_v FROM CUSTOM_FEATURE_EMPLOYEE WHERE (ID = employee_id_v)";

            proc.addStatement(statement);
            SchemaManager schema = new SchemaManager(((DatabaseSession) session));
            schema.replaceObject(proc);
        } else
            fail("store procedure is not supported!");
    }

    public void buildOracleStoredProcedureReadFromEmployeeCursor(Session session) {
        if (TestCase.supportsStoredProcedures(session)) {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("Read_Employee_Cursor");

            proc.addArgument("employee_id_v", Integer.class);
            proc.addOutputArgument("RESULT_CURSOR", "CURSOR_TYPE.ANY_CURSOR");
            proc.addStatement("OPEN RESULT_CURSOR FOR SELECT * FROM CUSTOM_FEATURE_EMPLOYEE WHERE (ID = employee_id_v)");

            SchemaManager schema = new SchemaManager(((DatabaseSession) session));
            schema.replaceObject(proc);
        } else
            fail("store procedure is not supported!");
    }

    /*
     * This method is necessary because of a bug in Oracle xdb 11.2.0.2: XDB - 11.2.0.2 DB FORMATS RETURNED XML 
     * This bug describes the following workaround:
     *   In init.ora, add "31151 trace name context forever, level 0x100" 
     * When the bug is fixed (or 11.2.0.2 db configurured as described in the workaround))
     * the special case for 11.2.0.2 Oracle db should be removed:
     *
     *  void compare(Employee readEmp, Employee emp) {
     *       if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
     *           fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
     *       }
     *   }
     */
    void compare(Employee readEmp, Employee emp) {
        if(Helper.compareVersions(dbVersion, "11.2.0.2") >= 0) {
            // Oracle db 11.2.0.2 returns formatted xml, therefore the original and the read back strings might differ.
            String originalReadResume_xml = null;
            String originalResume_xml = null;
            if(!readEmp.getResume_xml().equals(emp.getResume_xml())) {
                originalReadResume_xml = readEmp.getResume_xml(); 
                originalResume_xml = emp.getResume_xml();
                String unformattedReadResume_xml = removeWhiteSpaceFromString(originalReadResume_xml); 
                String unformattedResume_xml = removeWhiteSpaceFromString(originalResume_xml); 
                if(unformattedReadResume_xml.equals(unformattedResume_xml)) {
                    // xml docs defined by the two strings are equivalent
                    // temporary remove the strings from their owner Employees so that it could pass compareObjects
                    readEmp.setResume_xml(null);
                    emp.setResume_xml(null);
                } else {
                    fail("unformattedReadResume_xml == " + unformattedReadResume_xml + "\nunformattedResume_xml == " + unformattedResume_xml);
                }
            }

            // Oracle db 11.2.0.2 returns formatted xml, therefore the original and the read back doms might differ.
            Document originalReadResume_dom = null;
            Document originalResume_dom = null;
            if(!readEmp.getResume_dom().equals(emp.getResume_dom())) {
                originalReadResume_dom = readEmp.getResume_dom(); 
                originalResume_dom = emp.getResume_dom();
                Document unformattedReadResume_dom =  (Document)originalReadResume_dom.cloneNode(true);
                removeEmptyTextNodes(unformattedReadResume_dom);
                Document unformattedResume_dom =  (Document)originalResume_dom.cloneNode(true);
                removeEmptyTextNodes(unformattedResume_dom);
                String unformattedReadResume_dom_toString = convertDocumentToString(unformattedReadResume_dom);
                String unformattedResume_dom_toString = convertDocumentToString(unformattedResume_dom);
                if(unformattedReadResume_dom_toString.equals(unformattedResume_dom_toString)) {
                    // xml docs defined by the two strings are equivalent
                    // temporary remove the doms from their owner Employees so that it could pass compareObjects
                    readEmp.setResume_dom(null);
                    emp.setResume_dom(null);
                } else {
                    fail("unformattedReadResume_dom_toString == " + unformattedReadResume_dom_toString + "\nunformattedResume_dom_toString == " + unformattedResume_dom_toString);
                }
            }

            try {
                if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
                    fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
                }
            } finally {
                if(emp.getResume_xml() == null && originalResume_xml != null) {
                    // set back the temporary removed resume_xml into both objects
                    readEmp.setResume_xml(originalReadResume_xml);
                    emp.setResume_xml(originalResume_xml);
                }
                if(emp.getResume_dom() == null && originalResume_dom != null) {
                    // set back the temporary removed resume_dom into both objects
                    readEmp.setResume_dom(originalReadResume_dom);
                    emp.setResume_dom(originalResume_dom);
                }
            }
        } else {
            // Before version 11.2.0.2 Oracle db returned xml string exactly as it was written (keeping the same format: white spaces, \r, \n etc).
            // No special comparison for resume_xml is required.
            if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
                fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
            }
        }
    }
    
    // Contributed by Blaise
    public static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

    // Contributed by Blaise
    public static String removeWhiteSpaceFromString(String s) {
        String returnString = s.replaceAll(" ", "");
        returnString = returnString.replaceAll("\n", "");
        returnString = returnString.replaceAll("\t", "");
        returnString = returnString.replaceAll("\r", "");

        return returnString;
    }
    
    static String convertDocumentToString(Document doc) {
        XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        xmlTransformer.transform(doc, result);
        return writer.getBuffer().toString();
    }
}
