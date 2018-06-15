/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.Join;

/**
 * A <code>JPQLQueryDeclaration</code> represents either an identification variable declaration or a
 * collection member declaration. For a subquery, the declaration can be a derived path expression.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public interface JPQLQueryDeclaration {

    /**
     * Returns the range variable declaration if this is a range declaration otherwise the
     * collection-valued path expression when this is a collection member declaration.
     *
     * @return Either the range variable declaration or the collection-valued path expression
     */
    Expression getBaseExpression();

    /**
     * Returns the declaration expression, which is either an {@link
     * org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration IdentificationVariableDeclaration}
     * or a {@link org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration CollectionMemberDeclaration}
     * when part of a <b>FROM</b> clause, otherwise it's either the {@link
     * org.eclipse.persistence.jpa.jpql.parser.DeleteClause DeleteClause} or the {@link
     * org.eclipse.persistence.jpa.jpql.parser.UpdateClause UpdateClause}.
     *
     * @return The root of the declaration expression
     */
    Expression getDeclarationExpression();

    /**
     * Returns the <code><b>JOIN</b></code> expressions defined with this declaration, if supported.
     * The list contains the <code><b>JOIN</b></code> expressions in ordered they were declared.
     *
     * @return The <b>JOIN</b> expressions defined with this declaration or an empty list if this
     * declaration does not support it
     */
    List<Join> getJoins();

    /**
     * Determines the type this declaration represents.
     *
     * @return One of the possible types
     */
    Type getType();

    /**
     * Returns the identification variable name that is defining either the abstract schema name
     * or the collection-valued path expression
     *
     * @return The identification variable or an empty string if none was defined
     */
    String getVariableName();

    /**
     * Determines whether the declaration contains <b>JOIN</b> expressions. This can be
     * <code>true</code> only when {@link Type#isRange()} returns <code>true</code>. A collection
     * member declaration does not have <b>JOIN</b> expressions.
     *
     * @return <code>true</code> if at least one <b>JOIN</b> expression was parsed;
     * otherwise <code>false</code>
     */
    boolean hasJoins();

    /**
     * This enum type defines the various types of declarations supported by both the JPA functional
     * specification and EclipseLink.
     */
    public enum Type {

        /**
         * Indicates the "root" object maps a fully qualified class name.
         */
        CLASS_NAME(false),

        /**
         * Indicates the "root" object maps a collection-valued path expression.
         */
        COLLECTION(false),

        /**
         * Indicates the "root" object is a derived path expression where the identification variable
         * is declared in the super query, otherwise it's an entity name.
         */
        DERIVED(true),

        /**
         * Indicates the "root" object maps to an entity.
         */
        RANGE(true),

        /**
         * Indicates the "root" object maps to a subquery.
         */
        SUBQUERY(false),

        /**
         * Indicates the "root" object maps directly to a database table.
         */
        TABLE(false),

        /**
         * Indicates the "root" object maps to an unknown expression.
         */
        UNKNOWN(false);

        /**
         * Flag used to determine if the type represents a range variable declaration.
         */
        private boolean range;

        private Type(boolean range) {
            this.range = range;
        }

        /**
         * Determines whether this type represents a range variable declaration.
         *
         * @return <code>true</code> if this constant represents a range variable declaration;
         * <code>false</code> otherwise
         */
        public boolean isRange() {
            return range;
        }
    }
}
