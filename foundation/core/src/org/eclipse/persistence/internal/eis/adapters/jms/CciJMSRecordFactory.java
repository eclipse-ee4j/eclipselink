/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
    public IndexedRecord createIndexedRecord(String recordName) {
        return new CciJMSRecord();
    }

    /**
     * Mapped records are not supported
     */
    public MappedRecord createMappedRecord(String recordName) {
        throw ValidationException.operationNotSupported("createMappedRecord");
    }
}