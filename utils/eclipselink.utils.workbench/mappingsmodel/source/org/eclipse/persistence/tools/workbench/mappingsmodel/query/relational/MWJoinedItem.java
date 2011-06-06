/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public final class MWJoinedItem extends MWAttributeItem {


	// ******************* Static Methods **************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWJoinedItem.class);

		descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);
				
		return descriptor;
	}
	
	
	// ****************** Constructors ************

	/** Default constructor - for TopLink use only*/
	private MWJoinedItem() {
		super();
	}

	MWJoinedItem(MWAbstractRelationalReadQuery parent, MWQueryable queryable) {
		super(parent, queryable);		
	}
	
	MWJoinedItem(MWAbstractRelationalReadQuery parent, Iterator queryables) {
		super(parent, queryables);	
	}
	
	MWJoinedItem(MWAbstractRelationalReadQuery parent, Iterator queryables, Iterator allowsNull) {
		super(parent, queryables, allowsNull);		
	}

	
	// ****************** MWQueryItem implementation ************

	public void removeSelfFromParent() {
		((MWAbstractRelationalReadQuery) getParentQuery()).removeJoinedItem(this);
	}
	
	
	// ****************** Problem Handling************

	public Problem queryableNullProblem() {
		return buildProblem(
				ProblemConstants.QUERYABLE_NULL_FOR_JOINED_ITEM, 
				getParentQuery().signature(),
				new Integer(((MWAbstractRelationalReadQuery) getParentQuery()).indexOfJoinedItem(this) + 1));
	}

	public Problem queryableInvalidProblem(MWQueryable queryable) {
		return buildProblem(ProblemConstants.QUERYABLE_NOT_VALID_FOR_READ_QUERY_JOINED_READ_ITEM, queryable.displayString(), getParentQuery().signature());
	}
	
	public boolean isQueryableValid(MWQueryable queryable) {
		return queryable.isValidForJoinedAttribute();
	}

	
	// ****************** Runtime Conversion ************
	
	protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
		readQuery.addJoinedAttribute(getQueryableArgument().runtimeExpression(readQuery.getExpressionBuilder()));
	}

}
