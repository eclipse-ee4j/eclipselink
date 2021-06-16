/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
