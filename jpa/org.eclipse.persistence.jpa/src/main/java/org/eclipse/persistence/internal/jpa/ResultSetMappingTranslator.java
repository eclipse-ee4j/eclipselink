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

// Contributors:
//     New Jakarta Persistence 4.0 Features
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.sql.ColumnMapping;
import jakarta.persistence.sql.CompoundMapping;
import jakarta.persistence.sql.ConstructorMapping;
import jakarta.persistence.sql.EmbeddedMapping;
import jakarta.persistence.sql.EntityMapping;
import jakarta.persistence.sql.FieldMapping;
import jakarta.persistence.sql.MappingElement;
import jakarta.persistence.sql.MemberMapping;
import jakarta.persistence.sql.ResultSetMapping;
import jakarta.persistence.sql.TupleMapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;

/** Translates the programmatic result mapping using the existing row materializer. */
public final class ResultSetMappingTranslator {
    static final String MAPPING_LOCK_PROPERTY = EntityAgentProperties.MAPPING_LOCK;
    static final String EXECUTION_CONTEXT_PROPERTY = "eclipselink.result-mapping.execution-context";
    private static final String TRANSLATED_MAPPING_NAME = "jakarta.persistence.sql.ResultSetMapping";

    private ResultSetMappingTranslator() {
    }

    public static SQLResultSetMapping toEclipseLinkMapping(ResultSetMapping<?> mapping) {
        return toEclipseLinkMapping(mapping, null);
    }

    static SQLResultSetMapping toEclipseLinkMapping(ResultSetMapping<?> mapping, QueryExecutionContext context) {
        if (mapping == null) {
            throw new IllegalArgumentException("Result set mapping must not be null");
        }
        SQLResultSetMapping translated = new SQLResultSetMapping(TRANSLATED_MAPPING_NAME);
        SQLResult result = switch (mapping) {
            case CompoundMapping compound -> new CompoundResult(compound.elements(), context, false);
            case TupleMapping tuple -> new CompoundResult(tuple.elements(), context, true);
            case EntityMapping<?> entity -> toEntityResult(entity, context, false);
            case ConstructorMapping<?> constructor -> new ConstructedResult(constructor, context);
            case ColumnMapping<?> column -> toColumnResult(column);
        };
        translated.addResult(result);
        return translated;
    }

    /** Reconstruct the public typed form from the initialized annotation/XML metadata. */
    static ResultSetMapping<?> toJakartaMapping(SQLResultSetMapping mapping, AbstractSession session) {
        MappingElement<?>[] elements = mapping.getResults().stream()
                .map(result -> toMappingElement(result, session)).toArray(MappingElement<?>[]::new);
        return elements.length == 1 ? (ResultSetMapping<?>) elements[0] : ResultSetMapping.compound(elements);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static MappingElement<?> toMappingElement(SQLResult result, AbstractSession session) {
        if (result instanceof ColumnResult column) {
            Class<?> type = column.getColumn().getType();
            if (type == null) {
                type = Object.class;
            }
            return ResultSetMapping.column(column.getColumn().getName(), type);
        }
        if (result instanceof ConstructorResult constructor) {
            MappingElement<?>[] arguments = constructor.getColumnResults().stream()
                    .map(column -> toMappingElement(column, session)).toArray(MappingElement<?>[]::new);
            return ResultSetMapping.constructor(constructor.getTargetClass(), arguments);
        }
        if (result instanceof EntityResult entity) {
            Class<?> type = entity.getEntityClass();
            ClassDescriptor descriptor = session.getDescriptor(type);
            List<MemberMapping<?>> fields = new ArrayList<>();
            for (Object value : entity.getFieldResults().values()) {
                FieldResult field = (FieldResult) value;
                if (field.getFieldResults() == null) {
                    fields.add(toMemberMapping(descriptor, field, 0));
                } else {
                    for (FieldResult component : field.getFieldResults()) {
                        fields.add(toMemberMapping(descriptor, component, 0));
                    }
                }
            }
            String discriminator = entity.getDiscriminatorColumn() == null ? null
                    : entity.getDiscriminatorColumn().getName();
            return new EntityMapping(type, LockModeType.valueOf(entity.getLockMode()), discriminator,
                    fields.toArray(MemberMapping[]::new), null);
        }
        throw new IllegalArgumentException("Unsupported named SQL result mapping element: " + result.getClass().getName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static MemberMapping<?> toMemberMapping(ClassDescriptor descriptor, FieldResult field, int index) {
        String[] path = field.getMultipleFieldIdentifiers();
        DatabaseMapping attribute = descriptor.getMappingForAttributeName(path[index]);
        if (attribute == null) {
            throw new IllegalArgumentException("Unknown result mapping attribute: " + path[index]);
        }
        Class<?> type = attribute.getAttributeClassification();
        if (index == path.length - 1) {
            return new FieldMapping(descriptor.getJavaClass(), type == null ? Object.class : type,
                    path[index], field.getColumn().getName());
        }
        ClassDescriptor embedded = attribute.getReferenceDescriptor();
        if (embedded == null) {
            throw new IllegalArgumentException("Result mapping path has no nested descriptor: " + path[index]);
        }
        return new EmbeddedMapping(descriptor.getJavaClass(), embedded.getJavaClass(), path[index],
                new MemberMapping[] {toMemberMapping(embedded, field, index + 1)});
    }

    static boolean mappingRequiresTransaction(SQLResultSetMapping mapping) {
        return mapping != null && mapping.getResults().stream().anyMatch(result ->
                result instanceof EntityResult entity && !"NONE".equals(entity.getLockMode()));
    }

    static SQLResultSetMapping forResultClass(Class<?> resultClass, QueryExecutionContext context) {
        if (resultClass == null) {
            throw new IllegalArgumentException("Result class must not be null");
        }
        if (context.getSession().getDescriptor(resultClass) != null) {
            return new SQLResultSetMapping(resultClass);
        }
        SQLResultSetMapping mapping = new SQLResultSetMapping(resultClass.getName());
        mapping.addResult(new SQLResult() {
            @Override
            public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
                ResultSetMappingQuery typed = (ResultSetMappingQuery) query.clone();
                typed.setSQLResultSetMappings(java.util.Collections.singletonList(null));
                typed.setResultClass(resultClass);
                return typed.buildObjectsFromRecords(List.of(record), 0).get(0);
            }
        });
        return mapping;
    }

    static boolean mappingRequiresTransaction(ResultSetMapping<?> mapping) {
        return switch (mapping) {
            case EntityMapping<?> entity -> entity.lockMode() != LockModeType.NONE;
            case ConstructorMapping<?> constructor -> requiresTransaction(constructor.arguments());
            case CompoundMapping compound -> requiresTransaction(compound.elements());
            case TupleMapping tuple -> requiresTransaction(tuple.elements());
            case ColumnMapping<?> ignored -> false;
        };
    }

    private static boolean requiresTransaction(MappingElement<?>[] elements) {
        for (MappingElement<?> element : elements) {
            if (element instanceof EntityMapping<?> entity && entity.lockMode() != LockModeType.NONE
                    || element instanceof ConstructorMapping<?> constructor && requiresTransaction(constructor.arguments())) {
                return true;
            }
        }
        return false;
    }

    private static SQLResult toResult(MappingElement<?> element, QueryExecutionContext context, boolean prepareEntity) {
        return switch (element) {
            case EntityMapping<?> entity -> toEntityResult(entity, context, prepareEntity);
            case ConstructorMapping<?> constructor -> new ConstructedResult(constructor, context);
            case ColumnMapping<?> column -> toColumnResult(column);
        };
    }

    private static ColumnResult toColumnResult(ColumnMapping<?> mapping) {
        DatabaseField field = new DatabaseField(mapping.columnName());
        if (mapping.type() != Object.class) {
            field.setType(mapping.type());
        }
        return new ColumnResult(field);
    }

    private static EntityResult toEntityResult(EntityMapping<?> mapping, QueryExecutionContext context, boolean prepareEntity) {
        if (mapping.lockMode() != LockModeType.NONE && context == null) {
            throw new IllegalArgumentException("An entity lock mapping requires a query execution context");
        }
        EntityResult result = new EntityResult(mapping.entityClass()) {
            @Override
            public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
                // Mapping metadata may be shared by named queries and different handlers.
                // Resolve the live owner from the execution clone instead of capturing it.
                Object owner = query.getProperty(EXECUTION_CONTEXT_PROPERTY);
                QueryExecutionContext current = owner instanceof QueryExecutionContext value ? value : null;
                if (mapping.lockMode() != LockModeType.NONE) {
                    if (current == null) {
                        throw new IllegalStateException("An entity lock mapping requires a live query owner");
                    }
                    current.requireTransaction();
                }
                Object entity = super.getValueFromRecord(record, query);
                if (entity != null && mapping.lockMode() != LockModeType.NONE) {
                    current.lockResult(entity, mapping.lockMode());
                }
                return current == null || !prepareEntity ? entity : current.prepareResult(entity);
            }
        };
        if (mapping.discriminatorColumn() != null) {
            result.setDiscriminatorColumn(mapping.discriminatorColumn());
        }
        for (MemberMapping<?> field : mapping.fields()) {
            addMemberResult(result, null, field);
        }
        return result;
    }

    private static void addMemberResult(EntityResult result, String path, MemberMapping<?> member) {
        switch (member) {
            case FieldMapping<?, ?> field -> result.addFieldResult(
                    new FieldResult(append(path, field.name()), field.columnName()));
            case EmbeddedMapping<?, ?> embedded -> {
                for (MemberMapping<?> field : embedded.fields()) {
                    addMemberResult(result, append(path, embedded.name()), field);
                }
            }
        }
    }

    private static String append(String path, String name) {
        return path == null ? name : path + "." + name;
    }

    private static class CompoundResult extends SQLResult {
        final MappingElement<?>[] elements;
        final SQLResult[] results;
        private final boolean tuple;

        CompoundResult(MappingElement<?>[] elements, QueryExecutionContext context, boolean tuple) {
            this(elements, context, tuple, tuple);
        }

        CompoundResult(MappingElement<?>[] elements, QueryExecutionContext context, boolean tuple, boolean prepareEntities) {
            this.elements = elements.clone();
            this.results = new SQLResult[elements.length];
            this.tuple = tuple;
            for (int index = 0; index < elements.length; index++) {
                results[index] = toResult(elements[index], context, prepareEntities);
            }
        }

        @Override
        public void setSQLResultMapping(SQLResultSetMapping mapping) {
            super.setSQLResultMapping(mapping);
            for (SQLResult result : results) {
                result.setSQLResultMapping(mapping);
            }
        }

        Object[] values(DatabaseRecord record, ResultSetMappingQuery query) {
            Object[] values = new Object[results.length];
            for (int index = 0; index < values.length; index++) {
                values[index] = results[index].getValueFromRecord(record, query);
            }
            return values;
        }

        @Override
        public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
            Object[] values = values(record, query);
            return tuple ? new MappingTuple(elements, values) : values;
        }
    }

    private static final class ConstructedResult extends CompoundResult {
        private final Class<?> targetClass;
        private transient Constructor<?> constructor;

        ConstructedResult(ConstructorMapping<?> mapping, QueryExecutionContext context) {
            super(mapping.arguments(), context, false, true);
            targetClass = mapping.targetClass();
        }

        @Override
        public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
            Object[] values = values(record, query);
            if (constructor == null) {
                for (Constructor<?> candidate : targetClass.getDeclaredConstructors()) {
                    Class<?>[] types = candidate.getParameterTypes();
                    if (types.length != elements.length) {
                        continue;
                    }
                    boolean matches = true;
                    for (int index = 0; index < types.length; index++) {
                        Class<?> declared = elements[index].getJavaType();
                        if (declared == Object.class && values[index] != null) {
                            declared = values[index].getClass();
                        }
                        if (!ConversionManager.getObjectClass(types[index]).isAssignableFrom(
                                ConversionManager.getObjectClass(declared))) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        constructor = candidate;
                        break;
                    }
                }
                if (constructor == null) {
                    throw new IllegalArgumentException("No matching result constructor on " + targetClass.getName());
                }
                try {
                    constructor = PrivilegedAccessHelper.getConstructorFor(targetClass, constructor.getParameterTypes(), true);
                } catch (NoSuchMethodException exception) {
                    throw new PersistenceException(exception);
                }
            }
            try {
                return PrivilegedAccessHelper.invokeConstructor(constructor, values);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                throw new PersistenceException("Could not construct mapped result " + targetClass.getName(), exception);
            }
        }
    }

    private static final class MappingTuple implements Tuple {
        private final List<TupleElement<?>> elements;
        private final Object[] values;

        MappingTuple(MappingElement<?>[] elements, Object[] values) {
            this.elements = List.copyOf(Arrays.asList(elements));
            this.values = values.clone();
        }

        @Override
        public <X> X get(TupleElement<X> element) {
            int index = elements.indexOf(element);
            if (index < 0) {
                throw new IllegalArgumentException("Tuple element is not part of this result");
            }
            return get(index, element.getJavaType());
        }

        @Override
        public <X> X get(String alias, Class<X> type) {
            return typedValue(get(alias), type);
        }

        @Override
        public Object get(String alias) {
            if (alias != null) {
                for (int index = 0; index < elements.size(); index++) {
                    if (alias.equals(elements.get(index).getAlias())) {
                        return values[index];
                    }
                }
            }
            throw new IllegalArgumentException("Unknown tuple alias: " + alias);
        }

        @Override
        public <X> X get(int index, Class<X> type) {
            return typedValue(get(index), type);
        }

        @Override
        public Object get(int index) {
            if (index < 0 || index >= values.length) {
                throw new IllegalArgumentException("Invalid tuple index: " + index);
            }
            return values[index];
        }

        private static <X> X typedValue(Object value, Class<X> type) {
            if (type == null || value != null && !ConversionManager.getObjectClass(type).isInstance(value)) {
                throw new IllegalArgumentException("Tuple value does not match requested type");
            }
            return ConversionManager.<X>getObjectClass(type).cast(value);
        }

        @Override
        public Object[] toArray() {
            return values.clone();
        }

        @Override
        public List<TupleElement<?>> getElements() {
            return new ArrayList<>(elements);
        }
    }
}
