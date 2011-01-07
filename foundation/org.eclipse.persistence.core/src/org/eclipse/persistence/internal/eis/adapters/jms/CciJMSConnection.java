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
package org.eclipse.persistence.internal.eis.adapters.jms;


// JDK imports
import javax.jms.*;
import javax.resource.cci.*;

// TopLink imports
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * INTERNAL:
 * Connection to the Oracle JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSConnection implements javax.resource.cci.Connection {
    protected javax.jms.Connection connection;
    protected CciJMSConnectionSpec connectionSpec;
    protected Session session;
    protected CciJMSTransaction transaction;

    /**
     * Construct the CCI connection.
     */
    public CciJMSConnection(Session session, javax.jms.Connection conn, CciJMSConnectionSpec spec) {
        this.session = session;
        connection = conn;
        connectionSpec = spec;
        transaction = new CciJMSTransaction(this);
    }

    /**
     * Return the JMS connection.
     */
    public javax.jms.Connection getConnection() {
        return connection;
    }

    /**
     * Return the JMS session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Close the session and the connection.
     */
    public void close() throws EISException {
        try {
            getSession().close();
            getConnection().close();
        } catch (Exception exception) {
            throw EISException.createException(exception);
        }
    }

    /**
     * Create a CciJMSInteraction for this connection.
     *
     * @return a new CciJMSInteraction
     */
    public Interaction createInteraction() {
        return new CciJMSInteraction(this);
    }

    /**
     * Return the connection specification for this connection
     *
     * @return the CciJMSConnectionSpec
     */
    public CciJMSConnectionSpec getConnectionSpec() {
        return connectionSpec;
    }

    /**
     * Return the local transaction for this connection
     *
     * @return the CciJMSTransaction as a LocalTransaction
     */
    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    /**
     * Returns the JMSTransaction for this connection
     *
     * @return the JMSTransaction
     */
    public CciJMSTransaction getJMSTransaction() {
        return transaction;
    }

    /**
     * Return the connection meta-data
     *
     * @return the CciJMSConnectionMetaData for this connection
     */
    public javax.resource.cci.ConnectionMetaData getMetaData() {
        return new CciJMSConnectionMetaData(this);
    }

    /**
     * Result sets are not supported.
     */
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }
}
