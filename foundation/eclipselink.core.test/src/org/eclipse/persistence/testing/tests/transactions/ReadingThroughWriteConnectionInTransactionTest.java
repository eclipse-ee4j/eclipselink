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
package org.eclipse.persistence.testing.tests.transactions;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

/**
 * Tests using the client's write connection when in transaction to
 * maintain the proper isolation levels, even for reads.
 * @see CR#4334, SAM
 */
public class ReadingThroughWriteConnectionInTransactionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected boolean multipleClients = false;
    protected boolean useUnitOfWork = false;
    protected boolean readIntoCache = false;
    protected boolean instantiateValueHolders = false;
    protected boolean readAfterTransaction = false;
    protected String notSupportedExplanation = null;
    protected Expression searchExpression = null;
    protected Server serverSession;
    protected Vector backupReadConnections;
    protected DatabaseLogin login;
    protected Exception exception;
    protected Accessor clientWriteConnection;

    public ReadingThroughWriteConnectionInTransactionTest(String testId) {
        super();
        setName("ReadingThroughWriteConnectionInTransactionTest" + testId);
    }

    public boolean shouldUseMultipleClients() {
        return multipleClients;
    }

    public void useMultipleClients() {
        multipleClients = true;
    }

    public boolean shouldUseUnitOfWork() {
        return useUnitOfWork;
    }

    public void useUnitOfWork() {
        useUnitOfWork = true;
    }

    public boolean shouldReadIntoCache() {
        return readIntoCache;
    }

    public void readIntoCache() {
        readIntoCache = true;
    }

    public boolean shouldInstantiateValueHolders() {
        return instantiateValueHolders;
    }

    public void instantiateValueHolders() {
        instantiateValueHolders = true;
    }

    public boolean shouldReadAfterTransaction() {
        return readAfterTransaction;
    }

    public void readAfterTransaction() {
        readAfterTransaction = true;
    }

    protected String getNotSupportedExplanation() {
        return notSupportedExplanation;
    }

    protected void setNotSupportedExplanation(String notSupportedExplanation) {
        this.notSupportedExplanation = notSupportedExplanation;
    }

    protected Expression getSearchExpression() {
        return searchExpression;
    }

    protected void setSearchExpression(Expression searchExpression) {
        this.searchExpression = searchExpression;
    }

    protected Vector getBackupReadConnections() {
        return backupReadConnections;
    }

    protected void setBackupReadConnections(Vector backupReadConnections) {
        this.backupReadConnections = backupReadConnections;
    }

    protected DatabaseLogin getLogin() {
        return login;
    }

    protected void setLogin(DatabaseLogin login) {
        this.login = login;
    }

    protected Exception getException() {
        return exception;
    }

    protected void setException(Exception exception) {
        this.exception = exception;
    }

    protected Accessor getClientWriteConnection() {
        return clientWriteConnection;
    }

    protected void setClientWriteConnection(Accessor clientWriteConnection) {
        this.clientWriteConnection = clientWriteConnection;
    }

    public static Vector buildTests() {
        Vector tests = new Vector(14);
        ReadingThroughWriteConnectionInTransactionTest test = null;

        /* Test 1.
         * Tests: Uses correct accessor at root level + relationship traversal.
         * Result: No exception thrown.
         */
        test = new ReadingThroughWriteConnectionInTransactionTest("1");
        test.setDescription("base test for reading using write/transaction connection when in transaction (CR#4334).  Correct accessor used at root level and for triggered (attribute) queries.");
        tests.add(test);

        test = new ReadingThroughWriteConnectionInTransactionTest("1:UOW");
        test.setDescription("base test for reading using write/transaction connection when in transaction (CR#4334).  Correct accessor used at root level and for triggered (attribute) queries.  Uses Unit of Work.");
        test.useUnitOfWork();
        tests.add(test);

        /* Test 2.
         * Tests: Uses correct accessor for triggering value holder.
         * Result: No exception thrown.
         */
        test = new ReadingThroughWriteConnectionInTransactionTest("2");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests correct (write) accessor used when value holder triggered by in transaction session.");
        test.setNotSupportedExplanation(new String("Unless the value holder is wrapped, as by a UOW, it will instaniate with the same connection as its parent in cache, the server read connection."));
        test.instantiateValueHolders();
        tests.add(test);

        test = new ReadingThroughWriteConnectionInTransactionTest("2:UOW");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests correct (write) accessor used when value holder triggered by in transaction session.  Uses Unit of Work.");
        test.instantiateValueHolders();
        test.useUnitOfWork();
        tests.add(test);

        /* Test 3.
         * Tests: Uses correct accessor after transaction over.
         * Result: No exception thrown.
         */
        test = new ReadingThroughWriteConnectionInTransactionTest("3");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by no longer in transaction session.");
        test.readAfterTransaction();
        tests.add(test);

        test = new ReadingThroughWriteConnectionInTransactionTest("3:UOW");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by no longer in transaction session.  Uses unit of work.");
        test.readAfterTransaction();
        test.useUnitOfWork();
        tests.add(test);

        /* Test 4.
         * Tests: Trigger value holder on object from cache.
         * Result: No exception thrown.
         */
        test = new ReadingThroughWriteConnectionInTransactionTest("4");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests correct accessor used when value holder triggered by in transaction session, on object from global cache.");
        test.setNotSupportedExplanation(new String("A client session is not allowed to own the value holders of objects it has read, as they are put in the cache and another could potentially trigger them with your connection."));
        test.readIntoCache();
        test.instantiateValueHolders();
        tests.add(test);

        /*
         * This test has finally been removed.  Even at the time it was the nasty
         * one for clearly what it is doing is wrong: it is checking to see that
         * we are triggering valueholders in the shared cache with a write/dirty
         * connection.  Now we are triggering unit of work valueholders with this
         * connection.  See the new suite of tests for this:
         * UnitOfWorkTests/UnitOfWorkTransactionIsolationTests
         * Actually this only failed due to optimization.  If original already in the
         * shared cache we assume valueholders already triggered whether they are not,
         * so when we trigger them need a server connection, even though we are in
         * transaction.
        test = new ReadingThroughWriteConnectionInTransactionTest("4:UOW");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests correct accessor used when value holder triggered by in transaction session, on object from global cache.  Uses unit of work.");
        test.readIntoCache();
        test.instantiateValueHolders();
        test.useUnitOfWork();
        tests.add(test);
        */
        /* Test 5.
         * Tests: Triggered queries on triggered value holder of object from cache.
         * Result: No exception thrown.
         */
        //Can not implement in this current setup.  Should be covered automatically
        //though as the complex mapping model is now being used.
        /* Test 6.
         * Tests: Second client triggers value holder of object in a transaction cache.
         * Result: Write connection of first (in transaction) client used.
         *
         * This test no longer passes in the new architecture.
        test = new ReadingThroughWriteConnectionInTransactionTest("6");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by non in transaction session, on object originally read by in transaction session.");
        test.useMultipleClients();
        test.instantiateValueHolders();
        tests.add(test);
        */
        test = new ReadingThroughWriteConnectionInTransactionTest("6:UOW");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by non in transaction session, on object originally read by in transaction session.  Uses unit of work.");
        test.useMultipleClients();
        test.instantiateValueHolders();
        test.useUnitOfWork();
        tests.add(test);

        /* Test 7.
         * Tests: Second client triggers value holder of object once in a transaction cache.
         * Result: Read connection from server read connection pool used.
         */
        test = new ReadingThroughWriteConnectionInTransactionTest("7");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by non in transaction session, on object originally read by in transaction session that is no longer in transaction.");
        test.useMultipleClients();
        test.readAfterTransaction();
        tests.add(test);

        test = new ReadingThroughWriteConnectionInTransactionTest("7:UOW");
        test.setDescription("test for reading using write/transaction connection when in transaction (CR#4334).  Tests server read accessor used when value holder triggered by non in transaction session, on object originally read by in transaction session that is no longer in transaction.  Uses unit of work.");
        test.useMultipleClients();
        test.readAfterTransaction();
        test.useUnitOfWork();
        tests.add(test);

        return tests;
    }

    /*
     * Makes a client's write connection unusable, such that a null pointer
     * exception will be thrown if used.
     * Call when server read connection pool should be used.
     */
    private void corruptClientWriteConnection(ClientSession client) {
        if (getClientWriteConnection() != null) {
            return;
        }
        setClientWriteConnection(client.getWriteConnection());
        if (client.isInTransaction()) {
            getClientWriteConnection().rollbackTransaction(client);
        }
        client.setWriteConnection(null);
    }

    /*
     * Makes the server read connection pool unusable, such that a null pointer
     * exception will be thrown if used.
     * Call when a client's write/transaction connection should be used.
     */
    private void corruptServerReadConnections() {
        if (getBackupReadConnections() != null) {
            return;
        }
        List readConnections = getServerSession().getReadConnectionPool().getConnectionsAvailable();
        setBackupReadConnections(new Vector(readConnections));
        readConnections.clear();
        readConnections.add(null);
    }

    public Server getServerSession() {
        return serverSession;
    }

    public void reset() {
        try {
            getServerSession().logout();

            // This logging out and back in again is not understood clearly.
            // @see ClientServerTest.
            //((DatabaseSession) getSession()).logout();
            //((DatabaseSession) getSession()).login();
        } catch (Exception e) {
            // The likely reason logout failed is because 
            // corruptServerReadConnection was used by the test:
            // restoreServerReadConnections restores available connections only,
            // but doesn't restore corrupt used connection (getUsedConnections is a protected method on ConnectionPool).
            // Corrupt used connection is there in Test2 and Test4.
            // Therefore when readConnectionPool.shutdown is called it fails with NPE,
            // as the result the write connection pool is not shutdown and the connections are leaked.
            // Let's close these connections here.
            try {
                for (Iterator poolsEnum = ((ServerSession)getServerSession()).getConnectionPools().values().iterator(); poolsEnum.hasNext();) {
                    ((ConnectionPool)poolsEnum.next()).shutDown();
                }
            } catch (Exception ex) {
                // ignore
            }
            
            throw new TestErrorException("Failed in reset.", e);
        } finally {
            setServerSession(null);
        }
    }

    /**
     * Reverses a previous call to corruptClientWriteConnection
     */
    private void restoreClientWriteConnection(ClientSession client) {
        if (getClientWriteConnection() != null) {
            client.setWriteConnection(getClientWriteConnection());
            setClientWriteConnection(null);
        }
    }

    /**
     * Reverses a previous call to corruptServerReadConnections
     */
    private void restoreServerReadConnections() {
        if (getBackupReadConnections() != null) {
            List readConnections = getServerSession().getReadConnectionPool().getConnectionsAvailable();
            readConnections.clear();
            readConnections.addAll(getBackupReadConnections());
            setBackupReadConnections(null);
        }
    }

    public void setServerSession(ServerSession serverSession) {
        this.serverSession = serverSession;
    }

    /**
     * Creates a new server session with same login and descriptors as test session.
     * @see ClientServerTest setup()
     */
    public void setup() {
        exception = null;
        try {
            setLogin((DatabaseLogin)getSession().getLogin().clone());
            setServerSession(new ServerSession(login));
            getServerSession().useExclusiveReadConnectionPool(2, 2);
            Vector descriptors = new Vector(getAbstractSession().getDescriptors().values());
            getServerSession().addDescriptors(descriptors);
            getServerSession().setSessionLog(getSession().getSessionLog());
            getServerSession().login();
        } catch (Exception e) {
            throw new TestErrorException("Failed in setup.", e);
        }
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        setSearchExpression(builder.get("lastName").equal("Vadis"));
    }

    /**
     * Executes one of fourteen different tests, depending on how the receiver was
     * initialized.
     */
    public void test() {
        ClientSession client = getServerSession().acquireClientSession();
        UnitOfWork uow = null;
        UnitOfWork uow2 = null;
        Session session = null;

        // The employee read by the primary in transaction session.
        Employee emp = null;

        // The employee read by the secondary session.  emp and emp2 are
        // the same object.
        Employee emp2 = null;
        if (shouldUseUnitOfWork()) {
            uow = client.acquireUnitOfWork();
            session = uow;
        } else {
            session = client;
        }

        // Tests reading with write connection when object already in cache.
        if (shouldReadIntoCache()) {
            getServerSession().executeQuery(new ReadObjectQuery(Employee.class, getSearchExpression()));
        }

        // Start transaction in primary session.  Write connection should now be used.
        if (shouldUseUnitOfWork()) {
            uow.beginEarlyTransaction();
        } else {
            client.beginTransaction();
        }

        // The secondary session is for now always a unit of work not in transaction.
        if (shouldUseMultipleClients()) {
            uow2 = getServerSession().acquireUnitOfWork();
        }
        try {
            corruptServerReadConnections();
            emp = (Employee)session.executeQuery(new ReadObjectQuery(Employee.class, getSearchExpression()));
            if (shouldUseMultipleClients()) {
                // Read the same object into the secondary session also.  Since this session
                // is not in transaction the read connection pool is needed.
                restoreServerReadConnections();
                emp2 = (Employee)uow2.executeQuery(new ReadObjectQuery(Employee.class, getSearchExpression()));
            }
            if (shouldInstantiateValueHolders()) {
                if (shouldUseMultipleClients()) {
                    // Value holder should execute on server, even though created by
                    // an in transaction session.
                    corruptClientWriteConnection(client);
                    emp2.getPhoneNumbers();
                    emp2.getPolicies();
                    restoreClientWriteConnection(client);
                } else {
                    // Value holder should execute on client, as emp belongs to an
                    // in transaction session.
                    Vector phoneNumbers = emp.getPhoneNumbers();
                    phoneNumbers.size();
                    emp.getPolicies();
                }
            }

            // Now do some post transaction testing.
            if (shouldUseUnitOfWork()) {
                ((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)uow).rollbackTransaction();
            } else {
                client.rollbackTransaction();
            }

            // Server read pool should again be used as no transactions active.
            restoreServerReadConnections();
            corruptClientWriteConnection(client);
            if (shouldReadAfterTransaction()) {
                if (shouldUseMultipleClients()) {
                    emp2.getPhoneNumbers();
                } else {
                    emp.getPhoneNumbers();
                }
            }
        } catch (Exception e) {
            setException(e);
        } finally {
            restoreClientWriteConnection(client);
            restoreServerReadConnections();
            client.release();
            if (uow2 != null) {
                uow2.release();
            }
        }
    }

    public void verify() {
        if (getException() != null) {
            if (getNotSupportedExplanation() != null) {
                throw new TestWarningException("Not supported: " + getNotSupportedExplanation());
            }
            throw new TestErrorException("Test failed.  Either an in transaction session attempted to use a server read connection, or a session tried to use the connection of another session.");
        }
    }
}
