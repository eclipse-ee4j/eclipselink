/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     03/26/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.annotations;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.FetchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Variable one to one mappings are used to represent a pointer references
 * between a java object and an implementer of an interface. This mapping is
 * usually represented by a single pointer (stored in an instance variable)
 * between the source and target objects. In the relational database tables,
 * these mappings are normally implemented using a foreign key and a type code.
 * <p>
 * A VariableOneToOne can be specified within an Entity, MappedSuperclass
 * and Embeddable class.
 *
 * @author Guy Pelletier
 * @since Eclipselink 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface VariableOneToOne {
    /**
     * The interface class that is the target of the association.
     * If not specified it will be inferred from the type of the object being
     * referenced.
     */
    Class<?> targetInterface() default void.class;

    /**
     * The operations that must be cascaded to the target of the association.
     */
    CascadeType[] cascade() default {};

    /**
     * Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The {@linkplain jakarta.persistence.FetchType#EAGER} strategy is a
     * requirement on the persistence provider runtime that the value must be
     * eagerly fetched. The {@linkplain jakarta.persistence.FetchType#LAZY} strategy is a hint to the persistence provider
     * runtime. If not specified, defaults to {@linkplain jakarta.persistence.FetchType#EAGER}.
     */
    FetchType fetch() default FetchType.EAGER;

    /**
     * Whether the association is optional. If set to false then a
     * non-null relationship must always exist.
     */
    boolean optional() default true;

    /**
     * Whether to apply the remove operation to entities that have
     * been removed from the relationship and to cascade the remove operation to
     * those entities.
     */
    boolean orphanRemoval() default false;

    /**
     * The discriminator column will hold the type indicators. If the
     * discriminatorColumn is not specified, the name of the discriminator
     * column defaults to "{@code DTYPE}" and the discriminator type to
     * {@linkplain jakarta.persistence.DiscriminatorType#STRING}.
     */
    DiscriminatorColumn discriminatorColumn() default @DiscriminatorColumn;

    /**
     * The list of discriminator types that can be used with this
     * VariableOneToOne. If none are specified then those entities within the
     * persistence unit that implement the target interface will be added to
     * the list of types. The discriminator type will default as follows:
     * <ul>
     *  <li>If {@linkplain #discriminatorColumn()} type is {@linkplain jakarta.persistence.DiscriminatorType#STRING}: Entity.name()</li>
     *  <li>If {@linkplain #discriminatorColumn()} type is {@linkplain jakarta.persistence.DiscriminatorType#CHAR}: First letter of the Entity class</li>
     *  <li>If {@linkplain #discriminatorColumn()} type is {@linkplain jakarta.persistence.DiscriminatorType#INTEGER}: The next integer after the
     *    highest integer explicitly added.</li>
     * </ul>
     */
    DiscriminatorClass[] discriminatorClasses() default {};
}
