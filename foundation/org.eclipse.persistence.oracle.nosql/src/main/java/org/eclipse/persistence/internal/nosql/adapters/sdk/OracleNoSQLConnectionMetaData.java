/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.ConnectionMetaData;
import oracle.nosql.driver.DriverMain;


/**
 * Defines the meta-data for the Oracle NoSQL adaptor
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLConnectionMetaData implements ConnectionMetaData {
    protected OracleNoSQLConnection connection;

    /**
     * Default constructor.
     */
    public OracleNoSQLConnectionMetaData(OracleNoSQLConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getEISProductName() throws ResourceException {
        try {
            return "Oracle NoSQL Database";
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getEISProductVersion() throws ResourceException {
        try {
            return DriverMain.getLibraryVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getUserName() throws ResourceException {
        return "";
    }
}
