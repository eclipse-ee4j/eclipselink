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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.tests.feature.TestDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@SuppressWarnings("unchecked")
@RunWith(SkipBugzillaTestRunner.class)
public abstract class AbstractBaseTest extends JUnitTestCase {

    private final JPAEnvironment environment;
    private final String puName;
    private final static DataSource dataSource;
    private final static Map EMF_PROPERTIES;

    static {
        Map<String, String> properties = JUnitTestCaseHelper.getDatabaseProperties();
        String driver = properties.get(PersistenceUnitProperties.JDBC_DRIVER);
        String url = properties.get(PersistenceUnitProperties.JDBC_URL);
        String user = properties.get(PersistenceUnitProperties.JDBC_USER);
        String password = properties.get(PersistenceUnitProperties.JDBC_PASSWORD);

        Properties userPasswd = new Properties();
        userPasswd.put("user", user);
        userPasswd.put("password", password);

        DataSource ds = new TestDataSource(driver, url, userPasswd);
        dataSource = new PooledDataSource(ds);
        EMF_PROPERTIES = new HashMap();
        EMF_PROPERTIES.put("delimited-identifiers", "true");
        EMF_PROPERTIES.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
    }

    public AbstractBaseTest(String name) {
        environment = new EnvironmentAdapter();
        puName = name;
    }

    protected final JPAEnvironment getEnvironment() {
        return environment;
    }

    @Parameters
    public static void parameters() {

    }

    final class EnvironmentAdapter implements JPAEnvironment {

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
            JUnitTestCase.closeEntityManagerFactory(puName);
            return Persistence.createEntityManagerFactory(puName, EMF_PROPERTIES);
        }

        @Override
        public EntityManager getEntityManager() {
            return JUnitTestCase.createEntityManager(puName, EMF_PROPERTIES);
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return JUnitTestCase.getEntityManagerFactory(puName, EMF_PROPERTIES);
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

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    final public void closeEntityManager(EntityManager em) {
        if (!em.isOpen()) {
            return;
        }
        if (environment.isTransactionActive(em)/*
                                                * ||environment. isTransactionMarkedForRollback (em)
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
    }

    /**
     * intended to be overwritten by subclasses
     */
    protected void setup() throws SQLException {

    }

    public static final <T extends Serializable> T serializeDeserialize(T serializable) throws IOException,
            ClassNotFoundException {
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
     * Checks whether the given throwable is of type java.lang.IllegalStateException, or otherwise if the throwable contains a
     * java.lang.IllegalStateException somewhere in the cause stack.
     * 
     * @param e
     *            The throwable to check
     * @return <code>true</code> if the throwable is instance of or caused by java.lang.IllegalStateException
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
     * Checks whether the given throwable is of type java.sql.SQLException, or otherwise if the throwable contains a
     * java.sql.SQLException somewhere in the cause stack.
     * 
     * @param e
     *            The throwable to check
     * @return <code>true</code> if the throwable is instance of or caused by java.sql.SQLException
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

}
