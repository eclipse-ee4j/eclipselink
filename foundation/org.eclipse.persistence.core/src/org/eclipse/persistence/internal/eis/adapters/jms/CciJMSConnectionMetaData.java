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
import javax.resource.cci.*;
import org.eclipse.persistence.eis.EISException;

/**
 * Defines the meta-data for the Oracle JMS JCA connection
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSConnectionMetaData implements ConnectionMetaData {
    protected CciJMSConnection connection;// the cci connection

    /**
     * This constructor sets the cci connection.
     *
     * @param conn - the cci connection
     */
    public CciJMSConnectionMetaData(CciJMSConnection conn) {
        connection = conn;
    }

    /**
     * Return the product name
     *
     * @return the JMS provider name
     * @throws EISException
     */
    public String getEISProductName() throws EISException {
        try {
            return connection.getConnection().getMetaData().getJMSProviderName();
        } catch (Exception exception) {
            throw EISException.createException(exception);
        }
    }

    /**
     * Return the provider version
     *
     * @return the JMS provider version
     * @throws EISException
     */
    public String getEISProductVersion() throws EISException {
        try {
            return connection.getConnection().getMetaData().getProviderVersion();
        } catch (Exception exception) {
            throw EISException.createException(exception);
        }
    }

    /**
     * Username meta-data is not supported
     *
     * @return
     */
    public String getUserName() {
        return null;
    }
}
