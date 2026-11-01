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
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Tuple;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/** Adapts a JPQL selection without changing its cached or named template. */
final class JPQLResultClass {

    // Produced by the core JPQL ReportItemBuilder before result-class adaptation.
    private static final String INCOMPATIBLE_RESULT = "eclipselink.jpql.incompatible-result";
    private static final String CONSTRUCTOR = "eclipselink.jpql.implicit-constructor";
    private static final String PARAMETERS = "eclipselink.jpql.constructor-parameters";
    private static final Set<Class<?>> ADDITIONAL_SCALAR_TYPES = Set.of(
            java.time.Instant.class, java.time.LocalDate.class, java.time.LocalTime.class,
            java.time.LocalDateTime.class, java.time.OffsetDateTime.class, java.time.OffsetTime.class,
            java.time.Year.class, java.util.UUID.class, java.net.URL.class, Class.class, java.util.GregorianCalendar.class);

    private record ParameterTypes(Class<?> resultClass, Map<Integer, String> names) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    private JPQLResultClass() {
    }

    static DatabaseQuery adapt(DatabaseQuery original, Class<?> resultClass, AbstractSession session) {
        if (resultClass == null || !original.isJPQLCallQuery()) {
            return original;
        }
        if (Boolean.TRUE.equals(original.getProperty(INCOMPATIBLE_RESULT))) {
            throw new IllegalArgumentException("JPQL selection has incompatible result operand types");
        }
        if (!original.isObjectLevelReadQuery()) {
            throw new IllegalArgumentException("A typed JPQL query must be a select query");
        }
        if (!(original instanceof ReportQuery)
                && resultClass.isAssignableFrom(original.getReferenceClass())) {
            return original;
        }
        ParameterTypes pending = (ParameterTypes) original.getProperty(PARAMETERS);
        ReportQuery query = projection(original);
        query.setArguments(new ArrayList<>(original.getArguments()));
        // Parser-inferred argument types may legitimately be unresolved (null).
        List<Class<?>> argumentTypes = query.getArgumentTypes();
        argumentTypes.clear();
        argumentTypes.addAll(original.getArgumentTypes());
        query.setArgumentTypeNames(new ArrayList<>(original.getArgumentTypeNames()));
        List<ReportItem> items = query.getItems();
        if (Boolean.TRUE.equals(query.getProperty(CONSTRUCTOR))) {
            items = ((ConstructorReportItem) items.get(0)).getReportItems();
            query.setItems(new ArrayList<>(items));
        }
        query.removeProperty(CONSTRUCTOR);
        query.removeProperty(PARAMETERS);
        query.setResultClass(resultClass);
        query.setIsPrepared(false);
        query.setSession(session);
        query.checkDescriptor(session);

        if (query.hasIDFunctionSelectItemOnly()
                && resultClass == query.getDescriptor().getCMPPolicy().getPKClass()) {
            query.setReturnPKClassInstance();
            return query;
        }
        Class<?>[] types = new Class<?>[items.size()];
        Map<Integer, String> parameters = new LinkedHashMap<>();
        for (int index = 0; index < items.size(); index++) {
            ReportItem item = items.get(index);
            item.initialize(query);
            types[index] = pending != null && pending.names().containsKey(index) ? null : semanticType(item, query);
            if (types[index] == null && item.getAttributeExpression() instanceof ParameterExpression parameter) {
                parameters.put(index, parameter.getField().getName());
            }
        }
        if (!parameters.isEmpty()) {
            query.setProperty(PARAMETERS, new ParameterTypes(resultClass, Map.copyOf(parameters)));
        }
        if (items.size() == 1 && (resultClass == Object.class
                || types[0] != null && resultClass.isAssignableFrom(types[0])
                || isUnknownScalar(items.get(0), query)
                    && (ConversionManager.getDefaultManager().getDataTypesConvertedTo(boxed(resultClass)) != null
                        || ADDITIONAL_SCALAR_TYPES.contains(resultClass)))) {
            query.returnSingleAttribute();
            return query;
        }
        if (resultClass == Object[].class) {
            query.setReturnType(ReportQuery.ShouldReturnArray);
            return query;
        }
        if (resultClass == Tuple.class) {
            // Tuple has its own result packaging; it is never a constructor target.
            return query;
        }
        if (!parameters.isEmpty()) {
            return query;
        }
        construct(query, types);
        return query;
    }

    /** Resolve otherwise untyped selected parameters on each execution, including rebindings. */
    static DatabaseQuery resolve(DatabaseQuery original, AbstractSession session,
            Map<String, Object> values, Map<String, Class<?>> bindingTypes) {
        ParameterTypes pending = (ParameterTypes) original.getProperty(PARAMETERS);
        if (pending == null || pending.resultClass() == Object[].class || pending.resultClass() == Tuple.class
                || pending.resultClass() == Object.class && ((ReportQuery) original).getItems().size() == 1) {
            return original;
        }
        Map<Integer, String> parameters = pending.names();
        ReportQuery query = (ReportQuery) original.clone();
        query.setResultClass(pending.resultClass());
        if (Boolean.TRUE.equals(query.getProperty(CONSTRUCTOR))) {
            query.setItems(new ArrayList<>(((ConstructorReportItem) query.getItems().get(0)).getReportItems()));
        }
        query.removeProperty(CONSTRUCTOR);
        query.setIsPrepared(false);
        query.setSession(session);
        Class<?>[] types = new Class<?>[query.getItems().size()];
        for (int index = 0; index < types.length; index++) {
            ReportItem item = query.getItems().get(index);
            String name = parameters.get(index);
            if (name == null) {
                types[index] = semanticType(item, query);
            } else {
                Class<?> type = bindingTypes.get(name);
                if (type == null && values.get(name) != null) {
                    type = values.get(name).getClass();
                }
                if (type == null) {
                    throw new IllegalArgumentException("Selected JPQL parameter " + name
                            + " needs a non-null value or an explicit binding type for constructor matching");
                }
                types[index] = boxed(type);
                ParameterExpression expression = (ParameterExpression) item.getAttributeExpression().clone();
                expression.setField(expression.getField().clone());
                expression.setType(types[index]);
                expression.getField().setType(types[index]);
                item.setAttributeExpression(expression);
            }
        }
        if (types.length == 1 && query.getResultClass().isAssignableFrom(types[0])) {
            // The JDBC driver may expose a different numeric type. Convert to the
            // selected parameter's semantic type while retaining the requested target
            // in pending metadata for discovery and subsequent bindings.
            query.setResultClass(types[0]);
            query.returnSingleAttribute();
        } else {
            construct(query, types);
        }
        return query;
    }

    static Class<?> declaredResultClass(DatabaseQuery query) {
        ParameterTypes pending = (ParameterTypes) query.getProperty(PARAMETERS);
        return pending == null ? null : pending.resultClass();
    }

    private static ReportQuery projection(DatabaseQuery original) {
        if (original instanceof ReportQuery report) {
            return (ReportQuery) report.clone();
        }
        ObjectLevelReadQuery entityQuery = (ObjectLevelReadQuery) original.clone();
        ReportQuery report = new ReportQuery();
        report.copyFromQuery(entityQuery);
        report.setName(original.getName());
        ReportItem entity = new ReportItem("result", report.getExpressionBuilder());
        entity.setResultType(original.getReferenceClass());
        if (entityQuery.hasJoining()) {
            JoinedAttributeManager source = entityQuery.getJoinedAttributeManager();
            JoinedAttributeManager joined = new JoinedAttributeManager();
            joined.setJoinedAttributeExpressions_(new ArrayList<>(source.getJoinedAttributeExpressions()));
            joined.setShouldFilterDuplicates(source.shouldFilterDuplicates());
            entity.setJoinedAttributeManager(joined);
        }
        report.setJoinedAttributeManager(null);
        report.setShouldUseDefaultFetchGroup(false);
        report.setItems(new ArrayList<>(List.of(entity)));
        return report;
    }

    private static Class<?> semanticType(ReportItem item, ReportQuery query) {
        if (item.isConstructorItem()) {
            return item.getResultType();
        }
        if (item.getResultType() != null && item.getResultType() != Object.class) {
            return boxed(item.getResultType());
        }
        DatabaseMapping mapping = item.getMapping();
        if (mapping != null) {
            if (item.getAttributeExpression() instanceof MapEntryExpression entry) {
                if (entry.shouldReturnMapEntry()) {
                    return Map.Entry.class;
                }
                if (mapping.isCollectionMapping()) {
                    return (Class<?>) mapping.getContainerPolicy().getKeyType();
                }
            }
            return boxed(mapping.getAttributeClassification());
        }
        if (item.getDescriptor() != null) {
            return item.getDescriptor().getJavaClass();
        }
        if (item.getAttributeExpression() instanceof ConstantExpression constant && constant.getValue() != null) {
            return constant.getValue().getClass();
        }
        if (item.getAttributeExpression() instanceof ParameterExpression parameter) {
            if (parameter.getType() instanceof Class<?> type && type != Object.class) {
                return boxed(type);
            }
            int index = query.getArguments().indexOf(parameter.getField().getName());
            if (index >= 0 && index < query.getArgumentTypes().size()) {
                Class<?> type = query.getArgumentTypes().get(index);
                return type == Object.class ? null : boxed(type);
            }
            return null;
        }
        return isUnknownScalar(item, query) ? null : item.getResultType();
    }

    /** Unknown SQL function results retain built-in scalar conversion, not DTO inference. */
    private static boolean isUnknownScalar(ReportItem item, ReportQuery query) {
        if (item.isConstructorItem() || item.getMapping() != null || item.getDescriptor() != null
                || item.getResultType() != null && item.getResultType() != Object.class) {
            return false;
        }
        if (item.getResultType() == null && item.getAttributeExpression() instanceof FunctionExpression function) {
            // The report builder leaves only genuinely unresolved scalar results untyped.
            // Declared Object and incompatible known operands retain Object metadata.
            return switch (function.getOperator().getSelector()) {
                case 0, ExpressionOperator.Cast, ExpressionOperator.Case, ExpressionOperator.CaseCondition,
                     ExpressionOperator.Coalesce, ExpressionOperator.NullIf, ExpressionOperator.Add,
                     ExpressionOperator.Subtract, ExpressionOperator.Multiply, ExpressionOperator.Divide,
                     ExpressionOperator.Negate, ExpressionOperator.Abs, ExpressionOperator.Ceil,
                     ExpressionOperator.Floor, ExpressionOperator.Round, ExpressionOperator.Minimum,
                     ExpressionOperator.Maximum, ExpressionOperator.Sum -> true;
                default -> false;
            };
        }
        return false;
    }

    private static void construct(ReportQuery query, Class<?>[] types) {
        Class<?> resultClass = query.getResultClass();
        if (resultClass.isInterface() || Modifier.isAbstract(resultClass.getModifiers())) {
            throw new IllegalArgumentException("JPQL constructor result must be a nonabstract class: " + resultClass.getName());
        }
        Constructor<?> constructor;
        try {
            constructor = PrivilegedAccessHelper.callDoPrivilegedWithException(() -> matchingConstructor(resultClass, types));
        } catch (Exception failure) {
            throw new IllegalArgumentException("No matching JPQL result constructor for " + resultClass.getName()
                    + Arrays.toString(types), failure);
        }
        ConstructorReportItem item = new ConstructorReportItem("result");
        item.setResultType(resultClass);
        item.setConstructor(constructor);
        item.setConstructorArgTypes(constructor.getParameterTypes());
        item.setReportItems(new ArrayList<>(query.getItems()));
        query.setItems(new ArrayList<>(List.of(item)));
        query.returnSingleAttribute();
        query.setProperty(CONSTRUCTOR, Boolean.TRUE);
        query.setIsPrepared(false);
    }

    private static Constructor<?> matchingConstructor(Class<?> resultClass, Class<?>[] types) throws NoSuchMethodException {
        for (Class<?> type : types) {
            if (type == null) {
                throw new NoSuchMethodException("Unresolved JPQL select-item type");
            }
        }
        return PrivilegedAccessHelper.getDeclaredConstructorFor(resultClass, types, true);
    }

    private static Class<?> boxed(Class<?> type) {
        return type == null ? null : Helper.getObjectClass(type);
    }
}
