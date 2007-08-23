/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.jms;

import org.eclipse.persistence.internal.sessions.remote.RemoteCommand;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.JMSProcessingException;
import org.eclipse.persistence.logging.SessionLog;
import javax.jms.*;

/**
 * INTERNAL:
 *
 * <p>
 * <b>PURPOSE</b>:To Provide a framework for processing incomming messages</p>
 * <p>
 * <b>Descripton</b>:This class is a JMS message Listener that process incomming JMS
 * object message.</p>
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.jms.JMSClusteringService
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager}
 */
public class REMOVE_MessageListener implements MessageListener {
    protected AbstractSession session;

    public REMOVE_MessageListener(org.eclipse.persistence.sessions.Session session) {
        this.session = (AbstractSession)session;
    }

    public AbstractSession getSession() {
        return this.session;
    }

    /**
     * INTERNAL:
     * Casts the message to an ObjectMessage and extracts the TopLink message
     **/
    public void onMessage(Message message) {
        ObjectMessage objectMessage = null;
        try {
            String topic = ((Topic)message.getJMSDestination()).getTopicName();
            getSession().log(SessionLog.FINEST, SessionLog.PROPAGATION, "retreived_remote_message_from_JMS_topic", topic);
            if (message instanceof ObjectMessage) {
                objectMessage = (ObjectMessage)message;
                Object object = objectMessage.getObject();
                if (object instanceof RemoteCommand) {
                    getSession().log(SessionLog.FINEST, SessionLog.PROPAGATION, "processing_topLink_remote_command");
                    ((RemoteCommand)object).execute(getSession(), null);
                } else if (object == null) {
                    getSession().log(SessionLog.WARNING, SessionLog.PROPAGATION, "retreived_null_message", topic);
                } else {
                    getSession().log(SessionLog.WARNING, SessionLog.PROPAGATION, "retreived_unknown_message_type", object.getClass(), topic);
                }
            } else {
                getSession().log(SessionLog.WARNING, SessionLog.PROPAGATION, "received_unexpected_message_type", message.getClass().getName(), topic);
            }
        } catch (JMSException exception) {
            getSession().log(SessionLog.FINER, SessionLog.PROPAGATION, "JMS_exception_thrown");
            getSession().handleException(JMSProcessingException.buildDefault(exception));
        } catch (Throwable exception) {
            getSession().handleException(JMSProcessingException.buildDefault(exception));
        }
    }
}