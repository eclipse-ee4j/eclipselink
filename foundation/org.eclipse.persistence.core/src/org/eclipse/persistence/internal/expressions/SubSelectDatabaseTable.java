/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
    public void printSQL(ExpressionSQLPrinter printer) throws IOException {
        this.subSelect.printSQL(printer);
    }
}
