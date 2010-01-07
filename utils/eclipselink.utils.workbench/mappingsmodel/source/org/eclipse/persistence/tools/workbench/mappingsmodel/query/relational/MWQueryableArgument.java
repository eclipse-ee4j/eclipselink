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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * An MWQueryableArgument is always used as the left hand side of an MWBasicExpression.
 * It can also be used as the right hand side of an MWBinaryExpression.
 * 
 * When the user chooses a new queryable for this argument, sets something to allowsNull, or makes any other change
 * to the queryable, a new QueryableArgumentElement is created
 */
public final class MWQueryableArgument extends MWArgument 
{
	private volatile MWQueryableArgumentElement queryableArgumentElement;
		//property change
		public final static String QUERYABLE_ARGUMENT_ELEMENT_PROPERTY = "queryableArgumentElement";
	
	/**
	 * Default constructor - for TopLink use only.
	 */		
	private MWQueryableArgument() {
		super();
	}
	
	MWQueryableArgument(MWQueryableArgumentParent parent) {
		super(parent);
	}

	MWQueryableArgument(MWQueryableArgumentParent parent, MWQueryable queryable) {
		this(parent);
		setQueryableArgument(queryable);
	}
	
	MWQueryableArgument(MWQueryableArgumentParent parent, Iterator queryables) {
		this(parent);
		setQueryableArgument(queryables);
	}

	MWQueryableArgument(MWQueryableArgumentParent parent, Iterator queryables, Iterator allowsNull) {
		this(parent);
		setQueryableArgument(queryables, allowsNull);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.queryableArgumentElement);
	}
	
	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.queryableArgumentElement = new MWQueryableArgumentElement(this);
	}
	
	public String getType() {
		return QUERY_KEY_TYPE;
	}
	
	public String displayString()
	{
		return getQueryableArgumentElement().displayString();
	}

	//*********** problem support ****************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkQueryable(currentProblems);
	}
	
	private void checkQueryable(List currentProblems) {
        MWQueryable queryable = this.getQueryableArgumentElement().getQueryable();
		if (queryable == null) {
		    this.getQueryableArgumentParent().addQueryableNullProblemTo(currentProblems);
 		} else {
            if ( ! this.getQueryableArgumentParent().isQueryableValid(queryable)) {
                currentProblems.add(this.getQueryableArgumentParent().queryableInvalidProblem(queryable));
            }
        }
    }	
	
	// ********** model synchronization support **********

	public void nodeRemoved(Node node) {
		super.nodeRemoved(node);
		
		if (node instanceof MWMapping) {
			mappingReplaced((MWMapping) node, null);
		}
		
		if (node instanceof MWQueryKey) {
			//checking to see if any of the joined queryable elements have a null
			//queryable object at this point.  
			MWQueryableArgumentElement queryableArgumentElement = getQueryableArgumentElement().getJoinedQueryableElement();
			while(queryableArgumentElement != null)
			{
				if (queryableArgumentElement.getQueryable() == null || queryableArgumentElement.getQueryable().isLeaf(Filter.NULL_INSTANCE))
				{
					getQueryableArgumentElement().setQueryableToNull();
					break;
				}
				queryableArgumentElement = queryableArgumentElement.getJoinedQueryableElement();
			}
		}
	}
	
	public void descriptorUnmapped(Collection mappings) {
		super.descriptorUnmapped(mappings);
		for (Iterator stream = mappings.iterator(); stream.hasNext();) {
			mappingReplaced((MWMapping) stream.next(), null);
		}
	}
	
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) 
	{
		super.mappingReplaced(oldMapping, newMapping);
		
		//checking to see if any of the joined queryable elements have a null
		//queryable object at this point.  
		MWQueryableArgumentElement queryableArgumentElement = getQueryableArgumentElement().getJoinedQueryableElement();
		while(queryableArgumentElement != null)
		{
			if (queryableArgumentElement.getQueryable() == null || queryableArgumentElement.getQueryable().isLeaf(Filter.NULL_INSTANCE))
			{
				getQueryableArgumentElement().setQueryableToNull();
				break;
			}
			queryableArgumentElement = queryableArgumentElement.getJoinedQueryableElement();
		}
	}
	
	public void undoChange(String propertyName, Object oldValue, Object newValue)
	{
		if (propertyName == QUERYABLE_ARGUMENT_ELEMENT_PROPERTY)
			setQueryableArgumentElement((MWQueryableArgumentElement) oldValue);
	}	
			
	public MWQueryableArgumentElement getQueryableArgumentElement() 
	{
		return this.queryableArgumentElement;
	}
	
	/**
	 * Used for setting up a queryable argument which does not use joining
	 */
	public void setQueryableArgument(MWQueryable queryable)
	{
		List queryables = new ArrayList();
		queryables.add(queryable);
		this.setQueryableArgument(queryables.iterator());
	}
	
	/**
	 * Used for setting up a queryable argument which uses joining.
	 * 
	 * If the user chose manager(Allows Null).phoneNumbers.areaCode as the queryable argument, then
	 * 
	 * queryables = {areaCode,phoneNumbers, manager}
	 * allowsNull = {false, false, true}
	 * 
	 * The iterator MUST always have 1 element.  If it has more than 1 element, those
	 * are the joined queryable elements.
	 */
	public void setQueryableArgument(Iterator queryables, Iterator allowsNull)
	{
		if (!queryables.hasNext())
		{
			throw new UnsupportedOperationException();
		}
		
		MWQueryable queryable = (MWQueryable) queryables.next();
		queryables.remove();
		MWQueryableArgumentElement queryableArgumentElement = new MWQueryableArgumentElement(this, queryable);
		if (allowsNull.hasNext())
		{	
			queryableArgumentElement.setAllowsNull(((Boolean) allowsNull.next()).booleanValue());	
			allowsNull.remove();
		}
		if (queryables.hasNext())
		{
			queryableArgumentElement.setJoinedQueryable(queryables, allowsNull);
		}
		
		setQueryableArgumentElement(queryableArgumentElement);		
	}
	
	public void setQueryableArgument(Iterator queryables)
	{
		setQueryableArgument(queryables, NullIterator.instance());
	}
	
	private void setQueryableArgumentElement(MWQueryableArgumentElement element)
	{
		MWQueryableArgumentElement oldElement = getQueryableArgumentElement();
		this.queryableArgumentElement = element;
		firePropertyChanged(QUERYABLE_ARGUMENT_ELEMENT_PROPERTY, oldElement, element);
        getQueryableArgumentParent().propertyChanged(this, QUERYABLE_ARGUMENT_ELEMENT_PROPERTY, oldElement, element);
	}

	void recalculateQueryables()
	{	
		MWQueryableArgumentElement element = getQueryableArgumentElement();
			
		while (element.getJoinedQueryableElement() != null)
		{
			if (!element.getJoinedQueryableElement().getQueryable().subQueryableElements(Filter.NULL_INSTANCE).contains(element.getQueryable()))
			{
				getQueryableArgumentElement().setQueryableToNull();
				break;
			}
			element = element.getJoinedQueryableElement();
		}	
		if (!((MWRelationalDescriptor) getQueryableArgumentParent().getParentQuery().getOwningDescriptor()).getQueryables(Filter.NULL_INSTANCE).contains(element.getQueryable()))
			getQueryableArgumentElement().setQueryableToNull();		
	}

	public void toString(StringBuffer sb) 
	{
		super.toString(sb);
		sb.append(getQueryableArgumentElement());
	}
	
	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor(); 
		descriptor.setJavaClass(MWQueryableArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWArgument.class);

		XMLCompositeObjectMapping queryableArgumentElementMapping = new XMLCompositeObjectMapping();
		queryableArgumentElementMapping.setAttributeName("queryableArgumentElement");
		queryableArgumentElementMapping.setReferenceClass(MWQueryableArgumentElement.class);
		queryableArgumentElementMapping.setXPath("queryable-argument-element");
		descriptor.addMapping(queryableArgumentElementMapping);
		
		return descriptor;
	}

	//Conversion to Runtime
	Expression runtimeExpression(ExpressionBuilder builder) 
	{		
		return queryableArgumentElement.convertToRuntime(builder);
	}

	static MWQueryableArgument convertFromRuntime(MWBasicExpression bldrExpression, QueryKeyExpression runtimeExpression)
	{
		MWQueryableArgument newArgument = new MWQueryableArgument(bldrExpression);

		List queryableNames = new ArrayList();
		List allowsNullList = new ArrayList();
		queryableNames.add(0, runtimeExpression.getName());
		allowsNullList.add(0, Boolean.valueOf(runtimeExpression.shouldUseOuterJoin()));
 		
		while (runtimeExpression.getBaseExpression().isQueryKeyExpression())
		{
			runtimeExpression = (QueryKeyExpression) runtimeExpression.getBaseExpression();	
			queryableNames.add(0, runtimeExpression.getName()); 		
			allowsNullList.add(Boolean.valueOf(runtimeExpression.shouldUseOuterJoin()));
		}
 		
		String currentQueryableName = (String) queryableNames.remove(0);
		MWQueryable currentQueryable = ((MWTableDescriptor)newArgument.getQueryableArgumentParent().getParentQuery().getOwningDescriptor()).queryableNamed(currentQueryableName);

		List queryables = new ArrayList();
		queryables.add(0, currentQueryable);
		while (queryableNames.size() > 0)
		{
	
			Iterator subQueryableElements = currentQueryable.subQueryableElements(Filter.NULL_INSTANCE).iterator();
			String queryableName = (String) queryableNames.remove(0);
			while(subQueryableElements.hasNext())
			{
				MWQueryable queryable = (MWQueryable) subQueryableElements.next();
				if (queryable.getName().equals(queryableName))
				{
					currentQueryable = queryable;
					break;
				}
			}
			queryables.add(0, currentQueryable);
		}
		
		newArgument.setQueryableArgument(queryables.iterator(), allowsNullList.iterator());
  
		return newArgument;
	}
}
