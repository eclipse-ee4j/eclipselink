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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * An MWQueryableArgument holds on to 1 MWQueryableArgumentElement.
 * An example MWQueryableArgumentElement would be if the user chooses address(allowsNull).city as their query key
 * 
 * queryableHandle = city mapping
 * allowsNull would not be chosen by the user, therefore false
 * joinedQueryableElement = MWQueryableArgumentElement:
 * 								queryableHandle = address mapping
 * 								allowsNull = true
 * 								joinedQueryableElement = null
 * 
 */

public final class MWQueryableArgumentElement extends MWModel {
	
	private MWQueryableHandle queryableHandle;   //This is either a mapping or a query key
	
	//if the queryableObject is the state mapping found in the address descriptor,
	//then the joinedQueryableObject would be the address mapping.
	//The address mapping's joinedQueryableObject could be manager. (manager.address.state) 
	//Manager would have a null joinedQueryableObject
	private volatile MWQueryableArgumentElement joinedQueryableElement;	
	
	private volatile boolean allowsNull; 
		//property change
		public final static String ALLOWS_NULL_PROPERTY = "allowsNull";


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */		
	private MWQueryableArgumentElement() {
		super();
	}

	MWQueryableArgumentElement(MWQueryableArgument argument, MWQueryable queryable) {
		this(argument);
		this.queryableHandle.setQueryable(queryable);
	}	
	
	MWQueryableArgumentElement(MWQueryableArgumentElement argument, MWQueryable queryable) {
		this(argument);
		this.queryableHandle.setQueryable(queryable);
	}	
	
	MWQueryableArgumentElement(MWQueryableArgument argument) {
		super(argument);
	}
	
	MWQueryableArgumentElement(MWQueryableArgumentElement argument) {
		super(argument);
	}
	
	MWQueryableArgumentElement(MWOrderingItem orderingItem, MWQueryable queryable) {
		super(orderingItem);
		this.queryableHandle.setQueryable(queryable);
	}
	

	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.queryableHandle = new MWQueryableHandle(this, this.buildQueryableScrubber());
	}
	

	// ********** accessors **********

	public MWQueryable getQueryable() {
		return this.queryableHandle.getQueryable();
	}

	void setQueryableToNull() {
		this.queryableHandle.setQueryable(null);
		this.joinedQueryableElement = null;
	}
	
	public boolean isAllowsNull() {
		return this.allowsNull;
	}

	public void setAllowsNull(boolean allowsNull) {
		boolean oldAllowsNull = isAllowsNull();
		this.allowsNull = allowsNull;
		firePropertyChanged(ALLOWS_NULL_PROPERTY, oldAllowsNull, allowsNull);
	}
	
	public MWQueryableArgumentElement getJoinedQueryableElement() {
		return this.joinedQueryableElement;
	}

	void setJoinedQueryable(Iterator joinedQueryables, Iterator allowsNull) {	
		MWQueryable queryable = (MWQueryable) joinedQueryables.next();
		joinedQueryables.remove();
		this.joinedQueryableElement = new MWQueryableArgumentElement(this, queryable);	
		if (allowsNull.hasNext()) {
			this.joinedQueryableElement.setAllowsNull(((Boolean) allowsNull.next()).booleanValue());
			allowsNull.remove();
		}
		if (joinedQueryables.hasNext()) {
			this.joinedQueryableElement.setJoinedQueryable(joinedQueryables,allowsNull);			
		}
	}


	// ********** printing **********

	public String displayString() {
		StringBuffer displayString = new StringBuffer();

		if (getJoinedQueryableElement() != null) {
			displayString.append(this.getJoinedQueryableElement().displayString());
			displayString.append('.');
		}
		
		if (getQueryable() != null) {	
			displayString.append(this.getQueryable().getName());
			if (this.allowsNull) {
				//TODO i18n
				if (getQueryable().usesAnyOf()) {
					displayString.append("(Allows None)");
				} else {
					displayString.append("(Allows Null)");
				}
			}
		} else {
			displayString.append("<null>");
		}
		return displayString.toString();
	}	
	
	public void toString(StringBuffer sb) {
		if (getJoinedQueryableElement() != null) {
			sb.append(getJoinedQueryableElement().displayString() + ".");
		}
		if (getQueryable() != null) {
			sb.append(getQueryable().getName());
			if (this.allowsNull) {
				if (getQueryable().usesAnyOf()) {
					sb.append("(Allows None)");
				}
				else {
					sb.append("(Allows Null)");
				}
			}
		}
	}


	// ********** model synchronization support **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.queryableHandle);
		if (this.joinedQueryableElement != null) {
			children.add(this.joinedQueryableElement);
		}
	}

	private NodeReferenceScrubber buildQueryableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWQueryableArgumentElement.this.setQueryableToNull();
			}
			public String toString() {
				return "MWQueryableArgumentElement.buildQueryableScrubber()";
			}
		};
	}

	public void descriptorUnmapped(Collection mappings) {
		super.descriptorUnmapped(mappings);
		if (this.getQueryable() != null && mappings.contains(this.getQueryable())) {
			setQueryableToNull();
		}
	}
	
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		super.mappingReplaced(oldMapping, newMapping);
		if (this.getQueryable() == oldMapping) {
			this.queryableHandle.setQueryable(newMapping);
			if (newMapping != null && !((MWQueryable) newMapping).allowsOuterJoin()) {
				setAllowsNull(false);
			}
		}
	}


	// ********** runtime conversion **********

	Expression convertToRuntime(ExpressionBuilder builder) {		
		builder.derivedExpressions = null; //clearing out the derived expressions so we don't wind up with duplicates
		if (getJoinedQueryableElement() != null) {
			Expression joinedExpression = getJoinedQueryableElement().convertToRuntime(builder);
			return buildExpressionFrom(joinedExpression);			
		}
		return buildExpressionFrom(builder);
	}
	
	private Expression buildExpressionFrom(Expression expression) {
		String name = "";
		if (getQueryable() != null) {
			name = getQueryable().getName();
		}
		
		if (getQueryable() != null && !getQueryable().usesAnyOf()) {
			if ( ! this.allowsNull) {
				return expression.get(name);
			}
			return expression.getAllowingNull(name);
		}
		if ( ! this.allowsNull) {
			return expression.anyOf(name);
		}
		return expression.anyOfAllowingNone(name);
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWQueryableArgumentElement.class);

		XMLCompositeObjectMapping queryableHandleMapping = new XMLCompositeObjectMapping();
		queryableHandleMapping.setAttributeName("queryableHandle");
		queryableHandleMapping.setGetMethodName("getQueryableHandleForTopLink");
		queryableHandleMapping.setSetMethodName("setQueryableHandleForTopLink");
		queryableHandleMapping.setReferenceClass(MWQueryableHandle.class);
		queryableHandleMapping.setXPath("queryable-handle");
		descriptor.addMapping(queryableHandleMapping);
	
		((XMLDirectMapping) descriptor.addDirectMapping("allowsNull", "allows-null/text()")).setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping joinedQueryableElementMapping = new XMLCompositeObjectMapping();
		joinedQueryableElementMapping.setAttributeName("joinedQueryableElement");
		joinedQueryableElementMapping.setReferenceClass(MWQueryableArgumentElement.class);
		joinedQueryableElementMapping.setXPath("joined-queryable-element");
		descriptor.addMapping(joinedQueryableElementMapping);
				
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWQueryableHandle getQueryableHandleForTopLink() {
		return (this.queryableHandle.getQueryable() == null) ? null : this.queryableHandle;
	}
	private void setQueryableHandleForTopLink(MWQueryableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildQueryableScrubber();
		this.queryableHandle = ((handle == null) ? new MWQueryableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
