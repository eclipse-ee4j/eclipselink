/*******************************************************************************
 * Copyright (c) 2014, 2017 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/04/2014 - Rick Curtis
 *       - 450010 : Add java se test bucket
 *     12/19/2014 - Dalia Abo Sheasha
 *       - 454917 : Added a test to use the IDENTITY strategy to generate values
 *     02/16/2017 - Jody Grassel
 *       - 512255 : Eclipselink JPA/Auditing capablity in EE Environment fails with JNDI name parameter type
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.basic.model.XmlFish;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestBasicPersistence {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Dog.class, XmlFish.class, Person.class, Employee.class }, 
            properties = {@Property(name = "eclipselink.cache.shared.default", value = "false")}, 
            mappingFiles = { "META-INF/fish-orm.xml" })
    private EntityManagerFactory emf;

    @SQLListener
    List<String> _sql;
    
    private static final int rmiPort;
    private static final String dsName = "mockDataSource";
    private static final BogusDataSource mockDataSource = new BogusDataSource("tmpDataSourceImp getConnection called");
    private static Registry rmiRegistry = null;
    private static JMXConnectorServer connector = null;
    
    static {
        int rmiPortVal = 1099;
        
        String rmiPortProp = System.getProperty("rmi.port");
        if (!(rmiPortProp == null || rmiPortProp.isEmpty())) {
            try {
                rmiPortVal = new Integer(rmiPortProp);
            } catch (NumberFormatException nfe) {
                // Use default value.
            }
        }
            
        rmiPort = rmiPortVal;
    }
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        rmiRegistry = LocateRegistry.createRegistry(rmiPort);
        
        // Create and Bind Mock Data Source
        rmiRegistry.bind(dsName, mockDataSource);       

        connector = JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL("service:jmx:rmi://localhost:" + rmiPort),
                new HashMap<String, Object>(), ManagementFactory.getPlatformMBeanServer());
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (rmiRegistry != null) {
            rmiRegistry.unbind(dsName);
        } 
        
        if (connector != null) {
            connector.stop();
        }
    }
    
    @Test
    public void activeTransaction() {
        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @Test
    public void testNonNullEmf() {
        Assert.assertNotNull(emf);
    }

    @Test
    public void persistTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Person p = new Person();
            Dog d = new Dog("Bingo");
            p.setDog(d);
            d.setOwner(p);

            em.persist(p);
            em.persist(d);

            em.persist(new XmlFish());
            em.getTransaction().commit();
            em.clear();

            Dog foundDog = em.find(Dog.class, d.getId());
            foundDog.getOwner();
            Assert.assertTrue(_sql.size() > 0);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void identityStrategyTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee e = new Employee();
            em.persist(e);
            em.getTransaction().commit();
            em.clear();

            Employee foundEmp = em.find(Employee.class, e.getId());
            Assert.assertNotNull(foundEmp);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void testNonJTADataSourceOverride() throws Exception {
        if (emf == null)
            return;
        
        InitialContext ic = new InitialContext();
        Assert.assertNotNull(ic.lookup(dsName));
        
        EntityManager em = null;
        boolean pass = false;
        Map properties = new HashMap();  
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dsName);
        properties.put("eclipselink.jdbc.exclusive-connection.mode", "Always");
        
        try {
            em = emf.createEntityManager(properties);
            em.clear();
            em.find(Dog.class, 1);
        } catch (RuntimeException expected) {
            pass = expected.getMessage().indexOf("tmpDataSourceImp getConnection called") != -1;
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        Assert.assertTrue("Non JTA datasource was not set or accessed as expected through map of properties", pass);
    }
    
    /*
     * Taken from org.eclipse.persistence.testing.tests.jpa.validation.ValidationTestSuite
     */
    public static class BogusDataSource implements DataSource, Remote, Serializable {
        private String text = "foo";
        
        public BogusDataSource(String text){
            super();
            this.text = text;
        }
        
        public Connection getConnection() throws SQLException {
            RuntimeException exception = new RuntimeException(text);
            throw exception;
        }
        
        public Connection getConnection(String username, String password) throws SQLException {
            return getConnection();
        }
        
        //rest are ignored
        public java.io.PrintWriter getLogWriter() throws SQLException {
            return null;
        }
        
        public void setLogWriter(java.io.PrintWriter out) throws SQLException{}
        public void setLoginTimeout(int seconds) throws SQLException{}
        public int getLoginTimeout() throws SQLException { return 1; }
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
        public Logger getParentLogger() { return null; }
    }
}
