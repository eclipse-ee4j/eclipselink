/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.nosql.adapters.mongo;

import javax.resource.cci.ConnectionFactory;

import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoDatabaseConnectionFactory;

/**
 * Provides connection information to the Mongo 3 database.
 *
 * @since EclipseLink 2.7
 */
public class Mongo3ConnectionSpec extends MongoConnectionSpec {

    @Override
    protected ConnectionFactory createMongoConnectionFactory() {
        return new MongoDatabaseConnectionFactory();
    }
}
