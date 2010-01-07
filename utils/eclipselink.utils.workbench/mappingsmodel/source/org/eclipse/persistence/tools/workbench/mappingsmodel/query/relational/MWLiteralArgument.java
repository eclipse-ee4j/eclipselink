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

import java.text.NumberFormat;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * An MWLiteralArgument is only used as the right hand side of a MWBinaryExpression
 * The user chooses a type and then enters a value in the correct format of the chosen type.
 * 
 * If the type is changed, the value is changed appropriately to fit the new format. 
 */
public final class MWLiteralArgument extends MWArgument {


	private volatile MWTypeDeclaration type;
		public final static String TYPE_PROPERTY = "type";

	private volatile String value;
		public final static String VALUE_PROPERTY = "value";
	
	/**
	 * Default constructor - for TopLink use only.
	 */	
	private MWLiteralArgument()
	{
		super();
	}	
	
	MWLiteralArgument(MWBasicExpression expression, MWTypeDeclaration type, String value)
	{
		super(expression);
		this.type = type;
		this.value = value;	
	}
	
	MWLiteralArgument(MWBasicExpression expression)
	{
		super(expression);
	}
	
	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.type = new MWTypeDeclaration(this, typeNamed("java.lang.String"));
		this.value =  "";
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.type);
	}
    
    private MWBasicExpression getBasicExpression() {
        return (MWBasicExpression) getParent();
    }   

	public String getType() {
		return LITERAL_TYPE;
	}
	
		
	//certain operators only can only take a String for the type (Like, Not Like)
	public void operatorTypeChanged()
	{
		if (getBasicExpression().operatorIsStringType())
			setType(new MWTypeDeclaration(this, typeNamed("java.lang.String")));
	}

	
	public MWTypeDeclaration getLiteralType() 
	{
		return this.type;
	}

	public Object getValue() 
	{
		return this.value;
	}

	public void undoChange(String propertyName, Object oldValue, Object newValue)
	{
		if (propertyName == TYPE_PROPERTY)
			setType((MWTypeDeclaration) oldValue);
		else if (propertyName == VALUE_PROPERTY)
			setValue((String) oldValue);								
	}

	public void setType(MWTypeDeclaration type) 
	{
		MWTypeDeclaration oldType = getLiteralType();
		this.type = type;
		firePropertyChanged(TYPE_PROPERTY, oldType, getLiteralType());
		getBasicExpression().getRootCompoundExpression().propertyChanged(this, TYPE_PROPERTY, oldType, type);
	}
	
	public void setValue(String value) 
	{
		Object oldValue = getValue();
		this.value = value;
		firePropertyChanged(VALUE_PROPERTY, oldValue, getValue());
		getBasicExpression().getRootCompoundExpression().propertyChanged(this, VALUE_PROPERTY, oldValue, value);
	}

	public String displayString()
	{
		return "\"" + getValue() +"\"";
	}
	
	public void toString(StringBuffer sb) 
	{
		super.toString(sb);
		sb.append("type = ");
		sb.append(getLiteralType().typeName());
		sb.append(", value = ");
		sb.append(getValue());
	}
	
	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWLiteralArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWArgument.class);
	
		// 1:1 - type
		XMLCompositeObjectMapping typeMapping = new XMLCompositeObjectMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setReferenceClass(MWTypeDeclaration.class);
		typeMapping.setXPath("type");
		descriptor.addMapping(typeMapping);
        
		// DTF Object value
		XMLDirectMapping valueMapping = (XMLDirectMapping)descriptor.addDirectMapping("value", "value/text()");
		valueMapping.setNullValue("");
		
		return descriptor;
	}
	
	//Conversion to Runtime
	Expression runtimeExpression(ExpressionBuilder builder) {
		return new ConstantExpression(value(), builder);
	}
    
    private Object value() {
        Class nullClass = null;
        
        try {
            nullClass = Class.forName(getLiteralType().typeName());
        }
        catch (ClassNotFoundException cnfe) {
            // can't do anything with it, just don't do anything
            return null;
        }
        
        ConversionManager cm = ConversionManager.getDefaultManager();
        
        try {
            return cm.convertObject(this.value, nullClass);
        }
        catch (ConversionException ce) {
         // can't do anything with it, just don't do anything
            return null;
        }
    }
	static MWLiteralArgument convertFromRuntime(MWBasicExpression bldrExpression, ConstantExpression runtimeExpression) {
		Object value = runtimeExpression.getValue();
		MWClass type = bldrExpression.typeNamed(value.getClass().getName());
		MWLiteralArgument argument = new MWLiteralArgument(bldrExpression);
		argument.setType(new MWTypeDeclaration(argument, type));
		argument.setValue((String) value);
		return argument;
	}
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);

        Class literalTypeClass = null;
        try {
        	literalTypeClass = Class.forName(getLiteralType().typeName());
        }
        catch (ClassNotFoundException cnfe) {
        	//this should never happen, the list of types are all java classes
        }
        
        ConversionManager cm = ConversionManager.getDefaultManager();
        
        try {
            cm.convertObject(this.value, literalTypeClass);
        }
        catch (ConversionException ce) {
        	currentProblems.add(buildProblemForConversionException(literalTypeClass));
            //add a problem, value is not in the correct format for the given type.
            //should have a separate problem message for every type
        }
	}
	
	private Problem buildProblemForConversionException(Class javaClass) {
		String javaClassName = javaClass.getName();
		String lineNumber = getBasicExpression().getIndex();
		String queryName = getBasicExpression().getParentQuery().getName();
		
		if (javaClass == Boolean.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_BOOLEAN_FORMAT, 
											lineNumber,
											queryName,
											javaClassName);
		}
		else if (javaClass == Character.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_CHARACTER_FORMAT, 
												lineNumber,
												queryName,
												javaClassName);
		}
		else if (javaClass == Short.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT, 
												 new Object[] {lineNumber,
															  	   queryName,
															  	   javaClassName, 
																   NumberFormat.getInstance().format(Short.MIN_VALUE), 
																   NumberFormat.getInstance().format(Short.MAX_VALUE)});
		}
		else if (javaClass == Byte.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_BYTE_FORMAT, 
					 							new Object[] {lineNumber,
																queryName,
																javaClassName, 
																NumberFormat.getInstance().format(Byte.MIN_VALUE), 
																NumberFormat.getInstance().format(Byte.MAX_VALUE)});
		}
		else if (javaClass == Float.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT, 
												new Object[] {lineNumber,
															   queryName,
															   javaClassName,
															   NumberFormat.getInstance().format(Float.MIN_VALUE), 
															   NumberFormat.getInstance().format(Float.MAX_VALUE)});
		}
		else if (javaClass == Double.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT, 
												new Object[] {lineNumber,
															   	queryName,
															   	javaClassName,
															   	NumberFormat.getInstance().format(Double.MIN_VALUE), 
															   	NumberFormat.getInstance().format(Double.MAX_VALUE)});
		}
		else if (javaClass == Integer.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT, 
					 							new Object[] {lineNumber, 
																queryName,
																javaClassName,
																NumberFormat.getInstance().format(Integer.MIN_VALUE), 
																NumberFormat.getInstance().format(Integer.MAX_VALUE)});
		}
		else if (javaClass == Long.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT, 
												new Object[] {lineNumber, 
																queryName,
																javaClassName,
																NumberFormat.getInstance().format(Long.MIN_VALUE), 
																NumberFormat.getInstance().format(Long.MAX_VALUE)});
		}
		else if (javaClass == String.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_STRING_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.math.BigDecimal.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_BIGDECIMAL_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.math.BigInteger.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_BIGINTEGER_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.sql.Date.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_SQLDATE_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.sql.Time.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_SQLTIME_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.sql.Timestamp.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_SQLTIMESTAMP_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.util.Date.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_UTILDATE_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == java.util.Calendar.class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_UTILCALENDAR_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == byte[].class || javaClass == Byte[].class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_BYTEARRAY_FORMAT, lineNumber, queryName, javaClassName);
		}
		else if (javaClass == char[].class || javaClass == Character[].class) {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_CHARARRAY_FORMAT, lineNumber, queryName, javaClassName);
		}
		else {
			return buildProblem(ProblemConstants.LITERAL_ARGUMENT_ILLEGAL_GENERIC_FORMAT, lineNumber, queryName);
		}
	}
}
