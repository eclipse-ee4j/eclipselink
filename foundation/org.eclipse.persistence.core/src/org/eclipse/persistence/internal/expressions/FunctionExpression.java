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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

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
        if ((this.operator != expression.getOperator()) && ((this.operator == null) || (!this.operator.equals(expression.getOperator())))) {
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
        if (this.operator != null) {
            hashCode = hashCode + this.operator.hashCode();
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
    public Expression create(Expression base, Object singleArgument, ExpressionOperator anOperator) {
        baseExpression = base;
        addChild(base);
        Expression localBase = base;
        if(anOperator.isFunctionOperator()) {
            ExpressionBuilder builder = getBuilder();
            if(builder != null) {
                localBase = builder;
            }
        }
        Expression arg = Expression.from(singleArgument, localBase);
        addChild(arg);
        setOperator(anOperator);
        return this;
    }
    
    /**
     * INTERNAL: 
     * added for Trim support.  TRIM([trim_character FROM] string_primary)
     */
    public Expression createWithBaseLast(Expression base, Object singleArgument, ExpressionOperator anOperator) {
        baseExpression = base;
        Expression localBase = base;
        if(anOperator.isFunctionOperator()) {
            ExpressionBuilder builder = getBuilder();
            if(builder != null) {
                localBase = builder;
            }
        }
        Expression arg = Expression.from(singleArgument, localBase);
        addChild(arg);
        addChild(base);
        setOperator(anOperator);
        return this;
    }

    /**
     * INTERNAL:
     */
    @Override
    public Expression create(Expression base, List arguments, ExpressionOperator anOperator) {
        this.baseExpression = base;
        setOperator(anOperator);
        addChild(base);
        Expression localBase = base;
        if (anOperator.isFunctionOperator()) {
            ExpressionBuilder builder = getBuilder();
            if (builder != null) {
                localBase = builder;
            }
        }
        for (Object argument : arguments) {
            Expression arg = Expression.from(argument, localBase);
            addChild(arg);
        }
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
        int selector = this.operator.getSelector();
        
        // Must check for NOT and negate entire base expression.
        if (selector == ExpressionOperator.Not) {
            return !getBaseExpression().doesConform(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
        }

        // Conform between or in function.
        if ((selector == ExpressionOperator.Between) || (selector == ExpressionOperator.NotBetween)
                || (selector == ExpressionOperator.In) || (selector == ExpressionOperator.NotIn) 
                || (selector == ExpressionOperator.Like) || (selector == ExpressionOperator.Regexp)
                || (selector == ExpressionOperator.NotLike)) {
            // Extract the value from the left side.
            Object leftValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // Extract the value from the arguments, skip the first child which is the base.
            int size = this.children.size();
            Vector rightValue = new Vector(size);
            for (int index = 1; index < size; index++) {
                Object valueFromRight;
                Object child = this.children.get(index);
                if (child instanceof Expression) {
                    valueFromRight = ((Expression)child).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
                } else {
                    valueFromRight = child;
                }
                //If valueFromRight is a Vector, then there is only one child other than the base, e.g. valueFromRight is a collection of constants.  
                //Then it should be the vector to be compared with.  Don't add it to another collection.
                if (valueFromRight instanceof Vector) {
                    rightValue = (Vector)valueFromRight;
                //Single values should be added to the rightValue, which will be compared with leftValue.
                } else {
                    rightValue.add(valueFromRight);
                }
            }

            // If left is anyof collection of values, check each one.
            // If the right had an anyof not supported will be thrown from the operator.
            if (leftValue instanceof Vector) {
                for (Object tempLeft : (Vector)leftValue) {
                    if (this.operator.doesRelationConform(tempLeft, rightValue)) {
                        return true;
                    }
                }

                // Only return false if none of the values match.
                return false;
            } else {
                return this.operator.doesRelationConform(leftValue, rightValue);
            }
        } else if ((selector == ExpressionOperator.IsNull) || (selector == ExpressionOperator.NotNull)) {
            // Extract the value from the left side.
            Object leftValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // If left is anyof collection of values, check each one.
            if (leftValue instanceof Vector) {
                for (Object tempLeft : (Vector)leftValue) {
                    if (this.operator.doesRelationConform(tempLeft, null)) {
                        return true;
                    }
                }

                // Only return false if none of the values match.
                return false;
            } else {
                return this.operator.doesRelationConform(leftValue, null);
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
        if (this.operator.isComplete()) {
            platformOperator = this.operator;
            return;
        }
        platformOperator = platform.getOperator(this.operator.getSelector());
        if (platformOperator == null) {
            throw QueryException.invalidOperator(this.operator.toString());
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
        int selector = this.operator.getSelector();
        if (((selector != ExpressionOperator.IsNull) && (selector != ExpressionOperator.NotNull)) || (this.children.size() != 1)) {
            if (((selector != ExpressionOperator.InSubQuery) && (selector != ExpressionOperator.NotInSubQuery))
                    || (this.children.size() != 2)) {
                return false;
            }
        }

        Expression base = getBaseExpression();
        //bug 384641 - check that directCollections are not treated as object comparisons
        return (base.isObjectExpression() && (!((ObjectExpression)base).isAttribute()) && 
                !((ObjectExpression)base).isDirectCollection() );
    }

    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    public void iterateOn(ExpressionIterator iterator) {
        super.iterateOn(iterator);
        for (Enumeration childrenEnum = this.children.elements(); childrenEnum.hasMoreElements();) {
            Expression child = (Expression)childrenEnum.nextElement();
            child.iterateOn(iterator);
        }
    }

    /**
     * INTERNAL:
     * Normalize into a structure that is printable.
     * Also compute printing information such as outer joins.
     * This checks for object isNull, notNull, in and notIn comparisons.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        //This method has no validation but we should still make the method call for consistency
        //bug # 2956674
        //validation is moved into normalize to ensure that expressions are valid before we attempt to work with them
        validateNode();
        if (this.children.isEmpty()) {
            return this;
        }
        
        // Ensure session has been set.
        ExpressionBuilder builder = getBuilder();
        if ((builder != null) && (builder.getSession() == null)) {
            builder.setSession(normalizer.getSession().getRootSession(null));
        }
        
        if (this.operator.getSelector() == ExpressionOperator.Count) {
            // Attempting to count an Entity and not an attribute.  Need to augment this expression.
            // This is normally normalized in ReportQuery, but can get to here in a having clause.
            prepareObjectAttributeCount(normalizer, null, null, null);
        }

        if (!isObjectComparison()) {
            for (int index = 0; index < this.children.size(); index++) {
                this.children.set(index, ((Expression)this.children.get(index)).normalize(normalizer));
            }
            return this;
        } else {
            //if not normalizing we must still validate the corresponding node to make sure that they are valid
            //bug # 2956674
            for (int index = 0; index < this.children.size(); index++) {
                ((Expression)this.children.get(index)).validateNode();
            }
        }

        // This code is executed only in the case of an is[not]Null, or [not]in on an
        // object attribute.
        ObjectExpression base = (ObjectExpression)getBaseExpression();

        // For cr2334, fix code so that normalize is first called on base expressions.
        // I.e. if base itself had a base expression this expression would not be normalized.
        if (base.getBaseExpression() != null) {
            base.getBaseExpression().normalize(normalizer);
        }

        // Check for IN with objects, "e IN (Select e2 from Employee e2)".
        if ((this.operator.getSelector() == ExpressionOperator.InSubQuery)
                || (this.operator.getSelector() == ExpressionOperator.NotInSubQuery)) {
            // Switch object comparison to compare on primary key.
            if (this.children.size() != 2) {
                throw QueryException.invalidExpression(this);
            }
            // Check if the left is for a 1-1 mapping, then optimize to compare on foreign key to avoid join.
            DatabaseMapping mapping = null;
            if (base.isQueryKeyExpression()) {
                mapping = base.getMapping();
            }
            ClassDescriptor descriptor = null;
            List<DatabaseField> sourceFields = null;
            List<DatabaseField> targetFields = null;
            if ((mapping != null) && mapping.isOneToOneMapping()
                    && (!((OneToOneMapping)mapping).hasRelationTableMechanism())
                    && (!((OneToOneMapping)mapping).hasCustomSelectionQuery())) {
                base = (ObjectExpression)base.getBaseExpression();
                descriptor = mapping.getReferenceDescriptor();
                Map<DatabaseField, DatabaseField> targetToSourceKeyFields = ((OneToOneMapping)mapping).getTargetToSourceKeyFields();
                sourceFields = new ArrayList(targetToSourceKeyFields.size());
                targetFields = new ArrayList(targetToSourceKeyFields.size());
                for (Map.Entry<DatabaseField, DatabaseField> entry : targetToSourceKeyFields.entrySet()) {
                    sourceFields.add(entry.getValue());
                    targetFields.add(entry.getKey());
                }
            } else {
                mapping = null;
                descriptor = base.getDescriptor();
                sourceFields = descriptor.getPrimaryKeyFields();
                targetFields = sourceFields;
            }
            if (sourceFields.size() != 1) {
                base = (ObjectExpression)getBaseExpression();
                // For composite ids an exists and subselect is used.
                SubSelectExpression subSelectExp = (SubSelectExpression)this.children.get(1);
                ReportQuery subQuery = subSelectExp.getSubQuery();
                
                // some db (derby) require that in EXIST(SELECT...) subquery returns a single column
                subQuery.getItems().clear();
                subQuery.addItem("one", new ConstantExpression(Integer.valueOf(1), subQuery.getExpressionBuilder()));
                
                Expression subSelectCriteria = subQuery.getSelectionCriteria();
                ExpressionBuilder subBuilder = subQuery.getExpressionBuilder();
                Expression newExp;
                // Any or Some
                if (this.operator.getSelector() == ExpressionOperator.InSubQuery) {
                    subSelectCriteria = subBuilder.equal(base).and(subSelectCriteria);
                } else {
                    subSelectCriteria = subBuilder.notEqual(base).and(subSelectCriteria);
                }
                subQuery.setSelectionCriteria(subSelectCriteria);
                newExp = builder.exists(subQuery);
                return newExp.normalize(normalizer);
            }
            Expression newBase = base.getField(sourceFields.get(0));
            setBaseExpression(newBase);
            this.children.set(0, newBase);
            Expression right = (Expression)this.children.get(1);
            if (right.isSubSelectExpression()) {
                // Check for sub-select, need to replace sub-selects on object to select its id.
                ReportQuery query = ((SubSelectExpression)right).getSubQuery();
                if (query.getItems().size() != 1) {
                    throw QueryException.invalidExpression(this);                    
                }
                ReportItem item = query.getItems().get(0);
                item.setAttributeExpression(item.getAttributeExpression().getField(targetFields.get(0)));
            } else {
                throw QueryException.invalidExpression(this);
            }
            // Still need to normalize the children.
            for (int index = 0; index < this.children.size(); index++) {
                this.children.set(index, ((Expression)this.children.get(index)).normalize(normalizer));
            }
            return this;
        }
        // else isNull/notNull
        
        Expression foreignKeyJoin = null;
        if (base.getMapping() == null) {
            // Is an expression builder, transform to a null primary key expression.
            foreignKeyJoin = base.getDescriptor().getObjectBuilder().buildPrimaryKeyExpressionFromKeys(null, getSession());
            foreignKeyJoin = foreignKeyJoin.rebuildOn(base);
        } else {
            // Switch to null foreign key comparison (i.e. get('c').isNull() to getField('C_ID').isNull()).
            // For bug 3105559 also must handle aggregates: get("period").isNull();
            foreignKeyJoin = base.getMapping().buildObjectJoinExpression(base, (Object)null, getSession());
        }

        if (this.operator.getSelector() == ExpressionOperator.NotNull) {
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
     * Print SQL using the operator.
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        // If all children are parameters, some databases don't allow binding.
        if (printer.getPlatform().isDynamicSQLRequiredForFunctions() && !this.children.isEmpty()) {
            boolean allParams = true;
            for (Iterator iterator = this.children.iterator(); iterator.hasNext(); ) {
                Expression child = (Expression)iterator.next();
                if (!(child.isParameterExpression() || child.isConstantExpression())) {
                    allParams = false;
                }
            }
            if (allParams) {
                printer.getCall().setUsesBinding(false);                
            }
        }
        ExpressionOperator realOperator;
        realOperator = getPlatformOperator(printer.getPlatform());
        realOperator.printCollection(this.children, printer);
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        ExpressionOperator realOperator = getPlatformOperator(printer.getPlatform());
        realOperator.printJavaCollection(this.children, printer);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        Vector newChildren = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(this.children.size());
        for (int i = 1; i < this.children.size(); i++) {// Skip the first one, since it's also the base
            newChildren.addElement(((Expression)children.elementAt(i)).rebuildOn(newBase));
        }
        newLocalBase.setSelectIfOrderedBy(getBaseExpression().selectIfOrderedBy());
        FunctionExpression rebuilt = (FunctionExpression) newLocalBase.performOperator(this.operator, newChildren);
        rebuilt.setResultType(this.getResultType()); //copy over result type.
        return rebuilt;
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    public void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder){
        getBaseExpression().resetPlaceHolderBuilder(queryBuilder);
        for (int i = this.children.size()-1; i > 0; i--) {// Skip the first one, since it's also the base
            ((Expression)children.elementAt(i)).resetPlaceHolderBuilder(queryBuilder);
        }
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
    @Override
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        if (this.children.isEmpty()) {
            return (Expression)clone();
        }
        Vector newChildren = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(this.children.size());

        // For functions the base is the first child, we only want the arguments so start at the second.
        for (int index = 1; index < this.children.size(); index++) {
            newChildren.addElement(((Expression)children.elementAt(index)).twistedForBaseAndContext(newBase, context, oldBase));
        }

        // Aply the function to the twisted old base.
        Expression oldBaseExp = (Expression)this.children.elementAt(0);
        return oldBaseExp.twistedForBaseAndContext(newBase, context, oldBase).performOperator(this.operator, newChildren);
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        Object baseValue = getBaseExpression().valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);
        Vector arguments = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(this.children.size());
        for (int index = 1; index < this.children.size(); index++) {
            if (this.children.elementAt(index) instanceof Expression) {
                arguments.addElement(((Expression)this.children.elementAt(index)).valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered));
            } else {
                arguments.addElement(this.children.elementAt(index));
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
                    baseVector.addElement(this.operator.applyFunction(baseObject, arguments));
                }
            }
            return baseVector;
        } else {
            // Do not apply functions to null, just leave as null.
            if (baseValue == null) {
                return null;
            } else {
                return this.operator.applyFunction(baseValue, arguments);
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
    @Override
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
                field = field.clone();
            }
            
            // If the result type is set, use it.
            field.setSqlType(DatabaseField.NULL_SQL_TYPE);
            //we also cache the JDBC type now so reset it as well.
            if (hasResultType()) {
                field.setType(getResultType());
            } else {
                // If the function is anything but min or max, null out the
                // field type. The type will be calculated based on the
                // function.
                int selector = this.operator.getSelector();
                if (selector != ExpressionOperator.Maximum && selector != ExpressionOperator.Minimum) {
                    field.setType(null);
                }   
            }

            newFields.addElement(field);
        } else {
            // This field is a complex function value so any name can be used.
            DatabaseField field = new DatabaseField("*");
            // If the result type is set, use it.
            field.setSqlType(DatabaseField.NULL_SQL_TYPE);
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
    
    /**
     * INTERNAL:
     * JPQL allows count([distinct] e), where e can be an object, not just a single field,
     * however the database only allows a single field, so object needs to be translated to a single field.
     * If the descriptor has a single pk, it is used, otherwise any pk is used if distinct, otherwise a subselect is used.
     * If the object was obtained through an outer join, then the subselect also will not work, so an error is thrown.
     */
    public void prepareObjectAttributeCount(ExpressionNormalizer normalizer, ReportItem item, ReportQuery query, Map clonedExpressions) {
        // ** Note that any of the arguments may be null depending on the caller.
        if (getOperator().getSelector() == ExpressionOperator.Count) {
            Expression baseExp = getBaseExpression();
            boolean distinctUsed = false;
            if (baseExp.isFunctionExpression() && (((FunctionExpression)baseExp).getOperator().getSelector() == ExpressionOperator.Distinct)) {
                distinctUsed = true;
                baseExp = ((FunctionExpression)baseExp).getBaseExpression();
            }
            boolean outerJoin = false;
            ClassDescriptor newDescriptor = null;
            AbstractSession session = null;
            if (query != null) {
                session = query.getSession();
            } else {
                session = normalizer.getSession();
            }
            if (baseExp.isQueryKeyExpression()) {
                // now need to find out if it is a direct to field or something else.
                ClassDescriptor descriptor = null;
                if (query == null) {
                    descriptor = ((QueryKeyExpression) baseExp).getDescriptor();
                } else {
                    descriptor = query.getDescriptor();
                }
                DatabaseMapping mapping = baseExp.getLeafMapping(query, descriptor, session);
                if ((mapping != null) && !mapping.isAbstractDirectMapping()) {
                    outerJoin = ((QueryKeyExpression)baseExp).shouldUseOuterJoin();
                    if (mapping.isAggregateMapping()){
                        newDescriptor = mapping.getDescriptor();
                        baseExp = ((QueryKeyExpression)baseExp).getBaseExpression();
                    } else {
                        newDescriptor = mapping.getReferenceDescriptor();
                    }
                } else {
                    QueryKey queryKey = getLeafQueryKeyFor(query, baseExp, descriptor, session);
                    if ((queryKey != null) && queryKey.isForeignReferenceQueryKey()){
                        outerJoin = ((QueryKeyExpression) baseExp).shouldUseOuterJoin();
                        newDescriptor = session.getDescriptor(((ForeignReferenceQueryKey)queryKey).getReferenceClass());
                    }
                }
            } else if (baseExp.isExpressionBuilder()) {
                if (((ExpressionBuilder)baseExp).getQueryClass() == null) {
                    if (item != null) {
                        item.setResultType(ClassConstants.INTEGER);
                    }
                } else {
                    newDescriptor = session.getDescriptor(((ExpressionBuilder)baseExp).getQueryClass());
                }
            }
            
            if (newDescriptor != null) {
                // At this point we are committed to rewriting the query.
                if ((newDescriptor.getPrimaryKeyFields().size() == 1) || !distinctUsed) {
                    // case 1: single PK =>
                    // treat COUNT(entity) as COUNT(entity.pk)
                    Expression countArg = baseExp.getField(newDescriptor.getPrimaryKeyFields().get(0));
                    if (distinctUsed) {
                        countArg = countArg.distinct();
                    }
                    setBaseExpression(countArg);
                    getChildren().set(0, countArg);
                } else if (((DatabasePlatform)session.getPlatform(newDescriptor.getJavaClass())).supportsCountDistinctWithMultipleFields()) {                            
                    // case 3, is database allows multiple fields, then just print them
                    // treat COUNT(distinct entity) as COUNT(distinct entity.pk1, entity.pk2)
                    List args = new ArrayList(newDescriptor.getPrimaryKeyFields().size());
                    Expression firstField = null;
                    for (DatabaseField field : newDescriptor.getPrimaryKeyFields()) {
                        if (firstField == null) {
                            firstField = baseExp.getField(field);
                        } else {
                            args.add(baseExp.getField(field));
                        }
                    }
                    
                    ExpressionOperator anOperator = new ExpressionOperator();
                    anOperator.setType(ExpressionOperator.FunctionOperator);
                    Vector v = NonSynchronizedVector.newInstance(args.size());
                    v.addElement("DISTINCT ");
                    for (int index = 0; index < args.size(); index++) {
                        v.add(", ");
                    }
                    v.add("");
                    anOperator.printsAs(v);
                    anOperator.bePrefix();
                    anOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
                    Expression distinctFunction = anOperator.expressionForArguments(firstField, args);
                    
                    setBaseExpression(distinctFunction);
                    getChildren().set(0, distinctFunction);
                } else if (!outerJoin && (query != null)) {
                    // case 4: composite PK and DISTINCT, but no
                    // outer join => previous solution using
                    // COUNT(*) and EXISTS subquery
                    // TODO, this doesn't really work for most cases (joins, other things selected, group by),
                    // this should probably be removed and throw an error,
                    // or changed to just concat all the pks together.
                    
                    // If this is a subselect baseExp is yet uncloned,
                    // and will miss out if moved now from items into a selection criteria.
                    if (clonedExpressions != null) {
                        if (clonedExpressions.get(baseExp.getBuilder()) != null) {
                            baseExp = baseExp.copiedVersionFrom(clonedExpressions);
                        } else {
                            baseExp = baseExp.rebuildOn(query.getExpressionBuilder());
                        }
                    }
                    
                    // Now the reference class of the query needs to be reversed.
                    // See the bug description for an explanation.
                    ExpressionBuilder countBuilder = baseExp.getBuilder();
                    ExpressionBuilder outerBuilder ;
                    
                    ReportQuery subSelect = new ReportQuery(query.getReferenceClass(), countBuilder);
                    query.getSession().getPlatform().retrieveFirstPrimaryKeyOrOne(subSelect);
                    
                    // Make sure the outerBuilder does not appear on the left of the subselect.
                    // Putting a builder on the left is desirable to trigger an optimization.
                    if (query.getSelectionCriteria() != null) {
                        outerBuilder = new ExpressionBuilder(newDescriptor.getJavaClass());
                        query.setExpressionBuilder(outerBuilder);
                        subSelect.setSelectionCriteria(baseExp.equal(outerBuilder).and(query.getSelectionCriteria()));
                    } else {
                        outerBuilder = new ExpressionBuilder(newDescriptor.getJavaClass());
                        query.setExpressionBuilder(outerBuilder);
                        subSelect.setSelectionCriteria(baseExp.equal(outerBuilder));
                    }
                    query.setNonFetchJoinAttributeExpressions(null);
                    query.setSelectionCriteria(outerBuilder.exists(subSelect));
                    setBaseExpression(outerBuilder);
                    getChildren().set(0, outerBuilder);
                    query.setReferenceClass(newDescriptor.getJavaClass());
                    query.changeDescriptor(session);
                } else {
                    // case 4: composite PK, DISTINCT, outer join => 
                    // not supported, throw exception
                    DatabaseQuery reportQuery = query;
                    if (query == null) {
                        reportQuery = normalizer.getStatement().getQuery();
                    }
                    throw QueryException.distinctCountOnOuterJoinedCompositePK(newDescriptor, reportQuery);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Lookup the query key for this item.
     * If an aggregate of foreign mapping is found it is traversed.
     */
    protected QueryKey getLeafQueryKeyFor(DatabaseQuery query, Expression expression, ClassDescriptor rootDescriptor, AbstractSession session) throws QueryException {
        // Check for database field expressions or place holder
        if ((expression == null) || (expression.isFieldExpression())) {
            return null;
        }

        if (!(expression.isQueryKeyExpression())) {
            return null;
        }
        
        QueryKeyExpression qkExpression = (QueryKeyExpression)expression;
        Expression baseExpression = qkExpression.getBaseExpression();

        ClassDescriptor descriptor = baseExpression.getLeafDescriptor(query, rootDescriptor, session);
        return descriptor.getQueryKeyNamed(qkExpression.getName());
    }

    protected DatabaseMapping getMappingOfFirstPrimaryKey(ClassDescriptor descriptor) {
        if (descriptor != null) {
            for (Iterator i = descriptor.getMappings().iterator(); i.hasNext(); ) {
                DatabaseMapping m = (DatabaseMapping)i.next();
                if (m.isPrimaryKeyMapping()) {
                    return m;
                }
            }
        }
        return null;
    }


}
