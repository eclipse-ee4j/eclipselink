/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.history;

import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * INTERNAL:
 * A decorated database table is one that has an as of clause, for Oracle 9R2
 * query flashback.
 * Specifying a table to be read as of a past time is termed 'table decoration'.
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 * @author Stephen McRitchie
 */
public class DecoratedDatabaseTable extends DatabaseTable {
    private AsOfClause asOfClause;

    protected DecoratedDatabaseTable() {
        
    }
    
    public DecoratedDatabaseTable(String name, AsOfClause asOfClause) {
        super(name);
        this.asOfClause = asOfClause;
    }

    /**
     * INTERNAL:
     * This check is optimized for the case where no AsOfClause is specified.
     */
    public boolean isDecorated() {
        return ((getAsOfClause() != AsOfClause.NO_CLAUSE) && (getAsOfClause() != null) && (getAsOfClause().getValue() != null));
    }

    public AsOfClause getAsOfClause() {
        return asOfClause;
    }

    public void setAsOfClause(AsOfClause asOfClause) {
        this.asOfClause = asOfClause;
    }
}
