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
import org.eclipse.persistence.queries.ReportQuery;

//TODO what happens if a MWreportAttributeItem is remove, shouldn't this be removed if it was built with that particular attributeItem???
public final class MWReportOrderingItem extends MWAttributeItem implements Ordering {
   			
	private volatile boolean ascending;
		
	private volatile String itemName; //this is only used if the orderingItem is built with a MWReportAttributeItem
	
    // ******************* Static Methods **************
    
    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWReportOrderingItem.class);

        descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);

        ((AbstractDirectMapping) descriptor.addDirectMapping("ascending", "ascending/text()")).setNullValue(Boolean.TRUE);
                
        descriptor.addDirectMapping("itemName", "item-name/text()");

        return descriptor;
    }               

    
    // ****************** Constructors ************

	/** Default constructor - for TopLink use only*/
	private MWReportOrderingItem() {
		super();
	}

	MWReportOrderingItem(MWReportQuery parent, MWReportAttributeItem attributeItem) {
		super(parent, attributeItem.getQueryableArgument());	
		this.itemName = attributeItem.getName();
	}
	
	MWReportOrderingItem(MWReportQuery parent, MWQueryable queryable) {
		super(parent, queryable);		
	}
	
	MWReportOrderingItem(MWReportQuery parent, Iterator queryables) {
		super(parent, queryables);
		
	}
	
	MWReportOrderingItem(MWReportQuery parent, Iterator queryables, Iterator allowsNull) {
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

	public String getItemName() {
	    return this.itemName;
	}
	
	public void removeSelfFromParent() {
        ((MWReportQuery) getParentQuery()).removeOrderingItem(this);
    }
	
	// ****************** Problems ************
	
	public Problem queryableNullProblem() {
		return buildProblem(
				ProblemConstants.QUERYABLE_NULL_FOR_ORDERING_ITEM, 
				getParentQuery().signature(),
				new Integer(((MWReportQuery) getParentQuery()).indexOfOrderingItem(this) + 1));
	}
	
	public Problem queryableInvalidProblem(MWQueryable queryable) {
		throw new UnsupportedOperationException();
	}
	
	public boolean isQueryableValid(MWQueryable queryable) {
		return true;
	}

	
	// ****************** Runtime Conversion ************

	protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
		ReportQuery reportQuery = (ReportQuery) readQuery;
		Expression expression = getQueryableArgument().runtimeExpression(reportQuery.getExpressionBuilder());

		if (this.itemName != null) {
			MWReportAttributeItem attributeItem = attributeItem();
	
			if (attributeItem.getFunction() == MWReportAttributeItem.NO_FUNCTION) {
			    //do nothing
			}
			else if (attributeItem.getFunction() == MWReportAttributeItem.AVERAGE_FUNCTION) {
				expression = expression.average();
			}
			else if (attributeItem.getFunction() == MWReportAttributeItem.COUNT_FUNCTION) {
			    //TODO not sure what should be done for the count() case without an attribute chose, hmmm
				expression = expression.count();
			}
			else if (attributeItem.getFunction() == MWReportAttributeItem.DISTINCT_FUNCTION) {
				expression = expression.distinct();
			}
			else if (attributeItem.getFunction() == MWReportAttributeItem.MAXIMUM_FUNCTION) {
				expression = expression.maximum();
			}
			else if (attributeItem.getFunction() == MWReportAttributeItem.MINIMUM_FUNCTION) {
				expression = expression.minimum();
			}		
			else if (attributeItem.getFunction() == MWReportAttributeItem.STANDARD_DEVIATION_FUNCTION) {
				expression = expression.standardDeviation();
			}		
			else if (attributeItem.getFunction() == MWReportAttributeItem.SUM_FUNCTION) {
				expression = expression.sum();
			}		
			else if (attributeItem.getFunction() == MWReportAttributeItem.VARIANCE_FUNCTION) {
				expression = expression.variance();
			}	
			else {
			    expression = expression.getFunction(attributeItem.getFunction());
			}
		}
		
		if (isAscending()) {
			expression = expression.ascending();
		}
		else {
            expression = expression.descending();
		}
		
		reportQuery.addOrdering(expression);
	}
	
	
	MWReportAttributeItem attributeItem() {
	    for (Iterator i = ((MWReportQuery) getParent()).attributeItems(); i.hasNext();) {
	        MWReportAttributeItem item = (MWReportAttributeItem) i.next();
	        if (item.getName().equals(this.itemName)) {
	            return item;
	        }
	    }
	    return null;
	}

	public String displayString() {
	    String displayString; 
	    if (this.itemName == null) {
	        displayString = super.displayString();
	    }
	    else {
	        displayString = this.itemName;
	    }
		if (isAscending()) {
			displayString += " (Ascending)"; //TODO i18n?
		}
		else {
			displayString += " (Descending)";
		}
		return displayString;
	}
	
}
