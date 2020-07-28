/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Tomas Kraus - Initial API and implementation
package org.eclipse.persistence.internal.descriptors;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.StringHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p><b>Purpose</b>: Define any useful static methods to manipulate with descriptors content.</p>
 */
public class DescriptorHelper {

    /**
     * This class is just an envelope for static methods so no instances are allowed.
     */
    private DescriptorHelper() {
        throw new UnsupportedOperationException("Instances of DescriptorHelper are not allowed");
    }

    /**
     * Append list of columns from descriptor class to provided {@link StringBuilder}.
     * Use to create content of columns brackets in <code>INSERT INTO table (col1, col1) VALUES ...</code>.
     * @param target Target {@link StringBuilder}.
     * @param mappings Object attributes mappings.
     * @param separator String to separate values in list.
     */
    public static void buildColsFromMappings(final StringBuilder target,
            final Collection<DatabaseMapping> mappings, final String separator) {
        for (Iterator<DatabaseMapping> i = mappings.iterator(); i.hasNext(); ) {
            target.append(i.next().getField().getName());
            if (i.hasNext()) {
                target.append(separator);
            }
        }
    }

    /**
     * Append list of values as list of question marks from descriptor class to provided
     * {@link StringBuilder}.
     * Use to create content of <code>VALUES</code> brackets in <code>INSERT INTO table ... VALUES (?, ?) ...</code>.
     * @param target Target {@link StringBuilder}.
     * @param mappings Object attributes mappings.
     * @param separator String to separate values in list.
     */
    public static void buildValuesAsQMarksFromMappings(final StringBuilder target,
            final Collection<DatabaseMapping> mappings, final String separator) {
        final int count = mappings.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    target.append(separator);
                }
                target.append(StringHelper.QUESTION_MARK).append(Integer.toString(i+1));
            }
        }
    }

    /**
     * Append list of column to value bindings from descriptor class to provided {@link StringBuilder}.
     * Use to create value to column bindings of <code>SET</code> content in <code>UPDATE table SET col1 = ?2 WHERE ...</code>
     * @param target Target {@link StringBuilder}.
     * @param mappings Object attributes mappings.
     * @param keys Entity primary key fields.
     * @param valueIndex First value index in column to value bindings (e.g.
     *                   <code>column = ?&gt;valueIndex&gt;</code>).
     * @param binder String to bind value to column (e.g. <code>" = "</code>
     *               in <code>column = ?&gt;valueIndex&gt;</code>).
     * @param separator String to separate values in list.
     */
    public static void buildColsAndValuesBindingsFromMappings(final StringBuilder target,
            final Collection<DatabaseMapping> mappings, final Collection<DatabaseField> keys,
            final int valueIndex, final String binder, final String separator) {
        int idx = valueIndex;
        for (Iterator<DatabaseMapping> i = mappings.iterator(); i.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping) i.next();
            DatabaseField field = mapping.getField();
            if (!keys.contains(field)) {
                target.append(field.getName()).append(binder).append(Integer.toString(++idx));
                if (i.hasNext()) {
                    target.append(separator);
                }
            }
        }

    }

}
