/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.validation;

import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Map;
import javax.persistence.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

public class ValidationTestSuite extends JUnitTestCase {
    public ValidationTestSuite() {
    }
    
    public ValidationTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        super.setUp();
        // Don't clear the cache here since it bring the default pu into play.
    }
    
    public static Test suite() {
        return new TestSuite(ValidationTestSuite.class) {
        
            protected void setUp(){
            }

            protected void tearDown() {
            }
        };
    }
    
    public void testCacheIsolation_PropertiesDefault_Config() throws Exception {
        // Be sure not to modify the global properties.
        Map properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, Boolean.FALSE.toString());
        
        EntityManager em = createEntityManager("isolated1053", properties);
        assertTrue("Item descriptor did not have an isolated cache setting from a TopLink properties setting.", ((EntityManagerImpl)em).getServerSession().getClassDescriptorForAlias("Item").isIsolated());
        closeEntityManager(em);
        
        // Ensure this is done to avoid consecutive tests picking up our
        // very specific isolated persistence unit.
        this.closeEntityManagerFactory("isolated1053");
    }
    
    /**
     * This test assumes the persistence unit has the following property set:
     *       <property name="eclipselink.cache.shared.default" value="false"/>
     * @throws Exception
     */
     
    public void testCacheIsolation_XMLDefault_Config() throws Exception {
        EntityManager em = createEntityManager("isolated1053");
        assertTrue("Item descriptor did not have an isolated cache setting from an XML setting.", ((EntityManagerImpl)em).getServerSession().getClassDescriptorForAlias("Item").isIsolated());
        closeEntityManager(em);
        
        // Ensure this is done to avoid consecutive tests picking up our
        // very specific isolated persistence unit.
        this.closeEntityManagerFactory("isolated1053");
    }
    
    /**
     * This tests fix for gf bug 2492, specifically testing 
     * javax.persistence.jtaDataSource property.  There is no easy way outside a container
     * to ensure the look up fails, but this test ensures that the datasource passed in
     * is used to acquire a connection on login.  
     */
    public void testJTADatasource_Config_Override() throws Exception {
        boolean pass = false;
        Map properties = new HashMap();
        tmpDataSourceImp jtadatasourece = new tmpDataSourceImp();
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, jtadatasourece);
        EntityManager em = null;
        try {
            em = createEntityManager("ignore", properties);
        } catch (RuntimeException expected) {
            pass = expected.getMessage().indexOf("tmpDataSourceImp getConnection called") != -1;
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }
            // Ensure this is done to avoid consecutive tests picking up our
            // very specific isolated persistence unit.
            this.closeEntityManagerFactory("ignore");
        } 
        assertTrue("JTA datasource was not set or accessed as expected through map of properties", pass);
    }
    
    /**
     * This tests fix for gf bug 2492, specifically testing 
     * javax.persistence.jtaDataSource property.  There is no easy way outside a container
     * to ensure the look up fails, but this test ensures that the datasource passed in
     * is used to acquire a connection on login.  
     */
    public void testNonJTADatasource_Config_Override() throws Exception {    
        boolean pass = false;
        Map properties = new HashMap();
        tmpDataSourceImp nonJTADatasourece = new tmpDataSourceImp();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, nonJTADatasourece);
        EntityManager em = null;
        try {
            em = createEntityManager("ignore", properties);
        } catch (RuntimeException expected) {
            pass = expected.getMessage().indexOf("tmpDataSourceImp getConnection called") != -1;
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }
            // Ensure this is done to avoid consecutive tests picking up our
            // very specific isolated persistence unit.
            this.closeEntityManagerFactory("ignore");
        } 
        assertTrue("Non JTA datasource was not set or accessed as expected through map of properties", pass);
    }
    
    public void testPKClassTypeValidation(){
        try{
            createEntityManager().find(Employee.class, new Employee());
        }catch (IllegalArgumentException ex){
            return;
        }
        fail("Failed to throw expected IllegalArgumentException, when incorrect PKClass is used in find call");
    }
    
    public class tmpDataSourceImp implements DataSource{
        public tmpDataSourceImp(){
            super();
        }
        public Connection getConnection() throws SQLException{
            RuntimeException exception = 
                    new RuntimeException("tmpDataSourceImp getConnection called");
            throw exception;
        }
        public Connection getConnection(String username, String password) throws SQLException{
            return getConnection();
        }
        //rest are ignored
        public java.io.PrintWriter getLogWriter() throws SQLException{
            return null;
        }
        public void setLogWriter(java.io.PrintWriter out) throws SQLException{}
        public void setLoginTimeout(int seconds) throws SQLException{}
        public int getLoginTimeout() throws SQLException{return 1;}
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    }
}
