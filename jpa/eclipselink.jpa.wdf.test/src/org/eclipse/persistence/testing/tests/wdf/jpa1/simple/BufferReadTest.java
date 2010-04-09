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

package org.eclipse.persistence.testing.tests.wdf.jpa1.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesPropertyAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class BufferReadTest extends JPA1Base {

    private static final long SECONDS = 0;
    private static final Integer KEY = Integer.valueOf(1);
    private static final Integer MISS = Integer.valueOf(2);
    private static final boolean PRINTLN = false;
    private EntityManager em;

    @Override
    protected void setup() {
        JPAEnvironment env = getEnvironment();
        em = env.getEntityManager();
        try {
            BasicTypesPropertyAccess data = new BasicTypesPropertyAccess(1);
            data.fill();
            data.setSerializable(null);
            data.setWrapperByteArray2Binary(null);
            data.setWrapperByteArray2Blob(null);
            data.setWrapperByteArray2Longvarbinary(null);
            data.setWrapperCharacterArray2Clob(null);
            data.setWrapperCharacterArray2Varchar(null);
            env.beginTransaction(em);
            Department department = new Department(1, "something");
            em.persist(data);
            em.persist(department);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindOutsideTransaction() {
        Action action = new Action() {
            public void run() {
                em.clear();
                em.find(BasicTypesPropertyAccess.class, KEY);
            }

            public String description() {
                return "find outside transaction (large, hit)";
            }

            public void prepare() {
            }

            public void tearDown() {
            }
        };
        doTest(action);
    }

    @Test
    public void testFindOutsideTransactionSmall() {
        Action action = new Action() {
            public void run() {
                em.clear();
                em.find(Department.class, KEY);
            }

            public String description() {
                return "find outside transaction (small, hit)";
            }

            public void prepare() {
            }

            public void tearDown() {
            }
        };
        doTest(action);
    }

    @Test
    public void testQueryCompleteOutsideTransactionSmall() {
        Action action = new Action() {
            Query query;

            public void run() {
                em.clear();
            }

            public String description() {
                return "query complete outside transaction (small, hit)";
            }

            public void prepare() {
                query = em.createQuery("select d from Department d where d.id = ?1");
                query.setParameter(1, Integer.valueOf(1));
                query.getSingleResult();
            }

            public void tearDown() {
            }
        };
        doTest(action);
    }

    @Test
    public void testQueryExecuteOnlyOutsideTransactionSmall() {
        final JPAEnvironment environment = getEnvironment();
        final EntityManager myEm = environment.getEntityManager();
        Action action = new Action() {
            Query query;

            public void run() {
                myEm.clear();
                query.getSingleResult();
            }

            public String description() {
                return "query execute outside transaction (small, hit)";
            }

            public void prepare() {
                getEnvironment().beginTransaction(myEm);
                query = myEm.createQuery("select d from Department d where d.id = ?1");
                query.setParameter(1, Integer.valueOf(1));
            }

            public void tearDown() {
            }

        };
        doTest(action);
    }

    @Test
    @ToBeInvestigated
    public void testQueryExecuteOnlyOutsideTransactionNew() {
        final JPAEnvironment environment = getEnvironment();
        final EntityManager myEm = environment.getEntityManager();
        Action action = new Action() {
            Query query;

            public void run() {
                myEm.clear();
                query.getSingleResult();
            }

            public String description() {
                return "query execute outside transaction (new, hit)";
            }

            public void prepare() {
                getEnvironment().beginTransaction(myEm);
                query = myEm
                        .createQuery("select new com.sap.jpa.example.Department(d.id, d.name) from Department d where d.id = ?1");
                query.setParameter(1, Integer.valueOf(1));
            }

            public void tearDown() {
                if (getEnvironment().isTransactionActive(myEm)) {
                    myEm.getTransaction().commit();
                }
                closeEntityManager(myEm);
            }
        };
        doTest(action);
    }

    @Test
    public void testNamedQueryCompleteOutsideTransactionSmall() {
        Action action = new Action() {
            public void run() {
                em.clear();
                Query query = em.createNamedQuery("getDepartmentById");
                query.setParameter(1, Integer.valueOf(1));
                query.getSingleResult();
            }

            public String description() {
                return "query named complete outside transaction (small, hit)";
            }

            public void tearDown() {
            }

            public void prepare() {
            }
        };
        doTest(action);
    }

    @Test
    public void testNamedQueryExecuteOnlyOutsideTransactionSmall() {
        final JPAEnvironment environment = getEnvironment();
        final EntityManager myEm = environment.getEntityManager();
        Action action = new Action() {
            Query query;

            public void run() {
                myEm.clear();
                query.getSingleResult();
            }

            public String description() {
                return "query named execute outside transaction (small, hit)";
            }

            public void prepare() {
                getEnvironment().beginTransaction(myEm);
                query = myEm.createNamedQuery("getDepartmentById");
                query.setParameter(1, Integer.valueOf(1));
            }

            public void tearDown() {
            }
        };
        doTest(action);
    }

    @Test
    public void testNativeEntityExecuteOnlyOutsideTransactionSmall() {
        final JPAEnvironment environment = getEnvironment();
        final EntityManager myEm = environment.getEntityManager();
        Action action = new Action() {
            Query query;

            public void run() {
                myEm.clear();
                query.getSingleResult();
            }

            public String description() {
                return "native entity execute outside transaction (small, hit)";
            }

            public void prepare() {
                query = myEm.createNativeQuery("select ID, NAME, VERSION from TMP_DEP where ID = 1", Department.class);
            }

            public void tearDown() {

            }
        };
        doTest(action);
    }

    @Test
    public void testNativeFieldsExecuteOnlyOutsideTransactionSmall() {
        final JPAEnvironment environment = getEnvironment();
        final EntityManager myEm = environment.getEntityManager();
        Action action = new Action() {
            Query query;

            public void run() {
                myEm.clear();
                Object object = query.getSingleResult();
                Object[] array = (Object[]) object;
                int id = ((Number) array[0]).intValue();
                String name = (String) array[1];
                new Department(id, name);
            }

            public String description() {
                return "native fields execute outside transaction (small, hit)";
            }

            public void prepare() {
                query = myEm.createNamedQuery("getDepartmentFieldByField1");
            }

            public void tearDown() {
            }
        };
        doTest(action);
    }

    @Test
    public void testJDBCBestSmall() throws SQLException {
        final DataSource dataSource = getEnvironment().getDataSource();
        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try {
            final PreparedStatement preparedStatement = connection
                    .prepareStatement("select ID, NAME from TMP_DEP where ID = ?");
            try {
                preparedStatement.setInt(1, 1);
                Action action = new Action() {
                    public void run() {
                        try {
                            ResultSet rs = preparedStatement.executeQuery();
                            try {
                                rs.next();
                                new Department(rs.getInt(1), rs.getString(2));
                            } finally {
                                rs.close();
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    public void tearDown() {
                    }

                    public String description() {
                        return "JDBC excluding prepare and set (small, hit)";
                    }

                    public void prepare() {
                    }
                };
                doTest(action);
            } finally {
                preparedStatement.close();
            }
        } finally {

            connection.close();
        }
    }

    @Test
    public void testJDBCInclusivePreapreSmall() throws SQLException {
        final DataSource dataSource = getEnvironment().getDataSource();
        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try {
            Action action = new Action() {
                public void run() {
                    try {
                        final PreparedStatement preparedStatement = connection
                                .prepareStatement("select ID, NAME from TMP_DEP where ID = ?");
                        preparedStatement.setInt(1, 1);
                        try {
                            ResultSet rs = preparedStatement.executeQuery();
                            try {
                                rs.next();
                                new Department(rs.getInt(1), rs.getString(2));
                            } finally {
                                rs.close();
                                connection.commit();
                            }
                        } finally {
                            preparedStatement.close();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                public void tearDown() {
                }

                public String description() {
                    return "JDBC incuding prepare (small, hit)";
                }

                public void prepare() {
                }
            };
            doTest(action);
        } finally {

            connection.close();
        }
    }

    private void printRate(String description, int i) {
        // $JL-SYS_OUT_ERR$
        System.out.println(description + ": " + i + " in " + SECONDS + " second[s] (" + ((SECONDS * 1000.0) / i)
                + " milliseconds)");
    }

    @Test
    public void testNotFound() {
        Action action = new Action() {
            public void run() {
                em.clear();
                em.find(BasicTypesPropertyAccess.class, MISS);
            }

            public String description() {
                return "find outside transaction (miss)";
            }

            public void tearDown() {
            }

            public void prepare() {
            }
        };
        doTest(action);
    }

    @Test
    public void testFlush() {
        Action action = new Action() {
            public void run() {
                em.flush();
            }

            public void tearDown() {
            }

            public String description() {
                return "flush (unchanged)";
            }

            public void prepare() {
            }
        };
        doTest(action);
    }

    private void doTest(Action action) {
        JPAEnvironment env = getEnvironment();
        em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.find(BasicTypesPropertyAccess.class, KEY);
            em.find(Department.class, KEY);
            action.prepare();
            long start = System.currentTimeMillis();
            long end = start + 10;
            while (System.currentTimeMillis() < end) {
                action.run(); // 10 ms warmup in order to fill JIT
            }
            start = System.currentTimeMillis();
            end = start + 1000 * SECONDS;
            int i = 0;
            while (System.currentTimeMillis() < end) {
                // for (int j = 0; j < 10; j++) {
                action.run();
                i++;
            }
            if (PRINTLN) {
                printRate(action.description(), i);
            }

        } finally {
            action.tearDown();
            closeEntityManager(em);
        }
    }

    private interface Action {
        void prepare();

        void run();

        void tearDown();

        String description();
    }
}
