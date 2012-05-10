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
package org.eclipse.persistence.internal.eis.adapters.aq;

import javax.resource.*;
import javax.resource.cci.*;

/**
 * Defines the meta-data for the Oracle AQ adaptor
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQConnectionMetaData implements ConnectionMetaData {
    protected AQConnection connection;

    /**
     * Default constructor.
     */
    public AQConnectionMetaData(AQConnection connection) {
        this.connection = connection;
    }

    public String getEISProductName() throws ResourceException {
        try {
            return this.connection.getDatabaseConnection().getMetaData().getDatabaseProductName();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getEISProductVersion() throws ResourceException {
        try {
            return connection.getDatabaseConnection().getMetaData().getDatabaseProductVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getUserName() throws ResourceException {
        try {
            return this.connection.getDatabaseConnection().getMetaData().getUserName();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }
}
