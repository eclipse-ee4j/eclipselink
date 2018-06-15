/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;

public final class MWBatchReadItem extends MWAttributeItem {


    // ******************* Static Methods **************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWBatchReadItem.class);

        descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);

        return descriptor;
    }

    // ****************** Constructors ************

    /** Default constructor - for TopLink use only*/
    private MWBatchReadItem() {
        super();
    }

    MWBatchReadItem(MWRelationalReadAllQuery parent, MWQueryable queryable) {
        super(parent, queryable);
    }

    MWBatchReadItem(MWRelationalReadAllQuery parent, Iterator queryables) {
        super(parent, queryables);
    }

    MWBatchReadItem(MWRelationalReadAllQuery parent, Iterator queryables, Iterator allowsNull) {
        super(parent, queryables, allowsNull);
    }


    // ****************** MWQueryItem implementation ************

    public void removeSelfFromParent() {
        ((MWRelationalReadAllQuery) getParentQuery()).removeBatchReadItem(this);
    }


    // ****************** Problem Handling************

    public Problem queryableNullProblem() {
        return buildProblem(
                ProblemConstants.QUERYABLE_NULL_FOR_BATCH_READ_ITEM,
                getParentQuery().signature(),
                new Integer(((MWRelationalReadAllQuery) getParentQuery()).indexOfBatchReadItem(this) + 1));
    }

    public Problem queryableInvalidProblem(MWQueryable queryable) {
        return buildProblem(ProblemConstants.QUERYABLE_NOT_VALID_FOR_READ_ALL_QUERY_BATCH_READ_ITEM, queryable.displayString(), getParentQuery().signature());
    }

    public boolean isQueryableValid(MWQueryable queryable) {
        return queryable.isValidForBatchReadAttribute();
    }


    // ****************** runtime conversion ************

    protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
        ((ReadAllQuery) readQuery).addBatchReadAttribute(getQueryableArgument().runtimeExpression(readQuery.getExpressionBuilder()));
    }

}
