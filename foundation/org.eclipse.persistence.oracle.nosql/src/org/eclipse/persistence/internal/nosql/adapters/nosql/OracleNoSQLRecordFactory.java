/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.resource.cci.*;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Record factory for Oracle NoSQL JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLRecordFactory implements RecordFactory {

    /**
     * Default constructor.
     */
    public OracleNoSQLRecordFactory() {
    }

    @Override
    public IndexedRecord createIndexedRecord(String recordName) {
        throw ValidationException.operationNotSupported("createIndexedRecord");
    }

    @Override
    public MappedRecord createMappedRecord(String recordName) {
        return new OracleNoSQLRecord();
    }
}
