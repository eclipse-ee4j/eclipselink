/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.List;
import java.util.Vector;

import javax.persistence.criteria.Selection;

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
 * @see javax.persistence.criteria CriteriaQuery
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
