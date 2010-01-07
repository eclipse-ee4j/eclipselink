/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

    public Connection getConnection() {
        return new XMLFileConnection(new XMLFileConnectionSpec());
    }

    public Connection getConnection(ConnectionSpec spec) {
        return new XMLFileConnection((XMLFileConnectionSpec)spec);
    }

    public ResourceAdapterMetaData getMetaData() {
        return new XMLFileAdapterMetaData();
    }

    public RecordFactory getRecordFactory() {
        return new XMLFileRecordFactory();
    }

    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    public void setReference(Reference reference) {
    }
}
