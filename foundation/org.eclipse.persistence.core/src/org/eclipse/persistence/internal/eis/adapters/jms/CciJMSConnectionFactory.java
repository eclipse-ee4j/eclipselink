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
package org.eclipse.persistence.internal.eis.adapters.jms;


// JDK imports
import javax.jms.*;
import javax.naming.*;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.EISException;

/**
 * INTERNAL:
 * Connection factory for JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSConnectionFactory implements javax.resource.cci.ConnectionFactory {

    /**
     * Default constructor
     */
    public CciJMSConnectionFactory() {
    }

    /**
     * Return the connection.
     *
     * @return the CCI connection
     * @throws EISException
     */
    public javax.resource.cci.Connection getConnection() throws EISException {
        return getConnection(new CciJMSConnectionSpec());
    }

    /**
     * Return the connection.
     *
     * @return the CCI connection
     * @throws EISException
     */
    public javax.resource.cci.Connection getConnection(ConnectionSpec spec) throws EISException {
        CciJMSConnectionSpec jmsSpec = null;
        Session session = null;
        javax.jms.Connection conn = null;
        javax.jms.ConnectionFactory factory;

        try {
            jmsSpec = (CciJMSConnectionSpec)spec;

            // should have either a JNDI lookup name or connection factory class set in the spec
            if (jmsSpec.hasConnectionFactoryURL()) {
                factory = (javax.jms.ConnectionFactory)new InitialContext().lookup(jmsSpec.getConnectionFactoryURL());
            } else {
                factory = jmsSpec.getConnectionFactory();

                if (factory == null) {
                    throw EISException.noConnectionFactorySpecified();
                }
            }

            // if a username has been provided, create the connection with user/password
            if (jmsSpec.hasUsername()) {
                conn = ((QueueConnectionFactory)factory).createQueueConnection(jmsSpec.getUsername(), jmsSpec.getPassword());
            } else {
                conn = ((QueueConnectionFactory)factory).createQueueConnection();
            }
            conn.start();

            // 'true' - JMS session is transacted, i.e. always has a current transaction
            // 'AUTO_ACKNOWLEDGE' - session automatically acknowledges a client's receipt of a message either:
            //   - when the session has successfully returned from a call to receive
            //   - when the listener the session has called to process the message successfully returns
            session = ((QueueConnection)conn).createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception e) {
            throw EISException.createException(e);
        }

        return new CciJMSConnection(session, conn, jmsSpec);
    }

    /**
     * Return the adapter metadata.
     *
     * @return the adapter metadata
     */
    public ResourceAdapterMetaData getMetaData() {
        return new CciJMSAdapterMetaData();
    }

    /**
     * Return the record factory.
     *
     * @return the CciJMSRecordFactory
     */
    public RecordFactory getRecordFactory() {
        return new CciJMSRecordFactory();
    }

    /**
     * Return a reference object.
     *
     * @return reference
     */
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    /**
     * Does nothing - for interface implementation
     *
     * @param reference
     */
    public void setReference(Reference reference) {
    }
}
