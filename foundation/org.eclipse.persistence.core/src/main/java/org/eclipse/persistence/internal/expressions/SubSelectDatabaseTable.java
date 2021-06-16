/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.expressions;

import java.io.IOException;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.*;

/**
 * INTERNAL:
 * Allow a table to reference a sub-select to support sub-selects in the from clause.
 * @author James Sutherland
 */
public class SubSelectDatabaseTable extends DatabaseTable {
    private Expression subSelect;

    public SubSelectDatabaseTable(Expression subSelect) {
        super();
        this.subSelect = subSelect;
    }

    public Expression getSubSelect() {
        return subSelect;
    }

    public void setSubSelect(Expression subSelect) {
        this.subSelect = subSelect;
    }

    /**
     * Print the table's SQL from clause.
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) throws IOException {
        this.subSelect.printSQL(printer);
    }
}
