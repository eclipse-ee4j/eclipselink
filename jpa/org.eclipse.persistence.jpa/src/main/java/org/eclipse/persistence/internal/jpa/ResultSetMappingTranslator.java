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

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.SQLResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;

/**
 * Translates a Jakarta Persistence 4.0 {@link ResultSetMapping} into the {@link SQLResultSetMapping}
 * that EclipseLink's query machinery consumes.
 * <p>
 *
 * {@code ResultSetMapping} is the programmatic form of {@link jakarta.persistence.SqlResultSetMapping
 * &#64;SqlResultSetMapping}, and the two models correspond closely, so this is largely a structural
 * recursion:
 * <pre>
 *     CompoundMapping      -&gt; SQLResultSetMapping holding several SQLResults
 *     EntityMapping        -&gt; EntityResult
 *       FieldMapping       -&gt;   FieldResult
 *       EmbeddedMapping    -&gt;   FieldResults with dotted attribute paths
 *     ConstructorMapping   -&gt; ConstructorResult
 *     ColumnMapping        -&gt; ColumnResult
 * </pre>
 * Both {@code ResultSetMapping} and its element and member hierarchies are {@code sealed}, so each
 * switch below is exhaustive and a type added to the specification will fail to compile here rather
 * than being silently ignored.
 *
 * <p>
 * Three things the specification can express and EclipseLink's model cannot are rejected rather than
 * quietly dropped; see {@link #toConstructorResult}, {@link #toEclipseLinkMapping} and
 * {@link #toEntityResult}.
 */
public final class ResultSetMappingTranslator {

    /**
     * Name given to the translated mapping. {@link SQLResultSetMapping} refuses a null name, but the
     * name is only used to look a mapping up on the session; one built here is handed straight to a
     * query, so it is never looked up and the value is immaterial beyond being recognisable in logs.
     */
    private static final String TRANSLATED_MAPPING_NAME = "jakarta.persistence.sql.ResultSetMapping";

    private ResultSetMappingTranslator() {
    }

    /**
     * Translate the given mapping.
     *
     * @param mapping the specification's description of how a row should be read
     * @return the equivalent EclipseLink mapping
     * @throws IllegalArgumentException if the mapping describes something EclipseLink cannot represent
     */
    public static SQLResultSetMapping toEclipseLinkMapping(ResultSetMapping<?> mapping) {
        SQLResultSetMapping translated = new SQLResultSetMapping(TRANSLATED_MAPPING_NAME);

        switch (mapping) {
            // A compound mapping produces an Object[] per row, one element per result, which is
            // exactly what a multi-result SQLResultSetMapping produces. Order is significant: the
            // runtime reads results positionally, so the elements are copied in the order given
            // rather than grouped by kind the way the annotation processing does.
            case CompoundMapping compound -> addElements(translated, compound.elements());

            case EntityMapping<?> entity -> translated.addResult(toEntityResult(entity));
            case ConstructorMapping<?> constructor -> translated.addResult(toConstructorResult(constructor));
            case ColumnMapping<?> column -> translated.addResult(toColumnResult(column));

            // A tuple mapping differs from a compound one only in wrapping each row in a Tuple, whose
            // elements are addressable by the alias each MappingElement carries. EclipseLink produces
            // Object[], and nothing here can wrap the rows afterwards, so returning the untranslated
            // shape would hand the caller Object[] where it expects Tuple. Refuse instead.
            case TupleMapping ignored -> throw new IllegalArgumentException(
                    "A TupleMapping cannot be used here. EclipseLink returns each row of a result set"
                            + " mapping as an Object[], and the rows would have to be wrapped in a Tuple"
                            + " afterwards. Use ResultSetMapping.compound(...) to read rows as Object[].");
        }

        return translated;
    }

    private static void addElements(SQLResultSetMapping translated, MappingElement<?>[] elements) {
        for (MappingElement<?> element : elements) {
            translated.addResult(toResult(element));
        }
    }

    /**
     * Translate one element of a row. {@link MappingElement} permits exactly these three types.
     */
    private static SQLResult toResult(MappingElement<?> element) {
        return switch (element) {
            case EntityMapping<?> entity -> toEntityResult(entity);
            case ConstructorMapping<?> constructor -> toConstructorResult(constructor);
            case ColumnMapping<?> column -> toColumnResult(column);
        };
    }

    /**
     * Translate a scalar column.
     * <p>
     * {@link ColumnResult} has no type of its own; it carries a {@link DatabaseField}, and the type
     * rides on that, which is what the annotation processing does too. {@code Object} is the
     * specification's way of saying "no type given" - {@code ResultSetMapping.column(name)} produces
     * it - so it is left off rather than imposed on the field, where it would only stand in the way of
     * the conversion EclipseLink would otherwise infer.
     */
    private static ColumnResult toColumnResult(ColumnMapping<?> mapping) {
        DatabaseField field = new DatabaseField(mapping.columnName());

        if (mapping.type() != Object.class) {
            field.setType(mapping.type());
        }

        return new ColumnResult(field);
    }

    /**
     * Translate an entity result.
     * <p>
     * A lock mode other than {@link LockModeType#NONE} is refused: {@link EntityResult} has no notion
     * of locking, so honouring it would mean locking the query as a whole, which is a different thing
     * from locking the rows of one entity result.
     */
    private static EntityResult toEntityResult(EntityMapping<?> mapping) {
        if (mapping.lockMode() != LockModeType.NONE) {
            throw new IllegalArgumentException(
                    "A lock mode on an EntityMapping is not supported. EclipseLink's entity results carry"
                            + " no lock mode, so [" + mapping.lockMode() + "] requested for entity ["
                            + mapping.entityClass().getName() + "] cannot be applied to that result alone."
                            + " Lock the query instead, with Query.setLockMode.");
        }

        EntityResult entityResult = new EntityResult(mapping.entityClass());

        if (mapping.discriminatorColumn() != null) {
            entityResult.setDiscriminatorColumn(mapping.discriminatorColumn());
        }

        for (MemberMapping<?> field : mapping.fields()) {
            addMemberResult(entityResult, null, field);
        }

        return entityResult;
    }

    /**
     * Add one of an entity's members, flattening embeddables as it goes.
     * <p>
     * {@link EmbeddedMapping} has no counterpart in EclipseLink, and none is needed: a
     * {@link FieldResult} splits its attribute name on {@code .} and keeps the whole path, so a nested
     * embeddable is expressed the same way {@link jakarta.persistence.FieldResult &#64;FieldResult}
     * has always expressed one - as {@code outer.inner}. Embeddables nest arbitrarily, so this
     * recurses, accumulating the path as it descends.
     */
    private static void addMemberResult(EntityResult entityResult, String path, MemberMapping<?> member) {
        switch (member) {
            case FieldMapping<?, ?> field ->
                    entityResult.addFieldResult(new FieldResult(append(path, field.name()), field.columnName()));

            case EmbeddedMapping<?, ?> embedded -> {
                String embeddedPath = append(path, embedded.name());

                for (MemberMapping<?> field : embedded.fields()) {
                    addMemberResult(entityResult, embeddedPath, field);
                }
            }
        }
    }

    private static String append(String path, String name) {
        return path == null ? name : path + "." + name;
    }

    /**
     * Translate a constructor result.
     * <p>
     * The specification allows any {@link MappingElement} as a constructor argument, so an entity or a
     * nested constructor is legal there. EclipseLink's {@link ConstructorResult} takes only
     * {@link ColumnResult}s, so anything else is refused rather than approximated.
     */
    private static ConstructorResult toConstructorResult(ConstructorMapping<?> mapping) {
        ConstructorResult constructorResult = new ConstructorResult(mapping.targetClass());

        for (MappingElement<?> argument : mapping.arguments()) {
            if (!(argument instanceof ColumnMapping<?> column)) {
                throw new IllegalArgumentException(
                        "Only columns are supported as constructor arguments, but the mapping for ["
                                + mapping.targetClass().getName() + "] has an argument of type ["
                                + argument.getClass().getSimpleName() + "]. EclipseLink's constructor"
                                + " results are built from column values alone.");
            }

            constructorResult.addColumnResult(toColumnResult(column));
        }

        return constructorResult;
    }
}
