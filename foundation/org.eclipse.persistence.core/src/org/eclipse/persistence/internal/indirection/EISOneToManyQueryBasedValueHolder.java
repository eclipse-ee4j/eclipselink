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
package org.eclipse.persistence.internal.indirection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * Value holder used to defer an EIS 1-m mapping query.
 * For composite source foreign keys EIS 1-m's a query must be performed
 * for each primary key, so a different type of value holder is required.
 */
public class EISOneToManyQueryBasedValueHolder extends QueryBasedValueHolder {
    private EISOneToManyMapping mapping;

    public EISOneToManyQueryBasedValueHolder(EISOneToManyMapping mapping, ReadQuery query, AbstractRecord sourceRow, AbstractSession session) {
        super(query, sourceRow, session);
        this.mapping = mapping;
    }

    protected Object instantiate(AbstractSession session) throws DatabaseException {
        Vector rows = this.mapping.getForeignKeyRows(this.getRow(), session);

        int size = rows.size();
        ContainerPolicy cp = ((ReadAllQuery)this.getQuery()).getContainerPolicy();
        Object returnValue = cp.containerInstance(size);

        for (int i = 0; i < size; i++) {
        	AbstractRecord nextRow = (AbstractRecord)rows.get(i);
            Object results = session.executeQuery(getQuery(), nextRow);

            if (results instanceof Collection) {
                Iterator iter = ((Collection)results).iterator();
                while (iter.hasNext()) {
                    cp.addInto(iter.next(), returnValue, session);
                }
            } else if (results instanceof java.util.Map) {
                Iterator iter = ((java.util.Map)results).values().iterator();
                while (iter.hasNext()) {
                    cp.addInto(iter.next(), returnValue, session);
                }
            } else {
                cp.addInto(results, returnValue, session);
            }
        }
        return returnValue;
    }
}
