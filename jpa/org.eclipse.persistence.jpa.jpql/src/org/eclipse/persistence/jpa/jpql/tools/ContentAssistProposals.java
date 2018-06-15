/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;

/**
 * This object stores the various proposals available for content assist for a certain position
 * within a JPQL query. The proposals are stored in categories (abstract schema types, identifiers,
 * identification variables and mappings).
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public interface ContentAssistProposals {

    /**
     * Returns the collection of possible abstract schema types.
     *
     * @return The {@link IEntity entities} defined in the persistence context
     */
    Iterable<IEntity> abstractSchemaTypes();

    /**
     * Creates a new JPQL query by inserting the given proposal at the given position. The updated
     * JPQL query and position will be adjusted by converting some characters into their corresponding
     * escaped characters, for instance '\r' will be converted to '\\r.
     * <p>
     * The replacement will also handle compound JPQL identifiers when updating the JPQL query.
     * <p>
     * Example: If the cursor is within "IS NOT N|" and the proposal is "IS NOT NULL", then "IS NOT"
     * will not be added twice. If the word to replace is "IS NULL" and the proposal is "IS NOT NULL",
     * then "NOT" will be inserted between "IS" and "NULL".
     *
     * @param jpqlQuery The JPQL query to modify with the given proposal
     * @param proposal The proposal to insert into the query
     * @param position The position of insertion
     * @param insert Flag that determines if the partial word following the cursor should be left
     * intact or should be replaced by the proposal
     * @return The result of inserting the proposal into the query, including the adjust position, if
     * it was required
     */
    ResultQuery buildEscapedQuery(String jpqlQuery, String proposal, int position, boolean insert);

    /**
     * Creates a new JPQL query by inserting the given proposal at the given position.
     * <p>
     * The replacement will also handle compound JPQL identifiers when updating the JPQL query.
     * <p>
     * Example: If the cursor is within "IS NOT N|" and the proposal is "IS NOT NULL", then "IS NOT"
     * will not be added twice. If the word to replace is "IS NULL" and the proposal is "IS NOT NULL",
     * then "NOT" will be inserted between "IS" and "NULL".
     *
     * @param jpqlQuery The JPQL query to modify with the given proposal
     * @param proposal The proposal to insert into the query
     * @param position The position of insertion
     * @param insert Flag that determines if the partial word following the cursor should be left
     * intact or should be replaced by the proposal
     * @return The result of inserting the proposal into the query, including the adjust position, if
     * it was required
     */
    ResultQuery buildQuery(String jpqlQuery, String proposal, int position, boolean insert);

    /**
     * Creates a new JPQL query by inserting the given proposal at the given position. The updated
     * JPQL query and position will be adjusted by converting some characters into their corresponding
     * escaped characters, for instance '&gt;' will be converted to '&amp;gt;'.
     * <p>
     * The replacement will also handle compound JPQL identifiers when updating the JPQL query.
     * <p>
     * Example: If the cursor is within "IS NOT N|" and the proposal is "IS NOT NULL", then "IS NOT"
     * will not be added twice. If the word to replace is "IS NULL" and the proposal is "IS NOT NULL",
     * then "NOT" will be inserted between "IS" and "NULL".
     *
     * @param jpqlQuery The JPQL query to modify with the given proposal, which should be the non-
     * converted string, i.e. any escaped characters should not be converted
     * @param proposal The proposal to insert into the query
     * @param position The position of insertion, which was derived from the parsed tree representation
     * of the JPQL query
     * @param insert Flag that determines if the partial word following the cursor should be left
     * intact or should be replaced by the proposal
     * @return The result of inserting the proposal into the query, including the adjust position, if
     * it was required
     * @since 2.5
     */
    ResultQuery buildXmlQuery(String jpqlQuery, String proposal, int position, boolean insert);

    /**
     * Returns the filtered list of possible class names. This is usually available when the cursor
     * is within the constructor name of the constructor expression.
     *
     * @return The filtered list of possible class names
     * @see org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression
     * @since 2.5
     */
    Iterable<String> classNames();

    /**
     * Returns the filtered list of possible column names.
     *
     * @return The filtered list of possible column names
     * @since 2.5
     */
    Iterable<String> columnNames();

    /**
     * Returns the filtered list of possible enum constant names. This is usually available when the
     * cursor is after the dot separating a fully qualified enum type and the enum constant.
     *
     * @return The filtered list of possible enum constant names associated with its enum type
     * @since 2.5
     */
    Iterable<EnumProposals> enumConstant();

    /**
     * Retrieves the abstract schema type that is mapped with the given identification variable.
     *
     * @param identificationVariable The identification variable that, if defined as a range variable,
     * will be mapped to a managed type
     * @return The abstract schema type mapped with the given identification variable or
     * <code>null</code> if the given variable is mapped to something else or not mapped to anything
     */
    IEntity getAbstractSchemaType(String identificationVariable);

    /**
     * @since 2.5
     */
    ClassType getClassType();

    /**
     * Returns the role of the given JPQL identifier.
     *
     * @param identifier The JPQL identifier to retrieve its role
     * @return The {@link IdentifierRole} for the given JPQL identifier or <code>null</code> if no
     * role was defined or if the given string is not a valid JPQL identifier
     * @since 2.4
     */
    IdentifierRole getIdentifierRole(String identifier);

    /**
     * Determines whether there is at least one proposals.
     *
     * @return <code>true</code> if there is at least one proposal; otherwise <code>false</code>
     */
    boolean hasProposals();

    /**
     * Returns the collection of possible identification variables.
     *
     * @return The list of possible identification variables
     */
    Iterable<String> identificationVariables();

    /**
     * Returns the collection of possible JPQL identifiers.
     *
     * @return The list of possible JPQL identifiers
     */
    Iterable<String> identifiers();

    /**
     * Returns the collection of possible {@link IMapping mappings}, which can be state fields,
     * association fields and/or collection fields depending on the location used to retrieve the
     * possible proposals.
     *
     * @return The list of possible proposals {@link IMapping mappings}
     */
    Iterable<IMapping> mappings();

    /**
     * Returns the filtered list of possible table names.
     *
     * @return The filtered list of possible table names
     * @since 2.5
     */
    Iterable<String> tableNames();

    /**
     * This enumeration determines the type of classes returned by {@link ContentAssistProposals#classNames()}.
     */
    public enum ClassType {

        /**
         * Indicates the only class type allowed is an enum type.
         */
        ENUM,

        /**
         * Indicates the class has to be instantiable, i.e. anonymous, interfaces, enums, annotations
         * are not allowed.
         */
        INSTANTIABLE
    }

    /**
     * Holds onto the {@link IType} of the enum type and the list of possible enum constants.
     *
     * @version 2.5
     */
    public interface EnumProposals {

        /**
         * Returns the list of enum constants that have been filtered.
         */
        Iterable<String> enumConstants();

        /**
         * The {@link IType} representing the enum type.
         *
         * @return The {@link IType} representing the enum type
         */
        IType enumType();
    }
}
