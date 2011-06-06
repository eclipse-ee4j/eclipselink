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

import javax.resource.cci.*;

/**
 * INTERNAL:
 * Interaction spec for JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class CciJMSInteractionSpec implements InteractionSpec {
    protected String destinationURL;// JNDI name (URL) of the destination (queue/topic)
    protected String destination;// if no JNDI, the destination name 
    protected String messageSelector;// message selector to link the request and response messages

    /**
     * Default constructor.
     */
    public CciJMSInteractionSpec() {
        destinationURL = "";
        destination = "";
        messageSelector = "";
    }

    /**
     * Indicates if a JNDI lookup is to be performed to locate the destination.
     *
     * @return true if a destination URL has been specified, false otherwise
     */
    public boolean hasDestinationURL() {
        return (destinationURL != null) && (destinationURL.length() > 0);
    }

    /**
     * Set the destination URL to be looked up.
     *
     * @param url - the destination name as registered in JNDI
     */
    public void setDestinationURL(String url) {
        destinationURL = url;
    }

    /**
     * Return the URL of the destination.
     *
     * @return the destination name as registered in JNDI
     */
    public String getDestinationURL() {
        return destinationURL;
    }

    /**
     * Set the name of the destination to be used.  This is required if JNDI is
     * not being used to lookup the destination.
     *
     * @param dest
     */
    public void setDestination(String dest) {
        destination = dest;
    }

    /**
     * Return the name of the destination - required if JNDI is not being used.
     *
     * @return the name of the destination to be used
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the message selector to be used to link the request and response messages.
     * If this value is not set, it is assumed that the entity processing the request
     * will set the JMSCorrelationID of the reponse message using the JMSMessageID of
     * the request messsage.
     *
     * @param selector
     */
    public void setMessageSelector(String selector) {
        messageSelector = selector;
    }

    /**
     * Returns the message selector to be used to link the request and response messages.
     *
     * @return the message selector.
     */
    public String getMessageSelector() {
        return messageSelector;
    }

    /**
     * Returns the message selector in the appropriate format.  The selector is
     * set in JMS message selector format:  "JMSCorrelationID = 'message_selector'
     *
     * @return formatted message selector - uses JMSCorrelationID
     */
    public String getFormattedMessageSelector() {
        return "JMSCorrelationID = '" + messageSelector + "'";
    }

    /**
     * Indicates if the user has set a message selector.
     *
     * @return true if a message selector has been set, false otherwise
     */
    public boolean hasMessageSelector() {
        return (messageSelector != null) && (messageSelector.length() > 0);
    }

    /**
     * Returns the destination URL or class
     *
     * @return destination URL or destination class
     */
    public String toString() {
        if (hasDestinationURL()) {
            return getClass().getName() + "(" + getDestinationURL() + ")";
        }

        return getClass().getName() + "(" + getDestination() + ")";
    }
}
