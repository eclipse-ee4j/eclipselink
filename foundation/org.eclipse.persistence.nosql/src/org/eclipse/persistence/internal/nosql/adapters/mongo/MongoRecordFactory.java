/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.cci.*;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Record factory for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoRecordFactory implements RecordFactory {

    /**
     * Default constructor.
     */
    public MongoRecordFactory() {
    }

    public IndexedRecord createIndexedRecord(String recordName) {
        throw ValidationException.operationNotSupported("createIndexedRecord");
    }

    public MappedRecord createMappedRecord(String recordName) {
        return new MongoRecord();
    }
}
