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
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.resource.*;
import javax.resource.cci.*;

import oracle.kv.KVVersion;

/**
 * Defines the meta-data for the Oracle NoSQL adaptor
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLConnectionMetaData implements ConnectionMetaData {
    protected OracleNoSQLConnection connection;

    /**
     * Default constructor.
     */
    public OracleNoSQLConnectionMetaData(OracleNoSQLConnection connection) {
        this.connection = connection;
    }

    public String getEISProductName() throws ResourceException {
        try {
            return "Oracle NoSQL Database";
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    public String getEISProductVersion() throws ResourceException {
        try {
            return KVVersion.CURRENT_VERSION.getVersionString();
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
