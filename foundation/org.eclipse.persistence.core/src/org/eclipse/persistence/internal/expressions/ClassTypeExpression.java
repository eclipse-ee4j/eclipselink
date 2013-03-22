/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     May 21, 2009-2.0 Chris Delahunt 
 *       - TODO Bug#: Bug Description 
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * @author cdelahun
 *
 */
public class ClassTypeExpression extends DataExpression {

    //transient public ClassDescriptor descriptor;
    /** Cache the aliased field. Only applies to attributes. */
    protected DatabaseField field;
    /** Cache the aliased field. Only applies to attributes. */
    protected DatabaseField aliasedField;

    /**
     * 
     */
    public ClassTypeExpression(Expression base) {
        super();
        this.baseExpression = base;
    }
    
    public ClassTypeExpression() {
        super();
    }
    
    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Class For Inheritance";
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.expressions.Expression#rebuildOn(org.eclipse.persistence.expressions.Expression)
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        Expression result = newLocalBase.type();

        result.setSelectIfOrderedBy(selectIfOrderedBy());
        return result;
    }
    
    /**
     * INTERNAL
     * This method returns the inheritance field value for an object to conform in an in-memory query.
     * Similar to getFieldValue, but deals with an instance rather than a Class object directly
     */
    public Object typeValueFromObject(Object object, AbstractSession session) {
        // get the descriptor directly from the object, and use it to find the Java class
        ClassDescriptor objectDescriptor = session.getClassDescriptor(object);
        if (!objectDescriptor.hasInheritance() 
                || objectDescriptor.getInheritancePolicy().shouldUseClassNameAsIndicator()
                || objectDescriptor.getInheritancePolicy().hasClassExtractor() ) {
            return (objectDescriptor.getJavaClassName());
        } else {
            return objectDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(objectDescriptor.getJavaClass());
        }
    }
    
    public void validateNode() {
        
        ClassDescriptor descriptor = getContainingDescriptor();
        if (descriptor ==null){
            throw QueryException.invalidTypeExpression(getBaseExpression());
        }
        if ( (!descriptor.hasInheritance()) || (!descriptor.getInheritancePolicy().hasClassIndicator()) ) {
            throw QueryException.invalidTypeExpression(descriptor.getJavaClassName());
        }
        super.validateNode();
    }
    
    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for value expressions.
     * Pulled from QueryKeyExpression valueFromObject
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // The expression may be across a relationship, in which case it must be traversed.
        if ((!getBaseExpression().isExpressionBuilder()) && getBaseExpression().isQueryKeyExpression()) {
            object = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // toDo: Null means the join filters out the row, returning null is not correct if an inner join,
            // outer/inner joins need to be fixed to filter correctly.
            if (object == null) {
                return null;
            }

            // If from an anyof the object will be a collection of values,
            // A new vector must union the object values and the values extracted from it.
            if (object instanceof Vector) {
                Vector comparisonVector = new Vector(((Vector)object).size() + 2);
                for (Enumeration valuesToIterate = ((Vector)object).elements();
                         valuesToIterate.hasMoreElements();) {
                    Object vectorObject = valuesToIterate.nextElement();
                    if (vectorObject == null) {
                        comparisonVector.addElement(vectorObject);
                    } else {
                        Object valueOrValues = typeValueFromObject(vectorObject, session);

                        // If a collection of values were extracted union them.
                        if (valueOrValues instanceof Vector) {
                            for (Enumeration nestedValuesToIterate = ((Vector)valueOrValues).elements();
                                     nestedValuesToIterate.hasMoreElements();) {
                                comparisonVector.addElement(nestedValuesToIterate.nextElement());
                            }
                        } else {
                            comparisonVector.addElement(valueOrValues);
                        }
                    }
                }
                return comparisonVector;
            }
        }
        return typeValueFromObject(object, session);
    }
    
    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write("TYPE");
        writer.write(tableAliasesDescription());
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getField() {
        if (field == null) {            
            ClassDescriptor descriptor = getContainingDescriptor();

            if (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().hasClassExtractor()){
                throw QueryException.invalidTypeExpression(descriptor.getJavaClassName());
            }
            field = descriptor.getInheritancePolicy().getClassIndicatorField();
        }
        return field;
    }
    
    /**
     * INTERNAL:
     * Transform the object-level value into a database-level value
     * objectValue is a Class or collection of Class objects and the returned value is the database representation
     * Example:  ObjectValue=LargeProject returns "L". 
     */
    public Object getFieldValue(Object objectValue, AbstractSession session) {
        if (objectValue ==null){
            return null;
        }
        
        if (objectValue instanceof Collection) {
                // This can actually be a collection for IN within expressions... however it would be better for expressions to handle this.
                Collection values = (Collection)objectValue;
                Vector fieldValues = new Vector(values.size());
                for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                    Object value = iterator.next();
                    if (!(value instanceof Expression)){
                        value = getFieldValue(value, session);
                    }
                    fieldValues.add(value);
                }
                return fieldValues;
        } else {
            if (! (objectValue instanceof Class) ){
                throw QueryException.invalidTypeExpression(objectValue.getClass().toString());
            }
            
            ClassDescriptor descriptor = session.getDescriptor((Class)objectValue);
            if (descriptor == null){
                throw QueryException.invalidTypeExpression(objectValue.getClass().toString());
            }

            if (descriptor.hasInheritance() && !descriptor.getInheritancePolicy().shouldUseClassNameAsIndicator()){
                return descriptor.getInheritancePolicy().getClassIndicatorMapping().get(objectValue);
            } else {
                return ((Class)objectValue).getName();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Like QueryKeyExpression, return the descriptor for the class type used, null if one can't be determined yet.  
     * Should only be called when a session is already set. 
     */
    public ClassDescriptor getContainingDescriptor() {
        return ((ObjectExpression)getBaseExpression()).getDescriptor();

    }
    
    /**
     * INTERNAL:
     * Return the descriptor for the base expression.  This is used in ReportItem when building the 
     * return value (a class), as none of the expressions will have a session.  
     */
    public ClassDescriptor getContainingDescriptor(ObjectLevelReadQuery query) {
        Class queryClass = null;
        if (getBaseExpression().isExpressionBuilder()){
            queryClass = ((ExpressionBuilder)getBaseExpression()).getQueryClass();
            return query.getSession().getDescriptor(queryClass);
        } else {
            // It must be a QueryKeyExpression.
            return getBaseExpression().getLeafDescriptor(query, query.getDescriptor(), query.getSession());
        }
    }
    
    public boolean isClassTypeExpression(){
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isAttribute() {
        return true;
    }
    
    /**
     * INTERNAL:
     * For CR#2456 if this is part of an objExp.equal(objExp), do not need to add
     * additional expressions to normalizer both times, and the foreign key join
     * replaces the equal expression.
     */
    public Expression normalize(ExpressionNormalizer normalizer, Vector foreignKeyJoinPointer) {
        if (hasBeenNormalized()) {
            return this;
        }
        return super.normalize(normalizer);
    }
    
    /**
     * INTERNAL:
     * Alias the database field for our current environment
     */
    protected void initializeAliasedField() {
        DatabaseField tempField = getField().clone();
        DatabaseTable aliasedTable = getAliasedTable();

        aliasedField = tempField;
        aliasedField.setTable(aliasedTable);
    }
    
    /**
     * INTERNAL:
     * Return the field appropriately aliased
     */
    public DatabaseField getAliasedField() {
        if (aliasedField == null) {
            initializeAliasedField();
        }
        return aliasedField;

    }
    
    /**
     * Return the alias for our table
     */
    protected DatabaseTable getAliasedTable() {
        DataExpression base = (DataExpression)getBaseExpression();

        DatabaseTable alias = base.aliasForTable(getField().getTable());
        if (alias == null) {
            return getField().getTable();
        } else {
            return alias;
        }
    }
    
    /**
     * INTERNAL:
     * Return all the fields
     */
    public Vector getFields() {
        Vector result = new Vector(1);
        DatabaseField field = getField();
        if (field != null) {
            result.addElement(field);
        }
        return result;
    }
}
