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
 *     05/28/2008-1.0M8 Andrei Ilitchev. 
 *       - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.proxyauthentication;

import java.util.*;
import junit.framework.*;

import oracle.jdbc.OracleConnection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.proxyauthentication.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
/**
 * TestSuite to verifying that connectionUser(PAS_CONN) and proxyUser(PAS_PROXY) are used as expected.
 * To run this test suite several users should be setup in the Oracle database,
 * to setup Proxy Authentication users in Oracle db, need to execute in sqlPlus or EnterpriseManager
     * (sql in the following example uses default names):
    1 - connect sys/password as sysdba
        drop user PAS_CONN cascade;
        drop user PAS_PROXY cascade;

    2 - Create connectionUser:
        create user PAS_CONN identified by pas_conn;
        grant connect to PAS_CONN;
        grant resource to PAS_CONN;

    3 - Create proxyUsers:
        create user PAS_PROXY identified by pas_proxy;
        grant connect to PAS_PROXY;
        grant resource to PAS_PROXY;

    4.- Grant proxyUsers connection through connUser
        alter user PAS_PROXY grant connect through PAS_CONN;
        commit;

    5.- Create JPA_PROXY_EMPLOYEE and PROXY_EMPLOYEE_SEQ tables against PAS_CONN user
        CONNECT PAS_CONN/pas_conn;
        DROP TABLE JPA_PROXY_EMPLOYEE;
        CREATE TABLE JPA_PROXY_EMPLOYEE (EMP_ID NUMBER(15) NOT NULL, F_NAME VARCHAR2(40) NULL, L_NAME VARCHAR2(40) NULL, PRIMARY KEY (EMP_ID));
        DROP TABLE PROXY_EMPLOYEE_SEQ;
        CREATE TABLE PROXY_EMPLOYEE_SEQ (SEQ_NAME VARCHAR2(50) NOT NULL, SEQ_COUNT NUMBER(38) NULL, PRIMARY KEY (SEQ_NAME));
        INSERT INTO PROXY_EMPLOYEE_SEQ(SEQ_NAME, SEQ_COUNT) values ('PROXY_EMPLOYEE_SEQ', 1);
        COMMIT;

    6.- Create PROXY_PHONENUMBER table against PAS_PROXY user (you can also create these tables by login as PAS_CONN, and execute new PhoneNumberTableCreator().replaceTables(JUnitTestCase.getServerSession()); in testSetup())
        CONNECT PAS_PROXY/pas_proxy;
        ALTER TABLE PROXY_PHONENUMBER DROP CONSTRAINT FK_PROXY_PHONENUMBER_OWNER_ID;
        DROP TABLE PROXY_PHONENUMBER;
        CREATE TABLE PROXY_PHONENUMBER (OWNER_ID NUMBER(15) NOT NULL, TYPE VARCHAR2(15) NOT NULL, AREA_CODE VARCHAR2(3) NULL, NUMB VARCHAR2(8) NULL, PRIMARY KEY (OWNER_ID, TYPE));
        COMMIT;

    6.- Add object priviledges(ALTER, DELETE, INSERT, REFERENCE, SELECT, UPDATE, INDEX) to JPA_PROXY_EMPLOYEE and PROXY_EMPLOYEE_SEQ for PAS_PROXY user
        CONNECT SYS/PASSWORD as SYSDBA;
        GRANT ALTER ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;
        GRANT DELETE ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;
        GRANT INSERT ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;
        GRANT SELECT ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;
        GRANT UPDATE ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;
        GRANT INDEX ON PAS_CONN.JPA_PROXY_EMPLOYEE TO PAS_PROXY;

        GRANT ALTER ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        GRANT DELETE ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        GRANT INSERT ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        GRANT SELECT ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        GRANT UPDATE ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        GRANT INDEX ON PAS_CONN.PROXY_EMPLOYEE_SEQ TO PAS_PROXY;
        COMMIT;
 */
public class ProxyAuthenticationServerTestSuite extends JUnitTestCase {
    private static Integer empId = null;
    private Employee proxyEmp = null;
    private PhoneNumber proxyPhone = null;
    private static final String PROXY_PU = "proxyauthentication";
    private static boolean shouldOverrideGetEntityManager = false;

    public ProxyAuthenticationServerTestSuite(){
    }

    public ProxyAuthenticationServerTestSuite(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Proxy Authentication Test Suite");

        suite.addTest(new ProxyAuthenticationServerTestSuite("testSetup"));
        suite.addTest(new ProxyAuthenticationServerTestSuite("testCreateWithProxy"));
        suite.addTest(new ProxyAuthenticationServerTestSuite("testUpdateWithProxy"));
        suite.addTest(new ProxyAuthenticationServerTestSuite("testReadDeleteWithProxy"));
        suite.addTest(new ProxyAuthenticationServerTestSuite("testCreateWithOutProxy"));
        return suite;
    }

    public void testSetup() {
        shouldOverrideGetEntityManager = shouldOverrideGetEntityManager();
        System.out.println("====the shouldOverrideGetEntityManager====" + shouldOverrideGetEntityManager);
        //new PhoneNumberTableCreator().replaceTables(JUnitTestCase.getServerSession(PROXY_PU));
        //new EmployeeTableCreator().replaceTables(JUnitTestCase.getServerSession(PROXY_PU));
        //getServerSession(PROXY_PU).executeNonSelectingSQL("update PROXY_EMPLOYEE_SEQ set SEQ_COUNT = 1 where SEQ_NAME='PROXY_EMPLOYEE_SEQ'");
    }

    /**
     * Tests creating Entity with proxy setting
     */
    public void testCreateWithProxy() throws Exception{
        proxyEmp  = new Employee();
        EntityManager em = createEntityManager_proxy(PROXY_PU);
        try {
            startTransaction(em);
            proxyEmp.setFirstName("Guy");
            proxyEmp.setLastName("Pelletier");
            em.persist(proxyEmp);
            empId = proxyEmp.getId();
            
            proxyPhone = new PhoneNumber();
            proxyPhone.setAreaCode("61x");
            proxyPhone.setNumber("823-6262");
            proxyPhone.setOwner(proxyEmp);
            proxyPhone.setId(empId);
            proxyPhone.setType("Home");
            
            em.persist(proxyPhone);
            commitTransaction(em);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isTransactionActive(em)){
                    rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }

        EntityManager newEm = createEntityManager_proxy(PROXY_PU);
        try {
            startTransaction(newEm);
            PhoneNumberPK pk = new PhoneNumberPK();
            pk.setId(empId);
            pk.setType("Home");
            PhoneNumber phone = newEm.find(PhoneNumber.class, pk);
            Employee emp = newEm.find(Employee.class, empId);
            compareObjects(emp, proxyEmp, phone, proxyPhone);
            clearCache(PROXY_PU);
            compareObjects(emp, proxyEmp, phone, proxyPhone);
            commitTransaction(newEm);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isTransactionActive(newEm)){
                    rollbackTransaction(newEm);
            }
            throw ex;
        } finally {
            closeEntityManager(newEm);
        }

    }

    /**
     * Tests Read and Delete with proxy setting
     */
    public void testReadDeleteWithProxy() throws Exception{
        EntityManager em = createEntityManager_proxy(PROXY_PU);
        Employee readEmp = null;
        PhoneNumber readPhone = null;
        try {
            startTransaction(em);
            PhoneNumberPK pk = new PhoneNumberPK();
            pk.setId(empId);
            pk.setType("Home");
            readPhone = em.find(PhoneNumber.class, pk);
            readEmp = readPhone.getOwner();
            em.remove(readEmp);
            em.remove(readPhone);
            commitTransaction(em);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isTransactionActive(em)){
                    rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Tests Update with proxy setting
     */
    public void testUpdateWithProxy() throws Exception{
        EntityManager em = createEntityManager_proxy(PROXY_PU);
        try {
            startTransaction(em);
            Query query = em.createQuery("SELECT e FROM PhoneNumber e");
            List<PhoneNumber> phoneNumbers = query.getResultList();
            for (PhoneNumber phoneNumber : phoneNumbers) {
                phoneNumber.setAreaCode("613");
                Employee owner = phoneNumber.getOwner();
            }
            commitTransaction(em);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Tests create with out proxy setting, it should fail
     */
    public void testCreateWithOutProxy() throws Exception{
        Employee employee  = new Employee();
        EntityManager em = createEntityManager_proxy(PROXY_PU);
        try {
            beginTransaction(em);
            employee.setFirstName("Guy");
            employee.setLastName("Pelletier");
            em.persist(employee);
            
            PhoneNumber homeNumber = new PhoneNumber();
            homeNumber.setAreaCode("61x");
            homeNumber.setNumber("823-6262");
            homeNumber.setOwner(employee);
            homeNumber.setType("Home");
            
            em.persist(homeNumber);
            empId = employee.getId();
            commitTransaction(em);
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("ORA-00942: table or view does not exist") == -1){
                ex.printStackTrace();
                fail("it's not the right exception");
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Setup Proxy properties settings to EntityManager through EntityManagerImpl
     */
    private void setupProperties(EntityManager em){
        EntityManagerImpl empl = (EntityManagerImpl)em.getDelegate();
        empl.setProperties(createProperties());
    }
    
    private Map createProperties(){
        Map newProps = new HashMap(3);
        newProps.put(PersistenceUnitProperties.ORACLE_PROXY_TYPE, OracleConnection.PROXYTYPE_USER_NAME);
        newProps.put(OracleConnection.PROXY_USER_NAME, System.getProperty("proxy.user.name"));
        newProps.put(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Always);
        return newProps;
    }
    
    private void startTransaction(EntityManager em){
        if(shouldOverrideGetEntityManager) {
            beginTransaction(em);
            em.joinTransaction();
        } else {
            if (!isOnServer()){
                    setupProperties(em);
            }
            beginTransaction(em);
            if (isOnServer){
                setupProperties(em);
            }
        }
    }

    private void compareObjects(Employee readEmp, Employee writtenEmp, PhoneNumber readPhone, PhoneNumber writtenPhone){
        if (!getServerSession(PROXY_PU).compareObjects(readEmp, proxyEmp)) {
            fail("Object: " + readEmp + " does not match object that was written: " + proxyEmp + ". See log (on finest) for what did not match.");
        }
        if (!getServerSession(PROXY_PU).compareObjects(readPhone, writtenPhone)) {
            fail("Object: " + readPhone + " does not match object that was written: " + writtenPhone + ". See log (on finest) for what did not match.");
        }
    }
    
    EntityManager createEntityManager_proxy(String puName) {
        if(shouldOverrideGetEntityManager) {
            EntityManager em = getEntityManagerFactory(puName).createEntityManager(createProperties());
            return em;
        }else 
            return createEntityManager(puName);
    }

    private boolean shouldOverrideGetEntityManager(){
        if(getServerSession(PROXY_PU).getServerPlatform().getClass().getName().equals("org.eclipse.persistence.platform.server.oc4j.Oc4jPlatform") ||
           getServerSession(PROXY_PU).getServerPlatform().getClass().getName().equals("org.eclipse.persistence.platform.server.jboss.JBossPlatform") ){
            return true;
        }else
            return false;
    }

    protected java.util.Properties getServerProperties(){
        String proxy_user=System.getProperty("proxy.user.name");
        Properties p = new Properties();
        p.setProperty("proxy.user.name", proxy_user);
        return p;
    }

}
