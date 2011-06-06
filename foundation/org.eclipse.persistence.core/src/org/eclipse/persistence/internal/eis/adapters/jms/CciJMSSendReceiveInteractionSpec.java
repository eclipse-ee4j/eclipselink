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


/**
 * INTERNAL:
 * Interaction spec for JMS JCA adapter.
 *
 * Specifies a send/receive message interaction.  It is assumed here that the entity
 * processing the request sets the JMSCorrelationID on the response message to be
 * either the JMSCorrelationID of the request message (if non-null) or the
 * JMSMessageID of the request message (if the JMSCorrelationID is null).  This allows
 * for the user to define the correlation id to link the request/response messages if
 * they wish.
 *
 * Since we are doing a send/receive in one interaction, we know that the replyTo value
 * indicates the response queue as well.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSSendReceiveInteractionSpec extends CciJMSInteractionSpec {
    protected String replyToDestinationURL;// JNDI name (URL) of the JMSReplyTo destination
    protected String replyToDestination;// if no JNDI, the JMSReplyTo destination name 
    protected long timeout;// length of time to wait for a response

    public CciJMSSendReceiveInteractionSpec() {
        replyToDestinationURL = "";
        replyToDestination = "";
        timeout = 0;
    }

    /**
     * Indicates if a JNDI lookup is to be performed to locate the reply to destination.
     * Since we are doing a send/receive in one interaction, we know that this value
     * indicates the response queue as well.
     *
     * @return true if a replyTo destination URL has been specified, false otherwise
     */
    public boolean hasReplyToDestinationURL() {
        return (replyToDestinationURL != null) && (replyToDestinationURL.length() > 0);
    }

    /**
     * Return the destination URL where the entity processing the request is to place
     * the response.
     *
     * Since we are doing a send/receive in one interaction, we know that this value
     * indicates the response queue as well.
     *
     * @return JMSReplyTo destination URL
     */
    public String getReplyToDestinationURL() {
        return replyToDestinationURL;
    }

    /**
     * Set the replyTo destination JNDI name to be looked up.
     *
     * Since we are doing a send/receive in one interaction, we know that this value
     * indicates the response queue as well.
     *
     * @param url
     */
    public void setReplyToDestinationURL(String url) {
        replyToDestinationURL = url;
    }

    /**
     * Return the destination where the entity processing the request is to place
     * the response.
     *
     * Since we are doing a send/receive in one interaction, we know that this value
     * indicates the response queue as well.
     *
     * @return JMSReplyTo destination
     */
    public String getReplyToDestination() {
        return replyToDestination;
    }

    /**
     * Set the destination where the entity processing the request is to place
     * the response.  This is required if JNDI is not being used to lookup the
     * replyTo destination.
     *
     * Since we are doing a send/receive in one interaction, we know that this value
     * indicates the response queue as well.
     *
     * @param dest
     */
    public void setReplyToDestination(String dest) {
        replyToDestination = dest;
    }

    /**
     * Set the length of time to wait for a response.  A setting of 0 indicates
     * infinite wait time.
     *
     * @param timeout - should be set to 0 or above
     */
    public void setTimeout(String timeout) {
        this.timeout = Long.valueOf(timeout).longValue();
    }

    /**
     * Set the length of time to wait for a response.  A setting of 0 indicates
     * infinite wait time.
     *
     * @param timeout - should be set to 0 or above
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Return the length of time to wait for a response.
     *
     * @return length of time to wait for a response, 0 for infinite
     */
    public long getTimeout() {
        return timeout;
    }
}
