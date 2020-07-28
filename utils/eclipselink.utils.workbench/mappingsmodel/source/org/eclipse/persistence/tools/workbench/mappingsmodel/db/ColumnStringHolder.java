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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.ObjectStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class ColumnStringHolder extends ObjectStringHolder {

    public ColumnStringHolder(MWColumn column, StringConverter stringConverter) {
        super(column, stringConverter);
    }

    public ColumnStringHolder(MWColumn column) {
        this(column, DEFAULT_STRING_CONVERTER);
    }

    public MWColumn getColumn() {
        return (MWColumn) this.object;
    }


    // ********** static methods **********

    public static ColumnStringHolder[] buildHolders(Iterator columns) {
        return buildHolders(CollectionTools.list(columns));
    }

    public static ColumnStringHolder[] buildHolders(Collection columns) {
        MWColumn[] columnArray = (MWColumn[]) columns.toArray(new MWColumn[columns.size()]);
        ColumnStringHolder[] holders = new ColumnStringHolder[columnArray.length];
        for (int i = columnArray.length; i-- > 0; ) {
            holders[i] = new ColumnStringHolder(columnArray[i]);
        }
        return holders;
    }


    // ********** constants **********

    public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
        public String convertToString(Object o) {
            return ((MWColumn) o).getName().toLowerCase();
        }
    };

}
