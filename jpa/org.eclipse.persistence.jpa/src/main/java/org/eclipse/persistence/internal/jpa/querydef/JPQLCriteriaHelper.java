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

import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.queries.ExpressionQueryMechanism;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/** Imports the provider's parsed JPQL expression model into mutable criteria. */
final class JPQLCriteriaHelper {
    private JPQLCriteriaHelper() {
    }

    static DatabaseQuery parse(CriteriaBuilderImpl builder, String jpql) {
        var session = ((MetamodelImpl) builder.metamodel).getSession();
        return EJBQueryImpl.buildEJBQLDatabaseQuery("criteria", Objects.requireNonNull(jpql),
                session, null, null, session.getLoader());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> CriteriaQuery<T> select(CriteriaBuilderImpl builder, Class<T> type,
            DatabaseQuery source, boolean replaceSelection) {
        if (!(source instanceof ObjectLevelReadQuery)) {
            throw new IllegalArgumentException("A criteria SELECT requires a JPQL SELECT query");
        }
        ReadAllQuery seed;
        if (source instanceof ReadAllQuery readAll) {
            seed = (ReadAllQuery) readAll.clone();
        } else {
            seed = new ReadAllQuery();
            seed.copyFromQuery(source);
        }
        if (type == null) {
            type = (Class<T>) resultType(builder, seed);
        }
        ImportedSelect<T> result = new ImportedSelect<>(builder, type, seed);
        RootImpl root = root(builder, source);
        result.roots.add(root);
        result.baseExpression = root.getCurrentNode();
        result.where = predicate(builder, source.getSelectionCriteria());
        result.distinct = seed.getDistinctState() == ObjectLevelReadQuery.USE_DISTINCT;
        result.orderBy = new ArrayList<>();
        for (var ordering : seed.getOrderByExpressions()) {
            var expression = ordering;
            Nulls nulls = Nulls.NONE;
            boolean ascending = true;
            while (expression instanceof FunctionExpression function) {
                int selector = function.getOperator().getSelector();
                if (selector == ExpressionOperator.NullsFirst) {
                    nulls = Nulls.FIRST;
                } else if (selector == ExpressionOperator.NullsLast) {
                    nulls = Nulls.LAST;
                } else if (selector == ExpressionOperator.Ascending || selector == ExpressionOperator.Descending) {
                    ascending = selector == ExpressionOperator.Ascending;
                } else {
                    break;
                }
                expression = function.getBaseExpression();
            }
            result.orderBy.add(new OrderImpl(new ExpressionImpl<>(builder.metamodel, Object.class, expression), ascending, nulls));
        }
        if (seed instanceof ReportQuery report) {
            result.groupBy = new ArrayList<>();
            for (var grouping : report.getGroupByExpressions()) {
                result.groupBy.add(new ExpressionImpl<>(builder.metamodel, Object.class, grouping));
            }
            result.havingClause = predicate(builder, report.getHavingExpression());
            if (!replaceSelection) {
                List<Selection<?>> selections = new ArrayList<>();
                for (ReportItem item : report.getItems()) {
                    selections.add(selection(builder, report, item));
                }
                result.selection = (SelectionImpl) (selections.size() == 1 ? selections.get(0)
                        : new CompoundSelectionImpl<>(type, selections.toArray(new Selection<?>[0])));
            }
        } else if (!replaceSelection) {
            if (!type.isAssignableFrom(source.getReferenceClass())) {
                throw new IllegalArgumentException("JPQL selection is incompatible with the requested result type");
            }
            result.selection = root;
        }
        result.importedSelection = result.selection;
        result.selectionRequired = replaceSelection;
        parameters(result, source);
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> CriteriaUpdate<T> update(CriteriaBuilderImpl builder, Class<T> type, DatabaseQuery source) {
        if (!(source instanceof UpdateAllQuery update) || (type != null && type != source.getReferenceClass())) {
            throw new IllegalArgumentException("A criteria UPDATE requires the same JPQL target entity type");
        }
        CriteriaUpdateImpl<T> result = new CriteriaUpdateImpl<>(builder.metamodel, builder,
                type == null ? (Class<T>) source.getReferenceClass() : type) {
            @Override
            protected DatabaseQuery getDatabaseQuery() {
                return executable(super.getDatabaseQuery());
            }
        };
        result.query = new UpdateAllQuery(source.getReferenceClass(), update.getExpressionBuilder());
        result.query.setShouldDeferExecutionInUOW(false);
        update.getUpdateClauses().forEach((key, value) -> {
            if (key instanceof String attribute) {
                result.query.addUpdate(attribute, value);
            } else {
                result.query.addUpdate((org.eclipse.persistence.expressions.Expression) key, value);
            }
        });
        result.root = root(builder, source);
        result.where = predicate(builder, source.getSelectionCriteria());
        parameters(result, source);
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> CriteriaDelete<T> delete(CriteriaBuilderImpl builder, Class<T> type, DatabaseQuery source) {
        if (!(source instanceof DeleteAllQuery) || (type != null && type != source.getReferenceClass())) {
            throw new IllegalArgumentException("A criteria DELETE requires the same JPQL target entity type");
        }
        CriteriaDeleteImpl<T> result = new CriteriaDeleteImpl<>(builder.metamodel, builder,
                type == null ? (Class<T>) source.getReferenceClass() : type);
        result.root = root(builder, source);
        result.where = predicate(builder, source.getSelectionCriteria());
        parameters(result, source);
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static RootImpl root(CriteriaBuilderImpl builder, DatabaseQuery query) {
        EntityType entity = builder.metamodel.entity(query.getReferenceClass());
        return new RootImpl(entity, builder.metamodel, entity.getJavaType(), query instanceof ObjectLevelReadQuery read ? read.getExpressionBuilder()
                : ((org.eclipse.persistence.queries.ModifyAllQuery) query).getExpressionBuilder(), entity);
    }

    private static PredicateImpl predicate(CriteriaBuilderImpl builder, org.eclipse.persistence.expressions.Expression expression) {
        return expression == null ? null : new PredicateImpl(builder.metamodel, expression, List.of(), Predicate.BooleanOperator.AND);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Selection<?> selection(CriteriaBuilderImpl builder, ReportQuery query, ReportItem item) {
        if (item instanceof ConstructorReportItem constructor) {
            Selection<?>[] arguments = constructor.getReportItems().stream().map(nested -> selection(builder, query, nested)).toArray(Selection<?>[]::new);
            return new ConstructorSelectionImpl<>(constructor.getResultType(), arguments);
        }
        return new ExpressionImpl(builder.metamodel, resultType(builder, query, item), item.getAttributeExpression()).alias(item.getName());
    }

    private static Class<?> resultType(CriteriaBuilderImpl builder, ReadAllQuery query) {
        if (query instanceof ReportQuery report) {
            if (report.getResultClass() != null) {
                return report.getResultClass();
            }
            if (report.getItems().size() != 1) {
                return Object[].class;
            }
            return resultType(builder, report, report.getItems().get(0));
        }
        return query.getReferenceClass();
    }

    private static Class<?> resultType(CriteriaBuilderImpl builder, ReportQuery query, ReportItem item) {
        if (item.getResultType() != null) {
            return item.getResultType();
        }
        var descriptor = item.getDescriptor();
        var mapping = item.getMapping();
        if (descriptor == null && mapping == null && item.getAttributeExpression() != null
                && !(item.getAttributeExpression() instanceof MapEntryExpression entry && entry.shouldReturnMapEntry())) {
            // Resolve domain metadata without preparing SQL or initializing shared report items.
            var session = ((MetamodelImpl) builder.metamodel).getSession();
            var expression = item.getAttributeExpression();
            mapping = expression.getLeafMapping(query, query.getDescriptor(), session);
            if (expression.isExpressionBuilder() || (mapping != null && mapping.isForeignReferenceMapping())) {
                descriptor = expression.getLeafDescriptor(query, query.getDescriptor(), session);
            }
        }
        if (descriptor != null) {
            return descriptor.getJavaClass();
        }
        Class<?> type = mapping == null ? null : mapping.getAttributeClassification();
        return type == null ? Object.class : type;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void parameters(CommonAbstractCriteriaImpl<?> criteria, DatabaseQuery query) {
        for (int index = 0; index < query.getArguments().size(); index++) {
            String name = query.getArguments().get(index);
            Class<?> type = query.getArgumentTypes().get(index);
            criteria.addParameter(name.matches("[1-9][0-9]*")
                    ? new ParameterExpressionImpl(criteria.metamodel, type, Integer.valueOf(name))
                    : new ParameterExpressionImpl(criteria.metamodel, type, name));
        }
    }

    private static DatabaseQuery executable(DatabaseQuery query) {
        query.setArguments(new ArrayList<>());
        query.setArgumentTypes(new ArrayList<>());
        query.setSelectionCriteria(null);
        ((ExpressionQueryMechanism) query.getQueryMechanism()).clearStatement();
        query.setIsPrepared(false);
        return query;
    }

    private static final class ImportedSelect<T> extends CriteriaQueryImpl<T> {
        private final ReadAllQuery seed;
        private SelectionImpl<? extends T> importedSelection;
        private boolean selectionRequired;

        ImportedSelect(CriteriaBuilderImpl builder, Class<T> type, ReadAllQuery seed) {
            super(builder.metamodel, ((CriteriaQueryImpl<T>) builder.createQuery(type)).queryResult, type, builder);
            this.seed = seed;
        }

        @Override
        protected ReadAllQuery getDatabaseQuery(boolean toReportQuery) {
            if (selectionRequired && selection == null) {
                throw new IllegalArgumentException("The augmentation must supply a compatible selection");
            }
            if (selection != importedSelection || toReportQuery) {
                ReadAllQuery query = super.getDatabaseQuery(toReportQuery);
                query.setNonFetchJoinAttributeExpressions(new ArrayList<>(seed.getNonFetchJoinAttributeExpressions()));
                return query;
            }
            ReadAllQuery query = (ReadAllQuery) executable((DatabaseQuery) seed.clone());
            query.setOrderByExpressions(new ArrayList<>());
            if (query instanceof ReportQuery report) {
                report.setGroupByExpressions(groupBy == null ? new ArrayList<>()
                        : groupBy.stream().map(value -> ((InternalSelection) value).getCurrentNode()).toList());
                report.setHavingExpression(havingClause == null ? null : ((InternalSelection) havingClause).getCurrentNode());
            }
            return query;
        }
    }
}
