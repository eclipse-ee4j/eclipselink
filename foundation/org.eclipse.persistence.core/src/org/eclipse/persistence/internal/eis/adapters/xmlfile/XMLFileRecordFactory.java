/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.eis.EISDOMRecord;

/**
 * Record factory for XML file JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileRecordFactory implements RecordFactory {

    /**
     * Default constructor.
     */
    public XMLFileRecordFactory() {
    }

    public IndexedRecord createIndexedRecord(String recordName) {
        return null;
    }

    public MappedRecord createMappedRecord(String recordName) {
        EISDOMRecord record = new EISDOMRecord();
        record.setRecordName(recordName);
        return record;
    }
}
