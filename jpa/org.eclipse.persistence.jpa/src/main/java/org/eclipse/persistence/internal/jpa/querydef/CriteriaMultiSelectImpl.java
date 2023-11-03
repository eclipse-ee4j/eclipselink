/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, 2022 IBM Corporation. All rights reserved.
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
//     10/25/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa.querydef;

import java.util.Objects;
import java.util.function.BiFunction;

import jakarta.persistence.criteria.CriteriaSelect;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class CriteriaMultiSelectImpl<T> implements CriteriaSelect<T>, CriteriaSelectInternal<T> {

    // Union operator with query builder
    enum Union {

        UNION((first, second) -> {
            first.union(second);
            return first;
        }),
        UNION_ALL((first, second) -> {
            first.unionAll(second);
            return first;
        }),
        INTERSECT((first, second) -> {
            first.intersect(second);
            return first;
        }),
        INTERSECT_ALL((first, second) -> {
            first.intersectAll(second);
            return first;
        }),
        EXCEPT((first, second) -> {
            first.except(second);
            return first;
        }),
        EXCEPT_ALL((first, second) -> {
            first.exceptAll(second);
            return first;
        });

        // Builder for union/intersect/except query
        private final BiFunction<ReadAllQuery, ReportQuery, ReadAllQuery> builder;

        Union(BiFunction<ReadAllQuery, ReportQuery, ReadAllQuery> builder) {
            this.builder = builder;
        }

    }

    private final CriteriaSelectInternal<T> first;
    // 2nd query
    private final CriteriaSelectInternal<?> second;
    // Union operator
    private final Union union;
    protected Class<T> queryType;

    public CriteriaMultiSelectImpl(CriteriaSelect<T> first,
                                   CriteriaSelect<?> second,
                                   Union union) {
        Objects.requireNonNull(first, "First component of union expression is null");
        Objects.requireNonNull(second, "Second component of union expression is null");
        this.first = (CriteriaSelectInternal<T>) first;
        this.second = (CriteriaSelectInternal<?>) second;
        this.union = union;
        this.queryType = this.first.getResultType();
    }

    @Override
    public Class<T> getResultType() {
        return queryType;
    }

    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     */
    @Override
    public DatabaseQuery translate() {
        return translate(new BooleanValue());
    }

    private DatabaseQuery translate(BooleanValue haveTop) {
        DatabaseQuery firstQuery;
        if (first instanceof CriteriaQueryImpl) {
            // Only the top level DatabaseQuery instance may not be the ReportQuery.
            // This is the lowest level of all first node path
            if (haveTop.get()) {
                firstQuery = ((CriteriaQueryImpl<T>) first).transalteToReportQuery();
            } else {
                firstQuery = first.translate();
                haveTop.setTrue();
            }
        } else {
            firstQuery = ((CriteriaMultiSelectImpl<T>) first).translate(haveTop);
        }
        DatabaseQuery secondQuery = (second instanceof CriteriaQueryImpl)
                ? ((CriteriaQueryImpl<?>) second).transalteToReportQuery()
                : ((CriteriaMultiSelectImpl<?>) second).translate(haveTop);
        return union.builder.apply((ReadAllQuery) firstQuery, (ReportQuery) secondQuery);
    }

    // Boolean value holder used in criteria query to DatabaseQuery translation
    // Helps to find top level DatabaseQuery instance
    private static final class BooleanValue {

        private static boolean value;

        private BooleanValue() {
            value = false;
        }

        private boolean get() {
            return value;
        }

        private void setTrue() {
            value = true;
        }

    }

}
