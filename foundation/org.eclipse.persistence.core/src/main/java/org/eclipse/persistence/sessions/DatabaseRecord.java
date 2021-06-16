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
package org.eclipse.persistence.sessions;

import java.util.*;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p>
 * <b>Purpose</b>: Define a representation of a database row as field{@literal =>}value pairs.
 * This is the database row implementation class, the Record or java.util.Map interfaces
 * should be used to access this class instead of the implementation class.
 * <p>
 * <b>Responsibilities</b>: <ul>
 *        <li> Implement the common hashtable collection protocol.
 *        <li> Allow get and put on the field or field name.
 * </ul>
 * @see DatabaseField
 * @see Record
 * @see java.util.Map
 */
public class DatabaseRecord extends AbstractRecord {

    /**
     * INTERNAL:
     * Returns a record (of default size).
     */
    public DatabaseRecord() {
        super();
    }

    /**
     * INTERNAL:
     * Returns a record of the given initial capacity.
     * @param initialCapacity
     */
    public DatabaseRecord(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * INTERNAL:
     * Builds row from database result fields and values.
     * Note: the entire database result will share the same fields vector.
     * @param fields Vector of fields
     * @param values Vector of values
     */
    public DatabaseRecord(Vector fields, Vector values) {
        super(fields, values);
    }

    /**
     * INTERNAL:
     * Builds row from database result fields and values.
     * Note: the entire database result will share the same fields vector.
     * @param fields Vector of fields
     * @param values Vector of values
     * @param size of record
     */
    public DatabaseRecord(Vector fields, Vector values, int size) {
        super(fields, values, size);
    }
}
