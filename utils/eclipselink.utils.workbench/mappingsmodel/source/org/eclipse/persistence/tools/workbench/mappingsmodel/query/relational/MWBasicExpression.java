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
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * Abstract class extended by MWBinaryExpression and MWUnaryExpression
 * 
 * This class holds on to the first argument.
 */
public final class MWBasicExpression 
    extends MWExpression 
    implements MWQueryableArgumentParent {
	
	private volatile MWQueryableArgument firstArgument;  
		public final static String FIRST_ARUGMENT_PROPERTY = "firstArgument";			

	private volatile MWArgument secondArgument; //querykey, parameter, or literal
		public final static String SECOND_ARGUMENT_PROPERTY = "secondArgument";
		
	//Operators
    public static final String EQUAL = "EQUAL";
    public static final String EQUALS_IGNORE_CASE = "EQUALS IGNORE CASE";
    public static final String GREATER_THAN = "GREATER THAN";
    public static final String GREATER_THAN_EQUAL = "GREATER THAN EQUAL";
    public static final String LESS_THAN = "LESS THAN";
    public static final String LESS_THAN_EQUAL = "LESS THAN EQUAL";
    public static final String LIKE = "LIKE";
    public static final String LIKE_IGNORE_CASE = "LIKE IGNORE CASE";
    public static final String NOT_EQUAL = "NOT EQUAL";
    public static final String NOT_LIKE = "NOT LIKE";
	public static final String IS_NULL = "IS NULL";
    public static final String NOT_NULL = "NOT NULL";

    
    /**
	 * Default constructor - for TopLink use only.
	 */	    	
	protected MWBasicExpression() {
		super();
	}
	
	//parent will always be a BldrCompoundExpression
	MWBasicExpression(MWCompoundExpression parent, String operator) {
		super(parent, operator);
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.firstArgument);
		children.add(this.secondArgument);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.firstArgument = createDefaultQueryableArgument();
		this.secondArgument = createDefaultLiteralArgument();
	}

	private MWTableDescriptor getOwningDescriptor() {
		return (MWTableDescriptor) this.getParentQuery().getOwningDescriptor();
	}


	MWQueryableArgument createDefaultQueryableArgument() {
		MWQueryable queryable = getOwningDescriptor().firstQueryable();	
		
		return new MWQueryableArgument(this, queryable);		
	}
	
	void recalculateQueryables()
	{
		getFirstArgument().recalculateQueryables();	
		getSecondArgument().recalculateQueryables();	
	}
	
	public String displayString()
	{
		//should not use operatorType here for the display string, this is not resource bundled
		//maybe we should make an operator class just for the display string
		String displayString = getFirstArgument().displayString() +" "+ getOperatorType();
		displayString += " " + getSecondArgument().displayString();
		return displayString;
	}
	
	public MWQueryableArgument getFirstArgument() 
	{
		return this.firstArgument;
	}

	public String getIndex()
	{
		return getParentCompoundExpression().getIndex() + Integer.toString(getParentCompoundExpression().getIndexOf(this)) +".";
	}
	
	public MWQuery getParentQuery()
	{
			return getParentCompoundExpression().getParentQuery();
	}
	
	public MWCompoundExpression getParentCompoundExpression()
	{
		return (MWCompoundExpression) getParent();
	}
	
	public MWCompoundExpression getRootCompoundExpression()
	{
		return getParentCompoundExpression().getRootCompoundExpression();
	}

	public MWArgument getSecondArgument() {
		return this.secondArgument;
	} 
	
	private void setSecondArgument(MWArgument secondArgument) {
		if (operatorIsUnary(getOperatorType()) && !(secondArgument instanceof MWNullArgument)) {
			return;
		}
		MWArgument oldSecondArgument = getSecondArgument();
		this.secondArgument = secondArgument;
		firePropertyChanged(SECOND_ARGUMENT_PROPERTY, oldSecondArgument, getSecondArgument());
		getRootCompoundExpression().propertyChanged(this, SECOND_ARGUMENT_PROPERTY, oldSecondArgument, secondArgument);
	}
	
	public void setSecondArgumentToLiteral() {
		if (getSecondArgument().getType() != MWArgument.LITERAL_TYPE) {
			setSecondArgument(createDefaultLiteralArgument());
		}
	}
	
	public void setSecondArgumentToParameter() {
		if (getSecondArgument().getType() != MWArgument.PARAMETER_TYPE) {
			setSecondArgument(createDefaultQueryParameterArgument());
		}
	}

	public void setSecondArgumentToQueryable() {
		if (getSecondArgument().getType() != MWArgument.QUERY_KEY_TYPE) {
			setSecondArgument(createDefaultQueryableArgument());
		}
	}

	private MWLiteralArgument createDefaultLiteralArgument()
	{
		return new MWLiteralArgument(this);	
	}
	
	private MWQueryParameterArgument createDefaultQueryParameterArgument()
	{
		MWQueryParameter queryParameter = null;
		if (this.getParentQuery().parametersSize() > 0)
			queryParameter = this.getParentQuery().getParameter(0);
			
		return new MWQueryParameterArgument(this, queryParameter);	
	}


	public void clearExpressions() {
		//do nothing because a BasicExpression has no sub expressions
	}
	
	public void undoChange(String propertyName, Object oldValue, Object newValue)
	{
		super.undoChange(propertyName, oldValue, newValue);
		if (propertyName == FIRST_ARUGMENT_PROPERTY) {
			setFirstArgument((MWQueryableArgument) oldValue);
		}
		if (propertyName == SECOND_ARGUMENT_PROPERTY) {
			setSecondArgument((MWArgument) oldValue);
		}
	}
    
    public void propertyChanged(Undoable container, String propertyName, Object oldValue, Object newValue) {
        getRootCompoundExpression().propertyChanged(container, propertyName, oldValue, newValue);
    }

	protected void setFirstArgument(MWQueryableArgument firstArgument) 
	{
		MWArgument oldFirstArgument = getFirstArgument();
		this.firstArgument = firstArgument;
		firePropertyChanged(FIRST_ARUGMENT_PROPERTY, oldFirstArgument, getFirstArgument());
		getRootCompoundExpression().propertyChanged(this, FIRST_ARUGMENT_PROPERTY, oldFirstArgument, firstArgument);
	}

	private boolean operatorIsUnary(String operator)
	{
		return (operator == IS_NULL || operator == NOT_NULL);
	}
	
	//used to check if morphing between MWBinaryExpresison and MWUnaryExpression is needed
	private boolean operatorTypeHasChangedBetweenBinaryAndUnary(String oldOperatorType, String operatorType)
	{
		if (!operatorIsUnary(oldOperatorType))
		{
			if (operatorIsUnary(operatorType))
				return true;
			else
				return false;
		}
		else
			if (!operatorIsUnary(operatorType))
				return true;
				
		return false;
	}
	
	public void setOperatorType(String operatorType) {
		String oldOperatorType = this.getOperatorType();

		super.setOperatorType(operatorType);
		if (operatorTypeHasChangedBetweenBinaryAndUnary(oldOperatorType, operatorType)) {
			if (operatorIsUnary(operatorType)) {
				setSecondArgument(new MWNullArgument(this));
			}
			else {
				setSecondArgumentToLiteral();
			}
		}
		
		if (operatorIsStringType()) {
			getSecondArgument().operatorTypeChanged();
		}
	}
	
	
	//If one of these operators is chosen and the user choose Literal
	//for the type of the second arument, then the literal must be of type String
	//The ui will disable all other options.
	public boolean operatorIsStringType() {
		return (getOperatorType() == EQUALS_IGNORE_CASE
			|| getOperatorType() == LIKE
			|| getOperatorType() == LIKE_IGNORE_CASE
			|| getOperatorType() == NOT_LIKE);
	}

	
	// **************** problem support *****************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkReferenceMappingQueryableChosenWithoutUnaryOperatorChosen(currentProblems);
	}
   
    public void addQueryableNullProblemTo(List currentProblems) {
       currentProblems.add(queryableNullProblem());       
    }
    
    private Problem queryableNullProblem() {
        String queryName = getParentQuery().getName();
        String lineNumber = getIndex();
        return buildProblem(ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NO_QUERY_KEY_SPECIFIED, 
                                 lineNumber, queryName);
    }
    
    public Problem queryableInvalidProblem(MWQueryable queryable) {
        return buildProblem(ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_QUERY_KEY_NOT_VALID, queryable.displayString(), getParentQuery().signature());
    }
    
    public boolean isQueryableValid(MWQueryable queryable) {
        return queryable.isValidForQueryExpression();
    }

    
	private void checkReferenceMappingQueryableChosenWithoutUnaryOperatorChosen(List currentProblems) {
		if (checkIfNonLeafQueryableChosenWithoutOperatorIsNullOrNotNull() != null) {
			String queryName = getParentQuery().getName();
			String lineNumber = checkIfNonLeafQueryableChosenWithoutOperatorIsNullOrNotNull().getIndex();
			currentProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NON_UNARY_OPERATOR, 
										 lineNumber, queryName));				
		}			
	}	
	
	private MWBasicExpression checkIfNonLeafQueryableChosenWithoutOperatorIsNullOrNotNull() {
		MWQueryableArgument argument = getFirstArgument();
		if (argument.getQueryableArgumentElement().getQueryable() != null && argument.getQueryableArgumentElement().getQueryable().allowsChildren()) {
			if (this.getOperatorType() != IS_NULL && this.getOperatorType() != NOT_NULL) {
				return this;
			}
		}
			
		return null;
	}
	
	
	public void toString(StringBuffer sb) {
		super.toString(sb);
		sb.append("firstArgument = " );
		sb.append(getFirstArgument());
		sb.append(", operator = " );
		sb.append(getOperatorType());
	}

	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWBasicExpression.class);
			
		//Inheritance Policy
		descriptor.getInheritancePolicy().setParentClass(MWExpression.class);	
		
		//Mappings
		
		// 1-1 to the first BldrArgument
		XMLCompositeObjectMapping firstArgumentMapping = new XMLCompositeObjectMapping();
		firstArgumentMapping.setAttributeName("firstArgument");
		firstArgumentMapping.setReferenceClass(MWQueryableArgument.class);
		firstArgumentMapping.setXPath("first-argument");
		descriptor.addMapping(firstArgumentMapping);
		
		XMLCompositeObjectMapping secondArgumentMapping = new XMLCompositeObjectMapping();
		secondArgumentMapping.setAttributeName("secondArgument");
		secondArgumentMapping.setReferenceClass(MWArgument.class);
		secondArgumentMapping.setXPath("second-argument");
		descriptor.addMapping(secondArgumentMapping);

		return descriptor;
	
	}
	
	// ***************** Runtime Conversion ***********
	
	Expression buildRuntimeExpression(ExpressionBuilder builder) {
		Expression firstExpression = getFirstArgument().runtimeExpression(builder);
		Expression secondExpression = getSecondArgument().runtimeExpression(builder);		
		
		//TODO make operatorType an Object instead of a String?
		if (getOperatorType() == EQUAL) {
			return firstExpression.equal(secondExpression);
		}
		else if (getOperatorType() == GREATER_THAN) {
			return firstExpression.greaterThan(secondExpression);
		}
		else if (getOperatorType() == GREATER_THAN_EQUAL) {
			return firstExpression.greaterThanEqual(secondExpression);
		}
		else if (getOperatorType() == LESS_THAN) {
			return firstExpression.lessThan(secondExpression);
		}
		else if (getOperatorType() == LESS_THAN_EQUAL) {
			return firstExpression.lessThanEqual(secondExpression);
		}
		else if (getOperatorType() == NOT_EQUAL) {
			return firstExpression.notEqual(secondExpression);
		}
		else if (getOperatorType() == EQUALS_IGNORE_CASE) {
			return firstExpression.equalsIgnoreCase(secondExpression);
		}
		else if (getOperatorType() == LIKE) {
			return firstExpression.like(secondExpression);
		}
		else if (getOperatorType() == LIKE_IGNORE_CASE) {
			return firstExpression.likeIgnoreCase(secondExpression);
		}
		else if (getOperatorType() == NOT_LIKE) {
			return firstExpression.notLike(secondExpression);
		}
		else if (getOperatorType() == IS_NULL) {
			return firstExpression.isNull();
		}
		else  if (getOperatorType() == NOT_NULL) {
			return firstExpression.notNull();
		}
		else {
			throw new IllegalStateException("Operator type: " + getOperatorType() + " is not supported");
		}
	}
	
	static MWBasicExpression convertFromRuntime(MWCompoundExpression parent, Expression expression) {
		ExpressionOperator runtimeOperator = expression.getOperator();
		String bldrOperator = EQUAL;
		if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Equal))) {
			bldrOperator = EQUAL;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotEqual))) {
			bldrOperator = NOT_EQUAL;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.LessThan))) {
			bldrOperator = LESS_THAN;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.LessThanEqual))) {
			bldrOperator = LESS_THAN_EQUAL;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.GreaterThan))) {
			bldrOperator = GREATER_THAN;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.GreaterThanEqual))) {
			bldrOperator = GREATER_THAN_EQUAL;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Like))) {
			bldrOperator = LIKE;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotLike))) {
			bldrOperator = NOT_LIKE;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.IsNull))) {
			bldrOperator = IS_NULL;
		}
		else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotNull))) {
			bldrOperator = NOT_NULL;
		}
			
		MWBasicExpression bldrExpression = new MWBasicExpression(parent, bldrOperator);
		if (bldrOperator == MWBasicExpression.NOT_NULL || bldrOperator == MWBasicExpression.IS_NULL) {
			Expression firstChildExpression = ((FunctionExpression)expression).getBaseExpression();					
			bldrExpression.setFirstArgument(MWQueryableArgument.convertFromRuntime(bldrExpression, (QueryKeyExpression) firstChildExpression));
		}
		else {
			Expression firstChildExpression = ((RelationExpression) expression).getFirstChild();		
			
			//this will only happen if the user choose LikeIgnoreCase or EqualsIgnoreCase
			if (firstChildExpression.isFunctionExpression()) {
				firstChildExpression = ((FunctionExpression) firstChildExpression).getBaseExpression();
				if (bldrExpression.getOperatorType() == MWBasicExpression.EQUAL) {
					bldrExpression.setOperatorType(MWBasicExpression.EQUALS_IGNORE_CASE);
				}
				else {
					bldrExpression.setOperatorType(MWBasicExpression.LIKE_IGNORE_CASE);
				}
			}
			
			bldrExpression.setFirstArgument(MWQueryableArgument.convertFromRuntime(bldrExpression, (QueryKeyExpression) firstChildExpression));
		
			
			Expression secondChildExpression =((RelationExpression) expression).getSecondChild();
			
			//this will only happen if the user choose LikeIgnoreCase or EqualsIgnoreCase
			if (secondChildExpression.isFunctionExpression()) {
				secondChildExpression = ((FunctionExpression) secondChildExpression).getBaseExpression();
			}
				 
			//convert the second child which can be a queryableArgument, parameterArgument, or literaArgument
			if (secondChildExpression.isQueryKeyExpression()) {
				bldrExpression.setSecondArgument(MWQueryableArgument.convertFromRuntime(bldrExpression, (QueryKeyExpression) secondChildExpression));
			}
			else if (secondChildExpression.isConstantExpression()) {
				bldrExpression.setSecondArgument(MWLiteralArgument.convertFromRuntime(bldrExpression, (ConstantExpression) secondChildExpression));
			}
			else if (secondChildExpression.isParameterExpression()) {
				bldrExpression.setSecondArgument(MWQueryParameterArgument.convertFromRuntime(bldrExpression, (ParameterExpression) secondChildExpression));
			}
		}
		return bldrExpression;
	}

}
