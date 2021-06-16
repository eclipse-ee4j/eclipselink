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
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import jakarta.resource.cci.*;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Connection to XML file JCA adapter.
 * This is an emulated JCA adapter that access XML files.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileConnection implements Connection {
    protected XMLFileConnectionSpec spec;
    protected XMLFileTransaction transaction;

    /**
     * Default constructor.
     */
    public XMLFileConnection() {
        this.transaction = new XMLFileTransaction(this);
    }

    public XMLFileConnection(XMLFileConnectionSpec spec) {
        this();
        this.spec = spec;
    }

    @Override
    public void close() {
    }

    @Override
    public Interaction createInteraction() {
        return new XMLFileInteraction(this);
    }

    public XMLFileConnectionSpec getConnectionSpec() {
        return spec;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    public XMLFileTransaction getXMLFileTransaction() {
        return transaction;
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return new XMLFileConnectionMetaData();
    }

    /**
     * Result sets are not supported.
     */
    @Override
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }
}
