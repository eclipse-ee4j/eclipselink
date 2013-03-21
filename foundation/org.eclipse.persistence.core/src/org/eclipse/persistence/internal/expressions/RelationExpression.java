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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.AbstractColumnMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose</b>:Used for all relation operators except for between.
 */
public class RelationExpression extends CompoundExpression {
    /** PERF: Cache if the expression is an object comparison expression. */
    protected Boolean isObjectComparisonExpression;

    public RelationExpression() {
        super();
    }

    /**
     * Test that both of our children are field nodes
     */
    protected boolean allChildrenAreFields() {
        return (this.firstChild.getFields().size() == 1) && (this.secondChild.getFields().size() == 1);

    }

    /**
     * INTERNAL:
     * Modify this individual expression node to use outer joins wherever there are
     * equality operations between two field nodes.
     */
    protected void convertNodeToUseOuterJoin() {
        if ((this.operator.getSelector() == ExpressionOperator.Equal) && allChildrenAreFields()) {
            setOperator(getOperator(ExpressionOperator.EqualOuterJoin));
        }
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Relation";
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying.
     * If the expression in not able to determine if the object conform throw a not supported exception.
     */
    public boolean doesConform(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        if ((this.secondChild.getBuilder().getSession() == null) || (this.firstChild.getBuilder().getSession() == null)) {
            // Parallel selects are not supported in memory.
            throw QueryException.cannotConformExpression();
        }
        
        // Extract the value from the right side.
        //CR 3677 integration of valueHolderPolicy
        Object rightValue = 
            this.secondChild.valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

        // Extract the value from the object.
        //CR 3677 integration of valueHolderPolicy
        Object leftValue = 
            this.firstChild.valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

        // The right value may be a Collection of values from an anyof, or an in.
        if (rightValue instanceof Collection) {
            // Vector may mean anyOf, or an IN.
            // CR#3240862, code for IN was incorrect, and was check for between which is a function not a relation.
            // Must check for IN and NOTIN, currently object comparison is not supported.
            // IN must be handled separately because right is always a vector of values, vector never means anyof.
            if ((this.operator.getSelector() == ExpressionOperator.In) || 
                (this.operator.getSelector() == ExpressionOperator.NotIn)) {
                if (isObjectComparison(session)) {
                    // In object comparisons are not currently supported, in-memory or database.
                    throw QueryException.cannotConformExpression();
                } else {
                    // Left may be single value or anyof vector.
                    if (leftValue instanceof Vector) {
                        return doesAnyOfLeftValuesConform((Vector)leftValue, rightValue, session);
                    } else {
                        return this.operator.doesRelationConform(leftValue, rightValue);
                    }
                }
            }

            // Otherwise right vector means an anyof on right, so must check each value.
            for (Enumeration rightEnum = ((Vector)rightValue).elements(); rightEnum.hasMoreElements(); ) {
                Object tempRight = rightEnum.nextElement();

                // Left may also be an anyof some must check each left with each right.
                if (leftValue instanceof Vector) {
                    // If anyof the left match return true, otherwise keep checking.
                    if (doesAnyOfLeftValuesConform((Vector)leftValue, tempRight, session)) {
                        return true;
                    }
                }
                if (doValuesConform(leftValue, tempRight, session)) {
                    return true;
                }
            }

            // None of the value conform.
            return false;
        }

        // Otherwise the left may also be a vector of values from an anyof.
        if (leftValue instanceof Vector) {
            return doesAnyOfLeftValuesConform((Vector)leftValue, rightValue, session);
        }

        // Otherwise it is a simple value to value comparison, or simple object to object comparison.
        return doValuesConform(leftValue, rightValue, session);
    }

    /**
     * Conform in-memory the collection of left values with the right value for this expression.
     * This is used for anyOf support when the left side is a collection of values.
     */
    protected boolean doesAnyOfLeftValuesConform(Vector leftValues, Object rightValue, AbstractSession session) {
        // Check each left value with the right value.
        for (int index = 0; index < leftValues.size(); index++) {
            Object leftValue = leftValues.get(index);
            if (doValuesConform(leftValue, rightValue, session)) {
                // Return true if any value matches.
                return true;
            }
        }

        // Return false only if none of the values match.
        return false;
    }

    /**
     * Conform in-memory the two values.
     */
    protected boolean doValuesConform(Object leftValue, Object rightValue, AbstractSession session) {
        // Check for object comparison.
        if (isObjectComparison(session)) {
            return doesObjectConform(leftValue, rightValue, session);
        } else {
            return this.operator.doesRelationConform(leftValue, rightValue);
        }
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying across object relationships.
     */
    public boolean doesObjectConform(Object leftValue, Object rightValue, AbstractSession session) {
        if ((leftValue == null) && (rightValue == null)) {
            return performSelector(true);
        }
        if ((leftValue == null) || (rightValue == null)) {
            //both are not null.
            return performSelector(false);
        }

        Class javaClass = leftValue.getClass();
        if (javaClass != rightValue.getClass()) {
            return performSelector(false);
        }

        ClassDescriptor descriptor = session.getDescriptor(javaClass);
        // Currently cannot conform aggregate comparisons in-memory.
        if (descriptor.isAggregateDescriptor()) {
            throw QueryException.cannotConformExpression();
        }
        Object leftPrimaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(leftValue, session);
        Object rightPrimaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(rightValue, session);

        return performSelector(leftPrimaryKey.equals(rightPrimaryKey));

    }

    /**
     * INTERNAL:
     * Extract the values from the expression into the row.
     * Ensure that the query is querying the exact primary key.
     * @param requireExactMatch refers to the primary key extracted gaurenteeing the result,
     * if not exact it is a heuristic and the cache hit will be conformed to the expression after the lookup
     * Return false if not on the primary key.
     */
    @Override
    public boolean extractValues(boolean primaryKeyOnly, boolean requireExactMatch, ClassDescriptor descriptor, AbstractRecord primaryKeyRow, AbstractRecord translationRow) {
        // If an exact match is required then the operator must be equality.
        if (requireExactMatch && (!(this.operator.getSelector() == ExpressionOperator.Equal))) {
            return false;
        }

        // If not an exact match only =, <, <=, >=, >,... are allowed but not IN which has a different type
        if ((!requireExactMatch) && (this.operator.getSelector() == ExpressionOperator.In)) {
            return false;
        }

        DatabaseField field = null;

        Object value = null;
        if (this.secondChild.isConstantExpression()) {
            value = ((ConstantExpression)this.secondChild).getValue();
        } else if (this.secondChild.isParameterExpression() && (translationRow != null)) {
            value = translationRow.get(((ParameterExpression)this.secondChild).getField());
        } else if (this.firstChild.isConstantExpression()) {
            value = ((ConstantExpression)this.firstChild).getValue();
        } else if (this.firstChild.isParameterExpression() && (translationRow != null)) {
            value = translationRow.get(((ParameterExpression)this.firstChild).getField());
        }
        if (value == null) {
            return false;
        }

        // Ensure that the primary key is being queried on.
        if (this.firstChild.isFieldExpression()) {
            FieldExpression child = (FieldExpression)this.firstChild;
            // Only get value for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            field = child.getField();
        } else if (this.firstChild.isQueryKeyExpression()) {
            QueryKeyExpression child = (QueryKeyExpression)this.firstChild;
            // Only get value for the source object.            
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(child.getName());
            if (mapping != null) {
                if (primaryKeyOnly && !mapping.isPrimaryKeyMapping()) {
                    return false;
                }
                // Only support referencing limited number of relationship types.            
                if (mapping.isObjectReferenceMapping() || mapping.isAggregateObjectMapping()) {
                    mapping.writeFromAttributeIntoRow(value, primaryKeyRow, getSession());
                    return true;
                }

                if (!mapping.isAbstractColumnMapping()) {
                    return false;
                }
                field = ((AbstractColumnMapping)mapping).getField();
            } else {
                // Only get field for the source object.
                field = descriptor.getObjectBuilder().getFieldForQueryKeyName(child.getName());
            }
        } else if (this.secondChild.isFieldExpression()) {
            FieldExpression child = (FieldExpression)this.secondChild;
            // Only get field for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            field = child.getField();
        } else if (this.secondChild.isQueryKeyExpression()) {
            QueryKeyExpression child = (QueryKeyExpression)this.secondChild;
            // Only get value for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(child.getName());
            // Only support referencing limited number of relationship types.
            if (mapping != null) {
                if (primaryKeyOnly && !mapping.isPrimaryKeyMapping()) {
                    return false;
                }
                if (mapping.isObjectReferenceMapping() || mapping.isAggregateObjectMapping()) {
                    mapping.writeFromAttributeIntoRow(value, primaryKeyRow, getSession());
                    return true;
                }
                if (!mapping.isAbstractColumnMapping()) {
                    return false;
                }
                field = ((AbstractColumnMapping)mapping).getField();
            } else {
                field = descriptor.getObjectBuilder().getFieldForQueryKeyName(child.getName());
            }
        } else {
            return false;
        }
        if ((field == null) || (primaryKeyOnly && !descriptor.getPrimaryKeyFields().contains(field))) {
            return false;
        }
        primaryKeyRow.put(field, value);
        return true;
    }

    /**
     * INTERNAL:
     * Return if the expression is not a valid primary key expression and add all primary key fields to the set.
     */
    @Override
    public boolean extractFields(boolean requireExactMatch, boolean primaryKey, ClassDescriptor descriptor, List<DatabaseField> searchFields, Set<DatabaseField> foundFields) {
        // If an exact match is required then the operator must be equality.
        if (requireExactMatch && (!(this.operator.getSelector() == ExpressionOperator.Equal))) {
            return false;
        }
        // If not an exact match only =, <, <=, >=, >,... are allowed but not IN which has a different type
        if ((!requireExactMatch) && (this.operator.getSelector() == ExpressionOperator.In)) {
            return false;
        }
        DatabaseField field = null;
        if (!(this.secondChild.isConstantExpression() || this.secondChild.isParameterExpression())
                && !(this.firstChild.isConstantExpression() || (this.firstChild.isParameterExpression()))) {
            return false;
        }
        // Ensure that the primary key is being queried on.
        if (this.firstChild.isFieldExpression()) {
            FieldExpression child = (FieldExpression)this.firstChild;
            // Only get value for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            field = child.getField();
        } else if (this.firstChild.isQueryKeyExpression()) {
            QueryKeyExpression child = (QueryKeyExpression)this.firstChild;
            // Only get value for the source object.            
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(child.getName());
            if (mapping != null) {
                if (primaryKey && !mapping.isPrimaryKeyMapping()) {
                    return false;
                }
                // Only support referencing limited number of relationship types.            
                if (mapping.isObjectReferenceMapping() || mapping.isAggregateObjectMapping()) {
                    for (DatabaseField mappingField : mapping.getFields()) {
                        if (searchFields.contains(mappingField)) {
                            foundFields.add(mappingField);
                        }                        
                    }
                    return true;
                }

                if (!mapping.isAbstractColumnMapping()) {
                    return false;
                }
                field = ((AbstractColumnMapping)mapping).getField();
            } else {
                // Only get field for the source object.
                field = descriptor.getObjectBuilder().getFieldForQueryKeyName(child.getName());
            }
        } else if (this.secondChild.isFieldExpression()) {
            FieldExpression child = (FieldExpression)this.secondChild;
            // Only get field for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            field = child.getField();
        } else if (this.secondChild.isQueryKeyExpression()) {
            QueryKeyExpression child = (QueryKeyExpression)this.secondChild;
            // Only get value for the source object.
            if (!child.getBaseExpression().isExpressionBuilder()) {
                return false;
            }
            DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(child.getName());
            // Only support referencing limited number of relationship types.
            if (mapping != null) {
                if (!mapping.isPrimaryKeyMapping()) {
                    return false;
                }
                if (mapping.isObjectReferenceMapping() || mapping.isAggregateObjectMapping()) {
                    for (DatabaseField mappingField : mapping.getFields()) {
                        if (searchFields.contains(mappingField)) {
                            foundFields.add(mappingField);
                        }                        
                    }
                    return true;
                }
                if (!mapping.isAbstractColumnMapping()) {
                    return false;
                }
                field = ((AbstractColumnMapping)mapping).getField();
            } else {
                field = descriptor.getObjectBuilder().getFieldForQueryKeyName(child.getName());
            }
        } else {
            return false;
        }
        if ((field == null) || (!searchFields.contains(field))) {
            return false;
        }
        foundFields.add(field);
        return true;
    }
    
    /**
     * Check if the expression is an equal null expression, these must be handle in a special way in SQL.
     */
    public boolean isEqualNull(ExpressionSQLPrinter printer) {
        if (isObjectComparison(printer.getSession())) {
            return false;
        } else if (this.operator.getSelector() != ExpressionOperator.Equal) {
            return false;
        } else if (this.secondChild.isConstantExpression() && (((ConstantExpression)this.secondChild).getValue() == null)) {
            return true;
        } else if (this.secondChild.isParameterExpression() && (printer.getTranslationRow() != null) && 
            (((ParameterExpression)this.secondChild).getValue(printer.getTranslationRow(), printer.getSession()) == null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the expression is an equal null expression, these must be handle in a special way in SQL.
     */
    public boolean isNotEqualNull(ExpressionSQLPrinter printer) {
        if (isObjectComparison(printer.getSession())) {
            return false;
        } else if (this.operator.getSelector() != ExpressionOperator.NotEqual) {
            return false;
        } else if (this.secondChild.isConstantExpression() && (((ConstantExpression)this.secondChild).getValue() == null)) {
            return true;
        } else if (this.secondChild.isParameterExpression() && (printer.getTranslationRow() != null) && 
            (((ParameterExpression)this.secondChild).getValue(printer.getTranslationRow(), printer.getSession()) == null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Return if the represents an object comparison.
     */
    protected boolean isObjectComparison(AbstractSession session) {
        if (this.isObjectComparisonExpression == null) {
            // PERF: direct-access.
            // Base must have a session set in its builder to call getMapping, isAttribute
            if (this.firstChild.getBuilder().getSession() == null) {
                this.firstChild.getBuilder().setSession(session.getRootSession(null));
            }
            if (this.secondChild.getBuilder().getSession() == null) {
                this.secondChild.getBuilder().setSession(session.getRootSession(null));
            }
            if ((!this.firstChild.isObjectExpression()) || ((ObjectExpression)this.firstChild).isAttribute()) {
                if ((this.secondChild.isObjectExpression()) && !((ObjectExpression)this.secondChild).isAttribute()) {
                    DatabaseMapping mapping = ((ObjectExpression)this.secondChild).getMapping();
                    if ((mapping != null) && (mapping.isDirectCollectionMapping()) && !(this.secondChild.isMapEntryExpression())) {
                        this.isObjectComparisonExpression = Boolean.FALSE;
                    } else {
                        this.isObjectComparisonExpression = Boolean.valueOf(this.firstChild.isObjectExpression()
                                || this.firstChild.isValueExpression()
                                || this.firstChild.isSubSelectExpression()
                                || (this.firstChild.isFunctionExpression() && ((FunctionExpression)this.firstChild).operator.isAnyOrAll()));
                    }
                } else {
                    this.isObjectComparisonExpression = Boolean.FALSE;
                }
            } else {
                DatabaseMapping mapping = ((ObjectExpression)this.firstChild).getMapping();
                if ((mapping != null) && (mapping.isDirectCollectionMapping()) && !(this.firstChild.isMapEntryExpression())) {
                    this.isObjectComparisonExpression = Boolean.FALSE;
                } else {
                    this.isObjectComparisonExpression = Boolean.valueOf(this.secondChild.isObjectExpression()
                            || this.secondChild.isValueExpression()
                            || this.secondChild.isSubSelectExpression()
                            || (this.secondChild.isFunctionExpression() && ((FunctionExpression)this.secondChild).operator.isAnyOrAll()));
                }
            }
        }
        return this.isObjectComparisonExpression.booleanValue();
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationExpression() {
        return true;
    }

    /**
     * PERF: Optimize out unnecessary joins.
     * Check for relation based on foreign keys, i.e. emp.address.id = :id, and avoid join.
     * @return null if cannot be optimized, otherwise the optimized normalized expression.
     */
    protected Expression checkForeignKeyJoinOptimization(Expression first, Expression second, ExpressionNormalizer normalizer) {
        if (first.isQueryKeyExpression()
                && (((QueryKeyExpression)first).getBaseExpression() != null)
                && ((QueryKeyExpression)first).getBaseExpression().isQueryKeyExpression()) {
            // Do not optimize for subselect if it is using parent builder, and needs to clone.
            if(normalizer.getStatement().isSubSelect() && normalizer.getStatement().getParentStatement().getBuilder().equals(first.getBuilder())) {
                return null;
            }
            QueryKeyExpression mappingExpression = (QueryKeyExpression)((QueryKeyExpression)first).getBaseExpression();
            if ((mappingExpression.getBaseExpression() != null)
                    && mappingExpression.getBaseExpression().isObjectExpression()
                    && (!mappingExpression.shouldUseOuterJoin())) {
                // Must ensure it has been normalized first.
                mappingExpression.getBaseExpression().normalize(normalizer);
                DatabaseMapping mapping = mappingExpression.getMapping();
                if ((mapping != null) && mapping.isOneToOneMapping()
                        && (!((OneToOneMapping)mapping).hasCustomSelectionQuery())
                        && ((OneToOneMapping)mapping).isForeignKeyRelationship()
                        && (second.isConstantExpression() || second.isParameterExpression())) {
                    DatabaseField targetField = ((QueryKeyExpression)first).getField();
                    DatabaseField sourceField = ((OneToOneMapping)mapping).getTargetToSourceKeyFields().get(targetField);
                    if (sourceField != null) {
                        Expression optimizedExpression = this.operator.expressionFor(mappingExpression.getBaseExpression().getField(sourceField), second);
                        // Ensure the base still applies the correct conversion.
                        second.setLocalBase(first);
                        return optimizedExpression.normalize(normalizer);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Check for object comparison as this requires for the expression to be replaced by the object comparison.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        // PERF: Optimize out unnecessary joins.
        Expression optimizedExpression = checkForeignKeyJoinOptimization(this.firstChild, this.secondChild, normalizer);
        if (optimizedExpression == null) {
            optimizedExpression = checkForeignKeyJoinOptimization(this.secondChild, this.firstChild, normalizer);
        }
        if (optimizedExpression != null) {
            return optimizedExpression;
        }
        if (!isObjectComparison(normalizer.getSession())) {
            return super.normalize(normalizer);
        } else {
            //bug # 2956674
            //validation is moved into normalize to ensure that expressions are valid before we attempt to work with them
            // super.normalize will call validateNode as well.
            validateNode();
        }
        if ((this.operator.getSelector() != ExpressionOperator.Equal) && 
                (this.operator.getSelector() != ExpressionOperator.NotEqual) && 
                (this.operator.getSelector() != ExpressionOperator.In) && 
                (this.operator.getSelector() != ExpressionOperator.NotIn)) {
            throw QueryException.invalidOperatorForObjectComparison(this);
        }
        
        // Check for IN with objects, "object IN :objects", "objects IN (:object1, :object2 ...)", "object IN aList"
        if ((this.operator.getSelector() == ExpressionOperator.In) || (this.operator.getSelector() == ExpressionOperator.NotIn)) {
            // Switch object comparison to compare on primary key.
            Expression left = this.firstChild;
            if (!left.isObjectExpression()) {
                throw QueryException.invalidExpression(this);
            }
            // Check if the left is for a 1-1 mapping, then optimize to compare on foreign key to avoid join.
            DatabaseMapping mapping = null;
            if (left.isQueryKeyExpression()) {
                ((ObjectExpression)left).getBaseExpression().normalize(normalizer);
                mapping = ((ObjectExpression)left).getMapping();
            }
            ClassDescriptor descriptor = null;
            List<DatabaseField> sourceFields = null;
            List<DatabaseField> targetFields = null;
            if ((mapping != null) && mapping.isOneToOneMapping()
                    && (!((OneToOneMapping)mapping).hasRelationTableMechanism())
                    && (!((OneToOneMapping)mapping).hasCustomSelectionQuery())) {
                left = ((ObjectExpression)left).getBaseExpression();
                descriptor = mapping.getReferenceDescriptor();
                left = left.normalize(normalizer);
                Map<DatabaseField, DatabaseField> targetToSourceKeyFields = ((OneToOneMapping)mapping).getTargetToSourceKeyFields();
                sourceFields = new ArrayList(targetToSourceKeyFields.size());
                targetFields = new ArrayList(targetToSourceKeyFields.size());
                for (Map.Entry<DatabaseField, DatabaseField> entry : targetToSourceKeyFields.entrySet()) {
                    sourceFields.add(entry.getValue());
                    targetFields.add(entry.getKey());
                }
            } else {
                mapping = null;
                left = left.normalize(normalizer);
                descriptor = ((ObjectExpression)left).getDescriptor();
                sourceFields = descriptor.getPrimaryKeyFields();
                targetFields = sourceFields;
            }
            boolean composite = sourceFields.size() > 1;
            DatabaseField sourceField = sourceFields.get(0);
            DatabaseField targetField = targetFields.get(0);
            Expression newLeft = null;
            if (composite) {
                // For composite ids an array comparison is used, this only works on some databases.
                List fieldExpressions = new ArrayList();
                for (DatabaseField field : sourceFields) {
                    fieldExpressions.add(left.getField(field));
                }
                newLeft = getBuilder().value(sourceFields);
            } else {
               newLeft = left.getField(sourceField);
            }
            setFirstChild(newLeft);
            Expression right = this.secondChild;
            if (right.isConstantExpression()) {
                // Check for a constant with a List of objects, need to collect the ids (also allow a list of ids).
                ConstantExpression constant = (ConstantExpression)right;
                if (constant.getValue() instanceof Collection) {
                    Collection objects = (Collection)constant.getValue();
                    List newObjects = new ArrayList(objects.size());
                    for (Object object : objects) {
                        if (object instanceof Expression) {
                            if (composite) {
                                // For composite ids an array comparison is used, this only works on some databases.
                                List values = new ArrayList();
                                for (DatabaseField field : targetFields) {
                                    values.add(((Expression)object).getField(field));
                                }
                                object = getBuilder().value(values);
                            } else {
                                object = ((Expression)object).getField(targetField);
                            }
                        } else if (descriptor.getJavaClass().isInstance(object)) {
                            if (composite) {
                                // For composite ids an array comparison is used, this only works on some databases.
                                List values = new ArrayList();
                                for (DatabaseField field : targetFields) {
                                    values.add(descriptor.getObjectBuilder().extractValueFromObjectForField(object, field, normalizer.getSession()));
                                }
                                object = getBuilder().value(values);
                            } else {
                                object = descriptor.getObjectBuilder().extractValueFromObjectForField(object, targetField, normalizer.getSession());
                            }
                        } else {
                            // Assume it is an id, so leave it.
                        }
                        newObjects.add(object);
                    }
                    constant.setValue(newObjects);
                } else {
                    throw QueryException.invalidExpression(this);
                }
            } else if (right.isParameterExpression()) {
                // Parameters must be handled when the call is executed.
            } else {
                throw QueryException.invalidExpression(this);
            }
            return super.normalize(normalizer);
        }
        ObjectExpression first = null;
        Expression second = null;
        // One side must be an object expression.
        if (this.firstChild.isObjectExpression()) {
            first = (ObjectExpression)this.firstChild;
            second = this.secondChild;
        } else {
            first = (ObjectExpression)this.secondChild;
            second = this.firstChild;            
        }
        
        // Check for sub-selects, "object = ALL(Select object...) or ANY(Select object...), or "object = (Select object..)"
        if (second.isFunctionExpression()) {
            FunctionExpression funcExp = (FunctionExpression)second;
            if (funcExp.operator.isAnyOrAll()) {
                SubSelectExpression subSelectExp = (SubSelectExpression)funcExp.getChildren().get(1);
                ReportQuery subQuery = subSelectExp.getSubQuery();
                
                // some db (derby) require that in EXIST(SELECT...) subquery returns a single column
                subQuery.getItems().clear();
                subQuery.addItem("one", new ConstantExpression(Integer.valueOf(1), subQuery.getExpressionBuilder()));
                
                Expression subSelectCriteria = subQuery.getSelectionCriteria();
                ExpressionBuilder subBuilder = subQuery.getExpressionBuilder();

                ExpressionBuilder builder = first.getBuilder();

                Expression newExp;
                if (funcExp.operator.isAny()) {
                    // Any or Some
                    if (this.operator.getSelector() == ExpressionOperator.Equal) {
                        subSelectCriteria = subBuilder.equal(first).and(subSelectCriteria);
                    } else {
                        subSelectCriteria = subBuilder.notEqual(first).and(subSelectCriteria);
                    }
                    subQuery.setSelectionCriteria(subSelectCriteria);
                    newExp = builder.exists(subQuery);
                } else {
                    // All
                    if (this.operator.getSelector() == ExpressionOperator.Equal) {
                        subSelectCriteria = subBuilder.notEqual(first).and(subSelectCriteria);
                    } else {
                        subSelectCriteria = subBuilder.equal(first).and(subSelectCriteria);
                    }
                    subQuery.setSelectionCriteria(subSelectCriteria);
                    newExp = builder.notExists(subQuery);
                }
                return newExp.normalize(normalizer);
            }
        } else if (second.isSubSelectExpression()) {
            SubSelectExpression subSelectExp = (SubSelectExpression)second;
            ReportQuery subQuery = subSelectExp.getSubQuery();
            
            // some db (derby) require that in EXIST(SELECT...) subquery returns a single column
            subQuery.getItems().clear();
            subQuery.addItem("one", new ConstantExpression(Integer.valueOf(1), subQuery.getExpressionBuilder()));
            
            Expression subSelectCriteria = subQuery.getSelectionCriteria();
            ExpressionBuilder subBuilder = subQuery.getExpressionBuilder();

            ExpressionBuilder builder = first.getBuilder();

            Expression newExp;
            // Any or Some
            if (this.operator.getSelector() == ExpressionOperator.Equal) {
                subSelectCriteria = subBuilder.equal(first).and(subSelectCriteria);
            } else {
                subSelectCriteria = subBuilder.notEqual(first).and(subSelectCriteria);
            }
            subQuery.setSelectionCriteria(subSelectCriteria);
            newExp = builder.exists(subQuery);
            return newExp.normalize(normalizer);
        }
        
        // This can either be comparison to another object, null or another expression reference.
        // Object comparisons can be done on other object builders, 1:1 or 1:m m:m mappings,
        // 1:m/m:m must twist the primary key expression,
        // 1:1 must not join into the target but become the foreign key expression.
        // The value may be a constant or another expression.
        Expression foreignKeyJoin = null;

        // OPTIMIZATION 1: IDENTITY for CR#2456 / bug 2778339
        // Most exists subqueries have something like projBuilder.equal(empBuilder.anyOf("projects"))
        // to correlate the subquery to the enclosing query.
        // TopLink can produce SQL with one less join by not mapping projBuilder and 
        // anyOf("projects") to separate tables and equating them, but by treating
        // them as one and the same thing: as identical.
        // This trick can be pulled off by giving both the same TableAliasLookup,
        // but needs to be done very conservatively.
        // the equal() will be replaced directly with the mappingCriteria() of the anyOf("projects")
        // Example.  emp.equal(emp.get("manager")) will now produce this SQL:
        // SELECT ... FROM EMPLOYEE t0 WHERE (t0.EMP_ID = t0.MANAGER_ID) not:
        // SELECT ... FROM EMPLOYEE t0, EMPLOYEE t1 WHERE ((t0.EMP_ID = t1.EMP_ID) 
        //                                        AND (t0.MANAGER_ID = t1.EMP_ID))
        if // If setting two query keys to equal the user probably intends a proper join.
            //.equal(anyOf() or get())
            (first.isExpressionBuilder() && second.isQueryKeyExpression()
                    &&  (!((QueryKeyExpression)second).hasDerivedExpressions()) // The right side is not used for anything else.
                    && normalizer.getSession().getPlatform().shouldPrintInnerJoinInWhereClause()) {
            first = (ExpressionBuilder)first.normalize(normalizer);

            // If FK joins go in the WHERE clause, want to get hold of it and
            // not put it in normalizer.additionalExpressions.
            List<Expression> foreignKeyJoinPointer = new ArrayList(1);
            QueryKeyExpression queryKey = (QueryKeyExpression)second;

            // If inside an OR the foreign key join must be on both sides.
            if (queryKey.hasBeenNormalized()) {
                queryKey.setHasBeenNormalized(false);
            }
            queryKey = (QueryKeyExpression)queryKey.normalize(normalizer, first, foreignKeyJoinPointer);
            if (!foreignKeyJoinPointer.isEmpty()) {
                foreignKeyJoin = foreignKeyJoinPointer.get(0);
                // Will make left and right identical in the SQL.
                if (first.getTableAliases() == null) {
                    TableAliasLookup tableAliases = new TableAliasLookup();
                    first.setTableAliases(tableAliases);
                    queryKey.setTableAliases(tableAliases);
                } else {
                    queryKey.setTableAliases(first.getTableAliases());
                }
            }
        }
        // OPTIMIZATION 2: for 1-1 mappings and get(...).equal(null)
        // Imagine you had addr1 = emp.get("address"); then addr1.equal(addr2);
        // One could go (addr1.ADDRESS_ID = addr2.ADDRESS_ID) and (emp.ADDR_ID = addr1.ADDRESS_ID) (foreign key join).
        // The optimization is to drop addr1 and instead have: (emp.ADDR_ID = addr2.ADDRESS_ID).
        // Since emp can have only 1 address (OneToOne) the addr1.equal(addr2) is
        // implicit.  This way if addr1 is used only for the comparison it can
        // be optimized out.
        // Also if addr2 were NULL there must be no join, just (emp.ADDR_ID = NULL)
        // For bug 3105559 handle AggregateObject case (emp.get("period").equal(period2)
        // which always falls into this case.
        else if // For bug 2718460, some QueryKeyExpressions have a query key but no mapping.
            // An example is the "back-ref" query key for batch reads.  Must not
            // attempt the optimization for these.
            (!first.isExpressionBuilder() && !((QueryKeyExpression)first).shouldQueryToManyRelationship() && 
             (((QueryKeyExpression)first).getMapping() != null)) {
            // Normalize firstChild's base only, as firstChild will be optimized out.
            if (first.getBaseExpression() != null) {
                first.setBaseExpression(first.getBaseExpression().normalize(normalizer));
            }

            if (second.isConstantExpression()) {
                Object targetObject = ((ConstantExpression)second).getValue();
                foreignKeyJoin = first.getMapping().buildObjectJoinExpression(first, targetObject, getSession());
            } else if (second.isObjectExpression() || second.isParameterExpression()) {
                foreignKeyJoin = first.getMapping().buildObjectJoinExpression(first, second, getSession());
            } else {
                throw QueryException.invalidUseOfToManyQueryKeyInExpression(this);
            }
        }

        // DEFAULT:  Left and right are separate entities, and the
        // equal() will be replaced with a comparison by primary key.
        if (foreignKeyJoin == null) {
            first = (ObjectExpression)first.normalize(normalizer);

            // A ConstantExpression stores a selection object.  Compare the primary
            // keys of the first expression and the selection object.
            if (second.isConstantExpression()) {
                Object value = ((ConstantExpression)second).getValue();
                Expression keyExpression = 
                    first.getDescriptor().getObjectBuilder().buildPrimaryKeyExpressionFromObject(value, getSession());

                foreignKeyJoin = first.twist(keyExpression, first);

                // Each expression will represent a separate table, so compare the primary
                // keys of the first and second expressions.
            } else if (second.isObjectExpression() || second.isParameterExpression()) {
                foreignKeyJoin = 
                        first.twist(first.getDescriptor().getObjectBuilder().getPrimaryKeyExpression(), second);
            } else {
                throw QueryException.invalidUseOfToManyQueryKeyInExpression(this);
            }
        }
        if (this.operator.getSelector() == ExpressionOperator.NotEqual) {
            foreignKeyJoin = foreignKeyJoin.not();
        }

        return foreignKeyJoin.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * Check if the object conforms to the expression in memory.
     * This is used for in-memory querying across object relationships.
     */
    public boolean performSelector(boolean areValuesEqual) {
        if (this.operator.getSelector() == ExpressionOperator.Equal) {
            return areValuesEqual;
        } else if (this.operator.getSelector() == ExpressionOperator.NotEqual) {
            return !areValuesEqual;
        } else {
            throw QueryException.cannotConformExpression();
        }
    }

    /**
     * INTERNAL:
     * Print SQL
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        // If both sides are parameters, some databases don't allow binding.
        if (printer.getPlatform().isDynamicSQLRequiredForFunctions()
                && ((this.firstChild.isParameterExpression() || this.firstChild.isConstantExpression())
                        && (this.secondChild.isParameterExpression() || this.secondChild.isConstantExpression()))) {
            printer.getCall().setUsesBinding(false);
        }
        if (isEqualNull(printer)) {
            this.firstChild.isNull().printSQL(printer);
        } else if (isNotEqualNull(printer)) {
            this.firstChild.notNull().printSQL(printer);
        } else {
            super.printSQL(printer);
        }
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        ExpressionOperator realOperator = getPlatformOperator(printer.getPlatform());
        Expression tempFirstChild = this.firstChild;
        Expression tempSecondChild = this.secondChild;
        realOperator.printJavaDuo(tempFirstChild, tempSecondChild, printer);
    }

    /**
     * INTERNAL:
     * Print SQL without adding parentheses (for DB2 outer joins).
     */
    public void printSQLNoParens(ExpressionSQLPrinter printer) {
        ExpressionOperator realOperator = getPlatformOperator(printer.getPlatform());
        realOperator.printDuo(this.firstChild, this.secondChild, printer);
    }

    /**
     * Do any required validation for this node. Throw an exception if it's incorrect.
     */
    public void validateNode() {
        if (this.firstChild.isTableExpression()) {
            throw QueryException.cannotCompareTablesInExpression(((TableExpression)this.firstChild).getTable());
        }
        if (this.secondChild.isTableExpression()) {
            throw QueryException.cannotCompareTablesInExpression(((TableExpression)this.secondChild).getTable());
        }
    }
}
