/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
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
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
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
    public Expression create(Expression base, Vector arguments, ExpressionOperator anOperator) {
        baseExpression = base;
        setOperator(anOperator);
        addChild(base);
        Expression localBase = base;
        if(anOperator.isFunctionOperator()) {
            ExpressionBuilder builder = getBuilder();
            if(builder != null) {
                localBase = builder;
            }
        }
        for (Enumeration e = arguments.elements(); e.hasMoreElements();) {
            Expression arg = Expression.from(e.nextElement(), localBase);
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
                || (selector == ExpressionOperator.Like) || (selector == ExpressionOperator.NotLike)) {
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
        if (this.children.size() != 1) {
            return false;
        }

        int selector = this.operator.getSelector();
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
        for (Enumeration childrenEnum = this.children.elements(); childrenEnum.hasMoreElements();) {
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
        if (this.children.isEmpty()) {
            return this;
        }
        
        // Ensure session has been set.
        ExpressionBuilder builder = getBuilder();
        if ((builder != null) && (builder.getSession() == null)) {
            builder.setSession(normalizer.getSession().getRootSession(null));
        }
        
        if (this.operator.getSelector() == ExpressionOperator.Count && getBaseExpression().isObjectExpression() && (!((ObjectExpression)getBaseExpression()).isAttribute())){
            // we are attempting to count an Entity and not an attribute.  Need to augment this expression.
            prepareObjectAttributeCount(normalizer);
        }

        if (!isObjectComparison()) {
            for (int index = 0; index < this.children.size(); index++) {
                this.children.setElementAt(((Expression)this.children.elementAt(index)).normalize(normalizer), index);
            }
            return this;
        } else {
            //if not normalising we must still validate the corresponding node to make sure that they are valid
            //bug # 2956674
            for (int index = 0; index < this.children.size(); index++) {
                ((Expression)this.children.elementAt(index)).validateNode();
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
    
    private void prepareObjectAttributeCount(ExpressionNormalizer normalizer) {
        Expression baseExp = this.getBaseExpression();
        boolean distinctUsed = false;
        if (baseExp.isFunctionExpression() && (((FunctionExpression) baseExp).getOperator().getSelector() == ExpressionOperator.Distinct)) {
            distinctUsed = true;
            baseExp = ((FunctionExpression) baseExp).getBaseExpression();
        }
        boolean outerJoin = false;
        ClassDescriptor newDescriptor = null;
        if (baseExp.isQueryKeyExpression()) {
            // now need to find out if it is a direct to field or something
            // else.
            DatabaseMapping mapping = getLeafMappingFor(baseExp, ((QueryKeyExpression) baseExp).descriptor);
            if ((mapping != null) && !mapping.isAbstractDirectMapping()) {
                outerJoin = ((QueryKeyExpression) baseExp).shouldUseOuterJoin();
                if (mapping.isAggregateMapping()) {
                    newDescriptor = mapping.getDescriptor();
                    baseExp = ((QueryKeyExpression) baseExp).getBaseExpression();
                } else {
                    newDescriptor = mapping.getReferenceDescriptor();
                }
            } else {
                QueryKey queryKey = getLeafQueryKeyFor(baseExp, ((QueryKeyExpression) baseExp).descriptor);
                if ((queryKey != null) && !queryKey.isDirectQueryKey()){
                    outerJoin = ((QueryKeyExpression) baseExp).shouldUseOuterJoin();
                    newDescriptor = queryKey.getDescriptor();
                }
            }
        } else if (baseExp.isExpressionBuilder()) {
            newDescriptor = normalizer.getSession().getDescriptor(((ExpressionBuilder) baseExp).getQueryClass());
        }

        if (newDescriptor != null) {
            // At this point we are committed to rewriting the query.
            if (newDescriptor.hasSimplePrimaryKey() && (newDescriptor.getPrimaryKeyFields().size() == 1)) {
                // case 1: simple PK =>
                // treat COUNT(entity) as COUNT(entity.pk)
                DatabaseMapping pk = getMappingOfFirstPrimaryKey(newDescriptor);
                Expression countArg = baseExp.get(pk.getAttributeName());
                if (distinctUsed) {
                    countArg = countArg.distinct();
                }
                this.setBaseExpression(countArg);
                this.children.setElementAt(countArg, 0);
            } else if (!distinctUsed) {
                // case 2: composite PK, but no DISTINCT =>
                // pick a PK column for the COUNT aggregate
                DatabaseMapping pk = getMappingOfFirstPrimaryKey(newDescriptor);
                Expression countArg = baseExp.get(pk.getAttributeName());
                while (pk.isAggregateObjectMapping()) {
                    newDescriptor = ((AggregateObjectMapping) pk).getReferenceDescriptor();
                    pk = getMappingOfFirstPrimaryKey(newDescriptor);
                    countArg = countArg.get(pk.getAttributeName());
                }
                this.setBaseExpression(countArg);
                this.children.setElementAt(countArg, 0);
            } else if (!outerJoin) {
                // case 3: composite PK and DISTINCT, but no
                // outer join => previous solution using
                // COUNT(*) and EXISTS subquery

                // If this is a subselect baseExp is yet uncloned,
                // and will miss out if moved now from items into a selection
                // criteria.
                // Now the reference class of the query needs to be reversed.
                // See the bug description for an explanation.
                ExpressionBuilder countBuilder = baseExp.getBuilder();
                ExpressionBuilder outerBuilder = new ExpressionBuilder();

                ReportQuery subSelect = new ReportQuery(newDescriptor.getJavaClass(), countBuilder);
                subSelect.setShouldRetrieveFirstPrimaryKey(true);

                // Make sure the outerBuilder does not appear on the left of the
                // subselect.
                // Putting a builder on the left is desirable to trigger an
                // optimization.
                subSelect.setSelectionCriteria(baseExp.equal(outerBuilder));
                SubSelectExpression sub = new SubSelectExpression(subSelect, ((BaseExpression) baseExp).getBaseExpression());
                this.setBaseExpression(outerBuilder);
                this.children.setElementAt(outerBuilder, 0);
            } else {
                // case 4: composite PK, DISTINCT, outer join =>
                // not supported, throw exception
                throw new UnsupportedOperationException("COMPOSIT PK WITH DISTINCT OUTER");
            }
        }
    }

    /**
     * INTERNAL:
     * Lookup the mapping for this item by traversing its expression recursively.
     * If an aggregate of foreign mapping is found it is traversed.
     */
    protected DatabaseMapping getLeafMappingFor(Expression expression, ClassDescriptor rootDescriptor) throws QueryException {
        // Check for database field expressions or place holder
        if ((expression == null) || (expression.isFieldExpression())) {
            return null;
        }

        if (!(expression.isQueryKeyExpression())) {
            return null;
        }
        
        if (expression.isMapEntryExpression()){
            MapEntryExpression teExpression = (MapEntryExpression)expression;
            
            // get the expression that we want the table entry for
            QueryKeyExpression baseExpression = (QueryKeyExpression)teExpression.getBaseExpression();
            
            // get the expression that owns the mapping for the table entry
            Expression owningExpression = baseExpression.getBaseExpression();
            ClassDescriptor owningDescriptor = getLeafDescriptorFor(owningExpression, rootDescriptor);
            
            // Get the mapping that owns the table
            CollectionMapping mapping = (CollectionMapping)owningDescriptor.getObjectBuilder().getMappingForAttributeName(baseExpression.getName());
            
            if (teExpression.shouldReturnMapEntry()){
                return mapping;
            }
            if (mapping.getContainerPolicy().isMappedKeyMapPolicy()){
                MappedKeyMapContainerPolicy policy = (MappedKeyMapContainerPolicy)mapping.getContainerPolicy();
                return (DatabaseMapping)policy.getKeyMapping();
            }
            return mapping;
        }

        QueryKeyExpression qkExpression = (QueryKeyExpression)expression;
        Expression baseExpression = qkExpression.getBaseExpression();

        ClassDescriptor descriptor = getLeafDescriptorFor(baseExpression, rootDescriptor);
        return descriptor.getObjectBuilder().getMappingForAttributeName(qkExpression.getName());
    }

    /**
     * INTERNAL:
     * Lookup the query key for this item.
     * If an aggregate of foreign mapping is found it is traversed.
     */
    protected QueryKey getLeafQueryKeyFor(Expression expression, ClassDescriptor rootDescriptor) throws QueryException {
        // Check for database field expressions or place holder
        if ((expression == null) || (expression.isFieldExpression())) {
            return null;
        }

        if (!(expression.isQueryKeyExpression())) {
            return null;
        }
        
        QueryKeyExpression qkExpression = (QueryKeyExpression)expression;
        Expression baseExpression = qkExpression.getBaseExpression();

        ClassDescriptor descriptor = getLeafDescriptorFor(baseExpression, rootDescriptor);
        return descriptor.getQueryKeyNamed(qkExpression.getName());
    }
    
    /**
     * INTERNAL:
     * Lookup the descriptor for this item by traversing its expression recursively.
     * @param expression
     * @param rootDescriptor
     * @return
     * @throws org.eclipse.persistence.exceptions.QueryException
     */
    protected ClassDescriptor getLeafDescriptorFor(Expression expression, ClassDescriptor rootDescriptor) throws QueryException {
        // The base case
        if (expression.isExpressionBuilder()) {
            // The following special case is where there is a parallel builder
            // which has a different reference class as the primary builder.
            Class queryClass = ((ExpressionBuilder)expression).getQueryClass();
            return getSession().getDescriptor(queryClass);
        }
        Expression baseExpression = ((QueryKeyExpression)expression).getBaseExpression();
        ClassDescriptor baseDescriptor = getLeafDescriptorFor(baseExpression, rootDescriptor);
        ClassDescriptor descriptor = null;
        String attributeName = expression.getName();

        DatabaseMapping mapping = baseDescriptor.getObjectBuilder().getMappingForAttributeName(attributeName);

        if (mapping == null) {
            QueryKey queryKey = baseDescriptor.getQueryKeyNamed(attributeName);
            if (queryKey != null) {
                if (queryKey.isForeignReferenceQueryKey()) {
                    descriptor = getSession().getDescriptor(((ForeignReferenceQueryKey)queryKey).getReferenceClass());
                } else// if (queryKey.isDirectQueryKey())
                 {
                    descriptor = queryKey.getDescriptor();
                }
            }
            if (descriptor == null) {
                throw QueryException.invalidQueryKeyInExpression(attributeName);
            }
        } else if (mapping.isAggregateObjectMapping()) {
            descriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
        } else if (mapping.isForeignReferenceMapping()) {
            descriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
        }
        return descriptor;
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
