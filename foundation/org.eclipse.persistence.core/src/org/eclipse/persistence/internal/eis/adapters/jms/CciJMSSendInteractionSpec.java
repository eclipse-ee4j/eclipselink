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
 * INTRNAL:
 * Interaction spec for JMS JCA adapter.
 * Specifies a send message interaction.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSSendInteractionSpec extends CciJMSInteractionSpec {
    protected String replyToDestinationURL;// JNDI name (URL) of the JMSReplyTo destination
    protected String replyToDestination;// if no JNDI, the JMSReplyTo destination name 

    /**
     * The default constructor
     */
    public CciJMSSendInteractionSpec() {
        replyToDestinationURL = "";
        replyToDestination = "";
    }

    /**
     * Set the destination URL where the entity processing the request is to place
     * the response
     *
     * @param url - JMSReplyTo destination URL
     */
    public void setReplyToDestinationURL(String url) {
        replyToDestinationURL = url;
    }

    /**
     * Return the destination URL where the entity processing the request is to place
     * the response
     *
     * @return JMSReplyTo destination URL
     */
    public String getReplyToDestinationURL() {
        return replyToDestinationURL;
    }

    /**
     * Set the destination where the entity processing the request is to place
     * the response
     *
     * @param destination - JMSReplyTo destination
     */
    public void setReplyToDestination(String destination) {
        replyToDestination = destination;
    }

    /**
     * Return the destination where the entity processing the request is to place
     * the response
     *
     * @return JMSReplyTo destination
     */
    public String getReplyToDestination() {
        return replyToDestination;
    }

    /**
     * Indicates if a JNDI lookup is to be performed to locate the reply to destination.
     *
     * @return true if a replyTo destination URL has been specified, false otherwise
     */
    public boolean hasReplyToDestinationURL() {
        return (replyToDestinationURL != null) && (replyToDestinationURL.length() > 0);
    }
}
