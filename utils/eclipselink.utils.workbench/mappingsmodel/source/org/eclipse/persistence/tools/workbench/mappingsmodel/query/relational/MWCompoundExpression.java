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
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

/**
 *  This class contains a list of expressions and an operator used on those expressions
 *  The list of expressions can contain MWCompoundExpressions, MWBasicExpressions, or MWUnaryExpressions
 * 
 * 
 *  An example TopLink expressions which corresponds to a MWCompoundExpression is:
 *    get("firstName").equals("Karen").and(get("lastName").lessThan(getParameter("lastName")));
 *  
 * 	expressions = 
 * 			-BinaryExpression(operator = EQUAL, get("firstName"), "Karen")
 * 			-BinaryExpression(operator = LESS_THAN, get("lastName"), getParameter("lastName"))
 *  operatorType = AND
 * 
 *  This class also takes care of morphing a child expression between unary and binary
 *  if the user changes the child expression's operatorType
 * 
 */
public final class MWCompoundExpression extends MWExpression {
	
	//order is important
	private List expressions;
		// property change
		public final static String EXPRESSIONS_LIST = "expressions";

	//Logical Operators
	public final static String AND = "AND";
	public final static String OR = "OR";
	public final static String NAND = "NAND";
	public final static String NOR = "NOR";
	
	private Stack changes;
		//Change tracking keys
		public final static String ADD_EXPRESSION = "addExpression";
		public final static String REMOVE_EXPRESSION = "removeExpression";
		public final static String CLEARED_EXPRESSIONS = "clearedExpressions";
	
	/**
	 * This class holds on to the information necessary to undo a change
	 * 
	 * propertyChangeName -> The string used for a property change
	 * container -> The parent object on which the change is occuring
	 * oldValue -> the value before the property was changed
	 * newValue -> the value that the property was changed to
	 * 
	 * When restoring a change, you will want to reset it to the oldValue
	 */
	private class PropertyChangeHolder
	{
		private String propertyChangeName;
		private Undoable container;
		private Object oldValue;
		private Object newValue;
		
		private PropertyChangeHolder(Undoable container, String propertyName, Object oldValue, Object newValue)
		{
			this.container = container;
			this.propertyChangeName = propertyName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		private PropertyChangeHolder(Undoable container, String propertyName,  Object newValue)
		{
			this(container, propertyName, null, newValue);
		}
		
		protected void undoChange()
		{
			this.container.undoChange(this.propertyChangeName, this.oldValue, this.newValue);
		}
	}


	/**
	 * Default constructor - for TopLink use only.
	 */	
	private MWCompoundExpression()
	{
		super();
	}
	
	//The parent will either be a BldrExpressionQueryFormat or a BldrCompoundExpression
	MWCompoundExpression(MWModel parent)
	{
		super(parent, AND);
	}
	
	protected void addChildrenTo(List children) 
	{
		super.addChildrenTo(children);
		synchronized (this.expressions) { children.addAll(this.expressions); }
	}

	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.expressions = new Vector();					
	}
	
	protected void initialize()
	{
		super.initialize();
		this.changes = new Stack();
	}

	private void addExpression(MWExpression expression)
	{
		this.expressions.add(expression);
		fireItemAdded(EXPRESSIONS_LIST, this.expressions.lastIndexOf(expression), expression);
		getRootCompoundExpression().propertyChanged(this, ADD_EXPRESSION, expression);
	}

	//When a compound expression is created a basic expression is automatically added to it.
	//If the user has a basic expression chosen upon pressing the add button, then the
	//parent CompoundExpression is asked to addBasicExpression
	public MWBasicExpression addBasicExpression()
	{
		MWBasicExpression expression = new MWBasicExpression(this, MWBasicExpression.EQUAL);	
		addExpression(expression);
		return expression;
	}
	
	public MWCompoundExpression addSubCompoundExpression()
	{	
//		MWRelationalDescriptor owningDescriptor = getParentQuery().getQueryManager().getOwningDescriptor();
		MWCompoundExpression expression = new MWCompoundExpression(this);
		addExpression(expression);
		
		expression.addBasicExpression();
		
		return expression;
	}
	
	public int expressionsSize()
	{
		return this.expressions.size();
	}

	public ListIterator expressions()
	{
		return new CloneListIterator(this.expressions);
	}
	
	public MWExpression getExpression(int index)
	{
		return (MWExpression) this.expressions.get(index);
	}

	//Used to determine the line number of the expression in the ExpressionTree (1, 2, 2.1, 2.1.1, etc)
	public String getIndex()
	{
		if (getParentCompoundExpression() == null)
		{
			return "";
		}
		else 
		{
			return getParentCompoundExpression().getIndex() + Integer.toString(getParentCompoundExpression().getIndexOf(this)) + ".";
		}
	}
	
	//this will return an index of 1-n
	//this is only meant to be used by the ExpressionTree
	int getIndexOf(MWExpression expression)
	{
		Iterator expressions = expressions();
		int index = 1;
		while (expressions.hasNext())
		{
			if( expressions.next() == expression)
				return index;
			index++;
		}
		return -1;
	}
	
	
	public void clearExpressions() {
		for (int i =0; i < this.expressions.size(); i++) {
			removeExpression((MWExpression) this.expressions.get(i));
		}
	}
	
	public void removeExpression(MWExpression expression) {
		int oldIndex = this.expressions.lastIndexOf(expression);
		expression.clearExpressions();
		this.expressions.remove(expression);
		fireItemRemoved(EXPRESSIONS_LIST, oldIndex, expression);
		getRootCompoundExpression().propertyChanged(this, REMOVE_EXPRESSION, expression);
	}

	public String displayString() {
		return getOperatorType();
	}
		
	public MWCompoundExpression getParentCompoundExpression() {
		if (this.getParent().getClass().isAssignableFrom(MWExpressionQueryFormat.class))
			return null;
		else 
			return (MWCompoundExpression) getParent();
	}

	public MWQuery getParentQuery() {
		if (this.getParent().getClass().isAssignableFrom(MWExpressionQueryFormat.class))
			return ((MWQueryFormat) this.getParent()).getQuery();
		else //the parent is another MWCompoundExpression 
			return ((MWCompoundExpression)getParent()).getParentQuery();
	}
	
	public MWCompoundExpression getRootCompoundExpression() {
		if (this.getParent().getClass().isAssignableFrom(MWExpressionQueryFormat.class))
			return this;
		else
			return ((MWCompoundExpression)getParent()).getRootCompoundExpression();		
	}
		
	void recalculateQueryables() {
		Iterator expressions = expressions();
		while (expressions.hasNext())
			((MWExpression) expressions.next()).recalculateQueryables();
	}
			
	public void clearChanges() {
		this.changes.clear();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWExpression#restoreChanges(String, Object, Object)
	 */
	public void undoChange(String propertyName, Object oldValue, Object newValue) {
		super.undoChange(propertyName, oldValue, newValue);
		if (propertyName == ADD_EXPRESSION) {
			removeExpression((MWExpression) newValue);
		}
		else if (propertyName == REMOVE_EXPRESSION) {
			addExpression((MWExpression) newValue);
		}
		else if (propertyName == CLEARED_EXPRESSIONS) {
			//do something here, or remove the Expressions_cleared options
		}
	}
	
	void propertyChanged(Undoable container, String propertyName, Object oldValue, Object newValue) {
		this.changes.push(new PropertyChangeHolder(container, propertyName, oldValue, newValue));
	}
	
	void propertyChanged(Undoable container, String propertyName, Object newValue) {
		this.changes.push(new PropertyChangeHolder(container, propertyName, newValue));
	}
		
	public void restoreChanges() {
		Stack allChanges = new Stack();
		allChanges.addAll(this.changes);
		while (!allChanges.isEmpty()) {
			PropertyChangeHolder changeObject = (PropertyChangeHolder) allChanges.pop();
			changeObject.undoChange();
		}
		clearChanges();
	}
	
	public void toString(StringBuffer sb) {
		super.toString(sb);
		sb.append("operator = " );
		sb.append(getOperatorType());
		sb.append(", expressions = ");


		sb.append('(');
		for (Iterator stream = this.expressions(); stream.hasNext(); ) {
			MWExpression expression = (MWExpression) stream.next();
			sb.append(expression);
			if (stream.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(')');
	}
	
	//Persistence
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWCompoundExpression.class);
			
		//Inheritance Policy
		descriptor.getInheritancePolicy().setParentClass(MWExpression.class);
	
		// aggregate collection - expressions
		XMLCompositeCollectionMapping expressionsMapping = new XMLCompositeCollectionMapping();
		expressionsMapping.setAttributeName("expressions");
		expressionsMapping.setReferenceClass(MWExpression.class);
		expressionsMapping.setXPath("expression-list/expression");
		descriptor.addMapping(expressionsMapping);
			
		return descriptor;	
	}

	//review this one again and make sure to unit test it well
	//Conversion methods
	Expression buildRuntimeExpression(ExpressionBuilder builder) {	
		Expression finalExpression = null;
		int operator;
		boolean useNot = false;
		
		if (getOperatorType() == AND)
			operator = ExpressionOperator.And;
		else if (getOperatorType() == OR)
			operator = ExpressionOperator.Or;
		else if (getOperatorType() == NAND) {
			operator = ExpressionOperator.And;
			useNot = true;
		}
		else { //operator type == NOR
			operator = ExpressionOperator.Or;
			useNot = true;
		}
			
		if (expressionsSize() > 0){
			finalExpression = builder.and(((MWExpression) this.expressions.get(0)).buildRuntimeExpression(builder));
								
			for (int i = 0; i < expressionsSize() - 1; i++) {
				Expression nextExpression = ((MWExpression) this.expressions.get(i+1)).buildRuntimeExpression(builder);

				if (operator == ExpressionOperator.And)
					finalExpression = builder.and(finalExpression).and(nextExpression);
				else
					finalExpression = builder.or(finalExpression).or(nextExpression);					
			}
		}
		
		if (useNot) {
			return finalExpression.not();
		}
			
		return finalExpression;			
	}

	static MWCompoundExpression convertFromRuntime(MWModel parent, Expression selectionCriteria) {
		MWCompoundExpression bldrExpression = new MWCompoundExpression(parent);

		if (selectionCriteria.isRelationExpression()) {
			//this compound expression only contains one basic expression
			//set the operator to be AND by default
			bldrExpression.addExpression(MWBasicExpression.convertFromRuntime(bldrExpression, selectionCriteria));
			bldrExpression.setOperatorType(MWCompoundExpression.AND);
		}

		else { //selectionCriteria is a LogicalExpresison or a FunctionExpression
	
			ExpressionOperator runtimeOperator = selectionCriteria.getOperator();
			boolean usesNot = false;
			
			// if the user has chosen NAND or NOR .not() is called on the expression 
			// when converting to runtime, so it became a function expression
			if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not))) {
				//if the operator is NOT, we know it is a function expression
				selectionCriteria = ((FunctionExpression)selectionCriteria).getBaseExpression();
				usesNot = true;
				runtimeOperator = selectionCriteria.getOperator();
			}
			
			if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.And))) {
				if (!usesNot)
					bldrExpression.setOperatorType(AND);
				else
					bldrExpression.setOperatorType(NAND);
			}
			else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Or))) {
				if (!usesNot)
					bldrExpression.setOperatorType(OR);
				else//is FunctionExpression
					bldrExpression.setOperatorType(NOR);				
			}		
			//happens if a unary operator is used
			else if (selectionCriteria.isFunctionExpression()) {
				bldrExpression.addExpression(MWBasicExpression.convertFromRuntime(bldrExpression, selectionCriteria));
				return bldrExpression;
			}	
			
			//We will get here if the user has specified one basic expression and the operator NOR or NAND
			//Because toplink does not store whether it is NOR or NAND, we will just default to NAND.
			//The NOR case would not correctly convert.
			else if (selectionCriteria.isRelationExpression()) {
				bldrExpression.addExpression(MWBasicExpression.convertFromRuntime(bldrExpression, selectionCriteria));
				if (!usesNot)
					bldrExpression.setOperatorType(AND);
				else
					bldrExpression.setOperatorType(NAND);
				return bldrExpression;
			}
				
			Expression firstChild = ((LogicalExpression)selectionCriteria).getFirstChild();
			if (firstChild.isRelationExpression() || (firstChild.isFunctionExpression() && !(firstChild.getOperator() == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not)))))
				bldrExpression.addExpression(MWBasicExpression.convertFromRuntime(bldrExpression, firstChild));
			else
				bldrExpression.addExpression(MWCompoundExpression.convertFromRuntime(bldrExpression, firstChild));

			Expression secondChild = ((LogicalExpression)selectionCriteria).getSecondChild();
			if (secondChild.isRelationExpression() || (secondChild.isFunctionExpression() && !(secondChild.getOperator() == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not)))))			
				bldrExpression.addExpression(MWBasicExpression.convertFromRuntime(bldrExpression,secondChild));
			else
				bldrExpression.addExpression(MWCompoundExpression.convertFromRuntime(bldrExpression, secondChild));	
		}			
		return bldrExpression;
	}
	
}
