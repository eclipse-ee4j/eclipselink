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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.customfeatures;

import java.io.*;
import java.util.List;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.customfeatures.*;
import org.eclipse.persistence.tools.schemaframework.PackageDefinition;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;

public class CustomFeaturesJUnitTestSuite extends JUnitTestCase {
    private static int empId;
    protected static int NUM_INSERTS = 200;

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

    public void testSetup() {
        ServerSession session = JUnitTestCase.getServerSession("customfeatures");
        new EmployeeTableCreator().replaceTables(session);
        buildOraclePackage(session);
        buildOracleStoredProcedureReadFromEmployeeInOut(session);
        buildOracleStoredProcedureReadFromEmployeeCursor(session);
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
            Employee readEmp = em.find(Employee.class, empId);
            clearCache("customfeatures");
            if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
                closeEntityManager(em);
                fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
            }
        } catch (Exception exception) {
            closeEntityManager(em);
            fail("entityManager.refresh(removedObject) threw a wrong exception: " + exception.getMessage());
        }
        closeEntityManager(em);
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
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
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
            Employee readEmp = (Employee) em.createNamedQuery("ReadEmployeeInOut").setParameter("ID", emp.getId()).getSingleResult();
            clearCache("customfeatures");
            if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
                fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
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
            clearCache("customfeatures");
            EntityManager newEM = createEntityManager("customfeatures");
            Employee readEmp = (Employee) newEM.createNamedQuery("ReadEmployeeCursor").setParameter("ID", emp.getId()).getSingleResult();
            clearCache("customfeatures");
            if (!getServerSession("customfeatures").compareObjects(readEmp, emp)) {
                fail("Object: " + readEmp + " does not match object that was written: " + emp + ". See log (on finest) for what did not match.");
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
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
            proc.addOutputArgument("nchar_v", Character.class);

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

}
