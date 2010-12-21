/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;


public final class MWReportAttributeItem extends MWAttributeItem {
		
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private volatile String function;
		public static final String FUNCTION_PROPERTY = "function";
		public static final String AVERAGE_FUNCTION = "Average";
		public static final String COUNT_FUNCTION = "Count";
		public static final String DISTINCT_FUNCTION = "Distinct";
		public static final String MAXIMUM_FUNCTION = "Maximum";
		public static final String MINIMUM_FUNCTION = "Minimum";
		public static final String STANDARD_DEVIATION_FUNCTION = "Standard Deviation";
		public static final String VARIANCE_FUNCTION = "Variance";
		public static final String SUM_FUNCTION = "Sum";
		public static final String NO_FUNCTION = "None";
	
	public static final String[] FUNCTIONS = 
		new String[] 
				   {NO_FUNCTION,
					AVERAGE_FUNCTION,
					COUNT_FUNCTION,
					DISTINCT_FUNCTION,
					MAXIMUM_FUNCTION,
					MINIMUM_FUNCTION,
					SUM_FUNCTION,
					STANDARD_DEVIATION_FUNCTION,
					VARIANCE_FUNCTION};
				
	// ******************* Static Methods **************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWReportAttributeItem.class);
		
		descriptor.getInheritancePolicy().setParentClass(MWAttributeItem.class);

		descriptor.addDirectMapping("name", "item-name/text()");

		AbstractDirectMapping functionMapping = (AbstractDirectMapping) descriptor.addDirectMapping("function", "function/text()");
		ObjectTypeConverter converter = new ObjectTypeConverter(functionMapping);
		converter.addConversionValue(AVERAGE_FUNCTION, AVERAGE_FUNCTION);
		converter.addConversionValue(COUNT_FUNCTION, COUNT_FUNCTION);
		converter.addConversionValue(DISTINCT_FUNCTION, DISTINCT_FUNCTION);
		converter.addConversionValue(MAXIMUM_FUNCTION, MAXIMUM_FUNCTION);
		converter.addConversionValue(MINIMUM_FUNCTION, MINIMUM_FUNCTION);
		converter.addConversionValue(STANDARD_DEVIATION_FUNCTION, STANDARD_DEVIATION_FUNCTION);
		converter.addConversionValue(VARIANCE_FUNCTION, VARIANCE_FUNCTION);
		converter.addConversionValue(SUM_FUNCTION, SUM_FUNCTION);
		functionMapping.setConverter(converter);
		functionMapping.setNullValue(NO_FUNCTION);

				
		return descriptor;
	}				

	
	// ****************** Constructors ************

	/** Default constructor - for TopLink use only*/
	private MWReportAttributeItem() {
		super();
	}

	MWReportAttributeItem(MWQuery parent, String itemName, MWQueryable queryable) {
		super(parent, queryable);
		this.name = itemName;
		
	}
	
	MWReportAttributeItem(MWQuery parent, String itemName, Iterator queryables) {
		super(parent, queryables);
		this.name = itemName;		
	}
	
	MWReportAttributeItem(MWQuery parent, String itemName, Iterator queryables, Iterator allowsNull) {
		super(parent, queryables, allowsNull);	
		this.name = itemName;
	}

	
	/** Initialize persistent state*/
	protected void initialize(Node parentNode) {
		super.initialize(parentNode);	
		this.function = NO_FUNCTION;
	}
	
	
	// ****************** Accessors ************
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		firePropertyChanged(NAME_PROPERTY, old, this.name);
	}
	
	public String getFunction() {
		return this.function;
	}
	
	public void setFunction(String function) {
		String old = this.function;
		this.function = function;
		firePropertyChanged(FUNCTION_PROPERTY, old, this.function);
	}

	
	// ****************** MWQueryItem implementation ************

	public void removeSelfFromParent() {
		((MWReportQuery) getParentQuery()).removeAttributeItem(this);
	}

	
	// ****************** Problem Handling************
	
	public void addQueryableNullProblemTo(List currentProblems) {
	    if (getFunction() != COUNT_FUNCTION) {
	        currentProblems.add(queryableNullProblem());
	    }
    }
	
	public Problem queryableNullProblem() {
		return buildProblem(
				ProblemConstants.QUERYABLE_NULL_FOR_REPORT_ITEM, 
				getParentQuery().signature(),
				getName());
	}

    public Problem queryableInvalidProblem(MWQueryable queryable) {
        return buildProblem(ProblemConstants.QUERYABLE_NOT_VALID_FOR_REPORT_QUERY_ATTRIBUTE, getName(), getParentQuery().signature());
    }

    public boolean isQueryableValid(MWQueryable queryable) {
        return queryable.isValidForReportQueryAttribute();
    }
	
	// ****************** Runtime Conversion ************
	
	protected void adjustRuntimeQuery(ObjectLevelReadQuery readQuery) {
		ReportQuery reportQuery = (ReportQuery) readQuery;
		Expression expression = getQueryableArgument().runtimeExpression(reportQuery.getExpressionBuilder());
		if (getFunction() == NO_FUNCTION) {
			reportQuery.addItem(getName(), expression);
		}
		else if  (getFunction() == AVERAGE_FUNCTION) {
			reportQuery.addAverage(getName(), expression);
		}
		else if (getFunction() == COUNT_FUNCTION) {
			if (getName() != null) {
				reportQuery.addCount(getName(), expression);
			}
			else {
				reportQuery.addCount();
			}
		}
		else if (getFunction() == DISTINCT_FUNCTION) {
			reportQuery.addItem(getName(), expression.distinct());
		}
		else if (getFunction() == MAXIMUM_FUNCTION) {
			reportQuery.addMaximum(getName(), expression);
		}
		else if (getFunction() == MINIMUM_FUNCTION) {
			reportQuery.addMinimum(getName(), expression);
		}		
		else if (getFunction() == STANDARD_DEVIATION_FUNCTION) {
			reportQuery.addStandardDeviation(getName(), expression);
		}		
		else if (getFunction() == SUM_FUNCTION) {
			reportQuery.addSum(getName(), expression);
		}		
		else if (getFunction() == VARIANCE_FUNCTION) {
			reportQuery.addVariance(getName(), expression);
		}	
		else {
			reportQuery.addItem(getName(), expression.getFunction(getFunction()));
		}
	}

	
	
	public String displayString() {
	    if (getQueryableArgument().getQueryableArgumentElement().getQueryable() == null && getFunction() == COUNT_FUNCTION) {
	        return getFunction();
	    }
		return getName() + "->" + super.displayString() + " (" + getFunction() + ")";
	}
}
