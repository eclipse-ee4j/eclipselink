/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.*;
import javax.resource.cci.*;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;

/**
 * Defines the meta-data for the Mongo adaptor
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnectionMetaData implements ConnectionMetaData {
    private MongoConnection connection;

    /**
     * Default constructor.
     */
    public MongoConnectionMetaData(MongoConnection connection) {
        this.connection = connection;
    }

    /**
     * Constructor for inheritors
     */
    protected MongoConnectionMetaData() {
    }

    protected String getVersion() {
        BasicDBObject command = new BasicDBObject("buildInfo", null);
        CommandResult buildInfo = this.connection.getDB().command(command);
        return buildInfo.getString("version");
    }

    public String getEISProductName() throws ResourceException {
        try {
            return getVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getEISProductVersion() throws ResourceException {
        try {
            return getVersion();
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
