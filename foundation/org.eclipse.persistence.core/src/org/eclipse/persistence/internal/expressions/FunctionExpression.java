/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Used for expressions that have 0 to n children.
 * These include not, between and all functions.
 */
public class FunctionExpression extends BaseExpression {
    protected Vector children;
    protected ExpressionOperator operator;
    protected transient ExpressionOperator platformOperator;
    protected Class resultType;

    public FunctionExpression() {
        this.children = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        this.resultType = null;
    }
    
    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     * This must be over written by each subclass.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        FunctionExpression expression = (FunctionExpression) object;
        if ((getOperator() != expression.getOperator()) && ((getOperator() == null) || (!getOperator().equals(expression.getOperator())))) {
            return false;
        }
        List children = getChildren();
        List otherChildren = expression.getChildren();        
        int size = children.size();
        if (size != otherChildren.size()) {
            return false;
        }
        for (int index = 0; index < size; index++) {
            if (!children.get(index).equals(otherChildren.get(index))) {
                return false;
            }
        }
        return true;
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getOperator() != null) {
            hashCode = hashCode + getOperator().hashCode();
        }
        List children = getChildren();     
        int size = children.size();
        for (int index = 0; index < size; index++) {
            hashCode = hashCode + children.get(index).hashCode();
        }
        return hashCode;
    }

    public void addChild(Expression child) {
        getChildren().addElement(child);
    }

    /**
     * INTERNAL:
     * Find the alias for a given table
     */
    public DatabaseTable aliasForTable(DatabaseTable table) {
        return getBaseExpression().aliasForTable(table);
    }

    public Expression asOf(AsOfClause clause) {
        final AsOfClause finalClause = clause;
        ExpressionIterator iterator = new ExpressionIterator() {
            public void iterate(Expression each) {
                if (each.isDataExpression()) {
                    each.asOf(finalClause);
                }
            }

            public boolean shouldIterateOverSubSelects() {
                return true;
            }
        };
        iterator.iterateOn(this);
        return this;
    }

    /**
     * INTERNAL:
     */
    public Expression create(Expression base, Object singleArgument, ExpressionOperator operator) {
        baseExpression = base;
        addChild(base);
        Expression arg = Expression.from(singleArgument, base);
        addChild(arg);
        setOperator(operator);
        return this;
    }
    
    /**
     * INTERNAL: 
     * added for Trim support.  TRIM([trim_character FROM] string_primary)
     */
    public Expression createWithBaseLast(Expression base, Object singleArgument, ExpressionOperator anOperator) {
        baseExpression = base;
        Expression arg = Expression.from(singleArgument, base);
        addChild(arg);
        addChild(base);
        setOperator(anOperator);
        return this;
    }

    /**
     * INTERNAL:
     */
    public Expression create(Expression base, Vector arguments, ExpressionOperator operator) {
        baseExpression = base;
        addChild(base);
        for (Enumeration e = arguments.elements(); e.hasMoreElements();) {
            Expression arg = Expression.from(e.nextElement(), base);
            addChild(arg);
        }
        setOperator(operator);
        return this;
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Function";
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying.
     * If the expression in not able to determine if the object conform throw a not supported exception.
     */
    public boolean doesConform(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // Must check for NOT and negate entire base expression.
        if (getOperator().getSelector() == ExpressionOperator.Not) {
            return !getBaseExpression().doesConform(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
        }

        // Conform between or in function.
        if ((getOperator().getSelector() == ExpressionOperator.Between) || (getOperator().getSelector() == ExpressionOperator.NotBetween)
                ||(getOperator().getSelector() == ExpressionOperator.In) || (getOperator().getSelector() == ExpressionOperator.NotIn)) {
            // Extract the value from the left side.
            Object leftValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // Extract the value from the arguments, skip the first child which is the base.
            Vector rightValue = new Vector(getChildren().size());
            for (int index = 1; index < getChildren().size(); index++) {
                Object valueFromRight;
                if (getChildren().elementAt(index) instanceof Expression) {
                    valueFromRight = ((Expression)getChildren().elementAt(index)).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
                } else {      
                    valueFromRight = getChildren().elementAt(index);
                }
                //If valueFromRight is a Vector, then there is only one child other than the base, e.g. valueFromRight is a collection of constants.  
                //Then it should be the vector to be compared with.  Don't add it to another collection.
                if (valueFromRight instanceof Vector) {
                    rightValue = (Vector)valueFromRight;
                //Single values should be added to the rightValue, which will be compared with leftValue.
                } else {
                    rightValue.addElement(valueFromRight);
                }
            }

            // If left is anyof collection of values, check each one.
            // If the right had an anyof not supported will be thrown from the operator.
            if (leftValue instanceof Vector) {
                for (Enumeration leftEnum = ((Vector)leftValue).elements();
                         leftEnum.hasMoreElements();) {
                    Object tempLeft = leftEnum.nextElement();
                    if (getOperator().doesRelationConform(tempLeft, rightValue)) {
                        return true;
                    }
                }

                // Only return false if none of the values match.
                return false;
            } else {
                return getOperator().doesRelationConform(leftValue, rightValue);
            }
        } else if ((getOperator().getSelector() == ExpressionOperator.IsNull) || (getOperator().getSelector() == ExpressionOperator.NotNull)) {
            // Extract the value from the left side.
            Object leftValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // If left is anyof collection of values, check each one.
            if (leftValue instanceof Vector) {
                for (Enumeration leftEnum = ((Vector)leftValue).elements();
                         leftEnum.hasMoreElements();) {
                    Object tempLeft = leftEnum.nextElement();
                    if (getOperator().doesRelationConform(tempLeft, null)) {
                        return true;
                    }
                }

                // Only return false if none of the values match.
                return false;
            } else {
                return getOperator().doesRelationConform(leftValue, null);
            }
        }

        // No other relation functions are supported.
        // Non-relation functions are supported through valueFromObject().
        throw QueryException.cannotConformExpression();
    }

    public Vector getChildren() {
        return children;
    }

    /**
     * INTERNAL: Not to be confused with the public getField(String)
     * This returns a collection of all fields associated with this object. Really
     * only applies to query keys representing an object or to expression builders.
     *
     */
    public Vector getFields() {
        return getBaseExpression().getFields();
    }

    public ExpressionOperator getOperator() {
        return operator;
    }

    public ExpressionOperator getPlatformOperator(DatabasePlatform platform) {
        if (platformOperator == null) {
            initializePlatformOperator(platform);
        }
        return platformOperator;
    }
    
    public Class getResultType() {
        return resultType;
    }

    public boolean hasResultType() {
        return resultType != null;
    }
    
    /**
     * INTERNAL:
     */
    public void initializePlatformOperator(DatabasePlatform platform) {
        if (getOperator().isComplete()) {
            platformOperator = getOperator();
            return;
        }
        platformOperator = platform.getOperator(getOperator().getSelector());
        if (platformOperator == null) {
            throw QueryException.invalidOperator(getOperator().toString());
        }
    }

    public boolean isFunctionExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if the represents an object comparison.
     */
    protected boolean isObjectComparison() {
        if (getChildren().size() != 1) {
            return false;
        }

        int selector = getOperator().getSelector();
        if ((selector != ExpressionOperator.IsNull) && (selector != ExpressionOperator.NotNull)) {
            return false;
        }

        Expression base = getBaseExpression();
        return (base.isObjectExpression() && (!((ObjectExpression)base).isAttribute()));
    }

    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    public void iterateOn(ExpressionIterator iterator) {
        super.iterateOn(iterator);
        for (Enumeration childrenEnum = getChildren().elements(); childrenEnum.hasMoreElements();) {
            Expression child = (Expression)childrenEnum.nextElement();
            child.iterateOn(iterator);
        }
    }

    /**
     * INTERNAL:
     * Normalize into a structure that is printable.
     * Also compute printing information such as outer joins.
     * This checks for object isNull and notNull comparisons.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        //This method has no validation but we should still make the method call for consistency
        //bug # 2956674
        //validation is moved into normalize to ensure that expressions are valid before we attempt to work with them
        validateNode();
        if (getChildren().isEmpty()) {
            return this;
        }

        if (!isObjectComparison()) {
            for (int index = 0; index < getChildren().size(); index++) {
                getChildren().setElementAt(((Expression)getChildren().elementAt(index)).normalize(normalizer), index);
            }
            return this;
        } else {
            //if not normalising we must still validate the corresponding node to make sure that they are valid
            //bug # 2956674
            for (int index = 0; index < getChildren().size(); index++) {
                ((Expression)getChildren().elementAt(index)).validateNode();
            }
        }

        // This code is executed only in the case of an is[not]Null on an
        // object attribute.
        ObjectExpression base = (ObjectExpression)getBaseExpression();

        // For cr2334, fix code so that normalize is first called on base expressions.
        // I.e. if base itself had a base expression this expression would not be normalized.
        base.getBaseExpression().normalize(normalizer);

        // Switch to null foreign key comparison (i.e. get('c').isNull() to getField('C_ID').isNull()).
        // For bug 3105559 also must handle aggregates: get("period").isNull();
        Expression foreignKeyJoin = base.getMapping().buildObjectJoinExpression(base, (Object)null, getSession());

        if (getOperator().getSelector() == ExpressionOperator.NotNull) {
            foreignKeyJoin = foreignKeyJoin.not();
        }
        return foreignKeyJoin.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        Vector oldChildren = children;
        children = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        for (int i = 0; i < oldChildren.size(); i++) {
            addChild((((Expression)oldChildren.elementAt(i)).copiedVersionFrom(alreadyDone)));
        }
    }

    /**
     * INTERNAL:
     * Print SQL
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        ExpressionOperator realOperator;
        realOperator = getPlatformOperator(printer.getPlatform());
        realOperator.printCollection(getChildren(), printer);
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        ExpressionOperator realOperator = getPlatformOperator(printer.getPlatform());
        realOperator.printJavaCollection(getChildren(), printer);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        Vector newChildren = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getChildren().size());
        for (int i = 1; i < getChildren().size(); i++) {// Skip the first one, since it's also the base
            newChildren.addElement(((Expression)children.elementAt(i)).rebuildOn(newBase));
        }
        newLocalBase.setSelectIfOrderedBy(getBaseExpression().selectIfOrderedBy());
        FunctionExpression rebuilt = (FunctionExpression) newLocalBase.performOperator(getOperator(), newChildren);
        rebuilt.setResultType(this.getResultType()); //copy over result type.
        return rebuilt;
    }

    // Set the local base expression, ie the one on the other side of the operator
    // Most types will ignore this, since they don't need it.
    public void setLocalBase(Expression exp) {
        getBaseExpression().setLocalBase(exp);
    }

    public void setOperator(ExpressionOperator theOperator) {
        operator = theOperator;
    }
    
    public void setResultType(Class resultType) {
        this.resultType = resultType;
    }

    /**
     * INTERNAL:
     * Rebuild myself against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist
     * See the comment there for more details"
     */
    public Expression twistedForBaseAndContext(Expression newBase, Expression context) {
        if (getChildren().isEmpty()) {
            return (Expression)clone();
        }
        Vector newChildren = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getChildren().size());

        // For functions the base is the first child, we only want the arguments so start at the second.
        for (int index = 1; index < getChildren().size(); index++) {
            newChildren.addElement(((Expression)children.elementAt(index)).twistedForBaseAndContext(newBase, context));
        }

        // Aply the function to the twisted old base.
        Expression oldBase = (Expression)getChildren().elementAt(0);
        return oldBase.twistedForBaseAndContext(newBase, context).performOperator(getOperator(), newChildren);
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        Object baseValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
        Vector arguments = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getChildren().size());
        for (int index = 1; index < getChildren().size(); index++) {
            if (getChildren().elementAt(index) instanceof Expression) {
                arguments.addElement(((Expression)getChildren().elementAt(index)).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered));
            } else {
                arguments.addElement(getChildren().elementAt(index));
            }
        }
        if (baseValue instanceof Vector) {// baseValue might be a vector, so the individual values must be extracted before applying the function call to them
            Vector baseVector = new Vector();
            for (Enumeration valuesToCompare = ((Vector)baseValue).elements();
                     valuesToCompare.hasMoreElements();) {
                Object baseObject = valuesToCompare.nextElement();
                if (baseObject == null) {
                    baseVector.addElement(baseObject);
                } else {
                    baseVector.addElement(getOperator().applyFunction(baseObject, arguments));
                }
            }
            return baseVector;
        } else {
            // Do not apply functions to null, just leave as null.
            if (baseValue == null) {
                return null;
            } else {
                return getOperator().applyFunction(baseValue, arguments);
            }
        }
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(operator.toString());
    }

    /**
     * INTERNAL: called from SQLSelectStatement.writeFieldsFromExpression(...)
     */
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        if (getBaseExpression().isDataExpression()) {
            DatabaseField field = ((DataExpression)getBaseExpression()).getField();

            if (field == null) {
                // This means the select wants a *.
                field = new DatabaseField("*");
            } else {
                // Clone the field since we will change its type.
                field = (DatabaseField) field.clone();
            }
            
            // If the result type is set, use it.
            if (hasResultType()) {
                field.setType(getResultType());
            } else {
                // If the function is anything but min or max, null out the
                // field type. The type will be calculated based on the
                // function.
                int selector = getOperator().getSelector();
                if (selector != ExpressionOperator.Maximum && selector != ExpressionOperator.Minimum) {
                    field.setType(null);    
                }   
            }

            newFields.addElement(field);
        } else {
            // This field is a complex function value so any name can be used.
            DatabaseField field = new DatabaseField("*");
            field.setType(getResultType());
            newFields.addElement(field);
        }

        printSQL(printer);
    }

    /**
     * INTERNAL:
     * Used in SQL printing.
     */
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        if (baseExpression != null) {
            baseExpression.toString(writer, indent);
        }
    }
}
