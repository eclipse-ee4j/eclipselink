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

import javax.naming.Reference;
import jakarta.resource.cci.*;

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
