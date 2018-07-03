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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;


public final class MWGroupingItem extends MWAttributeItem {


    // ******************* Static Methods **************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWGroupingItem.class);

        descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);

        return descriptor;
    }


    // ****************** Constructors ************

    /** Default constructor - for TopLink use only*/
    private MWGroupingItem() {
        super();
    }

    MWGroupingItem(MWReportQuery parent, MWQueryable queryable) {
        super(parent, queryable);
    }

    MWGroupingItem(MWReportQuery parent, Iterator queryables) {
        super(parent, queryables);

    }

    MWGroupingItem(MWReportQuery parent, Iterator queryables, Iterator allowsNull) {
        super(parent, queryables, allowsNull);
    }


    // ****************** MWQueryItem implementation ************

    public void removeSelfFromParent() {
        ((MWReportQuery) getParentQuery()).removeGroupingItem(this);
    }



    // ****************** Problem Handling************

    public Problem queryableNullProblem() {
        return buildProblem(
                ProblemConstants.QUERYABLE_NULL_FOR_GROUPING_ITEM,
                getParentQuery().signature(),
                new Integer(((MWReportQuery) getParentQuery()).indexOfGroupingItem(this) + 1));
    }

    public Problem queryableInvalidProblem(MWQueryable queryable) {
        throw new IllegalStateException("Any type of MWQueryable is valid for a grouping item");
    }

    public boolean isQueryableValid(MWQueryable queryable) {
        return true;
    }


    // ****************** Runtime Conversion ************

    protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
        Expression expression = getQueryableArgument().runtimeExpression(readQuery.getExpressionBuilder());
        ((ReportQuery) readQuery).addGrouping(expression);
    }

}
