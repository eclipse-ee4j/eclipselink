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

    @Override
    public IndexedRecord createIndexedRecord(String recordName) {
        return null;
    }

    @Override
    public MappedRecord createMappedRecord(String recordName) {
        EISDOMRecord record = new EISDOMRecord();
        record.setRecordName(recordName);
        return record;
    }
}
