/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.framework.wdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.framework.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.server.ServerPlatform;
import org.eclipse.persistence.testing.tests.feature.TestDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@SuppressWarnings("unchecked")
@RunWith(SkipBugzillaTestRunner.class)
public abstract class AbstractBaseTest {

    // /** System variable to set the tests to run on the server. */
    // public static final String RUN_ON_SERVER = "server.run";
    //
    private static Map<String, EntityManagerFactory> emfNamedPersistenceUnits = new Hashtable<String, EntityManagerFactory>();

    /** Determine if the test is running on a JEE server, or in JSE. */

    private static ServerPlatform serverPlatform;

    private final JPAEnvironment environment;
    private final String puName;
    private final static DataSource dataSource;
    private final static Map EMF_PROPERTIES;

    private static boolean seesJPA2 = (LockModeType.values().length > 2);

    static {
        final DataSource aDataSource;

        if (!ServerInfoHolder.isOnServer()) {
            Map<String, String> properties = JUnitTestCaseHelper.getDatabaseProperties();
            String driver = properties.get(PersistenceUnitProperties.JDBC_DRIVER);
            String url = properties.get(PersistenceUnitProperties.JDBC_URL);
            String user = properties.get(PersistenceUnitProperties.JDBC_USER);
            String password = properties.get(PersistenceUnitProperties.JDBC_PASSWORD);

            Properties userPasswd = new Properties();
            userPasswd.put("user", user);
            userPasswd.put("password", password);

            DataSource ds = new TestDataSource(driver, url, userPasswd);
            aDataSource = new PooledDataSource(ds);

        } else {
            Context context;
            try {
                context = new InitialContext();
                aDataSource = (DataSource) context.lookup(ServerInfoHolder.getDataSourceName()); 
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
        dataSource = aDataSource;
        EMF_PROPERTIES = new HashMap();
        EMF_PROPERTIES.put("delimited-identifiers", "true");
        EMF_PROPERTIES.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
    }

    public AbstractBaseTest(String name) {
        environment = new ResourceLocalEnvironment();
        puName = name;
    }

    protected final JPAEnvironment getEnvironment() {
        return environment;
    }

    @Parameters
    public static void parameters() {

    }

    final class ResourceLocalEnvironment implements JPAEnvironment {

        @Override
        public void beginTransaction(EntityManager em) {
            em.getTransaction().begin();
        }

        @Override
        public void commitTransaction(EntityManager em) {
            em.getTransaction().commit();
        }

        @Override
        public void commitTransactionAndClear(EntityManager em) {
            try {
                commitTransaction(em);
            } finally {
                em.clear();
            }
        }

        @Override
        public EntityManagerFactory createNewEntityManagerFactory() throws NamingException {
            AbstractBaseTest.closeEntityManagerFactory(puName);
            return Persistence.createEntityManagerFactory(puName, EMF_PROPERTIES);
        }

        @Override
        public EntityManager getEntityManager() {
            return getEntityManagerFactory().createEntityManager();
            // return AbstractBaseTest.createEntityManager(puName,
            // EMF_PROPERTIES);
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return AbstractBaseTest.getEntityManagerFactory(puName, EMF_PROPERTIES);
        }

        @Override
        public boolean isTransactionActive(EntityManager em) {
            return em.getTransaction().isActive();
        }

        @Override
        public boolean isTransactionMarkedForRollback(EntityManager em) {
            return em.getTransaction().getRollbackOnly();
        }

        @Override
        public void markTransactionForRollback(EntityManager em) {
            em.getTransaction().setRollbackOnly();
        }

        @Override
        public void rollbackTransaction(EntityManager em) {
            em.getTransaction().rollback();
        }

        @Override
        public void rollbackTransactionAndClear(EntityManager em) {
            try {
                rollbackTransaction(em);
            } finally {
                em.clear();
            }
        }

        @Override
        public boolean usesExtendedPC() {
            return true;
        }

        @Override
        public DataSource getDataSource() {
            return AbstractBaseTest.this.getDataSource();
        }

        @Override
        public EntityManagerFactory createNewEntityManagerFactory(Map properties) throws NamingException {
            Map mergedProperties = new HashMap(); 
            mergedProperties.putAll(EMF_PROPERTIES); 
            mergedProperties.putAll(properties); 
            
            AbstractBaseTest.closeEntityManagerFactory(puName); 
            return Persistence.createEntityManagerFactory(puName, mergedProperties); 
        }

        @Override
        public Object getPropertyValue(EntityManager em, String key) {
            Object delegate = em.getDelegate();
            Method getEntityManagerFactoryMethod;
            try {
                getEntityManagerFactoryMethod = delegate.getClass().getMethod("getEntityManagerFactory");
                Object emf = getEntityManagerFactoryMethod.invoke(delegate);
                
                Method getPropertiesMethod = emf.getClass().getMethod("getProperties");
                @SuppressWarnings("rawtypes")
                Map map = (Map) getPropertiesMethod.invoke(emf);
                
                return map.get(key);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            
        }
        
        @Override
        public void evict(EntityManager em, Class<?> clazz) {
            Object delegate = em.getDelegate();
            Method getEntityManagerFactoryMethod;
            try {
                getEntityManagerFactoryMethod = delegate.getClass().getMethod("getEntityManagerFactory");
                Object emf = getEntityManagerFactoryMethod.invoke(delegate);
                
                Method getCacheMethod = emf.getClass().getMethod("getCache");
                Object cache =  getCacheMethod.invoke(emf);
                
                Method evictClassMethod = cache.getClass().getMethod("evict", Class.class);
                
                evictClassMethod.invoke(cache, clazz);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void evictAll(EntityManager em) {
            Object delegate = em.getDelegate();
            Method getEntityManagerFactoryMethod;
            try {
                getEntityManagerFactoryMethod = delegate.getClass().getMethod("getEntityManagerFactory");
                Object emf = getEntityManagerFactoryMethod.invoke(delegate);
                
                Method getCacheMethod = emf.getClass().getMethod("getCache");
                Object cache =  getCacheMethod.invoke(emf);
                
                Method evictClassMethod = cache.getClass().getMethod("evictAll", new Class[]{});
                
                evictClassMethod.invoke(cache, new Object[]{});
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    final class JTATxScopedEnvironment implements JPAEnvironment {

        @Override
        public void beginTransaction(EntityManager em) {
            AbstractBaseTest.this.beginTransaction(em);
        }

        @Override
        public void commitTransaction(EntityManager em) {
            AbstractBaseTest.this.commitTransaction(em);
        }

        @Override
        public void commitTransactionAndClear(EntityManager em) {
            try {
                commitTransaction(em);
            } finally {
                em.clear();
            }
        }

        @Override
        public EntityManagerFactory createNewEntityManagerFactory() throws NamingException {
            AbstractBaseTest.closeEntityManagerFactory(puName);
            return Persistence.createEntityManagerFactory(puName, EMF_PROPERTIES);
        }

        @Override
        public EntityManager getEntityManager() {
            return AbstractBaseTest.createEntityManager(puName, EMF_PROPERTIES);
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return AbstractBaseTest.getEntityManagerFactory(puName, EMF_PROPERTIES);
        }

        @Override
        public boolean isTransactionActive(EntityManager em) {
            return AbstractBaseTest.this.isTransactionActive(em);
        }

        @Override
        public boolean isTransactionMarkedForRollback(EntityManager em) {
            return AbstractBaseTest.this.getRollbackOnly(em);
        }

        @Override
        public void markTransactionForRollback(EntityManager em) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rollbackTransaction(EntityManager em) {
            AbstractBaseTest.this.rollbackTransaction(em);
        }

        @Override
        public void rollbackTransactionAndClear(EntityManager em) {
            try {
                rollbackTransaction(em);
            } finally {
                em.clear();
            }
        }

        @Override
        public boolean usesExtendedPC() {
            return true;
        }

        @Override
        public DataSource getDataSource() {
            return AbstractBaseTest.this.getDataSource();
        }

        @Override
        public EntityManagerFactory createNewEntityManagerFactory(Map<String, Object> properties) throws NamingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getPropertyValue(EntityManager em, String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void evict(EntityManager em, Class<?> clazz) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void evictAll(EntityManager em) {
            throw new UnsupportedOperationException();
        }

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    final public void closeEntityManager(EntityManager em) {
        if (!em.isOpen()) {
            return;
        }
        if (environment.isTransactionActive(em)/*
                                                * ||environment.
                                                * isTransactionMarkedForRollback
                                                * (em)
                                                */) { // FIXME discuss if tx is
            // active if marked for
            // rollback
            environment.rollbackTransaction(em);
        }
        // // close application-managed entity manager
        // if (!(getEnvironment() instanceof JTASharedPCEnvironment)) {
        // em.close(); FIXME
        // }
    }

    protected static void verify(boolean condition, String string) {
        Assert.assertTrue(string, condition);
    }

    protected final void flop(final String msg) {
        Assert.fail(msg);
    }

    abstract protected String[] getClearableTableNames();

    @Before
    public void clearAllTablesAndSetup() throws SQLException {
        clearAllTables();
        
        setup();
    }

    protected void clearAllTables() throws SQLException {
        Connection conn = getDataSource().getConnection();
        try {
            conn.setAutoCommit(false);

            Set<String> existingTables = new HashSet<String>();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "TMP_%", null);
            try {
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME").toUpperCase(Locale.ENGLISH));
                }
            } finally {
                rs.close();
            }

            Statement statement = conn.createStatement();
            try {
                if (existingTables.contains("TMP_CASC_NODE")) {
                    statement.executeUpdate("update TMP_CASC_NODE set PARENT = null");
                }
                if (existingTables.contains("TMP_NODE")) {
                    statement.executeUpdate("update TMP_NODE set PARENT = null");
                }
                if (existingTables.contains("TMP_COP")) {
                    statement.executeUpdate("update TMP_COP set PARTNER_ID = null");
                }
                for (String name : getClearableTableNames()) {
                    if (existingTables.contains(name.toUpperCase(Locale.ENGLISH))) {
                        statement.executeUpdate("delete from " + name);
                    }
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                throw ex;
            } finally {
                statement.close();
            }
            conn.commit();
        } finally {
            conn.close();
        }
        
        // TODO evictAll
        
    }

    /**
     * intended to be overwritten by subclasses
     */
    protected void setup() throws SQLException {

    }

    public static final <T extends Serializable> T serializeDeserialize(T serializable) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        try {
            objectOutputStream.writeObject(serializable);
        } finally {
            objectOutputStream.close();
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return (T) new ObjectInputStream(in).readObject();
    }

    public static boolean isInsideEngine() {
        return false;
    }

    /**
     * Checks whether the given throwable is of type
     * java.lang.IllegalStateException, or otherwise if the throwable contains a
     * java.lang.IllegalStateException somewhere in the cause stack.
     * 
     * @param e
     *            The throwable to check
     * @return <code>true</code> if the throwable is instance of or caused by
     *         java.lang.IllegalStateException
     */
    protected final boolean checkForIllegalStateException(Throwable e) {
        boolean contained = false;
        while (e != null) {
            if (e instanceof IllegalStateException) {
                contained = true;
                break;
            }
            e = e.getCause();
        }
        return contained;
    }

    /**
     * Checks whether the given throwable is of type java.sql.SQLException, or
     * otherwise if the throwable contains a java.sql.SQLException somewhere in
     * the cause stack.
     * 
     * @param e
     *            The throwable to check
     * @return <code>true</code> if the throwable is instance of or caused by
     *         java.sql.SQLException
     */
    protected final boolean checkForSQLException(Throwable e) {
        boolean contained = false;
        while (e != null) {
            if (e instanceof SQLException) {
                contained = true;
                break;
            }
            e = e.getCause();
        }
        return contained;
    }

    /**
     * Return if the transaction is active. This allows the same code to be used
     * on the server where JTA is used.
     */
    public boolean isTransactionActive(EntityManager entityManager) {
        if (ServerInfoHolder.isOnServer()) {
            return getServerPlatform().isTransactionActive();
        } else {
            return entityManager.getTransaction().isActive();
        }
    }

    /**
     * Return if the transaction is roll back only. This allows the same code to
     * be used on the server where JTA is used.
     */
    public boolean getRollbackOnly(EntityManager entityManager) {
        if (ServerInfoHolder.isOnServer()) {
            return getServerPlatform().getRollbackOnly();
        } else {
            return entityManager.getTransaction().getRollbackOnly();
        }
    }

    /**
     * Begin a transaction on the entity manager. This allows the same code to
     * be used on the server where JTA is used.
     */
    public void beginTransaction(EntityManager entityManager) {
        if (ServerInfoHolder.isOnServer()) {
            getServerPlatform().beginTransaction();
        } else {
            entityManager.getTransaction().begin();
        }
    }

    /**
     * Commit a transaction on the entity manager. This allows the same code to
     * be used on the server where JTA is used.
     */
    public void commitTransaction(EntityManager entityManager) {
        if (ServerInfoHolder.isOnServer()) {
            getServerPlatform().commitTransaction();
        } else {
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Rollback a transaction on the entity manager. This allows the same code
     * to be used on the server where JTA is used.
     */
    public void rollbackTransaction(EntityManager entityManager) {
        if (ServerInfoHolder.isOnServer()) {
            getServerPlatform().rollbackTransaction();
        } else {
            entityManager.getTransaction().rollback();
        }
    }

    /**
     * Return the server platform if running in JEE.
     */
    public static ServerPlatform getServerPlatform() {
        if (serverPlatform == null) {
            serverPlatform = new JEEPlatform();
        }
        return serverPlatform;
    }

    /**
     * Create a new entity manager for the "default" persistence unit. If in JEE
     * this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager() {
        if (ServerInfoHolder.isOnServer()) {
            return getServerPlatform().getEntityManager("default");
        } else {
            return getEntityManagerFactory().createEntityManager();
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the
     * properties. The properties will only be used the first time this entity
     * manager is accessed. If in JEE this will create or return the active
     * managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName, Map<String, String> properties) {
        if (ServerInfoHolder.isOnServer()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName, properties).createEntityManager();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName, JUnitTestCaseHelper.getDatabaseProperties());
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map<String, String> properties) {
        if (ServerInfoHolder.isOnServer()) {
            return getServerPlatform().getEntityManagerFactory(persistenceUnitName);
        } else {
            EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory) emfNamedPersistenceUnits.get(persistenceUnitName);
            if (emfNamedPersistenceUnit == null) {
                emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);
            }
            return emfNamedPersistenceUnit;
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return getEntityManagerFactory("default");
    }

    public static void closeEntityManagerFactory() {
        closeEntityManagerFactory("default");
    }

    public static void closeEntityManagerFactory(String persistenceUnitName) {
        EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory) emfNamedPersistenceUnits.get(persistenceUnitName);
        if (emfNamedPersistenceUnit != null) {
            if (emfNamedPersistenceUnit.isOpen()) {
                emfNamedPersistenceUnit.close();
            }
            emfNamedPersistenceUnits.remove(persistenceUnitName);
        }
    }

    public static boolean seesJPA2() {
        return seesJPA2;
    }

    public static Map<String, String> getTestProperties() {
        if (ServerInfoHolder.isOnServer()) {
            return ServerInfoHolder.getTestProperties();
        } else {
            return JUnitTestCaseHelper.getDatabaseProperties();
        }

    }
}
