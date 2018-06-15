/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import javax.naming.Reference;
import javax.resource.cci.*;

/**
 * Connection factory for XML file JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileConnectionFactory implements ConnectionFactory {

    /**
     * Default constructor.
     */
    public XMLFileConnectionFactory() {
    }

    @Override
    public Connection getConnection() {
        return new XMLFileConnection(new XMLFileConnectionSpec());
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) {
        return new XMLFileConnection((XMLFileConnectionSpec)spec);
    }

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new XMLFileAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        return new XMLFileRecordFactory();
    }

    @Override
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    @Override
    public void setReference(Reference reference) {
    }
}
