/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p>
 * <b>Purpose</b>: Define a singleton empty record to avoid record creation when not required.
 * <p>
 * @since 11
 * @author James Sutherland
 */
public class EmptyRecord extends DatabaseRecord {

    /** PERF: Store a singleton empty record to avoid creation when not required. */
    public static final DatabaseRecord emptyRecord = new EmptyRecord();

    /**
     * Return the singleton empty record.
     */
    public static DatabaseRecord getEmptyRecord() {
        return EmptyRecord.emptyRecord;
    }

    protected EmptyRecord() {
        super(0);
    }

    /**
     * Need to return a real record.
     */
    public DatabaseRecord clone() {
        return new DatabaseRecord();
    }

    public void add(DatabaseField key, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object put(DatabaseField key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void replaceAt(Object value, int index) {
        throw new UnsupportedOperationException();
    }
}
