/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial impl
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.jpa.plsql;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.plsql.Address;
import org.eclipse.persistence.testing.models.jpa.plsql.Employee;
import org.eclipse.persistence.testing.models.jpa.plsql.Phone;

public class PLSQLTestSuite extends JUnitTestCase {
        
    public static Test suite() {
        TestSuite suite = new TestSuite("PLSQLTests");
        suite.addTest(new PLSQLTestSuite("testSetup"));
        suite.addTest(new PLSQLTestSuite("testSimpleProcedure"));
        suite.addTest(new PLSQLTestSuite("testSimpleFunction"));
        suite.addTest(new PLSQLTestSuite("testRecordOut"));
        suite.addTest(new PLSQLTestSuite("testTableOut"));
        suite.addTest(new PLSQLTestSuite("testEmpRecordInOut"));
        suite.addTest(new PLSQLTestSuite("testConsultant"));
        return suite;
    }
    
    public PLSQLTestSuite(String name) {
        super(name);
    }
    
    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    @Override
    public String getPersistenceUnitName() {
        return "plsql";
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        if (!getServerSession().getPlatform().isOracle()) {
            warning("This test can only be run on Oracle.");
            return;
        }
        createTables(getDatabaseSession());
    }
    
    public void createTables(DatabaseSession session) {
        // Tables
        try {
            session.executeNonSelectingSQL("DROP TABLE PLSQL_ADDRESS");
        } catch (Exception ignore) {}
        try {
            session.executeNonSelectingSQL("DROP TABLE PLSQL_CONSULTANT");
        } catch (Exception ignore) {}
        session.executeNonSelectingSQL("CREATE TABLE PLSQL_ADDRESS ("
                + "ADDRESS_ID NUMBER(10) NOT NULL, STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(30), PRIMARY KEY (ADDRESS_ID))");
        session.executeNonSelectingSQL("INSERT INTO PLSQL_ADDRESS ("
                + "ADDRESS_ID, CITY) values (1234, 'Ottawa')");
        
        // Procedures
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_IN(P_VARCHAR IN VARCHAR2 DEFAULT '', P_BOOLEAN IN BOOLEAN, P_BINARY_INTEGER IN BINARY_INTEGER, "
                        + "P_DEC IN DEC, P_INT IN INT, P_NATURAL IN NATURAL, P_NATURALN IN NATURALN, "
                        + "P_PLS_INTEGER IN PLS_INTEGER, P_POSITIVE IN POSITIVE, P_POSITIVEN IN POSITIVEN, P_SIGNTYPE IN SIGNTYPE, P_NUMBER IN NUMBER) AS "                
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_IN_DEFAULTS(P_VARCHAR IN VARCHAR2 DEFAULT '', P_BOOLEAN IN BOOLEAN DEFAULT TRUE, P_BINARY_INTEGER IN BINARY_INTEGER DEFAULT 0, "
                + "P_DEC IN DEC DEFAULT 0, P_INT IN INT DEFAULT 0, P_NATURAL IN NATURAL DEFAULT 1, P_NATURALN IN NATURALN DEFAULT 1, "
                + "P_PLS_INTEGER IN PLS_INTEGER DEFAULT 0, P_POSITIVE IN POSITIVE DEFAULT 1, P_POSITIVEN IN POSITIVEN DEFAULT 1, P_SIGNTYPE IN SIGNTYPE DEFAULT 1, P_NUMBER IN NUMBER DEFAULT 0) AS "                
        + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_OUT(P_VARCHAR OUT VARCHAR2, P_BOOLEAN OUT BOOLEAN, P_BINARY_INTEGER OUT BINARY_INTEGER, "
                        + "P_DEC OUT DEC, P_INT OUT INT, P_NATURAL OUT NATURAL, " //P_NATURALN OUT NATURALN, "
                        + "P_PLS_INTEGER OUT PLS_INTEGER, P_POSITIVE OUT POSITIVE, " //P_POSITIVEN OUT POSITIVEN, "
                        + "P_SIGNTYPE OUT SIGNTYPE, P_NUMBER OUT NUMBER) AS "                
                + "BEGIN P_VARCHAR := 'varchar'; P_BOOLEAN := true; P_BINARY_INTEGER := 123; "
                + "P_DEC := 1; P_INT := 1; P_NATURAL := 1; " //P_NATURALN := 1; "
                        + "P_PLS_INTEGER := 1; P_POSITIVE := 1; " //P_POSITIVEN := 1; "
                        + "P_SIGNTYPE := 1; P_NUMBER := 123; \n"
                + "END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_INOUT(P_VARCHAR IN OUT VARCHAR2, P_BOOLEAN IN OUT BOOLEAN, P_BINARY_INTEGER IN OUT BINARY_INTEGER, "
                        + "P_DEC IN OUT DEC, P_INT IN OUT INT, P_NATURAL IN OUT NATURAL, P_NATURALN IN OUT NATURALN, "
                        + "P_PLS_INTEGER IN OUT PLS_INTEGER, P_POSITIVE IN OUT POSITIVE, P_POSITIVEN IN OUT POSITIVEN, P_SIGNTYPE IN OUT SIGNTYPE, P_NUMBER IN OUT NUMBER) AS "                
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_IN(P_ADDRESS IN PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_IN_DATA(P_ADDRESS IN PLSQL_ADDRESS%ROWTYPE, P_LOCAL IN VARCHAR2) AS "
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_OUT(P_ADDRESS OUT PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_OUT_DATA(P_ADDRESS OUT PLSQL_ADDRESS%ROWTYPE, P_LOCAL OUT VARCHAR2) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON'; P_LOCAL := 'Local';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_INOUT(P_ADDRESS IN OUT PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_INOUT_DATA(P_ADDRESS IN OUT PLSQL_ADDRESS%ROWTYPE, P_LOCAL IN OUT VARCHAR2) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON'; P_LOCAL := 'Local';  END;");

        // Functions
        session.executeNonSelectingSQL("CREATE OR REPLACE FUNCTION PLSQL_SIMPLE_IN_FUNC(P_VARCHAR IN VARCHAR2, P_BOOLEAN IN BOOLEAN, P_BINARY_INTEGER IN BINARY_INTEGER, "
                        + "P_DEC IN DEC, P_INT IN INT, P_NATURAL IN NATURAL, P_NATURALN IN NATURALN, "
                        + "P_PLS_INTEGER IN PLS_INTEGER, P_POSITIVE IN POSITIVE, P_POSITIVEN IN POSITIVEN, P_SIGNTYPE IN SIGNTYPE, P_NUMBER IN NUMBER) RETURN BOOLEAN AS "                
                + "BEGIN RETURN TRUE; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE FUNCTION PLSQL_ADDRESS_OUT_FUNC RETURN PLSQL_ADDRESS%ROWTYPE AS "
                + " P_ADDRESS PLSQL_ADDRESS%ROWTYPE; "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON';  RETURN P_ADDRESS; END;");
     
        // Types
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_EMP_REC FORCE");
        } catch (Exception ignore) {}
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_ADDRESS_REC FORCE");
        } catch (Exception ignore) {}
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_PHONE_REC FORCE");
        } catch (Exception ignore) {}
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_ADDRESS_REC AS OBJECT ("
                + "ADDRESS_ID NUMBER(10), STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(2))");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_PHONE_REC AS OBJECT ("
                + "AREA_CODE VARCHAR2(3), P_NUM VARCHAR2(7))");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_PHONE_LIST AS VARRAY(30) OF PLSQL_P_PLSQL_PHONE_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_EMP_REC AS OBJECT ("
                + "EMP_ID NUMBER(10), NAME VARCHAR2(30), ACTIVE NUMBER(1), ADDRESS PLSQL_P_PLSQL_ADDRESS_REC, PHONES PLSQL_P_PLSQL_PHONE_LIST)");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_CITY_LIST AS VARRAY(255) OF VARCHAR2(100)");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_ADDRESS_LIST AS VARRAY(255) OF PLSQL_P_PLSQL_ADDRESS_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_EMP_LIST AS VARRAY(255) OF PLSQL_P_PLSQL_EMP_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE PACKAGE PLSQL_P AS \n"
                    + "TYPE PLSQL_ADDRESS_REC IS RECORD (ADDRESS_ID NUMBER(10), STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(2)); \n"
                    + "TYPE PLSQL_ADDRESS_CUR IS REF CURSOR RETURN PLSQL_ADDRESS%ROWTYPE; \n"
                    + "TYPE PLSQL_ADDRESS_REC_CUR IS REF CURSOR RETURN PLSQL_ADDRESS_REC; \n"
                    + "TYPE PLSQL_PHONE_REC IS RECORD (AREA_CODE VARCHAR2(3), P_NUM VARCHAR2(7)); \n"
                    + "TYPE PLSQL_PHONE_LIST IS TABLE OF PLSQL_PHONE_REC INDEX BY BINARY_INTEGER; \n"
                    + "TYPE PLSQL_EMP_REC IS RECORD (EMP_ID NUMBER(10), NAME VARCHAR2(30), ACTIVE BOOLEAN, ADDRESS PLSQL_ADDRESS_REC, PHONES PLSQL_PHONE_LIST); \n"
                    + "TYPE PLSQL_CITY_LIST IS TABLE OF VARCHAR2(100) INDEX BY BINARY_INTEGER; \n"
                    + "TYPE PLSQL_ADDRESS_LIST IS TABLE OF PLSQL_ADDRESS_REC INDEX BY BINARY_INTEGER; \n"
                    + "TYPE PLSQL_EMP_LIST IS TABLE OF PLSQL_EMP_REC INDEX BY BINARY_INTEGER; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_IN(P_CITY_LIST IN PLSQL_CITY_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_CITY_LIST_OUT(P_CITY_LIST OUT PLSQL_CITY_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_CITY_LIST_INOUT(P_CITY_LIST IN OUT PLSQL_CITY_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_IN(P_ADDRESS_LIST IN PLSQL_ADDRESS_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_OUT(P_ADDRESS_LIST OUT PLSQL_ADDRESS_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_INOUT(P_ADDRESS_LIST IN OUT PLSQL_ADDRESS_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_IN(P_EMP_LIST IN PLSQL_EMP_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_OUT(P_EMP_LIST OUT PLSQL_EMP_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_INOUT(P_EMP_LIST IN OUT PLSQL_EMP_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_IN(P_EMP IN PLSQL_EMP_REC, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_OUT(P_EMP OUT PLSQL_EMP_REC, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_INOUT(P_EMP IN OUT PLSQL_EMP_REC, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_CUR_OUT(P_ADDRESS OUT PLSQL_ADDRESS_CUR); \n"
                    + "PROCEDURE PLSQL_ADDRESS_REC_CUR_OUT(P_ADDRESS OUT PLSQL_ADDRESS_REC_CUR); \n"
                    + "END PLSQL_P; \n");
        session.executeNonSelectingSQL("CREATE OR REPLACE PACKAGE BODY PLSQL_P AS \n"
                    + "PROCEDURE PLSQL_CITY_LIST_IN(P_CITY_LIST IN PLSQL_CITY_LIST, P_CITY IN VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "NULL; \n"
                        + "END PLSQL_CITY_LIST_IN; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_OUT(P_CITY_LIST OUT PLSQL_CITY_LIST, P_CITY OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; \n"
                        + "P_CITY_LIST(1) := 'Ottawa'; \n"
                        + "END PLSQL_CITY_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_INOUT(P_CITY_LIST IN OUT PLSQL_CITY_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; \n"
                        + "P_CITY_LIST(1) := 'Ottawa'; \n"
                        + "END PLSQL_CITY_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_IN(P_ADDRESS_LIST IN PLSQL_ADDRESS_LIST, P_CITY IN VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "NULL; \n"
                        + "END PLSQL_ADDRESS_LIST_IN; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_OUT(P_ADDRESS_LIST OUT PLSQL_ADDRESS_LIST, P_CITY OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; "
                        + "END PLSQL_ADDRESS_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_INOUT(P_ADDRESS_LIST IN OUT PLSQL_ADDRESS_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; \n"
                        + "END PLSQL_ADDRESS_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_IN(P_EMP_LIST IN PLSQL_EMP_LIST, P_CITY IN VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "NULL; \n"
                        + "END PLSQL_EMP_LIST_IN; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_OUT(P_EMP_LIST OUT PLSQL_EMP_LIST, P_CITY OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; "
                        + "END PLSQL_EMP_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_INOUT(P_EMP_LIST IN OUT PLSQL_EMP_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; "
                        + "END PLSQL_EMP_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_EMP_IN(P_EMP IN PLSQL_EMP_REC, P_CITY IN VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "NULL; \n"
                        + "END PLSQL_EMP_IN; \n"
                    + "PROCEDURE PLSQL_EMP_OUT(P_EMP OUT PLSQL_EMP_REC, P_CITY OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; "
                        + "END PLSQL_EMP_OUT; \n"
                    + "PROCEDURE PLSQL_EMP_INOUT(P_EMP IN OUT PLSQL_EMP_REC, P_CITY IN OUT VARCHAR2) AS \n"
                        + "BEGIN \n"
                        + "P_CITY := 'Nepean'; \n"
                        + "END PLSQL_EMP_INOUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_CUR_OUT(P_ADDRESS OUT PLSQL_ADDRESS_CUR) AS \n"
                        + "BEGIN \n"
                        + "OPEN P_ADDRESS FOR SELECT * FROM PLSQL_ADDRESS; \n"
                        + "END PLSQL_ADDRESS_CUR_OUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_REC_CUR_OUT(P_ADDRESS OUT PLSQL_ADDRESS_REC_CUR) AS \n"
                        + "BEGIN \n"
                        + "OPEN P_ADDRESS FOR SELECT * FROM PLSQL_ADDRESS; \n"
                        + "END PLSQL_ADDRESS_REC_CUR_OUT; \n"
                    + "END PLSQL_P; \n");
        session.executeNonSelectingSQL("CREATE TABLE PLSQL_CONSULTANT ("
                + "EMP_ID NUMBER(10), NAME VARCHAR2(30), ACTIVE NUMBER(1), ADDRESS PLSQL_P_PLSQL_ADDRESS_REC, PHONES PLSQL_P_PLSQL_PHONE_LIST, PRIMARY KEY (EMP_ID))");
    }
    
    /**
     * Test a simple procedure.
     */
    public void testSimpleProcedure() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createNamedQuery("PLSQL_SIMPLE_IN_DEFAULTS");
            query.setParameter("P_VARCHAR", "test");
            query.executeUpdate();
            query = em.createNamedQuery("PLSQL_SIMPLE_IN_DEFAULTS");
            query.setParameter("P_BOOLEAN", true);
            query.executeUpdate();
            query = em.createNamedQuery("PLSQL_SIMPLE_IN_DEFAULTS");
            query.setParameter("P_VARCHAR", "test");
            query.setParameter("P_BOOLEAN", true);
            query.setParameter("P_BINARY_INTEGER", 1);
            query.setParameter("P_DEC", 1);
            query.setParameter("P_INT", 1);
            query.setParameter("P_NATURAL", 1);
            query.setParameter("P_NATURALN", 1);
            query.setParameter("P_PLS_INTEGER", 1);
            query.setParameter("P_POSITIVE", 1);
            query.setParameter("P_POSITIVEN", 1);
            query.setParameter("P_SIGNTYPE", 1);
            query.setParameter("P_NUMBER", 1);
            query.executeUpdate();
            query.executeUpdate();
            query = em.createNamedQuery("PLSQL_SIMPLE_IN_DEFAULTS");
            query.setParameter("P_BOOLEAN", true);
            query.executeUpdate();
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test a simple function.
     */
    public void testSimpleFunction() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createNamedQuery("PLSQL_SIMPLE_IN_FUNC");
            query.setParameter("P_VARCHAR", "test");
            query.setParameter("P_BOOLEAN", true);
            query.setParameter("P_BINARY_INTEGER", 1);
            query.setParameter("P_DEC", 1);
            query.setParameter("P_INT", 1);
            query.setParameter("P_NATURAL", 1);
            query.setParameter("P_NATURALN", 1);
            query.setParameter("P_PLS_INTEGER", 1);
            query.setParameter("P_POSITIVE", 1);
            query.setParameter("P_POSITIVEN", 1);
            query.setParameter("P_SIGNTYPE", 1);
            query.setParameter("P_NUMBER", 1);
            int result = (Integer)query.getSingleResult();
            if (result != 1) {
                fail("Incorrect result.");
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test a record out procedure.
     */
    public void testRecordOut() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createNamedQuery("PLSQL_ADDRESS_OUT");
            Object result = query.getSingleResult();
            if (!(result instanceof Address)) {
                fail("Incorrect result:" + result);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test a record out procedure.
     */
    public void testEmpRecordInOut() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createNamedQuery("PLSQL_EMP_INOUT");
            Employee employee = new Employee();
            employee.setId(new BigDecimal(1234));
            employee.setName("Bob");
            employee.setAddress(new Address());
            employee.getAddress().setId(new BigDecimal(1234));
            employee.getAddress().setCity("Ottawa");
            employee.getAddress().setNumber(12345);
            employee.getAddress().setState("ON");
            employee.getAddress().setStreet("Bank");
            Phone phone = new Phone();
            phone.setAreaCode("613");
            phone.setNumber("1234567");
            employee.getPhones().add(phone);
            query.setParameter("P_EMP", employee);
            Object[] result = (Object[])query.getSingleResult();
            if (!(result[0] instanceof Employee)) {
                fail("Incorrect result:" + result);
            }
            compareObjects(employee, result[0]);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test a table out procedure.
     */
    public void testTableOut() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createNamedQuery("PLSQL_ADDRESS_LIST_OUT");
            Object[] result = (Object[])query.getSingleResult();
            if (!(result[0] instanceof List)) {
                fail("Incorrect result:" + result);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test Consultant, relational object with o/r data-types.
     */
    public void testConsultant() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("Select c from Consultant c").getResultList();
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
}