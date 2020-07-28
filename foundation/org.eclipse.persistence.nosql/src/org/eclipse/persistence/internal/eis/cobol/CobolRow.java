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
package org.eclipse.persistence.internal.eis.cobol;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.DatabaseRecord;

import java.util.Vector;

/**
* <b>Purpose</b>: This class extends database row to allow for <code>CobolRedefinedFieldValue</code>
* use as a value and for on-demand value extraction.
*/
public class CobolRow extends DatabaseRecord {
    public CobolRow() {
        super();
    }

    public CobolRow(int initialCapacity) {
        super(initialCapacity);
    }

    public CobolRow(Vector fields, Vector values) {
        super(fields, values);
    }

    /**
    * overrides get method to allow on-demand extraction.
    */
    public Object get(Object key) {
        Object value = super.get(key);
        if (value instanceof CobolRedefinedFieldValue) {
            value = ((CobolRedefinedFieldValue)value).getValue();
        }
        return value;
    }

    /**
    * overrides get method to allow on-demand extraction.
    */
    public Object get(String fieldName) {
        Object value = super.get(fieldName);
        if (value instanceof CobolRedefinedFieldValue) {
            value = ((CobolRedefinedFieldValue)value).getValue();
        }
        return value;
    }

    /**
    * overrides get method to allow on-demand extraction.
    */
    public Object get(DatabaseField key) {
        Object value = super.get(key);
        if (value instanceof CobolRedefinedFieldValue) {
            value = ((CobolRedefinedFieldValue)value).getValue();
        }
        return value;
    }
}
