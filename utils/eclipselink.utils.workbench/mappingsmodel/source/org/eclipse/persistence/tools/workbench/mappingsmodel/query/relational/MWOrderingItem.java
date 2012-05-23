/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;


public final class MWOrderingItem extends MWAttributeItem implements Ordering {
		
	private volatile boolean ascending;
	
	// ******************* Static Methods **************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWOrderingItem.class);

		descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);

		((AbstractDirectMapping) descriptor.addDirectMapping("ascending", "ascending/text()")).setNullValue(Boolean.TRUE);
				
		return descriptor;
	}				

	
	// ****************** Constructors ************

	/** Default constructor - for TopLink use only*/
	private MWOrderingItem() {
		super();
	}

	MWOrderingItem(MWRelationalReadAllQuery parent, MWQueryable queryable) {
		super(parent, queryable);		
	}
	
	MWOrderingItem(MWRelationalReadAllQuery parent, Iterator queryables) {
		super(parent, queryables);
		
	}
	
	MWOrderingItem(MWRelationalReadAllQuery parent, Iterator queryables, Iterator allowsNull) {
		super(parent, queryables, allowsNull);		
	}
	
	
	/** Initialize persistent state*/
	protected void initialize(Node parentNode) {
		super.initialize(parentNode);
		
		this.ascending = true;
	}
	
	
	// ****************** Accessors ************
	
	public boolean isAscending() {
		return this.ascending;
	}
	
	public boolean isDesending() {
		return !isAscending();
	}
	
	public void setAscending(boolean ascending) {
		boolean old = this.ascending;
		this.ascending = ascending;
		firePropertyChanged(ASCENDING_PROPERTY, old, this.ascending);
	}

	
	// ****************** MWQueryItem implementation ************

	public void removeSelfFromParent() {
		((MWRelationalReadAllQuery) getParentQuery()).removeOrderingItem(this);
	}
	
	
	// ****************** Problems ************
	
	public Problem queryableNullProblem() {
		return buildProblem(
				ProblemConstants.QUERYABLE_NULL_FOR_ORDERING_ITEM, 
				getParentQuery().signature(),
				new Integer(((MWRelationalReadAllQuery) getParentQuery()).indexOfOrderingItem(this) + 1));
	}
	
	public Problem queryableInvalidProblem(MWQueryable queryable) {
		return buildProblem(
                        ProblemConstants.QUERYABLE_NOT_VALID_FOR_READ_ALL_QUERY_ORDERING_ITEM,
						queryable.displayString(),
						getParentQuery().signature());
	}
	
	public boolean isQueryableValid(MWQueryable queryable) {
		return queryable.isValidForReadAllQueryOrderable();
	}
	
	
	// ****************** Runtime Conversion ************
	
	protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
		Expression expression = getQueryableArgument().runtimeExpression(readQuery.getExpressionBuilder());

		if (isAscending()) {
            ((ReadAllQuery) readQuery).addOrdering(expression.ascending());
		}
		else {
            ((ReadAllQuery) readQuery).addOrdering(expression.descending());
		}
		
	}

	
	public String displayString() {
		String displayString = super.displayString() + " (";
		if (isAscending()) {
			displayString += "Ascending"; //TODO i18n?
		}
		else {
			displayString += "Descending";
		}
		return displayString + ")";
	}
}
