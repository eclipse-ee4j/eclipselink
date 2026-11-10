/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaStatement;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.PluralExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.DataExpression;
import org.eclipse.persistence.internal.expressions.ExpressionNormalizer;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.queries.ReportQuery;

/** Collection operations retain the owner separately from the element join. */
public class PluralExpressionImpl<C, E> extends ExpressionImpl<C> implements PluralExpression<C, E> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final FromImpl<?, ?> owner;
    private final String attributeName;
    private final Class<E> elementType;

    public PluralExpressionImpl(Metamodel metamodel, Class<? extends C> javaType,
            org.eclipse.persistence.expressions.Expression expressionNode, FromImpl<?, ?> owner,
            String attributeName, Class<E> elementType) {
        super(metamodel, javaType, expressionNode);
        this.owner = owner;
        this.attributeName = attributeName;
        this.elementType = elementType;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
        owner.findRootAndParameters(query);
    }

    @Override
    public Predicate isEmpty() {
        return existence(null).not();
    }

    @Override
    public Predicate isNotEmpty() {
        return existence(null);
    }

    @Override
    public NumericExpression<Integer> size() {
        ReportQuery query = collectionQuery(null);
        ConstantExpression one = new ConstantExpression(1, query.getExpressionBuilder());
        one.setCanBind(false);
        query.addCount("collectionCount", one, Integer.class);
        FunctionExpression node = (FunctionExpression) new CollectionSubqueryExpression(query, owner.getCurrentNode()).getFunction("");
        node.setResultType(Integer.class);
        ExpressionImpl<Integer> count = new FunctionExpressionImpl<>(metamodel, Integer.class,
                node, List.of(this), "collectionCardinality");
        return new NumericExpressionImpl<>(count);
    }

    @Override
    public Predicate contains(Expression<? extends E> elem) {
        Objects.requireNonNull(elem, "elem");
        CriteriaBuilderImpl builder = new CriteriaBuilderImpl(metamodel);
        // Empty takes precedence over null. A typed null result preserves UNKNOWN
        // under selection, NOT and CASE rather than collapsing it through EXISTS.
        Expression<Integer> membership = builder.selectCase(Integer.class)
                .when(isEmpty(), 0)
                .when(memberIsNull(elem, builder), (Integer) null)
                .when(existence(elem), 1)
                .otherwise(0);
        return builder.equal(membership, 1);
    }

    @Override
    public Predicate contains(E elem) {
        return contains(createLiteral(Objects.requireNonNull(elem, "elem"), metamodel, elementType));
    }

    @Override
    public Predicate notContains(Expression<? extends E> elem) {
        return contains(elem).not();
    }

    @Override
    public Predicate notContains(E elem) {
        return contains(elem).not();
    }

    private Predicate memberIsNull(Expression<? extends E> member, CriteriaBuilderImpl builder) {
        org.eclipse.persistence.expressions.Expression node = ((InternalSelection) member).getCurrentNode();
        if (member instanceof SubQueryImpl<?> subquery && isEntityElement()) {
            org.eclipse.persistence.expressions.Expression nullIdentity = null;
            for (DatabaseField field : entityIdentityFields()) {
                org.eclipse.persistence.expressions.Expression isNull = identityProjection(subquery, field).isNull();
                nullIdentity = nullIdentity == null ? isNull : nullIdentity.and(isNull);
            }
            return new CompoundExpressionImpl(metamodel, nullIdentity, List.of(member), "entityIdentityNull");
        }
        if (node instanceof ConstantExpression constant) {
            return builder.equal(builder.literal(0), builder.literal(constant.getValue() == null ? 0 : 1));
        }
        if ((node instanceof ParameterExpression || node.isExpressionBuilder()) && metamodel != null
                && ((MetamodelImpl) metamodel).getType(elementType).getPersistenceType() == PersistenceType.ENTITY) {
            org.eclipse.persistence.expressions.Expression occurrence = node instanceof ParameterExpression ? node.clone() : node;
            ManagedTypeImpl<?> type = (ManagedTypeImpl<?>) metamodel.entity(elementType);
            org.eclipse.persistence.expressions.Expression nullIdentity = null;
            for (DatabaseField field : type.getDescriptor().getPrimaryKeyFields()) {
                org.eclipse.persistence.expressions.Expression isNull = occurrence.getField(field).isNull();
                nullIdentity = nullIdentity == null ? isNull : nullIdentity.and(isNull);
            }
            return new CompoundExpressionImpl(metamodel, nullIdentity, List.of(member), "entityIdentityNull");
        }
        return builder.isNull(member);
    }

    private Predicate existence(Expression<? extends E> member) {
        ReportQuery query = collectionQuery(member);
        query.setShouldRetrieveFirstPrimaryKey(true);
        List<Expression<?>> children = member == null ? List.of(this) : List.of(this, member);
        org.eclipse.persistence.expressions.Expression exists = new CollectionSubqueryExpression(query, owner.getCurrentNode())
                .getFunction(ExpressionOperator.Exists);
        return new CompoundExpressionImpl(metamodel, exists, children, "collectionExists");
    }

    private ReportQuery collectionQuery(Expression<? extends E> member) {
        FromImpl<?, ?> entityOwner = owner;
        List<String> embeddedPath = new ArrayList<>();
        while (entityOwner.managedType != null && entityOwner.managedType.getPersistenceType() == PersistenceType.EMBEDDABLE) {
            embeddedPath.addFirst(((Attribute<?, ?>) entityOwner.modelArtifact).getName());
            entityOwner = (FromImpl<?, ?>) entityOwner.getParentPath();
        }
        ExpressionBuilder inner = new ExpressionBuilder(entityOwner.getJavaType());
        ReportQuery query = new ReportQuery(entityOwner.getJavaType(), inner);
        org.eclipse.persistence.expressions.Expression collectionOwner = inner;
        for (String attribute : embeddedPath) {
            collectionOwner = collectionOwner.get(attribute);
        }
        org.eclipse.persistence.expressions.Expression element = collectionOwner.anyOf(attributeName);
        query.addNonFetchJoinedAttribute(element);
        org.eclipse.persistence.expressions.Expression criteria = inner.equal(entityOwner.getCurrentNode());
        if (member != null) {
            if (member instanceof SubQueryImpl<?> subquery && isEntityElement()) {
                for (DatabaseField field : entityIdentityFields()) {
                    criteria = criteria.and(element.getField(field).equal(identityProjection(subquery, field)));
                }
                query.setSelectionCriteria(criteria);
                return query;
            }
            org.eclipse.persistence.expressions.Expression operand = ((InternalSelection) member).getCurrentNode();
            // Equality supplies the collection mapping's conversion context to a
            // value occurrence without rebasing the caller's reusable operand.
            if (operand.isValueExpression()) {
                operand = operand.clone();
            }
            criteria = criteria.and(element.equal(operand));
        }
        query.setSelectionCriteria(criteria);
        return query;
    }

    private boolean isEntityElement() {
        return metamodel != null && ((MetamodelImpl) metamodel).getType(elementType).getPersistenceType() == PersistenceType.ENTITY;
    }

    private List<DatabaseField> entityIdentityFields() {
        return ((ManagedTypeImpl<?>) metamodel.entity(elementType)).getDescriptor().getPrimaryKeyFields();
    }

    private org.eclipse.persistence.expressions.Expression identityProjection(SubQueryImpl<?> subquery, DatabaseField field) {
        return new EntityIdentityExpression(subquery, field, owner.getCurrentNode());
    }

    /** Normalize the real owner in the outer statement before deferring a collection operation. */
    private static final class CollectionSubqueryExpression extends SubSelectExpression {
        @Serial
        private static final long serialVersionUID = 1L;

        private CollectionSubqueryExpression(ReportQuery query, org.eclipse.persistence.expressions.Expression owner) {
            super(query, owner);
        }

        @Override
        public org.eclipse.persistence.expressions.Expression normalize(ExpressionNormalizer normalizer) {
            if (!hasBeenNormalized) {
                setBaseExpression(getBaseExpression().normalize(normalizer));
            }
            return super.normalize(normalizer);
        }
    }

    /** Materialize each scalar identity occurrence when the completed Criteria graph is copied. */
    private static final class EntityIdentityExpression extends SubSelectExpression {
        @Serial
        private static final long serialVersionUID = 1L;

        private SubQueryImpl<?> source;
        private final DatabaseField identityField;

        private EntityIdentityExpression(SubQueryImpl<?> source, DatabaseField identityField,
                org.eclipse.persistence.expressions.Expression owner) {
            super(source.subQuery, owner);
            this.source = source;
            this.identityField = identityField;
        }

        @Override
        protected void postCopyIn(Map<org.eclipse.persistence.expressions.Expression,
                org.eclipse.persistence.expressions.Expression> alreadyDone) {
            if (source == null) {
                super.postCopyIn(alreadyDone);
                return;
            }
            ReportQuery projection = (ReportQuery) source.subQuery.clone();
            projection.getItems().clear();
            projection.setShouldRetrievePrimaryKeys(false);
            projection.addItem("memberIdentity", new FieldExpression(identityField, source.selection.getCurrentNode()));
            setSubQuery(projection);

            Set<ExpressionBuilder> enclosingBuilders = Collections.newSetFromMap(new IdentityHashMap<>());
            CommonAbstractCriteria containing = source.getContainingQuery();
            while (containing != null) {
                if (containing instanceof AbstractQuery<?> query) {
                    query.getRoots().forEach(root -> enclosingBuilders.add(((InternalSelection) root).getCurrentNode().getBuilder()));
                } else if (containing instanceof CriteriaStatement<?> statement && statement.getRoot() != null) {
                    enclosingBuilders.add(((InternalSelection) statement.getRoot()).getCurrentNode().getBuilder());
                }
                containing = containing instanceof SubQueryImpl<?> subquery ? subquery.getContainingQuery() : null;
            }
            // Each occurrence owns its local roots. Only actual enclosing paths are
            // resolved through the completed query's map, including their ON state.
            Map<org.eclipse.persistence.expressions.Expression, org.eclipse.persistence.expressions.Expression> occurrenceCopies = new IdentityHashMap<>() {
                @Override
                public org.eclipse.persistence.expressions.Expression get(Object key) {
                    if (key instanceof DataExpression expression && enclosingBuilders.contains(expression.getBuilder())) {
                        return expression.copiedVersionFrom(alreadyDone);
                    }
                    return super.get(key);
                }
            };
            super.postCopyIn(occurrenceCopies);
            // The query snapshot discovers active original handles from these keys;
            // each occurrence still retains its own parameter conversion context.
            occurrenceCopies.forEach((original, copy) -> {
                if (original instanceof ParameterExpression) {
                    alreadyDone.putIfAbsent(original, copy);
                }
            });
            source = null;
        }
    }

}
