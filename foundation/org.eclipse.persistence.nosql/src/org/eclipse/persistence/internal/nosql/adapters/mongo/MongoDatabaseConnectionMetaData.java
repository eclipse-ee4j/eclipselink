/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.bson.Document;

import com.mongodb.BasicDBObject;

/**
 * Defines the meta-data for the Mongo adaptor
 *
 * @since EclipseLink 2.7
 */
public class MongoDatabaseConnectionMetaData extends MongoConnectionMetaData {
    protected MongoDatabaseConnection connection;

    /**
     * Default constructor.
     */
    public MongoDatabaseConnectionMetaData(MongoDatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    protected String getVersion() {
        BasicDBObject command = new BasicDBObject("buildInfo", null);
        Document buildInfo = this.connection.getDB().runCommand(command);
        return buildInfo.getString("version");
    }
}
