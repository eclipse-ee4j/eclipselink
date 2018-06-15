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
package org.eclipse.persistence.internal.eis.adapters.jms;


// JDK imports
import javax.resource.cci.*;

// TopLink imports
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * INTERNAL:
 * Record factory for the Oracle JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSRecordFactory implements RecordFactory {

    /**
     * The default constructor
     */
    public CciJMSRecordFactory() {
    }

    /**
     * Create and return an indexed record
     *
     * @param recordName - the name of the new record
     * @return the newly created record
     */
    @Override
    public IndexedRecord createIndexedRecord(String recordName) {
        return new CciJMSRecord();
    }

    /**
     * Mapped records are not supported
     */
    @Override
    public MappedRecord createMappedRecord(String recordName) {
        throw ValidationException.operationNotSupported("createMappedRecord");
    }
}
