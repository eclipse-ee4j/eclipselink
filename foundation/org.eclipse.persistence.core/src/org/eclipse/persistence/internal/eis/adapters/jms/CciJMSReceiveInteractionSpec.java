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


/**
 * INTERNAL:
 * Interaction spec for JMS JCA adapter.
 * Specifies a receive message interaction.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSReceiveInteractionSpec extends CciJMSInteractionSpec {
    protected long timeout;// length of time to wait for a response

    /**
     * The default constructor.
     */
    public CciJMSReceiveInteractionSpec() {
        timeout = 0;
    }

    /**
     * Set the length of time to wait for a response.  A setting of 0 indicates
     * infinite wait time.
     *
     * @param timeout
     */
    public void setTimeout(String timeout) {
        this.timeout = new Long(timeout).longValue();
    }

    /**
     * Set the length of time to wait for a response.  A setting of 0 indicates
     * infinite wait time.
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Return the length of time to wait for a response.
     *
     * @return the length of time to wait for a response, 0 for infinite
     */
    public long getTimeout() {
        return timeout;
    }
}
