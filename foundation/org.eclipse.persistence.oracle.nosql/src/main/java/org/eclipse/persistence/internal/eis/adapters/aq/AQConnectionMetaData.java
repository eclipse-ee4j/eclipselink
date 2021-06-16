/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.aq;

import jakarta.resource.*;
import jakarta.resource.cci.*;

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

    @Override
    public String getEISProductName() throws ResourceException {
        try {
            return this.connection.getDatabaseConnection().getMetaData().getDatabaseProductName();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getEISProductVersion() throws ResourceException {
        try {
            return connection.getDatabaseConnection().getMetaData().getDatabaseProductVersion();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public String getUserName() throws ResourceException {
        try {
            return this.connection.getDatabaseConnection().getMetaData().getUserName();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }
}
