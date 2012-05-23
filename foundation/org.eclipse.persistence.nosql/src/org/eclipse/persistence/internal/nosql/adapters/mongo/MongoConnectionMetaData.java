/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.*;
import javax.resource.cci.*;

/**
 * Defines the meta-data for the Mongo adaptor
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnectionMetaData implements ConnectionMetaData {
    protected MongoConnection connection;

    /**
     * Default constructor.
     */
    public MongoConnectionMetaData(MongoConnection connection) {
        this.connection = connection;
    }

    public String getEISProductName() throws ResourceException {
        try {
            return this.connection.getDB().getMongo().getVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getEISProductVersion() throws ResourceException {
        try {
            return this.connection.getDB().getMongo().getVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getUserName() throws ResourceException {
        try {
            return "";
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }
}
