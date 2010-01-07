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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorQueryParameterHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * An MWQueryParameterArgument is only used as the right hand side of a MWBinaryExpression
 * The user chooses a parameter from the MWQuery's list of parameters.  It is then held on to
 * by the DescriptorQueryParameterHandle
 * 
 * If the chosen parameter is deleted from the query, this object is notified and the parameter
 * is set to null.  The queryParameterArgument held on to by the BasicExpression is not set to null.
 */
public final class MWQueryParameterArgument extends MWArgument {

	private MWDescriptorQueryParameterHandle queryParameterHandle;
		// property change
		public final static String QUERY_PARAMETER_PROPERTY = "queryParameter";

	/**
	 * Default constructor - for TopLink use only.
	 */		
	private MWQueryParameterArgument()
	{
		super();
	}

	MWQueryParameterArgument(MWBasicExpression expression)
	{
		super(expression);
	}
	
	MWQueryParameterArgument(MWBasicExpression expression, MWQueryParameter parameter)
	{
		this(expression);
		this.queryParameterHandle.setQueryParameter(parameter);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.queryParameterHandle = new MWDescriptorQueryParameterHandle(this, this.buildQueryParameterScrubber());
	}
		
	public String getType() {
		return PARAMETER_TYPE;
	}

	public MWQueryParameter getQueryParameter() {
		return this.queryParameterHandle.getQueryParameter();
	}
	
    private MWBasicExpression getBasicExpression() {
        return (MWBasicExpression) getParent();
    }
    
    
	//*********** problem support ****************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkParameter(currentProblems);
	}
	
	private void checkParameter(List currentProblems) {
		if (this.getQueryParameter() == null) {
			String queryName = this.getBasicExpression().getParentQuery().getName();
			String lineNumber = this.getBasicExpression().getIndex();
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NO_PARAMETER_SPECIFIED, 
					 lineNumber, queryName));	
		}
	}
		

	// ********** model synchronization support **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.queryParameterHandle);
	}

	private NodeReferenceScrubber buildQueryParameterScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWQueryParameterArgument.this.setQueryParameter(null);
			}
			public String toString() {
				return "MWQueryParameterArgument.buildQueryParameterScrubber()";
			}
		};
	}

	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.query.Restorable#undoChange(String, Object)
	 */
	public void undoChange(String propertyName, Object oldValue, Object newValue)
	{
		if (propertyName == QUERY_PARAMETER_PROPERTY)
			setQueryParameter((MWQueryParameter) oldValue);
	}
		
	public String displayString()
	{
		if (getQueryParameter() == null)
			return "";
		return getQueryParameter().getName();
	}
	
	public void toString(StringBuffer sb) 
	{
		super.toString(sb);
		sb.append("queryParameter = ");
		if (getQueryParameter() != null) {
			sb.append(getQueryParameter().getName());
		}
	}

	public void setQueryParameter(MWQueryParameter queryParameter) 
	{
		MWQueryParameter oldQueryParameter = getQueryParameter();
		this.queryParameterHandle.setQueryParameter(queryParameter);
		firePropertyChanged(QUERY_PARAMETER_PROPERTY, oldQueryParameter, getQueryParameter());
		getBasicExpression().getRootCompoundExpression().propertyChanged(this, QUERY_PARAMETER_PROPERTY, oldQueryParameter, queryParameter);
	}


	// ********** Conversion to Runtime **********

	Expression runtimeExpression(ExpressionBuilder builder) {
		return builder.getParameter(getQueryParameter().getName());
	}

	static MWQueryParameterArgument convertFromRuntime(MWBasicExpression bldrExpression, ParameterExpression runtimeExpression) 	{
		String parameterName = runtimeExpression.getField().getName();
		MWQueryParameter parameter  = bldrExpression.getParentQuery().getParameterNamed(parameterName);
		return new MWQueryParameterArgument(bldrExpression, parameter);
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWQueryParameterArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWArgument.class);
		
		XMLCompositeObjectMapping queryParameterHandleMapping = new XMLCompositeObjectMapping();
		queryParameterHandleMapping.setAttributeName("queryParameterHandle");
		queryParameterHandleMapping.setGetMethodName("getQueryParameterHandleForTopLink");
		queryParameterHandleMapping.setSetMethodName("setQueryParameterHandleForTopLink");
		queryParameterHandleMapping.setReferenceClass(MWDescriptorQueryParameterHandle.class);
		queryParameterHandleMapping.setXPath("query-parameter-handle");
		descriptor.addMapping(queryParameterHandleMapping);
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWDescriptorQueryParameterHandle getQueryParameterHandleForTopLink() {
		return (this.queryParameterHandle.getQueryParameter() == null) ? null : this.queryParameterHandle;
	}
	private void setQueryParameterHandleForTopLink(MWDescriptorQueryParameterHandle handle) {
		NodeReferenceScrubber scrubber = this.buildQueryParameterScrubber();
		this.queryParameterHandle = ((handle == null) ? new MWDescriptorQueryParameterHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
}
