/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.List;
import java.util.Vector;

import jakarta.persistence.criteria.Selection;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

/**
 * <p>
 * <b>Purpose</b>: This is a special subclass of the ReportQuery that constructs Tuple results.
 * <p>
 * <b>Description</b>: A subclass of ReportQuery this query type combines multiple selections into
 * <p>
 *
 * @see jakarta.persistence.criteria CriteriaQuery
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class TupleQuery extends ReportQuery {

    protected List<? super Selection<?>> selections;
    public TupleQuery(List<? super Selection<?>> selections){
        super();
        this.selections = selections;
    }

    /**
     * INTERNAL:
     * Construct a result from a row. Either return a ReportQueryResult or just the attribute.
     */
    @Override
    public Object buildObject(AbstractRecord row, Vector toManyJoinData) {
        return new TupleImpl(this.selections, new ReportQueryResult(this, row, toManyJoinData));
    }
}
