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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.adapters.jms;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.EISException;

/**
 * INTERNAL:
 * Interaction to Oracle JMS JCA adapter.
 * Executes the interaction spec to send or receive a message.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class CciJMSInteraction implements Interaction {
    protected CciJMSConnection connection;// the cci connection

    /**
     * This constructor sets the cci connection.
     */
    public CciJMSInteraction(CciJMSConnection conn) {
        connection = conn;
    }

    /**
     * Execute the interaction spec.
     * The spec is either a send, receive, or send/receive interaction.
     *
     * @param spec - the interaction spec
     * @param input - the input record
     * @throws EISException
     */
    public Record execute(InteractionSpec spec, Record input) throws EISException {
        CciJMSRecord record = new CciJMSRecord();
        execute(spec, input, record);
        return record;
    }

    /**
     * Execute the interaction and set the output into the output record.
     * The spec is a send interaction, a receive interaction, or a send/receive interaction.
     * Only text messages of XML content are supported.
     *
     * @param spec - the interaction spec
     * @param input - the input record
     * @param output - the output record
     * @throws EISException
     */
    public boolean execute(InteractionSpec spec, Record input, Record output) throws EISException {
        if (!(spec instanceof CciJMSInteractionSpec)) {
            throw EISException.invalidInteractionSpecType();
        }

        if ((!(input instanceof CciJMSRecord)) || (!(output instanceof CciJMSRecord))) {
            throw EISException.invalidRecordType();
        }

        // Use auto-commit if not in a transaction
        boolean autocommit = false;
        if (!connection.getJMSTransaction().isInTransaction()) {
            autocommit = true;
            connection.getJMSTransaction().begin();
        }

        try {
            if (spec instanceof CciJMSSendInteractionSpec) {
                executeSendInteraction((CciJMSSendInteractionSpec)spec, (CciJMSRecord)input, (CciJMSRecord)output);
            } else if (spec instanceof CciJMSReceiveInteractionSpec) {
                executeReceiveInteraction((CciJMSReceiveInteractionSpec)spec, (CciJMSRecord)input, (CciJMSRecord)output);
            } else if (spec instanceof CciJMSSendReceiveInteractionSpec) {
                executeSendReceiveInteraction((CciJMSSendReceiveInteractionSpec)spec, (CciJMSRecord)input, (CciJMSRecord)output);
            } else {
                throw EISException.unknownInteractionSpecType();
            }
        } catch (Exception exception) {
            throw EISException.createException(exception);
        } finally {
            if (autocommit) {
                connection.getJMSTransaction().commit();
            }
        }
        return true;
    }

    /**
     * Execute the send message interaction.
     * Only text messages of XML content are supported.
     *
     * @param spec - the send interaction spec
     * @param input - the input record
     * @param output - the output record
     * @throws EISException
     */
    protected void executeSendInteraction(CciJMSSendInteractionSpec spec, CciJMSRecord input, CciJMSRecord output) throws EISException {
        // verify input record
        if (input.size() != 1) {
            throw EISException.invalidInput();
        }

        try {
            Queue queue;
            QueueSession qSession = (QueueSession)connection.getSession();

            if (spec.hasDestinationURL()) {
                queue = (Queue)new InitialContext().lookup(spec.getDestinationURL());
            } else {
                queue = qSession.createQueue(spec.getDestination());
            }

            Message msg = createMessage(input.get(0), qSession);

            if (spec.hasMessageSelector()) {
                msg.setJMSCorrelationID(spec.getMessageSelector());
            }

            if (spec.hasReplyToDestinationURL()) {
                msg.setJMSReplyTo((Queue)new InitialContext().lookup(spec.getReplyToDestinationURL()));
            } else {
                msg.setJMSReplyTo(qSession.createQueue(spec.getReplyToDestination()));
            }

            qSession.createSender(queue).send(msg);
        } catch (Exception ex) {
            throw EISException.createException(ex);
        }
    }

    /**
     * Execute the receive message interaction.
     * Only text messages of XML content are supported.
     *
     * @param spec - the receive interaction spec
     * @param input - the input record
     * @param output - the output record
     * @throws EISException
     */
    protected void executeReceiveInteraction(CciJMSReceiveInteractionSpec spec, CciJMSRecord input, CciJMSRecord output) throws EISException {
        try {
            Queue queue;
            QueueSession qSession = (QueueSession)connection.getSession();

            if (spec.hasDestinationURL()) {
                queue = (Queue)new InitialContext().lookup(spec.getDestinationURL());
            } else {
                queue = qSession.createQueue(spec.getDestination());
            }

            // create the receiver using a user-defined message selector, if one exists
            QueueReceiver receiver;
            if (spec.hasMessageSelector()) {
                receiver = qSession.createReceiver(queue, spec.getFormattedMessageSelector());
            } else {
                receiver = qSession.createReceiver(queue);
            }

            Message msg = receiver.receive(spec.getTimeout());

            // check for timeout
            if (msg == null) {
                throw EISException.timeoutOccurred();
            }
            output.add(msg);
        } catch (Exception ex) {
            throw EISException.createException(ex);
        }
    }

    /**
     * Execute the send/receive message interaction.
     * Only text messages of XML content are supported.
     *
     * @param spec - the send/receive interaction spec
     * @param input - the input record
     * @param output - the output record
     * @throws EISException
     */
    protected void executeSendReceiveInteraction(CciJMSSendReceiveInteractionSpec spec, CciJMSRecord input, CciJMSRecord output) throws EISException {
        // verify input record
        if (input.size() != 1) {
            throw EISException.invalidInput();
        }

        try {
            Queue sendQueue;
            Queue replyToQueue;
            QueueSession qSession = (QueueSession)connection.getSession();

            // perform the send portion of the interaction
            // set the request queue
            if (spec.hasDestinationURL()) {
                sendQueue = (Queue)new InitialContext().lookup(spec.getDestinationURL());
            } else {
                sendQueue = qSession.createQueue(spec.getDestination());
            }

            // set the replyTo queue
            if (spec.hasReplyToDestinationURL()) {
                replyToQueue = (Queue)new InitialContext().lookup(spec.getReplyToDestinationURL());
            } else {
                replyToQueue = qSession.createQueue(spec.getReplyToDestination());
            }

            Message msg = createMessage(input.get(0), qSession);
            msg.setJMSReplyTo(replyToQueue);

            // set the user-defined message selector, if one exists, else use the JMSMessageID
            if (spec.hasMessageSelector()) {
                msg.setJMSCorrelationID(spec.getMessageSelector());
                sendMessageAndCommit(qSession, sendQueue, msg);
            } else {
                sendMessageAndCommit(qSession, sendQueue, msg);
                spec.setMessageSelector(msg.getJMSMessageID());
            }

            // at this point the message selector set in the spec is either user-defined or the JMSMessageID
            // perform the receive portion of the interaction
            Queue receiveQueue;
            if (spec.hasReplyToDestinationURL()) {
                receiveQueue = (Queue)new InitialContext().lookup(spec.getReplyToDestinationURL());
            } else {
                receiveQueue = qSession.createQueue(spec.getReplyToDestination());
            }

            msg = qSession.createReceiver(receiveQueue, spec.getFormattedMessageSelector()).receive(spec.getTimeout());

            // check for timeout
            if (msg == null) {
                throw EISException.timeoutOccurred();
            }
            output.add(msg);
        } catch (Exception ex) {
            throw EISException.createException(ex);
        }
    }

    /**
     * This helper method will send the message to the queue, then commit the transaction so the
     * sent message can be consumed.  The local transaction will be restarted for the response
     * portion of this call.
     *
     * @param qSession - the JMS session
     * @param queue - the destination of the message
     * @param msg - the message to send
     */
    protected void sendMessageAndCommit(QueueSession qSession, Queue queue, Message msg) throws EISException {
        try {
            qSession.createSender(queue).send(msg);
        } catch (Exception ex) {
            throw EISException.createException(ex);
        }
        connection.getJMSTransaction().commit();
        connection.getJMSTransaction().begin();
    }

    /**
     * This helper method will create the appropriate JMS message according to the input record
     * data type.  Only text messages of XML content are supported at this time.
     *
     * @param recordData - the input record
     * @param session - the JMS queue session
     * @return the JMS message
     * @throws EISException - if an unknown message type is encountered
     */
    protected Message createMessage(Object recordData, QueueSession session) throws EISException {
        Message msg;

        try {
            if (recordData instanceof String) {
                msg = session.createTextMessage((String)recordData);
            } else {
                throw EISException.unsupportedMessageInInputRecord();
            }
        } catch (JMSException jmse) {
            throw EISException.createException(jmse);
        }
        return msg;
    }

    /**
     * Return the cci connection.
     *
     * @return the cci connection
     */
    public javax.resource.cci.Connection getConnection() {
        return connection;
    }

    /**
     * Resource warnings are not supported.
     */
    public ResourceWarning getWarnings() {
        return null;
    }

    /**
     * Resource warnings are not supported.
     */
    public void clearWarnings() {
    }

    /**
     * Satisfy the Interaction interface.
     */
    public void close() {
    }
}
