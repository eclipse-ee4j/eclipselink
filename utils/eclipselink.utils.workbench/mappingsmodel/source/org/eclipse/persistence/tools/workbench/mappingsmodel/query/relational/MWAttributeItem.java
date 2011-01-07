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
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

public abstract class MWAttributeItem 
	extends MWModel
	implements MWQueryItem, MWQueryableArgumentParent
{
  
	private MWQueryableArgument queryableArgument;

	
	// ******************* Static Methods **************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAttributeItem.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWReportAttributeItem.class, "attribute");
		ip.addClassIndicator(MWGroupingItem.class, "grouping");
		ip.addClassIndicator(MWOrderingItem.class, "ordering");
		ip.addClassIndicator(MWJoinedItem.class, "join");
        ip.addClassIndicator(MWBatchReadItem.class, "batch-read");
        ip.addClassIndicator(MWReportOrderingItem.class, "report-ordering");
		
		XMLCompositeObjectMapping firstArgumentMapping = new XMLCompositeObjectMapping();
		firstArgumentMapping.setAttributeName("queryableArgument");
		firstArgumentMapping.setReferenceClass(MWQueryableArgument.class);
		firstArgumentMapping.setXPath("queryable-argument");
		descriptor.addMapping(firstArgumentMapping);
				
		return descriptor;
	}
	
	
	// ****************** Constructors ************

	/** Default constructor - for TopLink use only*/	
	MWAttributeItem() {
		super();
	}

	MWAttributeItem(MWQuery parent, MWQueryable queryable) {
		super(parent);
		this.queryableArgument = new MWQueryableArgument(this, queryable);	
	}
	
	MWAttributeItem(MWQuery parent, Iterator queryables) {
		super(parent);
		this.queryableArgument = new MWQueryableArgument(this, queryables);		
	}
	
	MWAttributeItem(MWQuery parent, Iterator queryables, Iterator allowsNull) {
		super(parent);
		this.queryableArgument = new MWQueryableArgument(this, queryables, allowsNull);		
	}
	
	MWAttributeItem(MWQuery parent, MWQueryableArgument queryableArgument) {
		super(parent);
		this.queryableArgument = queryableArgument;		
	}
	
	protected void addChildrenTo(List list) {
		super.addChildrenTo(list);
		list.add(this.queryableArgument);
	}
	
	public MWQueryableArgument getQueryableArgument() {	
		return this.queryableArgument;
	}
	
	public MWQuery getParentQuery() {
		return (MWQuery) getParent();
	}
	
    public void propertyChanged(Undoable container, String propertyName, Object oldValue, Object newValue) {
            //do nothing
    }

    
    // ****************** Problem Handling************
    
    public void addQueryableNullProblemTo(List currentProblems) {
        currentProblems.add(queryableNullProblem());
    }
    
    protected abstract Problem queryableNullProblem();
    
    
	// ****************** Runtime Conversion ************
	
	protected abstract void adjustRuntimeQuery(ObjectLevelReadQuery readQuery);

	
	public String displayString() {
		return getQueryableArgument().displayString();
	}

}
