/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import javax.resource.cci.*;
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

    public void close() {
    }

    public Interaction createInteraction() {
        return new XMLFileInteraction(this);
    }

    public XMLFileConnectionSpec getConnectionSpec() {
        return spec;
    }

    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    public XMLFileTransaction getXMLFileTransaction() {
        return transaction;
    }

    public ConnectionMetaData getMetaData() {
        return new XMLFileConnectionMetaData();
    }

    /**
     * Result sets are not supported.
     */
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }
}
