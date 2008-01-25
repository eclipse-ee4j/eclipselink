/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ObjectTypeMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


/**
 *  This abstract class holds on to the operator type.  Each subclass has a list of
 *  static string for their allowed operator types.
 * 
 *  ExpressionQueryFormat holds on to an MWCompoundExpression
 * 
 *  When converted to a runtime project, the expression is the selectionCriteria
 *  of a named query. It is the 'Where' clause of a query.
 * 
 */
public abstract class MWExpression extends MWModel implements Undoable
{

	private volatile String operatorType;
		// property change
		public final static String OPERATOR_TYPE_PROPERTY = "operatorType";
	
	/**
	 * Default constructor - for TopLink use only.
	 */	
	protected MWExpression() 
	{
		super();
	}
	
	MWExpression(MWModel parent, String operatorType) {
		super(parent);
		this.operatorType = operatorType;
	}
		
	public abstract String getIndex();

	public String getOperatorType() 
	{
		return this.operatorType;
	}

	public abstract MWCompoundExpression getParentCompoundExpression();
	
	public abstract MWCompoundExpression getRootCompoundExpression();
	
	public abstract void clearExpressions();
	
	public void undoChange(String propertyName, Object oldValue, Object newValue)
	{
		if (propertyName == OPERATOR_TYPE_PROPERTY)
			setOperatorType((String) oldValue);
	}

	abstract void recalculateQueryables();
	
	public void setOperatorType(String operatorType) 
	{
		String oldOperatorType = getOperatorType();
		this.operatorType = operatorType;
		firePropertyChanged(OPERATOR_TYPE_PROPERTY, oldOperatorType, operatorType);
		getRootCompoundExpression().propertyChanged(this, OPERATOR_TYPE_PROPERTY, oldOperatorType, operatorType);
	}

	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWExpression.class);
			
		//Inheritance Policy
		org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("expression-class/text()");
		ip.readSubclassesOnQueries();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWBasicExpression.class, "basic");
		ip.addClassIndicator(MWCompoundExpression.class, "compound");
		
		//Mappings
		//use an object type mapping to preserve object identity for the operator type
		ObjectTypeConverter operatorTypeConverter = new ObjectTypeConverter();
		operatorTypeConverter.addConversionValue(MWCompoundExpression.AND, MWCompoundExpression.AND);
		operatorTypeConverter.addConversionValue(MWCompoundExpression.OR, MWCompoundExpression.OR);
		operatorTypeConverter.addConversionValue(MWCompoundExpression.NAND, MWCompoundExpression.NAND);
		operatorTypeConverter.addConversionValue(MWCompoundExpression.NOR, MWCompoundExpression.NOR);
		operatorTypeConverter.addConversionValue(MWBasicExpression.EQUAL, MWBasicExpression.EQUAL);
		operatorTypeConverter.addConversionValue(MWBasicExpression.EQUALS_IGNORE_CASE, MWBasicExpression.EQUALS_IGNORE_CASE);
		operatorTypeConverter.addConversionValue(MWBasicExpression.GREATER_THAN, MWBasicExpression.GREATER_THAN);
		operatorTypeConverter.addConversionValue(MWBasicExpression.GREATER_THAN_EQUAL, MWBasicExpression.GREATER_THAN_EQUAL);
		operatorTypeConverter.addConversionValue(MWBasicExpression.IS_NULL, MWBasicExpression.IS_NULL);
		operatorTypeConverter.addConversionValue(MWBasicExpression.LESS_THAN, MWBasicExpression.LESS_THAN);
		operatorTypeConverter.addConversionValue(MWBasicExpression.LESS_THAN_EQUAL, MWBasicExpression.LESS_THAN_EQUAL);
		operatorTypeConverter.addConversionValue(MWBasicExpression.LIKE, MWBasicExpression.LIKE);
		operatorTypeConverter.addConversionValue(MWBasicExpression.LIKE_IGNORE_CASE, MWBasicExpression.LIKE_IGNORE_CASE);
		operatorTypeConverter.addConversionValue(MWBasicExpression.NOT_EQUAL, MWBasicExpression.NOT_EQUAL);
		operatorTypeConverter.addConversionValue(MWBasicExpression.NOT_LIKE, MWBasicExpression.NOT_LIKE);
		operatorTypeConverter.addConversionValue(MWBasicExpression.NOT_NULL, MWBasicExpression.NOT_NULL);
		XMLDirectMapping operatorTypeMapping = new XMLDirectMapping();
		operatorTypeMapping.setAttributeName("operatorType");
		operatorTypeMapping.setXPath("operator-type/text()");
		operatorTypeMapping.setConverter(operatorTypeConverter);
		descriptor.addMapping(operatorTypeMapping);
		
		return descriptor;
	
	}
	public static ClassDescriptor legacy50BuildDescriptor() 
	{
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWExpression.class);
		descriptor.setTableName("expression");
			
		//Inheritance Policy
		InheritancePolicy ip = descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("expression-class");
		ip.readSubclassesOnQueries();
		ip.addClassIndicator(MWBasicExpression.class, "MWBasicExpression");
		ip.addClassIndicator(MWCompoundExpression.class, "MWCompoundExpression");
		ip.addClassIndicator(MWBasicExpression.class, "MWBinaryExpression");
		ip.addClassIndicator(MWBasicExpression.class, "MWUnaryExpression");
		
		//Mappings
		
		//use an object type mapping to preserve object identity for the operator type
		ObjectTypeMapping operatorType = new ObjectTypeMapping();
		operatorType.setAttributeName("operatorType");
		operatorType.addConversionValue(MWCompoundExpression.AND, MWCompoundExpression.AND);
		operatorType.addConversionValue(MWCompoundExpression.OR, MWCompoundExpression.OR);
		operatorType.addConversionValue(MWCompoundExpression.NAND, MWCompoundExpression.NAND);
		operatorType.addConversionValue(MWCompoundExpression.NOR, MWCompoundExpression.NOR);
		operatorType.addConversionValue(MWBasicExpression.EQUAL, MWBasicExpression.EQUAL);
		operatorType.addConversionValue(MWBasicExpression.EQUALS_IGNORE_CASE, MWBasicExpression.EQUALS_IGNORE_CASE);
		operatorType.addConversionValue(MWBasicExpression.GREATER_THAN, MWBasicExpression.GREATER_THAN);
		operatorType.addConversionValue(MWBasicExpression.GREATER_THAN_EQUAL, MWBasicExpression.GREATER_THAN_EQUAL);
		operatorType.addConversionValue(MWBasicExpression.IS_NULL, MWBasicExpression.IS_NULL);
		operatorType.addConversionValue(MWBasicExpression.LESS_THAN, MWBasicExpression.LESS_THAN);
		operatorType.addConversionValue(MWBasicExpression.LESS_THAN_EQUAL, MWBasicExpression.LESS_THAN_EQUAL);
		operatorType.addConversionValue(MWBasicExpression.LIKE, MWBasicExpression.LIKE);
		operatorType.addConversionValue(MWBasicExpression.LIKE_IGNORE_CASE, MWBasicExpression.LIKE_IGNORE_CASE);
		operatorType.addConversionValue(MWBasicExpression.NOT_EQUAL, MWBasicExpression.NOT_EQUAL);
		operatorType.addConversionValue(MWBasicExpression.NOT_LIKE, MWBasicExpression.NOT_LIKE);
		operatorType.addConversionValue(MWBasicExpression.NOT_NULL, MWBasicExpression.NOT_NULL);
		operatorType.setFieldName("operator-type");
		descriptor.addMapping(operatorType);
		
		return descriptor;
	
	}
	
	// *************** Runtime Conversion ******************
	abstract Expression buildRuntimeExpression(ExpressionBuilder builder);
	

}
