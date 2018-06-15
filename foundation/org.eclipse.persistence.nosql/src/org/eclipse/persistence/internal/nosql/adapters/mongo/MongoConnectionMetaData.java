/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

    @Override
    public String getEISProductName() throws ResourceException {
        try {
            return getVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getEISProductVersion() throws ResourceException {
        try {
            return getVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getUserName() throws ResourceException {
        try {
            return "";
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }
}
